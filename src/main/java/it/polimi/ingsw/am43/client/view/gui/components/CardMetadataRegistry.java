package it.polimi.ingsw.am43.client.view.gui.components;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class CardMetadataRegistry {
    private static final String CARDS_FILE_PATH = "/it/polimi/ingsw/am43/cards.json";
    private static final Map<Integer, CardMetadata> CARDS = new HashMap<>();
    private static boolean loaded;

    private CardMetadataRegistry() {
    }

    public static CardMetadata get(int cardId) {
        ensureLoaded();
        return CARDS.getOrDefault(cardId, CardMetadata.unknown(cardId));
    }

    public static Map<String, List<Integer>> groupByType(List<Integer> cardIds) {
        ensureLoaded();
        return cardIds.stream()
                .collect(Collectors.groupingBy(cardId -> get(cardId).groupName()));
    }

    private static void ensureLoaded() {
        if (loaded) {
            return;
        }
        loaded = true;
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream inputStream = CardMetadataRegistry.class.getResourceAsStream(CARDS_FILE_PATH)) {
            if (inputStream == null) {
                return;
            }
            JsonNode board = mapper.readTree(inputStream).path("board");
            loadCharacters(board.path("characters"));
            loadEvents(board.path("events"));
            loadBuildings(board.path("buildings"));
        } catch (IOException ignored) {
            CARDS.clear();
        }
    }

    private static void loadCharacters(JsonNode nodes) {
        for (JsonNode node : nodes) {
            int id = node.path("id").asInt();
            String type = node.path("type").asText("Character");
            int era = node.path("era").asInt();
            String detail = switch (type) {
                case "HUNTER" -> node.path("active").asBoolean() ? "Active hunter" : "Hunter";
                case "BUILDER" -> "Discount " + node.path("buildingDiscount").asInt() + " food to buy buildings";
                case "GATHERER" -> "Sustenance discount";
                case "SHAMAN" -> node.path("shamansStars").asInt() + " shaman stars";
                case "INVENTOR" -> "Symbol: " + prettify(node.path("symbol").asText(""));
                case "ARTIST" -> " ";
                default -> "Character";
            };
            CARDS.put(id, new CardMetadata(id, prettify(type), "Character", era, detail));
        }
    }

    private static void loadEvents(JsonNode nodes) {
        for (JsonNode node : nodes) {
            int id = node.path("id").asInt();
            String type = node.path("type").asText("Event");
            int era = node.path("era").asInt();
            CARDS.put(id, new CardMetadata(id, prettify(type) + " Event", "Event", era, "Resolved in the bottom row at round end"));
        }
    }

    private static void loadBuildings(JsonNode nodes) {
        for (JsonNode node : nodes) {
            int id = node.path("id").asInt();
            int era = node.path("era").asInt();
            int cost = node.path("cost").asInt();
            int prestige = node.path("prestigePoints").asInt();
            String detail = "Cost " + cost + " food";
            if (prestige != 0) {
                detail += ", " + prestige + " prestige points at the endgame";
            }
            CARDS.put(id, new CardMetadata(id, "Building", "Building", era, detail));
        }
    }

    private static String prettify(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        String lower = value.toLowerCase().replace('_', ' ');
        StringBuilder builder = new StringBuilder();
        for (String part : lower.split(" ")) {
            if (!part.isBlank()) {
                builder.append(Character.toUpperCase(part.charAt(0)))
                        .append(part.substring(1))
                        .append(" ");
            }
        }
        return builder.toString().trim();
    }

    public record CardMetadata(int id, String name, String category, int era, String detail) {
        private static CardMetadata unknown(int cardId) {
            return new CardMetadata(cardId, "Unknown card", "Card", 0, "Image unavailable");
        }

        public String displayName() {
            return this.name;
        }

        public String shortDescription() {
            if (this.era <= 0) {
                return this.detail;
            }
            return "Era " + this.era + " - " + this.detail;
        }

        public String tooltipText() {
            return this.displayName() + "\n" + this.category + "\n" + this.shortDescription();
        }

        public String groupName() {
            return this.category.equals("Character") ? this.name : this.category;
        }
    }
}

package it.polimi.ingsw.am43.client.view.gui.components;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Registry that loads and provides display metadata for card identifiers.
 */
public final class CardMetadataRegistry {
    private static final String CARDS_FILE_PATH = "/it/polimi/ingsw/am43/cards.json";
    private static final Map<Integer, CardMetadata> CARDS = new HashMap<>();
    private static final Logger LOGGER = Logger.getLogger(CardMetadataRegistry.class.getName());
    private static LoadState loadState = LoadState.NOT_LOADED;

    private enum LoadState {
        NOT_LOADED,
        LOADED,
        FAILED
    }

    private CardMetadataRegistry() {
    }

    /**
     * Returns the metadata associated with the specified card identifier.
     *
     * @param cardId the identifier of the card
     * @return the metadata for the card, or fallback metadata if the card is not registered
     */
    public static CardMetadata get(int cardId) {
        ensureLoaded();
        return CARDS.getOrDefault(cardId, CardMetadata.unknown(cardId));
    }

    /**
     * Groups card identifiers by the display group derived from their metadata.
     *
     * @param cardIds the card identifiers to group
     * @return a map from display group name to card identifiers in that group
     */
    public static Map<String, List<Integer>> groupByType(List<Integer> cardIds) {
        ensureLoaded();
        return cardIds.stream()
                .collect(Collectors.groupingBy(cardId -> get(cardId).groupName(), LinkedHashMap::new, Collectors.toList()));
    }

    private static synchronized void ensureLoaded() {
        if (loadState != LoadState.NOT_LOADED) {
            return;
        }
        ObjectMapper mapper = new ObjectMapper();
        Map<Integer, CardMetadata> loadedCards = new HashMap<>();
        try (InputStream inputStream = CardMetadataRegistry.class.getResourceAsStream(CARDS_FILE_PATH)) {
            if (inputStream == null) {
                loadState = LoadState.FAILED;
                LOGGER.warning("Card metadata file not found: " + CARDS_FILE_PATH);
                return;
            }
            JsonNode board = mapper.readTree(inputStream).path("board");
            loadCharacters(board.path("characters"), loadedCards);
            loadEvents(board.path("events"), loadedCards);
            loadBuildings(board.path("buildings"), loadedCards);
            CARDS.clear();
            CARDS.putAll(loadedCards);
            loadState = LoadState.LOADED;
        } catch (IOException exception) {
            CARDS.clear();
            loadState = LoadState.FAILED;
            LOGGER.log(Level.WARNING, "Unable to load card metadata from " + CARDS_FILE_PATH, exception);
        }
    }

    private static void loadCharacters(JsonNode nodes, Map<Integer, CardMetadata> target) {
        for (JsonNode node : nodes) {
            int id = node.path("id").asInt();
            String type = node.path("type").asText("Character");
            int era = node.path("era").asInt();
            String detail = switch (type) {
                case "HUNTER" -> node.path("active").asBoolean() ? "Active hunter" : "Hunter";
                case "BUILDER" -> builderDetail(node);
                case "GATHERER" -> "Sustenance discount";
                case "SHAMAN" -> node.path("shamansStars").asInt() + " shaman stars";
                case "INVENTOR" -> "Symbol: " + prettify(node.path("symbol").asText(""));
                case "ARTIST" -> "Artist character";
                default -> "Character";
            };
            target.put(id, new CardMetadata(id, prettify(type), "Character", era, detail));
        }
    }

    private static void loadEvents(JsonNode nodes, Map<Integer, CardMetadata> target) {
        for (JsonNode node : nodes) {
            int id = node.path("id").asInt();
            String type = node.path("type").asText("Event");
            int era = node.path("era").asInt();
            target.put(id, new CardMetadata(id, prettify(type) + " Event", "Event", era, "Resolved in the bottom row at round end"));
        }
    }

    private static void loadBuildings(JsonNode nodes, Map<Integer, CardMetadata> target) {
        for (JsonNode node : nodes) {
            int id = node.path("id").asInt();
            int era = node.path("era").asInt();
            int cost = node.path("cost").asInt();
            int prestige = node.path("prestigePoints").asInt();
            String name = buildingName(node.path("classBuilding").asText("Building"));
            String detail = "Cost " + cost + " food";
            if (prestige != 0) {
                detail += ", " + prestige + " prestige points at the endgame";
            }
            String description = node.path("effect").path("description").asText("");
            if (!description.isBlank()) {
                detail += ". " + description;
            }
            target.put(id, new CardMetadata(id, name, "Building", era, detail));
        }
    }

    private static String builderDetail(JsonNode node) {
        String detail = "Discount " + node.path("buildingDiscount").asInt() + " food to buy buildings";
        int prestige = node.path("prestigePoint").asInt();
        if (prestige != 0) {
            detail += ", " + prestige + " prestige points at the endgame";
        }
        return detail;
    }

    private static String buildingName(String classBuilding) {
        String name = prettify(classBuilding);
        if (name.isBlank()) {
            return "Building";
        }
        if (name.endsWith("Building")) {
            return name;
        }
        return name + " Building";
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

    /**
     * Immutable display metadata for a card.
     *
     * @param id the card identifier
     * @param name the display name of the card
     * @param category the display category of the card
     * @param era the card era, or {@code 0} when no era is available
     * @param detail the display detail associated with the card
     */
    public record CardMetadata(int id, String name, String category, int era, String detail) {
        private static CardMetadata unknown(int cardId) {
            return new CardMetadata(cardId, "Unknown card", "Card", 0, "Image unavailable");
        }

        /**
         * Returns the display name of this card.
         *
         * @return the display name
         */
        public String displayName() {
            return this.name;
        }

        /**
         * Returns a short display description for this card.
         *
         * @return the short description
         */
        public String shortDescription() {
            if (this.era <= 0) {
                return this.detail;
            }
            return "Era " + this.era + " - " + this.detail;
        }

        /**
         * Returns the tooltip text for this card.
         *
         * @return the tooltip text
         */
        public String tooltipText() {
            return this.displayName() + "\n" + this.category + "\n" + this.shortDescription();
        }

        /**
         * Returns the display group name used to group this card with related cards.
         *
         * @return the display group name
         */
        public String groupName() {
            return this.category.equals("Character") ? this.name : this.category;
        }
    }
}

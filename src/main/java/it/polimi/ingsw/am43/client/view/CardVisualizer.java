package it.polimi.ingsw.am43.client.view;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.am43.client.OfferTrackElement;
import it.polimi.ingsw.am43.model.enums.Color;
import it.polimi.ingsw.am43.model.enums.InventorSymbol;
import it.polimi.ingsw.am43.model.enums.OfferAction;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

import static it.polimi.ingsw.am43.client.view.TextFormatting.*;

public final class CardVisualizer {
    private static final String CARDS_FILE_PATH = "/it/polimi/ingsw/am43/cards.json";
    private static final Map<Integer, List<String>> idToASCII = new HashMap<>();
    private static final Map<Integer, String> idToIMG = new HashMap<>();
    private static Map<Integer, List<Integer>> orderQueueMap;
    private static final Map<Integer, String> idToType = new HashMap<>();

    public static void loadAscii() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = CardVisualizer.class.getResourceAsStream(CARDS_FILE_PATH);
        if (is == null) throw new FileNotFoundException("File not found.");
        JsonNode root = mapper.readTree(is);
        JsonNode board = root.get("board");

        board.get("characters").forEach(node -> {
            int id = node.get("id").asInt();
            idToASCII.put(id, createCharacterASCII(node));
            idToType.put(id, node.get("type").asText());
        });
        board.get("events").forEach(node -> {
            int id = node.get("id").asInt();
            idToASCII.put(id, createEventASCII(node));
            idToType.put(id, "EVENT");
        });
        board.get("buildings").forEach(node -> {
            int id = node.get("id").asInt();
            idToASCII.put(id, createBuildingASCII(node));
            idToType.put(id, "BUILDING");
        });
        orderQueueMap = mapper.convertValue(board.get("orderQueue"), new TypeReference<>() {
        });
    }

    private static List<String> createBuildingASCII(JsonNode node) {
        List<String> lines = new ArrayList<>();
        int prestige = node.get("prestigePoints").asInt();
        String color;
        switch (node.get("era").asInt()) {
            case 1 -> color = BRONZE;
            case 2 -> color = SILVER;
            case 3 -> color = GOLD;
            default -> color = RESET;
        }

        lines.add("╭─────┬─────────╮");
        lines.add("│" + centerLine("-" + node.get("cost").asText() + "F", color, 5) + "│         │");
        lines.add("├─────╯         │");
        lines.add("│               │");
        lines.add("│" + centerLine("BUILDING ", color, lines.getFirst().length() - 2) + "│");
        if (prestige == 0) {
            lines.add("│               │");
            lines.add("├───────────────┤");
        } else {
            lines.add("│         ╭─────┤");
            lines.add("├─────────╯ " + color + "+" + prestige + "P" + RESET + " │");
        }
        lines.add("│" + centerLine(node.get("effect").get("classEvent").asText().substring(0, 7), color, lines.getFirst().length() - 2) + "│");
        lines.add("╰───────────────╯");
        return lines;
    }

    private static List<String> createEventASCII(JsonNode node) {
        List<String> lines = new ArrayList<>();
        String type = node.get("type").asText();
        int era = node.get("era").asInt();
        String color;
        switch (type) {
            case "HUNT" -> color = RED;
            case "RITUAL" -> color = PURPLE;
            case "SUSTENANCE" -> color = ORANGE;
            case "PAINTING" -> color = YELLOW;
            default -> color = RESET;
        }

        lines.add("╭───────────────╮");
        lines.add("│               │");
        lines.add("│" + centerLine(type, color, lines.getFirst().length() - 2) + "│");
        lines.add("│     " + color + "EVENT" + RESET + "     │");
        lines.add("│               │");
        lines.add("├───────────────┤");
        switch (type) {
            case "HUNT": {
                lines.add("│  Earn 1F & " + era + "P │");
                lines.add("│    x Hunter   │");
                break;
            }
            case "RITUAL": {
                lines.add(String.format("│  Max * : +%2dP │", era * 5));
                lines.add("│  Min * : " + node.get("malus").asInt() + "P  │");
                break;
            }
            case "SUSTENANCE": {
                lines.add("│ Lose 1F xChar │");
                lines.add("│ F=0 : lose " + era + "P │");
                break;
            }
            case "PAINTING": {
                lines.add("│  <" + era + " A : -2P   │");
                lines.add("│  " + era + "+ A : +" + era + "PxA │");
                break;
            }
            default: {
            }
        }
        lines.add("╰───────────────╯");
        return lines;
    }

    private static List<String> createCharacterASCII(JsonNode node) {
        List<String> lines = new ArrayList<>();
        String type = node.get("type").asText();
        String color;
        String bgColor;
        switch (type) {
            case "HUNTER" -> {
                color = RED;
                bgColor = BG_RED;
            }
            case "INVENTOR" -> {
                color = TEAL;
                bgColor = BG_TEAL;
            }
            case "GATHERER" -> {
                color = ORANGE;
                bgColor = BG_ORANGE;
            }
            case "BUILDER" -> {
                color = BROWN;
                bgColor = BG_BROWN;
            }
            case "ARTIST" -> {
                color = YELLOW;
                bgColor = BG_YELLOW;
            }
            case "SHAMAN" -> {
                color = PURPLE;
                bgColor = BG_PURPLE;
            }
            default -> {
                color = RESET;
                bgColor = RESET;
            }
        }

        lines.add("╭─────────┬─────╮");
        lines.add("│         │ " + bgColor + "   " + RESET + " │");
        if (type.equals("INVENTOR")) {
            lines.add("│         │  " + color + getInventorSymbol(node.get("symbol").asText()) + RESET + "  │");
            lines.add("│         ╰─────┤");
        } else if (type.equals("BUILDER")) {
            lines.add("│         │ " + color + "-" + node.get("buildingDiscount").asInt() + "F" + RESET + " │");
            lines.add("│         ╰─────┤");
        } else if (type.equals("SHAMAN")) {
            lines.add("│         │ " + centerLine("*".repeat(node.get("shamansStars").asInt()), color, 3) + " │");
            lines.add("│         ╰─────┤");
        } else if (type.equals("HUNTER") && node.get("active").asBoolean()) {
            lines.add("│         │  " + color + BOLD + "»" + RESET + "  │");
            lines.add("│         ╰─────┤");
        } else {
            lines.add("│         ╰─────┤");
            lines.add("│               │");
        }
        lines.add("│" + centerLine(type, color, lines.getFirst().length() - 2) + "│");
        lines.add("│               │");
        if (type.equals("GATHERER")) {
            lines.add("├────────╮      │");
            lines.add("│ " + bgColor + "   " + RESET + color + " -3F" + RESET + "│      │");
            lines.add("╰────────┴──────╯");
        } else {
            lines.add("├─────╮         │");
            if (type.equals("BUILDER")) {
                lines.add("│ " + color + "+" + node.get("prestigePoint").asInt() + "P" + RESET + " │         │");
            } else lines.add("│ " + bgColor + "   " + RESET + " │         │");
            lines.add("╰─────┴─────────╯");
        }
        return lines;
    }

    private static char getInventorSymbol(String symbol) {
        return (char) ('A' + InventorSymbol.valueOf(symbol).ordinal());
    }

    public static String centerLine(String word, String color, int width) {
        String visibleText = word.replaceAll("\u001B\\[[;\\d]*m", "");
        int visibleLength = visibleText.length();
        int totalPadding = width - visibleLength;
        if (totalPadding < 0) totalPadding = 0;
        int leftPadding = totalPadding / 2;
        int rightPadding = totalPadding - leftPadding;
        return " ".repeat(leftPadding) + color + word + RESET + " ".repeat(rightPadding);
    }

    private static List<String> emptyASCII() {
        List<String> lines = new ArrayList<>();
        lines.add("╭──────────┬────╮");
        lines.add("│          │    │");
        lines.add("│          ╰────┤");
        lines.add("│               │");
        lines.add("│               │");
        lines.add("│               │");
        lines.add("├────╮          │");
        lines.add("│    │          │");
        lines.add("╰────┴──────────╯");
        return lines;
    }

    public static List<String> getASCII(int id) {
        return idToASCII.getOrDefault(id, emptyASCII());
    }

    public static List<String> getEraASCII(int currentEra) {
        List<String> lines = new ArrayList<>();
        String color;
        switch (currentEra) {
            case 1 -> color = BRONZE;
            case 2 -> color = SILVER;
            case 3 -> color = GOLD;
            default -> color = RESET;
        }

        lines.add("╭───────────────╮");
        lines.add("│               │");
        lines.add("│" + centerLine("═╦═".repeat(currentEra), color, lines.getFirst().length() - 2) + "│");
        lines.add("│" + centerLine(" ║ ".repeat(currentEra), color, lines.getFirst().length() - 2) + "│");
        lines.add("│" + centerLine(" ║ ".repeat(currentEra), color, lines.getFirst().length() - 2) + "│");
        lines.add("│" + centerLine(" ║ ".repeat(currentEra), color, lines.getFirst().length() - 2) + "│");
        lines.add("│" + centerLine("═╩═".repeat(currentEra), color, lines.getFirst().length() - 2) + "│");
        lines.add("│               │");
        lines.add("╰───────────────╯");

        return lines;
    }

    public static List<String> getOrderQueueASCII(int numPlayers, List<Color> orderQueue) {
        List<String> lines = new ArrayList<>();
        List<Integer> bonus = orderQueueMap.get(numPlayers);
        lines.add("╭───────────────╮");
        lines.add("│               │");
        for (int i = 0; i < 5; i++) {
            if (i >= bonus.size()) {
                lines.add("│               │");
                continue;
            }
            lines.add(String.format("│  %2d  [%3s]    │", bonus.get(i), i>=orderQueue.size() ? "   " : getASCIIBGColor(orderQueue.get(i)) + "   " + RESET));
        }
        lines.add("│               │");
        lines.add("╰───────────────╯");
        return lines;
    }

    public static List<String> getOfferTrackASCII(OfferTrackElement card) {
        List<String> lines = new ArrayList<>();

        lines.add("╭───────────────╮");
        lines.add("│               │");
        lines.add("│               │");
        lines.add(String.format("│     [%3s]     │",  card.getColor() == null ? "   " : getASCIIBGColor(card.getColor()) + "   " + RESET));
        lines.add("│               │");
        lines.add("│               │");
        lines.add("├───────────────┤");
        lines.add("│" + centerLine(getOfferActionsASCII(card), RESET, lines.getFirst().length() - 2) + "│");
        lines.add("╰───────────────╯");
        return lines;
    }

    private static String getOfferActionsASCII(OfferTrackElement card) {
        StringBuilder actions = new StringBuilder();
        for (OfferAction o :  card.getOfferActions()) {
            switch (o) {
                case TOP -> actions.append(" ⇑ ");
                case BOTTOM -> actions.append(" ⇓ ");
                case FOOD -> actions.append("+3F");
            }
        }
        return actions.toString();
    }

    public static String getASCIIColor(Color color) {
        String colorCode;
        switch (color) {
            case RED -> colorCode = RED;
            case YELLOW -> colorCode = YELLOW;
            case WHITE -> colorCode = WHITE;
            case BLACK ->  colorCode = BLACK;
            case CYAN -> colorCode = TEAL;
            default -> colorCode = RESET;
        }
        return colorCode;
    }

    public static String getASCIIBGColor(Color color) {
        String bGColorCode;
        switch (color) {
            case RED -> bGColorCode = BG_RED;
            case YELLOW -> bGColorCode = BG_YELLOW;
            case WHITE -> bGColorCode = BG_WHITE;
            case BLACK ->  bGColorCode = BG_BLACK;
            case CYAN -> bGColorCode = BG_TEAL;
            default -> bGColorCode = RESET;
        }
        return bGColorCode;
    }

    public static String getColorArrayString(List<Color> colors) {
        StringJoiner joiner = new StringJoiner(", ");
        for (Color color : colors) {
            String coloredName = getASCIIColor(color) + color.name().toLowerCase() + RESET;
            joiner.add(coloredName);
        }
        return joiner.toString();
    }

    public static Map<String, List<Integer>> divideTribe(List<Integer> ids) {
        return ids.stream().collect(Collectors.groupingBy(CardVisualizer::getCharacterType));
    }

    private static String getCharacterType(int id) {
        return idToType.get(id);
    }
}
package it.polimi.ingsw.am43.client.view.tui;

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

import static it.polimi.ingsw.am43.client.view.tui.TextFormatting.*;

/**
 * Generates and caches ASCII art representations of game cards for the TUI.
 */
public class CardVisualizer {
    private static final String CARDS_FILE_PATH = "/it/polimi/ingsw/am43/cards.json";
    private static final int CARD_WIDTH = 15;
    private final Map<Integer, List<String>> idToASCII = new HashMap<>();
    private final Map<Integer, List<Integer>> orderQueueMap;
    private final Map<Integer, String> idToType = new HashMap<>();

    /**
     * Parses the external JSON file to populate internal ASCII asset dictionaries.
     *
     * @throws IOException if the file reading fails
     */
    public CardVisualizer() throws IOException {
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

    /**
     * Formats building card data into an ASCII art template.
     *
     * @param node The JSON data node defining the building.
     * @return the multi-line populated template.
     */
    private List<String> createBuildingASCII(JsonNode node) {
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
        lines.add("│               │");
        lines.add("│" + centerLine("BUILDING ", color, CARD_WIDTH) + "│");
        lines.add("│               │");
        if (prestige == 0) {
            lines.add("│               │");
            lines.add("├───────────────┤");
        } else {
            lines.add("│         ╭─────┤");
            lines.add("├─────────╯ " + color + "+" + prestige + "P" + RESET + " │");
        }
        lines.add("│" + centerLine(node.get("effect").get("symbol").asText(), color, CARD_WIDTH) + "│");
        lines.add("╰───────────────╯");
        return lines;
    }

    /**
     * Formats event card data into an ASCII art template.
     *
     * @param node The JSON data node defining the event.
     * @return the multi-line populated template.
     */
    private List<String> createEventASCII(JsonNode node) {
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
        lines.add("│               │");
        lines.add("│" + centerLine(type, color, CARD_WIDTH) + "│");
        lines.add("│     " + color + "EVENT" + RESET + "     │");
        lines.add("│               │");
        lines.add("│               │");
        lines.add("├───────────────┤");
        switch (type) {
            case "HUNT": {
                lines.add("│ Earn 1F & " + era + "PP │");
                lines.add("│    x Hunter   │");
                break;
            }
            case "RITUAL": {
                lines.add(String.format("│ Max * : +%2dPP │", era * 5));
                lines.add("│ Min * : " + node.get("malus").asInt() + "PP  │");
                break;
            }
            case "SUSTENANCE": {
                lines.add("│ Lose 1F xChar │");
                lines.add("│ F=0: lose " + era + "PP │");
                break;
            }
            case "PAINTING": {
                lines.add("│  <" + era + " A:  -2PP  │");
                lines.add("│  " + era + "+ A: +" + era + "PPxA │");
                break;
            }
            default: {
            }
        }
        lines.add("╰───────────────╯");
        return lines;
    }

    /**
     * Formats character card data into an ASCII art template.
     *
     * @param node The JSON data node defining the character.
     * @return the multi-line populated template.
     */
    private List<String> createCharacterASCII(JsonNode node) {
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
        lines.add("│               │");
        lines.add("│" + centerLine(type, color, CARD_WIDTH) + "│");
        lines.add("│               │");
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

    private char getInventorSymbol(String symbol) {
        return (char) ('A' + InventorSymbol.valueOf(symbol).ordinal());
    }

    /**
     * Pads dynamic text blocks evenly, ignoring nested ANSI escape codes when computing alignment offsets.
     *
     * @param word  the text string to center
     * @param color the ANSI color code to wrap around the text
     * @param width the target bounding box width
     * @return the centered and colored string line
     */
    public String centerLine(String word, String color, int width) {
        String visibleText = word.replaceAll("\u001B\\[[;\\d]*m", "");
        int visibleLength = visibleText.length();
        int totalPadding = width - visibleLength;
        if (totalPadding < 0) totalPadding = 0;
        int leftPadding = totalPadding / 2;
        int rightPadding = totalPadding - leftPadding;
        return " ".repeat(leftPadding) + color + word + RESET + " ".repeat(rightPadding);
    }

    private List<String> emptyASCII() {
        List<String> lines = new ArrayList<>();
        lines.add("╭──────────┬────╮");
        lines.add("│          │    │");
        lines.add("│          ╰────┤");
        lines.add("│               │");
        lines.add("│               │");
        lines.add("│               │");
        lines.add("│               │");
        lines.add("│               │");
        lines.add("├────╮          │");
        lines.add("│    │          │");
        lines.add("╰────┴──────────╯");
        return lines;
    }

    /**
     * Extracts the ASCII art for the specified card.
     *
     * @param id The identifier of the requested card.
     * @return the multi-line populated template.
     */
    public List<String> getASCII(int id) {
        return idToASCII.getOrDefault(id, emptyASCII());
    }

    /**
     * Extracts the ASCII art for the specified era.
     *
     * @param currentEra The number of the requested era.
     * @return the multi-line populated template.
     */
    public List<String> getEraASCII(int currentEra) {
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
        lines.add("│               │");
        lines.add("│" + centerLine("═╦═".repeat(currentEra), color, CARD_WIDTH) + "│");
        lines.add("│" + centerLine(" ║ ".repeat(currentEra), color, CARD_WIDTH) + "│");
        lines.add("│" + centerLine(" ║ ".repeat(currentEra), color, CARD_WIDTH) + "│");
        lines.add("│" + centerLine(" ║ ".repeat(currentEra), color, CARD_WIDTH) + "│");
        lines.add("│" + centerLine("═╩═".repeat(currentEra), color, CARD_WIDTH) + "│");
        lines.add("│               │");
        lines.add("│               │");
        lines.add("╰───────────────╯");

        return lines;
    }

    /**
     * Extracts the ASCII art for the specified order queue configuration.
     *
     * @param numPlayers          The number of slots needed.
     * @param orderQueue          The active players to display.
     * @param disconnectedPlayers The disconnected players to display with special appearance.
     * @param waitingPlayers      The waiting players to display with special ordering.
     * @param top                 {@code true} if the empty slots need to be added at the bottom, {@code false} otherwise.
     * @return the multi-line populated template.
     */
    public List<String> getOrderQueueASCII(int numPlayers, List<Color> orderQueue, List<Color> disconnectedPlayers, List<Color> waitingPlayers, boolean top) {
        List<String> lines = new ArrayList<>();
        List<Integer> bonus = orderQueueMap.get(numPlayers);

        lines.add("╭───────────────╮");
        lines.add("│               │");
        lines.add("│               │");
        int i = 0;
        if (!top) {
            while (i < numPlayers - (orderQueue.size() + waitingPlayers.size() + disconnectedPlayers.size())) {
                lines.add(String.format("│  %2d  [   ]    │", bonus.get(i)));
                i++;
            }
        }
        for (Color color : orderQueue) {
            lines.add(String.format("│  %2d  [%s]    │", bonus.get(i), getASCIIBGColor(color) + "   " + RESET));
            i++;
        }
        if (top) {
            while (i < numPlayers - (waitingPlayers.size() + disconnectedPlayers.size())) {
                lines.add(String.format("│  %2d  [   ]    │", bonus.get(i)));
                i++;
            }
        }
        for (Color color : waitingPlayers) {
            lines.add(String.format("│  %2d  [%s]    │", bonus.get(i), getASCIIBGColor(color) + "   " + RESET));
            i++;
        }
        for (Color color : disconnectedPlayers) {
            lines.add(String.format("│  %2d  [%s]    │", bonus.get(i), getASCIIBGColor(color) + MESOS + "///" + RESET));
            i++;
        }
        while (i < 5) {
            lines.add("│               │");
            i++;
        }
        lines.add("│               │");
        lines.add("│               │");
        lines.add("╰───────────────╯");
        return lines;
    }

    /**
     * Extracts the ASCII art for the specified offer track card.
     *
     * @param card The requested offer track card.
     * @return the multi-line populated template.
     */
    public List<String> getOfferTrackASCII(OfferTrackElement card) {
        List<String> lines = new ArrayList<>();

        lines.add("╭───────────────╮");
        lines.add("│               │");
        lines.add("│               │");
        lines.add("│               │");
        lines.add(String.format("│     [%3s]     │", card.getColor() == null ? "   " : getASCIIBGColor(card.getColor()) + "   " + RESET));
        lines.add("│               │");
        lines.add("│               │");
        lines.add("│               │");
        lines.add("├───────────────┤");
        lines.add("│" + centerLine(getOfferActionsASCII(card), RESET, CARD_WIDTH) + "│");
        lines.add("╰───────────────╯");
        return lines;
    }

    private String getOfferActionsASCII(OfferTrackElement card) {
        StringJoiner actions = new StringJoiner(" ");
        for (OfferAction o : card.getOfferActions()) {
            switch (o) {
                case TOP -> actions.add(ARROW_UP + " ⇑ " + RESET);
                case BOTTOM -> actions.add(ARROW_DOWN + " ⇓ " + RESET);
                case FOOD -> actions.add("+3F");
            }
        }
        return actions.toString();
    }

    /**
     * Maps {@link Color} objects to ANSI foreground color escape codes.
     *
     * @param color The color to map.
     * @return the ANSI escape string.
     */
    public String getASCIIColor(Color color) {
        String colorCode;
        switch (color) {
            case RED -> colorCode = RED;
            case YELLOW -> colorCode = YELLOW;
            case WHITE -> colorCode = WHITE;
            case BLACK -> colorCode = BLACK;
            case CYAN -> colorCode = TEAL;
            default -> colorCode = RESET;
        }
        return colorCode;
    }

    /**
     * Maps {@link Color} objects to ANSI background color escape codes.
     *
     * @param color The color to map.
     * @return the ANSI escape string.
     */
    public static String getASCIIBGColor(Color color) {
        String bGColorCode;
        switch (color) {
            case RED -> bGColorCode = BG_RED;
            case YELLOW -> bGColorCode = BG_YELLOW;
            case WHITE -> bGColorCode = BG_WHITE;
            case BLACK -> bGColorCode = BG_BLACK;
            case CYAN -> bGColorCode = BG_TEAL;
            default -> bGColorCode = RESET;
        }
        return bGColorCode;
    }

    /**
     * Returns a colored, comma-separated list representation of mapped {@link Color} objects.
     *
     * @param colors The list of colors to format.
     * @return the aggregated and colored string.
     */
    public String getColorArrayString(List<Color> colors) {
        StringJoiner joiner = new StringJoiner(", ");
        for (Color color : colors) {
            String coloredName = getASCIIColor(color) + color.name().toLowerCase() + RESET;
            joiner.add(coloredName);
        }
        return joiner.toString();
    }

    /**
     * Classifies an unorganized list of pickable card based on their type.
     *
     * @param ids the list of card identifiers.
     * @return a map linking category text labels to sets of card IDs.
     */
    public Map<String, List<Integer>> divideTribe(List<Integer> ids) {
        return ids.stream().collect(Collectors.groupingBy(this::getCharacterType));
    }

    private String getCharacterType(int id) {
        return idToType.get(id);
    }
}
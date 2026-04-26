package it.polimi.ingsw.am43.client.view;

public final class TextFormatting {
    private TextFormatting() {}

    public static final String RESET = "\u001B[0m";
    public static final String BOLD  = "\u001B[1m";
    public static final String DIM   = "\u001B[2m";
    public static final String CLEAR_SCREEN = "\033[H\033[2J";

    public static final String HUNT_RED    = "\u001B[38;5;160m";
    public static final String GATHER_GREEN = "\u001B[38;5;34m";
    public static final String BUILD_GOLD   = "\u001B[38;5;214m";
    public static final String INFO_CYAN    = "\u001B[38;5;39m";

    public static final String BG_HUNT   = "\u001B[48;5;160m";
    public static final String BG_GATHER = "\u001B[48;5;34m";
    public static final String BG_BUILD  = "\u001B[48;5;214m";
    public static final String BG_BLUE   = "\u001B[48;5;25m"; // For Table Headers

    public static final String WHITE = "\u001B[37m";
    public static final String ERROR = "\u001B[1;31m"; // Bold Red

    public static void clear() {
        System.out.print(CLEAR_SCREEN);
        System.out.flush();
    }
}

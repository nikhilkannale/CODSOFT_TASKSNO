/**
 * ConsoleColors
 * ----------------------------------------------------------------------
 * Small utility that wraps ANSI escape codes so the ATM console output
 * is easier to read (green for success, red for errors, yellow for
 * warnings/prompts, cyan for headers). Falls back gracefully on
 * terminals that don't support ANSI - the codes are simply ignored.
 * ----------------------------------------------------------------------
 */
public final class ConsoleColors {

    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String CYAN = "\u001B[36m";
    public static final String BOLD = "\u001B[1m";

    private ConsoleColors() {
    }

    public static void printSuccess(String message) {
        System.out.println(GREEN + message + RESET);
    }

    public static void printError(String message) {
        System.out.println(RED + message + RESET);
    }

    public static void printWarning(String message) {
        System.out.println(YELLOW + message + RESET);
    }

    public static void printHeader(String message) {
        System.out.println(CYAN + BOLD + message + RESET);
    }
}

import java.util.Scanner;

/**
 * InputValidator
 * ----------------------------------------------------------------------
 * A collection of static helper methods used to safely read and validate
 * user input from the console. Centralising validation here keeps the
 * ATM and BankAccount classes focused purely on business logic, and
 * guarantees the application never crashes because of bad user input.
 * ----------------------------------------------------------------------
 */
public final class InputValidator {

    // Prevent instantiation - this is a pure utility class.
    private InputValidator() {
    }

    /**
     * Safely reads an integer menu choice from the user.
     * Re-prompts on non-numeric input instead of throwing/crashing.
     */
    public static int readMenuChoice(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                ConsoleColors.printError("Invalid choice. Please enter a number.");
            }
        }
    }

    /**
     * Safely reads a monetary amount, rejecting non-numeric, negative,
     * or zero values. Returns -1 only when the caller explicitly wants
     * a single-shot read that can be treated as "cancel" (not used by
     * default flows, but kept for extensibility).
     */
    public static double readPositiveAmount(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                double amount = Double.parseDouble(line);
                if (amount <= 0) {
                    ConsoleColors.printError("Amount must be greater than zero. Please try again.");
                    continue;
                }
                if (amount > 10_000_000) {
                    ConsoleColors.printError("Amount is unrealistically large. Please try again.");
                    continue;
                }
                return amount;
            } catch (NumberFormatException e) {
                ConsoleColors.printError("Invalid input. Please enter a valid numeric amount.");
            }
        }
    }

    /**
     * Reads a raw line of text (used for names, PIN entry, etc.)
     * ensuring the result is never null.
     */
    public static String readLine(Scanner scanner, String prompt) {
        System.out.print(prompt);
        String line = scanner.nextLine();
        return line == null ? "" : line.trim();
    }

    /**
     * Validates that a PIN is exactly 4 numeric digits.
     */
    public static boolean isValidPinFormat(String pin) {
        return pin != null && pin.matches("\\d{4}");
    }

    /**
     * Validates that an account number is numeric and a reasonable length.
     */
    public static boolean isValidAccountNumber(String accountNumber) {
        return accountNumber != null && accountNumber.matches("\\d{6,16}");
    }
}

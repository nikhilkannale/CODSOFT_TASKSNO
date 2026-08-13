import java.util.Scanner;

/**
 * Main
 * ----------------------------------------------------------------------
 * Application entry point. Sets up (or restores) a demo bank account,
 * wires it up to the ATM console interface, and starts the session.
 *
 * Default demo credentials (used the very first time the app runs,
 * before any data has been saved to disk):
 *   Account Number : 7795914504
 *   PIN            : 959145
 * ----------------------------------------------------------------------
 */
public class Main {

    private static final String DEMO_ACCOUNT_NUMBER = "7795914504";
    private static final String DEMO_ACCOUNT_HOLDER = "Nikhil kannale";
    private static final double DEMO_INITIAL_BALANCE = 100_000.00;
    private static final String DEMO_PIN = "959145";

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            BankAccount account = loadOrCreateAccount();

            ATM atm = new ATM(account, scanner);
            atm.start();
        } catch (Exception e) {
            // Final safety net - the application should never crash with
            // an unhandled exception, no matter what happens above.
            ConsoleColors.printError("An unexpected error occurred: " + e.getMessage());
            System.out.println("Application Closed.");
        }
    }

    /**
     * Restores the demo account from disk if a previous session saved
     * data for it; otherwise creates a fresh account with the seed
     * balance and PIN defined above.
     */
    private static BankAccount loadOrCreateAccount() {
        if (AccountStorage.accountExists(DEMO_ACCOUNT_NUMBER)) {
            BankAccount restored = AccountStorage.loadAccount(DEMO_ACCOUNT_NUMBER);
            if (restored != null) {
                return restored;
            }
        }
        return new BankAccount(DEMO_ACCOUNT_NUMBER, DEMO_ACCOUNT_HOLDER,
                DEMO_INITIAL_BALANCE, DEMO_PIN);
    }
}

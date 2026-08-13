import java.util.List;
import java.util.Scanner;

/**
 * ATM
 * ----------------------------------------------------------------------
 * Drives the console user interface: login, the main menu loop, and
 * dispatching each menu choice to the appropriate BankAccount method.
 * The ATM never reaches into BankAccount's fields directly - every
 * interaction happens through the account's public API, keeping the
 * two classes properly decoupled.
 * ----------------------------------------------------------------------
 */
public class ATM {

    private static final int MAX_LOGIN_ATTEMPTS = 3;

    private final BankAccount account;
    private final Scanner scanner;
    private boolean sessionActive;

    public ATM(BankAccount account, Scanner scanner) {
        this.account = account;
        this.scanner = scanner;
    }

    /**
     * Runs the full ATM session: login screen followed by the main
     * menu loop. Returns true if the user successfully authenticated
     * and used the ATM (regardless of how the session ended), false
     * if login failed and the account was locked.
     */
    public boolean start() {
        printWelcomeBanner();
        if (!login()) {
            ConsoleColors.printError("Account Locked.");
            System.out.println("Application Closed.");
            return false;
        }
        runMenuLoop();
        return true;
    }

    // =========================================================================
    // Login
    // =========================================================================

    private void printWelcomeBanner() {
        System.out.println(ConsoleColors.CYAN + ConsoleColors.BOLD);
        System.out.println("==================================");
        System.out.println("      WELCOME TO JAVA ATM");
        System.out.println("==================================" + ConsoleColors.RESET);
    }

    /**
     * Handles account-number + PIN authentication, allowing up to
     * MAX_LOGIN_ATTEMPTS attempts before locking the account.
     */
    private boolean login() {
        for (int attempt = 1; attempt <= MAX_LOGIN_ATTEMPTS; attempt++) {
            String enteredAccountNumber = InputValidator.readLine(scanner, "Enter Account Number: ");
            String enteredPin = InputValidator.readLine(scanner, "Enter PIN: ");

            boolean accountMatches = enteredAccountNumber.equals(account.getAccountNumber());
            boolean pinMatches = account.verifyPin(enteredPin);

            if (accountMatches && pinMatches) {
                ConsoleColors.printSuccess("\nLogin Successful! Welcome, " + account.getAccountHolderName() + ".\n");
                return true;
            }

            int remaining = MAX_LOGIN_ATTEMPTS - attempt;
            if (remaining > 0) {
                ConsoleColors.printError("Invalid PIN. Attempts remaining: " + remaining + "\n");
            }
        }
        return false;
    }

    // =========================================================================
    // Main menu loop
    // =========================================================================

    private void runMenuLoop() {
        sessionActive = true;
        while (sessionActive) {
            printMenu();
            int choice = InputValidator.readMenuChoice(scanner, "Enter Choice: ");
            System.out.println();
            handleChoice(choice);
            System.out.println();
        }
    }

    private void printMenu() {
        System.out.println(ConsoleColors.CYAN + "==================================");
        System.out.println("            ATM MENU");
        System.out.println("==================================" + ConsoleColors.RESET);
        System.out.println("1. Check Balance");
        System.out.println("2. Deposit Money");
        System.out.println("3. Withdraw Money");
        System.out.println("4. Transaction History");
        System.out.println("5. Change PIN");
        System.out.println("6. Account Information");
        System.out.println("7. Exit");
        System.out.println(ConsoleColors.CYAN + "----------------------------------" + ConsoleColors.RESET);
    }

    private void handleChoice(int choice) {
        switch (choice) {
            case 1 -> handleCheckBalance();
            case 2 -> handleDeposit();
            case 3 -> handleWithdraw();
            case 4 -> handleTransactionHistory();
            case 5 -> handleChangePin();
            case 6 -> handleAccountInformation();
            case 7 -> handleExit();
            default -> ConsoleColors.printError("Invalid choice. Please select an option between 1 and 7.");
        }
    }

    // =========================================================================
    // Menu option handlers
    // =========================================================================

    private void handleCheckBalance() {
        double balance = account.checkBalance();
        ConsoleColors.printHeader("Current Balance:");
        System.out.printf("Rs. %,.2f%n", balance);
    }

    private void handleDeposit() {
        double amount = InputValidator.readPositiveAmount(scanner, "Enter Deposit Amount: Rs. ");
        try {
            account.deposit(amount);
            ConsoleColors.printSuccess("Deposit Successful!");
            System.out.printf("Amount Deposited : Rs. %,.2f%n", amount);
            System.out.printf("Updated Balance   : Rs. %,.2f%n", account.getBalance());
        } catch (IllegalArgumentException e) {
            ConsoleColors.printError(e.getMessage());
        }
    }

    private void handleWithdraw() {
        System.out.printf("(Daily withdrawal limit: Rs. %,.2f | Remaining today: Rs. %,.2f)%n",
                BankAccount.getDailyWithdrawalLimit(), account.getRemainingDailyLimit());
        double amount = InputValidator.readPositiveAmount(scanner, "Enter Withdrawal Amount: Rs. ");
        try {
            account.withdraw(amount);
            ConsoleColors.printSuccess("Withdrawal Successful!");
            System.out.printf("Remaining Balance : Rs. %,.2f%n", account.getBalance());
        } catch (IllegalStateException e) {
            // Insufficient balance or daily limit exceeded
            ConsoleColors.printError(e.getMessage());
        } catch (IllegalArgumentException e) {
            ConsoleColors.printError(e.getMessage());
        }
    }

    private void handleTransactionHistory() {
        List<Transaction> history = account.getTransactionHistory();
        ConsoleColors.printHeader("========== TRANSACTION HISTORY ==========");
        if (history.isEmpty()) {
            System.out.println("No transactions yet.");
            return;
        }
        int index = 1;
        for (Transaction t : history) {
            System.out.println(index++ + ". " + t.toDisplayString());
        }
    }

    private void handleChangePin() {
        String currentPin = InputValidator.readLine(scanner, "Enter Current PIN: ");
        String newPin = InputValidator.readLine(scanner, "Enter New PIN: ");
        String confirmPin = InputValidator.readLine(scanner, "Confirm New PIN: ");

        boolean updated = account.changePin(currentPin, newPin, confirmPin);
        if (updated) {
            ConsoleColors.printSuccess("PIN Updated Successfully.");
        }
    }

    private void handleAccountInformation() {
        ConsoleColors.printHeader("========== ACCOUNT INFORMATION ==========");
        account.displayAccountInfo();
    }

    private void handleExit() {
        AccountStorage.saveAccount(account);
        AccountStorage.saveTransactionHistory(account);
        System.out.println(ConsoleColors.GREEN + "Thank You For Using Java ATM." + ConsoleColors.RESET);
        System.out.println("Visit Again!");
        sessionActive = false;
    }
}

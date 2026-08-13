import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * BankAccount
 * ----------------------------------------------------------------------
 * Represents a single bank account with strict encapsulation - every
 * field is private and can only be read or modified through methods.
 * The ATM class never touches these fields directly; it only calls the
 * public API defined here (deposit, withdraw, checkBalance, changePin,
 * displayAccountInfo, ...).
 *
 * The PIN itself is never stored in plain text - it is hashed with
 * SHA-256 the moment it is set, and only the hash is compared during
 * authentication / PIN changes.
 * ----------------------------------------------------------------------
 */
public class BankAccount {

    // ---- Core account data ------------------------------------------------
    private final String accountNumber;
    private String accountHolderName;
    private double balance;
    private String pinHash;

    // ---- Daily withdrawal limit tracking ----------------------------------
    private static final double DAILY_WITHDRAWAL_LIMIT = 30_000.0;
    private double withdrawnToday;
    private LocalDate lastWithdrawalDate;

    // ---- Transaction history ------------------------------------------------
    private final List<Transaction> transactionHistory = new ArrayList<>();

    /**
     * Creates a new bank account.
     *
     * @param accountNumber     unique account identifier
     * @param accountHolderName name printed on account info screens
     * @param initialBalance    opening balance
     * @param pin               plain-text 4-digit PIN (hashed internally)
     */
    public BankAccount(String accountNumber, String accountHolderName,
                        double initialBalance, String pin) {
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.balance = initialBalance;
        this.pinHash = hashPin(pin);
        this.withdrawnToday = 0.0;
        this.lastWithdrawalDate = LocalDate.now();
    }

    /**
     * Private constructor used only by {@link #restoreFromHash} to rebuild
     * an account from persisted data where the PIN is already hashed.
     */
    private BankAccount(String accountNumber, String accountHolderName,
                         double balance, String pinHash, boolean alreadyHashed) {
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.balance = balance;
        this.pinHash = pinHash;
        this.withdrawnToday = 0.0;
        this.lastWithdrawalDate = LocalDate.now();
    }

    /**
     * Rebuilds a BankAccount from data previously persisted to disk
     * (see {@code AccountStorage}), where the PIN hash is already known
     * and must NOT be re-hashed.
     */
    public static BankAccount restoreFromHash(String accountNumber, String accountHolderName,
                                               double balance, String pinHash) {
        return new BankAccount(accountNumber, accountHolderName, balance, pinHash, true);
    }

    /**
     * Exposes the raw PIN hash so it can be persisted to disk.
     * (Never exposes the plain-text PIN - it is never stored anywhere.)
     */
    public String getPinHash() {
        return pinHash;
    }

    // =========================================================================
    // Authentication
    // =========================================================================

    /**
     * Verifies a plain-text PIN against the stored hash.
     */
    public boolean verifyPin(String pin) {
        return pinHash.equals(hashPin(pin));
    }

    /**
     * Changes the account PIN after verifying the current PIN and
     * confirming the new PIN was entered correctly twice.
     *
     * @return true if the PIN was updated, false if validation failed
     */
    public boolean changePin(String currentPin, String newPin, String confirmPin) {
        if (!verifyPin(currentPin)) {
            ConsoleColors.printError("Current PIN is incorrect.");
            return false;
        }
        if (!InputValidator.isValidPinFormat(newPin)) {
            ConsoleColors.printError("New PIN must contain exactly 4 digits.");
            return false;
        }
        if (!newPin.equals(confirmPin)) {
            ConsoleColors.printError("PIN confirmation does not match.");
            return false;
        }
        this.pinHash = hashPin(newPin);
        recordTransaction(Transaction.Type.PIN_CHANGE, 0.0);
        return true;
    }

    // =========================================================================
    // Core banking operations
    // =========================================================================

    /**
     * Deposits a positive amount into the account.
     *
     * @throws IllegalArgumentException if the amount is invalid
     */
    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be greater than zero.");
        }
        balance += amount;
        recordTransaction(Transaction.Type.DEPOSIT, amount);
    }

    /**
     * Withdraws a positive amount from the account, enforcing both the
     * available balance and the daily withdrawal limit.
     *
     * @throws IllegalArgumentException if the amount is invalid
     * @throws IllegalStateException    if funds are insufficient or the
     *                                  daily limit would be exceeded
     */
    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be greater than zero.");
        }
        resetDailyLimitIfNewDay();

        if (amount > balance) {
            throw new IllegalStateException("Insufficient Balance.");
        }
        if (withdrawnToday + amount > DAILY_WITHDRAWAL_LIMIT) {
            double remaining = DAILY_WITHDRAWAL_LIMIT - withdrawnToday;
            throw new IllegalStateException(String.format(
                    "Daily withdrawal limit exceeded. You can withdraw up to Rs. %,.2f more today.",
                    Math.max(remaining, 0)));
        }

        balance -= amount;
        withdrawnToday += amount;
        recordTransaction(Transaction.Type.WITHDRAWAL, amount);
    }

    /**
     * Returns the current balance and logs a "Balance Inquiry" transaction.
     */
    public double checkBalance() {
        recordTransaction(Transaction.Type.BALANCE_INQUIRY, 0.0);
        return balance;
    }

    /**
     * Prints formatted account information (holder, number, balance).
     */
    public void displayAccountInfo() {
        System.out.println(ConsoleColors.CYAN + "----------------------------------------" + ConsoleColors.RESET);
        System.out.println("Account Holder  : " + accountHolderName);
        System.out.println("Account Number  : " + accountNumber);
        System.out.printf("Current Balance : Rs. %,.2f%n", balance);
        System.out.println(ConsoleColors.CYAN + "----------------------------------------" + ConsoleColors.RESET);
    }

    // =========================================================================
    // Transaction history
    // =========================================================================

    private void recordTransaction(Transaction.Type type, double amount) {
        transactionHistory.add(new Transaction(type, amount, balance));
    }

    public List<Transaction> getTransactionHistory() {
        return new ArrayList<>(transactionHistory); // defensive copy
    }

    // =========================================================================
    // Getters (read-only access for the ATM / display layer)
    // =========================================================================

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public double getBalance() {
        return balance;
    }

    public double getRemainingDailyLimit() {
        resetDailyLimitIfNewDay();
        return DAILY_WITHDRAWAL_LIMIT - withdrawnToday;
    }

    public static double getDailyWithdrawalLimit() {
        return DAILY_WITHDRAWAL_LIMIT;
    }

    // =========================================================================
    // Internal helpers
    // =========================================================================

    private void resetDailyLimitIfNewDay() {
        LocalDate today = LocalDate.now();
        if (!today.equals(lastWithdrawalDate)) {
            withdrawnToday = 0.0;
            lastWithdrawalDate = today;
        }
    }

    /**
     * Hashes a PIN using SHA-256 so it is never stored or compared in
     * plain text. This keeps the account secure even if the in-memory
     * object were somehow inspected or the object were serialized.
     */
    private static String hashPin(String pin) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(pin.getBytes());
            StringBuilder hex = new StringBuilder();
            for (byte b : hashBytes) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 is always available on any standard JVM, but if this
            // ever happened we fail loudly rather than store a plain PIN.
            throw new RuntimeException("Required hashing algorithm not available.", e);
        }
    }
}

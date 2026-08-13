import java.io.*;
import java.nio.file.*;
import java.util.List;

/**
 * AccountStorage
 * ----------------------------------------------------------------------
 * Handles simple file-based persistence so account balance and
 * transaction history survive between runs of the application
 * (a bonus feature beyond the core CodSoft requirements).
 *
 * Files are written to a local "data" folder:
 *   data/account_<accountNumber>.dat        -> holder|balance|pinHash
 *   data/transactions_<accountNumber>.txt   -> one line per transaction
 * ----------------------------------------------------------------------
 */
public final class AccountStorage {

    private static final Path DATA_DIR = Paths.get("data");

    private AccountStorage() {
    }

    private static Path accountFile(String accountNumber) {
        return DATA_DIR.resolve("account_" + accountNumber + ".dat");
    }

    private static Path transactionsFile(String accountNumber) {
        return DATA_DIR.resolve("transactions_" + accountNumber + ".txt");
    }

    /** Returns true if a previously saved account file exists. */
    public static boolean accountExists(String accountNumber) {
        return Files.exists(accountFile(accountNumber));
    }

    /**
     * Loads a persisted account from disk. Returns null if no saved
     * data exists or the file is unreadable/corrupt.
     */
    public static BankAccount loadAccount(String accountNumber) {
        Path file = accountFile(accountNumber);
        if (!Files.exists(file)) {
            return null;
        }
        try {
            String line = Files.readString(file).trim();
            String[] parts = line.split("\\|", 4);
            if (parts.length != 4) {
                return null;
            }
            String name = parts[0];
            double balance = Double.parseDouble(parts[1]);
            String pinHash = parts[2];
            // parts[3] reserved for the account number itself (sanity check)
            return BankAccount.restoreFromHash(accountNumber, name, balance, pinHash);
        } catch (IOException | NumberFormatException e) {
            ConsoleColors.printWarning("Could not load saved account data. Starting fresh.");
            return null;
        }
    }

    /**
     * Persists the account's current balance, holder name, and PIN hash.
     */
    public static void saveAccount(BankAccount account) {
        try {
            Files.createDirectories(DATA_DIR);
            String line = String.join("|",
                    account.getAccountHolderName(),
                    String.valueOf(account.getBalance()),
                    account.getPinHash(),
                    account.getAccountNumber());
            Files.writeString(accountFile(account.getAccountNumber()), line,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            ConsoleColors.printWarning("Warning: could not save account data to disk.");
        }
    }

    /**
     * Appends the full in-memory transaction history to the account's
     * transaction log file (called on exit).
     */
    public static void saveTransactionHistory(BankAccount account) {
        try {
            Files.createDirectories(DATA_DIR);
            List<Transaction> history = account.getTransactionHistory();
            try (BufferedWriter writer = Files.newBufferedWriter(
                    transactionsFile(account.getAccountNumber()),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                for (Transaction t : history) {
                    writer.write(t.toFileLine());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            ConsoleColors.printWarning("Warning: could not save transaction history to disk.");
        }
    }
}

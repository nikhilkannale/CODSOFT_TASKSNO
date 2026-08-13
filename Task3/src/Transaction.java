import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Transaction
 * ----------------------------------------------------------------------
 * An immutable record of a single ATM operation (deposit, withdrawal,
 * balance inquiry, or PIN change). Every BankAccount keeps a list of
 * these so a full transaction history / mini statement can be printed
 * or persisted to disk.
 * ----------------------------------------------------------------------
 */
public class Transaction {

    /** The kind of operation this transaction represents. */
    public enum Type {
        DEPOSIT("Deposit"),
        WITHDRAWAL("Withdraw"),
        BALANCE_INQUIRY("Balance Inquiry"),
        PIN_CHANGE("PIN Changed");

        private final String label;

        Type(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss");

    private final Type type;
    private final double amount;
    private final LocalDateTime dateTime;
    private final double balanceAfter;

    public Transaction(Type type, double amount, double balanceAfter) {
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.dateTime = LocalDateTime.now();
    }

    public Type getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public double getBalanceAfter() {
        return balanceAfter;
    }

    /**
     * Formats the transaction the way it should appear in the
     * "Transaction History" screen.
     */
    public String toDisplayString() {
        StringBuilder sb = new StringBuilder();
        sb.append(type.getLabel());
        if (type == Type.DEPOSIT || type == Type.WITHDRAWAL) {
            sb.append(String.format("  |  Rs. %,.2f", amount));
        }
        sb.append(String.format("  |  Balance: Rs. %,.2f", balanceAfter));
        sb.append("  |  ").append(dateTime.format(FORMATTER));
        return sb.toString();
    }

    /**
     * Formats the transaction as a single line suitable for saving to a
     * plain text file (used by BankAccount#saveTransactionHistory).
     */
    public String toFileLine() {
        return String.format("%s | %.2f | %.2f | %s",
                type.name(), amount, balanceAfter, dateTime.format(FORMATTER));
    }

    @Override
    public String toString() {
        return toDisplayString();
    }
}

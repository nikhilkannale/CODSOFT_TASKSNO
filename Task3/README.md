# 🏧 Java ATM Interface

A professional, menu-driven **ATM Management System** built in Core Java as part of the **CodSoft Java Development Internship — Task 3**. It simulates real ATM behavior — PIN-protected login, deposits, withdrawals with a daily limit, transaction history, PIN changes, and account information — with clean object-oriented design, robust input validation, and file-based persistence.

---

## ✨ Features

- 🔐 **Secure Login** — account number + 4-digit PIN, limited to 3 attempts before the account locks
- 🔑 **Hashed PINs** — PINs are hashed with SHA-256 and never stored or compared in plain text
- 💰 **Check Balance** — instant balance inquiry, logged to transaction history
- 💵 **Deposit Money** — with strict validation (rejects negative, zero, and non-numeric input)
- 🏧 **Withdraw Money** — enforces sufficient balance **and** a configurable daily withdrawal limit (₹20,000)
- 📜 **Transaction History** — every deposit, withdrawal, balance inquiry, and PIN change is timestamped and listed
- 🔁 **Change PIN** — requires current PIN + new PIN confirmation, validated to be exactly 4 digits
- 👤 **Account Information** — displays holder name, account number, and current balance
- 💾 **Data Persistence** — balance and transaction history are saved to disk and restored on the next run
- 🎨 **Colored Console Output** — ANSI colors highlight success, errors, warnings, and headers
- 🛡️ **Bulletproof Input Handling** — the app never crashes, no matter what is typed

---

## 🛠️ Technologies Used

| Technology | Purpose |
|---|---|
| Java 17+ | Core language |
| OOP (Encapsulation, Classes, Objects) | Application architecture |
| `Scanner` | Console input |
| `ArrayList` / `List` | Transaction history storage |
| `LocalDateTime` | Transaction timestamps |
| `MessageDigest` (SHA-256) | PIN hashing |
| `java.nio.file` | Account/transaction persistence |
| `try-catch`, custom exceptions | Input validation & error handling |
| ANSI escape codes | Colored console UI |

---

## 📁 Folder Structure

```
ATMInterface/
│
├── src/
│   ├── Main.java             # Application entry point
│   ├── ATM.java               # Login screen, menu loop, user interaction
│   ├── BankAccount.java        # Encapsulated account data & banking operations
│   ├── Transaction.java        # Immutable transaction record
│   ├── InputValidator.java     # Centralized input validation helpers
│   ├── ConsoleColors.java      # ANSI color output helper
│   └── AccountStorage.java     # File-based persistence (balance + history)
│
├── screenshots/                # Screenshot placeholders
├── README.md
└── LICENSE
```

---

## 📷 Screenshots

> See the [`screenshots/`](./screenshots) folder. Add your own captures of the login screen, main menu, and each operation as you run the app.

---

## 🚀 Installation & How to Run

### Prerequisites
- **JDK 17 or later** installed and on your `PATH`
- Any IDE (IntelliJ IDEA, Eclipse, NetBeans, VS Code) — or just a terminal

### Option 1: Command Line

```bash
# 1. Clone or download the project, then navigate into it
cd ATMInterface

# 2. Compile all source files
javac -d bin src/*.java

# 3. Run the application
java -cp bin Main
```

### Option 2: IDE

1. Open the `ATMInterface` folder as a project in IntelliJ IDEA / Eclipse / NetBeans / VS Code.
2. Mark `src` as the source root (if prompted).
3. Run `Main.java`.

### Demo Login Credentials
| Field | Value |
|---|---|
| Account Number | `1234567890` |
| PIN | `1234` |
| Starting Balance | ₹25,000.00 |

> 💡 On the very first run, the app seeds this demo account. From then on, your balance, PIN, and transaction history are saved to a local `data/` folder and restored automatically the next time you run it.

---

## 🖥️ Sample Output

```
==================================
      WELCOME TO JAVA ATM
==================================
Enter Account Number: 1234567890
Enter PIN: 1234

Login Successful! Welcome, John Doe.

==================================
            ATM MENU
==================================
1. Check Balance
2. Deposit Money
3. Withdraw Money
4. Transaction History
5. Change PIN
6. Account Information
7. Exit
----------------------------------
Enter Choice: 2
Enter Deposit Amount: Rs. 5000
Deposit Successful!
Amount Deposited : Rs. 5,000.00
Updated Balance   : Rs. 30,000.00
```

```
Enter Choice: 3
(Daily withdrawal limit: Rs. 20,000.00 | Remaining today: Rs. 20,000.00)
Enter Withdrawal Amount: Rs. 2000
Withdrawal Successful!
Remaining Balance : Rs. 28,000.00
```

```
Enter Choice: 4
========== TRANSACTION HISTORY ==========
1. Deposit  |  Rs. 5,000.00  |  Balance: Rs. 30,000.00  |  13-Aug-2026 10:15:02
2. Withdraw  |  Rs. 2,000.00  |  Balance: Rs. 28,000.00  |  13-Aug-2026 10:15:20
```

---

## 🎯 Design Highlights

- **`BankAccount`** keeps every field `private`; the ATM only ever calls its public methods (`deposit`, `withdraw`, `checkBalance`, `changePin`, `displayAccountInfo`) — it never mutates account state directly.
- **`ATM`** owns the console interaction and delegates all business rules to `BankAccount`, keeping presentation and logic cleanly separated.
- **`Transaction`** is immutable — once recorded, a transaction can never be altered, which keeps the history trustworthy.
- **`InputValidator`** centralizes every input check so validation logic isn't duplicated across menu handlers.
- **`AccountStorage`** isolates all file I/O, so persistence can be swapped out (e.g., for a database) without touching the ATM or BankAccount classes.
- PINs are **hashed with SHA-256** the moment they're set — the plain-text PIN is never stored or logged anywhere.

---

## 📚 Learning Outcomes

Building this project reinforced:
- Practical application of **encapsulation** and controlled access via public methods
- Designing a clean **separation of concerns** between UI (`ATM`), domain logic (`BankAccount`), and persistence (`AccountStorage`)
- Defensive **input validation** and exception handling to make a console app unbreakable
- Working with `LocalDateTime` for real timestamps
- Basic **security hygiene** (hashing sensitive data instead of storing it in plain text)
- Simple **file-based persistence** with `java.nio.file`

---

## 🔮 Future Improvements

- Support multiple accounts with an account-creation flow
- Money transfer between accounts
- Admin mode for managing accounts and viewing system-wide stats
- Session timeout after a period of inactivity
- Interest calculator and mini statement (PDF export)
- Migrate persistence to an embedded database (e.g., SQLite)
- Unit tests (JUnit) for `BankAccount` business rules

---

## 👤 Author

**CodSoft Java Development Internship — Task 3**
Built as a portfolio project demonstrating Core Java, OOP, and clean software design.

---

## 📄 License

This project is licensed under the [MIT License](./LICENSE).


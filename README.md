# Smart Library Management System

A console-based Library Management System built in core Java, developed as an
evaluated project for the *Programming in Java* course.

## Overview

The system lets a librarian manage a book catalog, register members, issue and
return books, and generate reports — all through a simple menu-driven console
interface, backed by CSV file persistence so data survives between runs.

## Features

- **Book Management**: add, search (by title/author), and list books; tracks
  total vs. available copies per title.
- **Member Management**: register members; each member has a borrowing
  history, an active-loan count, and an outstanding fine balance.
- **Issue / Return workflow**:
  - Enforces a 3-books-per-member borrowing limit.
  - Blocks new issues while a member has an unpaid fine.
  - 14-day loan period; automatic fine calculation (₹5/day) on late returns.
- **Reporting**:
  - Overdue books report.
  - Most-borrowed books report (ranked).
  - Per-member activity report (loan history, current fine).
- **Persistence**: all data (books, members, transactions) is stored in
  human-readable CSV files under `data/`, so no external database is required.
- **Logging**: every add/issue/return/error is timestamped and appended to
  `logs/app.log` for auditability.
- **Validation & error handling**: custom checked exceptions
  (`BookNotFoundException`, `MemberNotFoundException`,
  `BookNotAvailableException`, `InvalidTransactionException`) are used
  throughout instead of letting the program crash on bad input.

## Technologies / Tools Used

- **Java 11+** (core Java only — no external dependencies/libraries)
- Collections Framework (`HashMap`, `ArrayList`, `Comparator`)
- `java.time` (`LocalDate`) for issue/due/return dates
- File I/O (`java.io`, `java.nio.file`) for CSV persistence
- Git for version control

## Project Structure

```
LibraryManagementSystem/
├── src/
│   ├── Main.java                 # Console UI / entry point
│   ├── model/                    # Domain classes
│   │   ├── Person.java           # Abstract base class
│   │   ├── Member.java           # extends Person
│   │   ├── Librarian.java        # extends Person
│   │   ├── Book.java
│   │   └── Transaction.java
│   ├── exception/                # Custom checked exceptions
│   │   ├── BookNotFoundException.java
│   │   ├── MemberNotFoundException.java
│   │   ├── BookNotAvailableException.java
│   │   └── InvalidTransactionException.java
│   ├── dao/                      # Data Access Objects (CSV-backed)
│   │   ├── BookDAO.java
│   │   ├── MemberDAO.java
│   │   └── TransactionDAO.java
│   ├── service/                  # Business logic
│   │   ├── LibraryService.java
│   │   ├── Searchable.java       # interface
│   │   └── Reportable.java       # interface
│   └── util/                     # Shared helpers
│       ├── FileHandler.java
│       └── AppLogger.java
├── data/                         # CSV data files (created at runtime)
├── logs/                         # app.log (created at runtime)
├── README.md
└── statement.md
```

## Steps to Install & Run

**Prerequisites**: JDK 11 or newer installed (`javac -version` to check).

1. Clone the repository:
   ```bash
   git clone <your-repo-url>
   cd LibraryManagementSystem
   ```
2. Compile:
   ```bash
   javac -d bin $(find src -name "*.java")
   ```
   *(On Windows, use: `javac -d bin (Get-ChildItem -Recurse src\*.java).FullName` in PowerShell,
   or simply compile all `.java` files under `src` from your IDE.)*
3. Run:
   ```bash
   java -cp bin Main
   ```
4. On first run, the app seeds a few demo books and members automatically so
   you can start issuing/returning books right away.

## Instructions for Testing

Manual test flow (no external test framework needed for a console app of this
scope, but each business rule below maps to a testable scenario):

1. **List All Books** (option 9) — confirm the 3 seeded books appear.
2. **Issue Book** (option 4) — issue `ISBN003` (*The Hobbit*, 1 copy) to
   member `M001`. Confirm success.
3. Try issuing `ISBN003` again to `M002` — should raise
   `BookNotAvailableException` ("no available copies").
4. Try issuing 4 different books to the same member — the 4th should raise
   `InvalidTransactionException` (borrowing limit).
5. **Return Book** (option 5) — return `ISBN003` from `M001`. Confirm it
   becomes available again.
6. **Overdue Report** (option 6) and **Most Borrowed Report** (option 7) —
   confirm they reflect the transactions above.
7. Check `logs/app.log` — every action above should have a timestamped entry.

If you'd like automated tests, the `service.LibraryService` layer is
dependency-injected with its DAOs, so it can be unit-tested with JUnit by
passing in DAOs pointed at temporary CSV files.



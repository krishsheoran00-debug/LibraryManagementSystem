# Problem Statement

## Problem Statement

Libraries — whether in schools, colleges, or small community setups — often
rely on manual registers or basic spreadsheets to track which books are
available, who has borrowed what, and when fines are owed. This leads to
lost records, disputes over due dates, and no easy way to see which books
are most in demand. There is a need for a lightweight, reliable system that
librarians can run without setting up a database server, while still
enforcing borrowing rules automatically.

## Scope of the Project

The project covers the core day-to-day operations of a small-to-medium
library:

- Maintaining a catalog of books with multiple copies per title.
- Registering and maintaining member records.
- Issuing and returning books, with automatic due-date and fine
  calculation.
- Enforcing borrowing rules (maximum books per member, blocking new
  issues when a member has unpaid fines).
- Producing operational reports (overdue books, most-borrowed titles,
  per-member activity).

Out of scope: multi-branch library networks, online member self-service
portals, and integration with external ID/authentication systems — these
are noted as future enhancements.

## Target Users

- **Librarians / library staff**, who use the system to manage the
  catalog, process issues/returns, and generate reports.
- **Library administrators**, who may review reports (overdue books,
  popular titles) to make purchasing or policy decisions.

*(The current version is a single-user console tool for library staff;
member self-service is a future enhancement.)*

## High-Level Features

1. Book catalog management (add, search, list, delete).
2. Member management (add, view, delete).
3. Issue/return workflow with a 14-day loan period and automatic fine
   calculation (₹5/day late).
4. Business rules: 3-book borrowing limit per member; blocked issuance
   while fines are outstanding.
5. Reports: overdue books, most-borrowed books, per-member activity.
6. Persistent storage via CSV files (no external database required).
7. Action logging to a file for auditability.

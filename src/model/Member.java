package model;

public class Member extends Person {
    private int booksCurrentlyIssued;
    private double outstandingFine;

    public Member(String id, String name, String email) {
        super(id, name, email);
        this.booksCurrentlyIssued = 0;
        this.outstandingFine = 0.0;
    }

    @Override
    public String getRole() {
        return "MEMBER";
    }

    public int getBooksCurrentlyIssued() { return booksCurrentlyIssued; }
    public void incrementIssued() { booksCurrentlyIssued++; }
    public void decrementIssued() { if (booksCurrentlyIssued > 0) booksCurrentlyIssued--; }

    public double getOutstandingFine() { return outstandingFine; }
    public void addFine(double amount) { outstandingFine += amount; }
    public void clearFine() { outstandingFine = 0.0; }

    public String toCsv() {
        return String.join(",", id, name.replace(",", ";"), email,
                String.valueOf(booksCurrentlyIssued), String.valueOf(outstandingFine));
    }

    public static Member fromCsv(String line) {
        String[] p = line.split(",", -1);
        Member m = new Member(p[0], p[1], p[2]);
        for (int i = 0; i < Integer.parseInt(p[3]); i++) m.incrementIssued();
        m.addFine(Double.parseDouble(p[4]));
        return m;
    }
}

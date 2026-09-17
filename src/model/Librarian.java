package model;

public class Librarian extends Person {
    public Librarian(String id, String name, String email) {
        super(id, name, email);
    }

    @Override
    public String getRole() {
        return "LIBRARIAN";
    }
}

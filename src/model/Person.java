package model;

/**
 * Abstract base class demonstrating inheritance.
 * Both Member and Librarian extend this.
 */
public abstract class Person {
    protected String id;
    protected String name;
    protected String email;

    public Person(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    /** Polymorphic method — overridden differently by each subclass. */
    public abstract String getRole();

    @Override
    public String toString() {
        return String.format("%-8s | %-20s | %-25s | Role:%s", id, name, email, getRole());
    }
}

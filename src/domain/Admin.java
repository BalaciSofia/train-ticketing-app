package domain;

public class Admin implements User {
    String username;
    public Admin(String username) {
        this.username = username;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return String.format("Admin{username='%s'}", username);
    }
}

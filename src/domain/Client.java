package domain;

public class Client implements User {
    private String username;

    public Client(String username) {
        this.username = username;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return String.format("Client{username='%s'}", username);
    }
}

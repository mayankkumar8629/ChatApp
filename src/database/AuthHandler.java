package database;

public interface AuthHandler {
    boolean signUp(String username, String password);
    boolean login(String username, String password);
}

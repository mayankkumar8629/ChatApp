package database;

public class DatabaseManagerTest {
    public static void main(String[] args) {
        DatabaseManager dbManager = new DatabaseManager();
        dbManager.saveMessage("TestUser", "Hello, this is a test message!");
        dbManager.saveMessage("testUser2","hello double check");
        dbManager.closeConnection();
    }
}
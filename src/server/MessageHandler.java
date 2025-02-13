package server;

public interface MessageHandler {

    void broadcastMessage(String message);
    void saveMessage(String username,String message);
    void loadChatHistory();
    void cleanup();
}

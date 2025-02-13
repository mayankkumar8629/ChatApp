package server;
import models.Message;
public interface MessageHandler {

    void broadcastMessage(String message);
    void saveMessage(Message message);
    void loadChatHistory();
    void cleanup();
    void run();
}

package database;

import models.Message;


import java.util.List;
public interface DatabaseOperations {


    void closeConnection();

    void saveMessage(Message msg);

    List<Message> getLastMessages(int limit);
}

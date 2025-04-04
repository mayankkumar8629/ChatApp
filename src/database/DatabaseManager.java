package database;

import models.Message;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager implements DatabaseOperations {
    private static final String URL = "jdbc:mysql://localhost:3306/ChatAppDB";
    private static final String USER="root";
    private static final String PASSWORD="yourpassword";

    private Connection connection;

    public DatabaseManager() {
        try {
            // Step 1: Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Step 2: Establish Connection
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("[DATABASE] Connected successfully.");
        } catch (ClassNotFoundException e) {
            System.out.println("[ERROR] MySQL JDBC Driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("[ERROR] Connection failed.");
            e.printStackTrace();
        }
    }



    @Override
    public void saveMessage(Message msg){
        String query="INSERT INTO messages (username,message,timestamp) VALUES (?,?,?)";
        try(PreparedStatement statement=connection.prepareStatement(query)){
            statement.setString(1,msg.getUsername());
            statement.setString(2,msg.getMessage());
            statement.setTimestamp(3,Timestamp.valueOf(msg.getTimestamp()));
            statement.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
            System.out.println("Error while saving message");
        }
    }
    @Override
    public List<Message> getLastMessages(int limit){
        List<Message> messages =new ArrayList<>();
        String query = "SELECT username, message, timestamp FROM messages ORDER BY timestamp DESC LIMIT ?";


        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setInt(1,limit);
            ResultSet resultSet=statement.executeQuery();

            while(resultSet.next()){
                String username=resultSet.getString("username");
                String message=resultSet.getString("message");
                LocalDateTime timestamp=resultSet.getTimestamp("timestamp").toLocalDateTime();
                messages.add(new Message(username,message,timestamp));
            }
        }catch(SQLException e){
            e.printStackTrace();
            System.out.println("Failed to retrieve last messages");
        }
        return messages;
    }
    @Override
    public void closeConnection(){
        try {
            if (connection != null) {
                connection.close();
                System.out.println("Database connection closed");
            }
        }catch (SQLException e){
                e.printStackTrace();
        }
    }
    public Connection getConnection() {
        return this.connection;
    }
}




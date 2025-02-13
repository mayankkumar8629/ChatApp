package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String URL = "jdbc:mysql://localhost:3306/ChatAppDB";
    private static final String USER="root";
    private static final String PASSWORD="14102003";

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




    public void saveMessage(String username,String message){
        String query="INSERT INTO messages (username,message) VALUES (?,?)";
        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setString(1,username);
            statement.setString(2,message);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getLastMessages(int limit){
        List<String> messages =new ArrayList<>();
        String query="SELECT username, message FROM messages ORDER BY timestamp DESC LIMIT ?";

        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setInt(1,limit);
            ResultSet resultSet=statement.executeQuery();

            while(resultSet.next()){
                String sender=resultSet.getString("username");
                String message=resultSet.getString("message");
                messages.add(sender+": "+message);
            }
        }catch(SQLException e){
            e.printStackTrace();
            System.out.println("Failed to retrive last messages");
        }
        return messages;
    }
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




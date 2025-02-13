package database;


import models.User;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class UserAuth implements AuthHandler {
    private Connection connection;

    public UserAuth(Connection connection) {
        this.connection = connection;
    }
    @Override
    public boolean signUp(String username,String password){
        String hashedPassword = hashPassword(password);

        String query = "INSERT INTO users (username, password_hash) VALUES (?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, hashedPassword);
            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    @Override
    public boolean login(String username,String password){
        String query = "SELECT password_hash FROM users WHERE username = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String storedHash = resultSet.getString("password_hash");
                return storedHash.equals(hashPassword(password));  // ✅ Compare hashed passwords
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    private String hashPassword(String password){
        try{
            MessageDigest md=MessageDigest.getInstance("SHA-256");
            byte[] hash=md.digest(password.getBytes());
            StringBuilder hexString=new StringBuilder();
            for(byte b:hash){
                hexString.append(String.format("%02x",b));
            }
            return hexString.toString();
        }catch (NoSuchAlgorithmException e){
            throw new RuntimeException("Password Hashing failed.",e);
        }
    }
    public User getUserByUsername(String username) {
        String query = "SELECT username, password_hash FROM users WHERE username = ?";


        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                return new User(resultSet.getString("username"), resultSet.getString("password_hash"));
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] Checking username failed: " + e.getMessage());
        }
        return null;
    }

}

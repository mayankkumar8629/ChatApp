package database;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class UserAuth {
    private Connection connection;

    public UserAuth(Connection connection) {
        this.connection = connection;
    }
    public boolean signUp(String username,String password){
        if (userExists(username)) {
            System.out.println("[ERROR] Username already taken.");
            return false;
        }
        String hashedPassword = hashPassword(password);
        String query = "INSERT INTO users (username, password_hash) VALUES (?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, hashedPassword);
            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            System.out.println("[ERROR] Signup failed: " + e.getMessage());
            return false;
        }
    }
    public boolean login(String username,String password){
        String query="select password_hash from users where username=? ";

        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setString(1,username);
            ResultSet resultSet=statement.executeQuery();

            if(resultSet.next()){
                String storedHash=resultSet.getString("password_hash");
                return storedHash.equals(password);
            }
        }catch(SQLException e){
            System.out.println("Login Failed: "+e.getMessage());
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
            throw new RuntimeException("Password Hashng failed.",e);
        }
    }
    private boolean userExists(String username) {
        String query = "SELECT username FROM users WHERE username = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            System.out.println("[ERROR] Checking username failed: " + e.getMessage());
        }
        return false;
    }

}

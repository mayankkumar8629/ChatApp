package server;

import database.DatabaseManager;
import database.UserAuth;
import models.Message;
import models.User;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.time.LocalDateTime;

public class ClientHandler implements Runnable,MessageHandler {
    private Socket clientSocket;
    private BufferedReader reader;
    private PrintWriter writer;
    private User user;
    private String username;
    private static ConcurrentHashMap<String, ClientHandler> activeClients;
    private DatabaseManager databaseManager;
    private UserAuth userAuth;

    public ClientHandler(Socket socket, ConcurrentHashMap<String, ClientHandler> clients, DatabaseManager dbManager) {
        this.clientSocket = socket;
        activeClients = clients;
        this.databaseManager = dbManager;
        this.userAuth = new UserAuth(this.databaseManager.getConnection());

        try {
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            writer = new PrintWriter(clientSocket.getOutputStream(), true);

            authenticateUser();
            activeClients.put(user.getUsername(), this);
            loadChatHistory();
            broadcastMessage("[SERVER] " + user.getUsername() + " has joined the chat.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void authenticateUser() throws IOException {
        while (true) {
            writer.println("Enter 'login' to sign in or 'signup' to register:");
            String choice = reader.readLine();
            if (choice == null) return;
            choice = choice.trim().toLowerCase();

            if (choice.equals("signup")) {
                writer.println("Enter a username:");
                String newUsername = reader.readLine();
                if (newUsername == null) return;
                writer.println("Enter a password:");
                String newPassword = reader.readLine();
                if (newPassword == null) return;

                if (userAuth.getUserByUsername(newUsername)==null) {
                    if(userAuth.signUp(newUsername, newPassword)) {
                        writer.println("[SUCCESS] Signup successful! You are now logged in.");
                        this.user=new User (newUsername,newPassword);
                        break;
                    }
                } else {
                    writer.println("[ERROR] Signup failed. Try again with a different username.");
                }
            } else if (choice.equals("login")) {
                writer.println("Enter your username:");
                String loginUsername = reader.readLine();
                if (loginUsername == null) return;
                writer.println("Enter your password:");
                String loginPassword = reader.readLine();
                if (loginPassword == null) return;

                User existingUser = userAuth.getUserByUsername(loginUsername);
                if (existingUser != null && userAuth.login(loginUsername, loginPassword)) {
                    writer.println("[SUCCESS] Login successful!");
                    this.user = existingUser;
                    break;
                } else {
                    writer.println("[ERROR] Invalid credentials. Try again.");
                }
            } else {
                writer.println("[ERROR] Invalid option. Type 'login' or 'signup'.");
            }
        }
    }

    @Override
    public void run() {
        String message;
        try {
            while ((message = reader.readLine()) != null) {
                if (message.equalsIgnoreCase("exit")) {
                    break;
                }
                broadcastMessage(user.getUsername() + ": " + message);
                saveMessage(new Message(user.getUsername(),message,LocalDateTime.now()));// Save message to database
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            cleanup();
        }
    }
    @Override
    public void broadcastMessage(String message) {
        for (ClientHandler client : activeClients.values()) {
            client.writer.println(message);
        }
    }
    @Override
    public void saveMessage(Message msg) {
        databaseManager.saveMessage(msg);
    }
    @Override
    public void loadChatHistory(){
        for(Message msg:databaseManager.getLastMessages(10)){
            writer.println(msg);
        }
    }
    @Override
    public void cleanup() {
        try {
            if(user!=null){
                activeClients.remove(user.getUsername());
                broadcastMessage("[SERVER] " + user.getUsername() + " has left the chat.");
            }
            broadcastMessage("[SERVER] " + user.getUsername() + " has left the chat.");
            if (clientSocket != null) clientSocket.close();
            if (reader != null) reader.close();
            if (writer != null) writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

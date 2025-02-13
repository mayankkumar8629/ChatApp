package server;

import database.DatabaseManager;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServer {
    private static final int PORT=5000;
    private static ConcurrentHashMap<String,ClientHandler> activeClients=new ConcurrentHashMap<>();
    private static DatabaseManager databaseManager;

    public static void main(String[] args){
        databaseManager=new DatabaseManager();
        System.out.println("Server starting on port "+PORT);

        try(ServerSocket serverSocket=new ServerSocket(PORT)){
            while(true){
                Socket clientSocket=serverSocket.accept();
                System.out.println("New Client connected: "+clientSocket);

                ClientHandler clientHandler=new ClientHandler(clientSocket,activeClients,databaseManager);
                Thread thread=new Thread(clientHandler);
                thread.start();
            }
        }catch (IOException e){
            e.printStackTrace();
        }

    }
}

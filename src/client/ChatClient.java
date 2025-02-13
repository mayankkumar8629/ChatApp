package client;

import java.io.*;
import java.net.Socket;

public class ChatClient {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 5000;
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    public ChatClient(){
        try{
            //conecting to the server
            socket = new Socket(SERVER_IP,SERVER_PORT);
            System.out.println("Connected to the chat server.");

            // setup input and output streams
            reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer=new PrintWriter(socket.getOutputStream(),true);

            //start message listening thread
            new Thread(new MessageListener()).start();

            //read user input and send messages
            BufferedReader consoleReader=new BufferedReader(new InputStreamReader(System.in));
            String message;
            while((message = consoleReader.readLine())!=null){
                writer.println(message);
            }



        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private class MessageListener implements Runnable{
        @Override
        public void run(){
            try{
                String serverMessage;
                while((serverMessage=reader.readLine())!=null){
                    System.out.println(serverMessage);
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }
    public static void main(String[] args){
        new ChatClient();
    }

}

import java.io.IOException;
import java.net.*;
import java.util.*;

public class MainClass {

    public static void main(String[] args) {
        List<Socket> clientSocketList = Collections.synchronizedList(new LinkedList<>());

        try (ServerSocket server = new ServerSocket(8188)) {
            System.out.println("Server is listening on port 8188...");

            while (true) {
                // Accept new client connections
                Socket clientSocket = server.accept();
                System.out.println("New client connected");

                // Add the new client socket to the list
                clientSocketList.add(clientSocket);

                // Start a new thread to handle the client connection
                //Thread has List of clients and current client.
                Runnable r = new ThreadedServer(clientSocketList, clientSocket);
                Thread t = new Thread(r);
                t.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

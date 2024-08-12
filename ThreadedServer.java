import java.io.*;
import java.net.*;
import java.util.*;

// Every client has a thread but every thread broadcasts to all the client in the list.
//run() method works on current client.
public class ThreadedServer implements Runnable {
    private final List<Socket> clientSockets;
    private final Socket clientSocket;

    public ThreadedServer(List<Socket> clientSockets, Socket clientSocket) {
        this.clientSockets = clientSockets;
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try (InputStream serverInStream = clientSocket.getInputStream();
             OutputStream serverOutStream = clientSocket.getOutputStream();
             Scanner scanner = new Scanner(serverInStream, "UTF-8");
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(serverOutStream, "UTF-8"), true)) {

            writer.println("Hello there! Enter your message. Type Go Away! to exit.");

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.trim().equalsIgnoreCase("Go Away!")) {
                    break;
                }
                broadcastMessage(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Clean up resources
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void broadcastMessage(String message) {
        synchronized (clientSockets) {
            for (Socket socket : clientSockets) {
                if (socket != clientSocket) { // Avoid sending the message back to the sender
                    try {
                        PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
                        writer.println(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}

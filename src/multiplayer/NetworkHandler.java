package multiplayer;

import multiplayer.GameState;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class NetworkHandler {
    private Socket clientSocket;
    private ServerSocket serverSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private static final int PORT = 12345;

    public void startServer() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.printf("Server started on port %s. Waiting for client...",PORT);
            clientSocket = serverSocket.accept();
            setupStreams();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void connectToServer(String hostAddress) {
        try {
            clientSocket = new Socket(hostAddress, PORT);
            setupStreams();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void setupStreams() throws IOException {
        out = new ObjectOutputStream(clientSocket.getOutputStream());
        in = new ObjectInputStream(clientSocket.getInputStream());
    }

    public void sendGameState(GameState state) {
        try {
            if (out == null) {
                throw new IOException("Output stream is not initialized. Ensure connection is established.");
            }
            out.writeObject(state);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public GameState receiveGameState() {
        try {
            if (in == null) {
                throw new IOException("Input stream is not initialized. Ensure connection is established.");
            }
            return (GameState) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void close() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (clientSocket != null) clientSocket.close();
            if (serverSocket != null) serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return clientSocket != null && clientSocket.isConnected() && !clientSocket.isClosed();
    }
}
   
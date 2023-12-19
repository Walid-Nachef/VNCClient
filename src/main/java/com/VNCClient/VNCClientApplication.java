package com.VNCClient;

import com.VNCClient.GUI.PasswordInput;
import com.VNCClient.GUI.RenderWindow;
import com.VNCClient.network.MessagesReadFromClient;
import com.VNCClient.network.VNCConnectionManager;

import java.awt.*;
import java.io.IOException;

public class VNCClientApplication {

    private static VNCConnectionManager connectionManager;
    private static RenderWindow renderWindow;
    private static MessagesReadFromClient messagesReadFromClient;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            PasswordInput passwordInput = new PasswordInput();
            passwordInput.setVisible(true);
        });
    }

    public static void initiateConnection(String host, int port, String password) throws Exception {
        connectionManager = new VNCConnectionManager(host, port, password);
        try {
            if (connectionManager.initializeConnection()) {
                // After successful initialization, read the server's initial message
                if (connectionManager.readServerInit()) {
                    // Setup the RenderWindow with the details received from the server
                    renderWindow = new RenderWindow(connectionManager.getServerName(),
                            connectionManager.getFrameBufferWidth(),
                            connectionManager.getFrameBufferHeight());
                    EventQueue.invokeLater(() -> renderWindow.setVisible(true));

                    // Start the background thread to listen for updates
                    //startListeningForUpdates();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Handle errors (e.g., show a message to the user)
        }
    }

}

package com.VNCClient.GUI;

import com.VNCClient.network.VNCConnectionManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class PasswordInput extends JFrame {
    private JTextField pingIDField;
    private JPasswordField passwordField;
    private JButton btnTry;
    private JButton btnConnect;
    private JButton btnClose;
    private boolean isConnected = false;

    public PasswordInput() {
        setTitle("VNC Authentication");
        setSize(350, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        initializeComponents();
    }

    private void initializeComponents() {
        VNCConnectionManager connectionManager = new VNCConnectionManager();
        pingIDField = new JTextField(20);
        passwordField = new JPasswordField(20);
        pingIDField.setText("PLACEHOLDER: Under construction");
        pingIDField.setEditable(false); //Disables PingID field

        btnTry = new JButton("Try");
        btnTry.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                VNCConnectionManager connectionManager = new VNCConnectionManager("192.168.61.214", 5900, new String(getPassword()));

                // Initialize connection to VNC server
                try {
                    isConnected = connectionManager.initializeConnection();
                    if (isConnected)
                    {
                        JOptionPane.showMessageDialog(PasswordInput.this, "The password is correct");
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(PasswordInput.this, "The password is NOT correct");
                    }

                }
                catch(Exception ex)
                {
                    System.err.println("Exception occurred during VNC connection: " + e);
                    ex.printStackTrace();
                }
                finally
                {
                    try
                    {
                        connectionManager.closeConnection();
                    }
                    catch(IOException ex)
                    {
                        System.err.println("Exception occurred while closing VNC connection: " + e);
                        ex.printStackTrace();
                    }
                }
            }
        });

        btnConnect = new JButton("Connect");
        btnConnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isConnected) {
                    try {
                        VNCConnectionManager connectionManager = new VNCConnectionManager("192.168.61.214", 5900, new String(getPassword()));
                        isConnected = connectionManager.initializeConnection();

                        if (isConnected) {
                            connectionManager.readServerInit(); // Read server initializations here

                            // Now that we're connected, we should display the RenderWindow
                            EventQueue.invokeLater(() -> {
                                RenderWindow renderWindow = new RenderWindow(connectionManager.getServerName(), connectionManager.getFrameBufferWidth(), connectionManager.getFrameBufferHeight());
                                renderWindow.setVisible(true);
                            });

                            PasswordInput.this.dispose(); // Close the PasswordInput window
                        } else {
                            JOptionPane.showMessageDialog(PasswordInput.this, "Could not connect to the server. Check the password and try again.");
                        }
                    } catch (Exception ex) {
                        isConnected = false;
                        JOptionPane.showMessageDialog(PasswordInput.this, "Failed to connect: " + ex.getMessage());
                        ex.printStackTrace();
                    } finally {
                        if (!isConnected && connectionManager != null) {
                            try {
                                connectionManager.closeConnection();
                            } catch (IOException ex) {
                                JOptionPane.showMessageDialog(PasswordInput.this, "Error closing connection: " + ex.getMessage());
                                ex.printStackTrace();
                            }
                        }
                    }
                }
            }
        });


        btnClose = new JButton("Close");
        btnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(new JLabel("Ping ID:"));
        panel.add(pingIDField);
        panel.add(btnTry);
        panel.add(btnConnect);
        panel.add(btnClose);

        getContentPane().add(panel, BorderLayout.CENTER);
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(btnClose);
        getContentPane().add(bottomPanel, BorderLayout.PAGE_END);
    }

    public String getPingID() {
        return pingIDField.getText();
    }

    public char[] getPassword() {
        return passwordField.getPassword();
    }




    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new PasswordInput().setVisible(true);
            }
        });
    }
}

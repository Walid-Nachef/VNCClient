package com.VNCClient.network;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class VNCConnectionManager {

    private Socket socket;
    private String host;
    private int port;
    private String password;
    private DataInputStream in;
    private DataOutputStream out;

    public String getServerName() {
        return serverName;
    }

    public int getFrameBufferWidth() {
        return frameBufferWidth;
    }

    public int getFrameBufferHeight() {
        return frameBufferHeight;
    }

    public String serverName;
    public int frameBufferWidth;
    public int frameBufferHeight;

    private int bitsPerPixel;

    public int getBitsPerPixel() {
        return bitsPerPixel;
    }
    private int depth;
    private boolean bigEndianFlag;
    private boolean trueColourFlag;
    private int redMax;
    private int greenMax;
    private int blueMax;
    private int redShift;
    private int greenShift;
    private int blueShift;

    public VNCConnectionManager(String host, int port, String password) {
        this.host = host;
        this.port = port;
        this.password = password;
    }

    public VNCConnectionManager() {

    }

    public boolean initializeConnection() throws Exception {
        try {
            socket = new Socket(host, port);
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

            System.out.println("Performing protocol version negotiation...");
            byte[] versionMessage = new byte[12];
            in.readFully(versionMessage);
            System.out.println("Server Version: " + new String(versionMessage).trim());

            out.write(versionMessage); // Echo the version back to the server
            out.flush();

            int numSecurityTypes = in.readUnsignedByte();
            if (numSecurityTypes == 0) {
                int reasonLength = in.readInt();
                byte[] reason = new byte[reasonLength];
                in.readFully(reason);
                throw new IOException("Server indicates no security types supported: " + new String(reason));
            }

            System.out.println("Number of security types supported by server: " + numSecurityTypes);
            byte[] securityTypes = new byte[numSecurityTypes];
            in.readFully(securityTypes);
            System.out.println("Supported security types: " + Arrays.toString(securityTypes));

            // Assuming VNC Authentication is supported and is security type 2
            if (Arrays.binarySearch(securityTypes, (byte) 2) >= 0) {
                out.writeByte(2); // Inform the server that we're choosing VNC Authentication
                out.flush();
            } else {
                throw new IOException("VNC Authentication not supported by server.");
            }

            // Handle VNC Authentication
            byte[] challenge = new byte[16];
            in.readFully(challenge);
            byte[] response = encryptChallenge(challenge, password);
            out.write(response);
            out.flush();

            int authResult = in.readInt();
            if (authResult != 0) {
                throw new IOException("Authentication failed with result code: " + authResult);
            }
            System.out.println("Authentication successful.");

            System.out.println("Sending client initialization message...");
            out.writeByte(1); // Shared flag set to 1 (true)
            out.flush();

            return true; // Connection successful
        } catch (IOException e) {
            // Log and handle exception
            return false; // Connection failed
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] encryptChallenge(byte[] challenge, String password) throws Exception {
        byte[] keyBytes = new byte[8]; // DES key is 8 bytes long
        byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);

        // Only the first eight characters of the password are used
        for (int i = 0; i < 8; i++) {
            // If the password is less than 8 characters, the rest of the key is nulls
            keyBytes[i] = (i < passwordBytes.length) ? passwordBytes[i] : 0;
        }

        // Reverse the bits of each byte in the key
        for (int i = 0; i < keyBytes.length; i++) {
            keyBytes[i] = reverseBits(keyBytes[i]);
        }

        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "DES");
        Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);

        // Encrypt the challenge
        return cipher.doFinal(challenge);
    }


    private byte reverseBits(byte in) {
        int b = in & 0xFF;
        int reversed = 0;
        for (int i = 0; i < 8; i++) {
            reversed = (reversed << 1) | (b & 1);
            b >>= 1;
        }
        return (byte)reversed;
    }

    public boolean readServerInit() throws IOException {
        System.out.println("Reading server initialization...");

        // Read framebuffer width and height
        frameBufferWidth = in.readUnsignedShort();
        frameBufferHeight = in.readUnsignedShort();
        System.out.println("Framebuffer width: " + frameBufferWidth);
        System.out.println("Framebuffer height: " + frameBufferHeight);

        // Read pixel format information
        bitsPerPixel = in.readUnsignedByte();
        depth = in.readUnsignedByte();
        bigEndianFlag = in.readUnsignedByte() != 0;
        trueColourFlag = in.readUnsignedByte() != 0;
        redMax = in.readUnsignedShort();
        greenMax = in.readUnsignedShort();
        blueMax = in.readUnsignedShort();
        redShift = in.readUnsignedByte();
        greenShift = in.readUnsignedByte();
        blueShift = in.readUnsignedByte();
        in.skipBytes(3); // Skip padding

        // Read name length
        int nameLength = in.readInt();
        System.out.println("Name length: " + nameLength);

        // Read the server name
        byte[] nameBytes = new byte[nameLength];
        in.readFully(nameBytes);
        String serverName = new String(nameBytes, StandardCharsets.UTF_8);
        System.out.println("Server name: " + serverName);


        MessagesSentToClient messagesSentToClient = null;
        messagesSentToClient.sendFrameBufferUpdateRequest(false, 0, 0, frameBufferWidth, frameBufferHeight);
        return true;
    }

    public void closeConnection() throws IOException {
        if (socket != null) {
            socket.close();
        }
        if (in != null) {
            in.close();
        }
        if (out != null) {
            out.close();
        }
    }
}

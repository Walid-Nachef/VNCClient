package com.VNCClient.GUI;

import com.VNCClient.codec.Decoder;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class RenderWindow extends JFrame {
    private BufferedImage frameBufferImage; // This will store your received framebuffer image
    private Decoder decoder = new Decoder(); // Assuming Decoder is in an accessible package


    public RenderWindow(String serverName, int width, int height) {
        setTitle(serverName);
        setSize(width, height); // Set this to the size of the framebuffer
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // To close this window without stopping the whole application
        initializeComponents();
    }

    public RenderWindow() {}

    private void initializeComponents() {
        JPanel displayPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (frameBufferImage != null) {
                    g.drawImage(frameBufferImage, 0, 0, this);
                }
            }
        };

        add(displayPanel);
    }

//    public void updateFrameBuffer(FrameBufferUpdate update) {
//        // Assuming you have a method to convert the FrameBufferUpdate to an Image
//        BufferedImage image = decoder.decodeRaw(update);
//        displayFrameBuffer(image);
//    }

    public void displayFrameBuffer(byte[] rawPixelData, int width, int height) {
        // Encoding type is passed to handle different types of encoding
        this.frameBufferImage = decoder.decodeRaw(rawPixelData, width, height);
        repaint(); // This will cause the paintComponent method to be called
    }


    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            RenderWindow renderWindow = new RenderWindow();
            renderWindow.setVisible(true);

            // For demonstration, replace this with actual framebuffer image
            BufferedImage image = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = image.createGraphics();
            g2.setColor(Color.BLUE);
            g2.fillRect(0, 0, image.getWidth(), image.getHeight());
            g2.dispose();
        });
    }
}

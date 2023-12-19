package com.VNCClient.GUI;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FrameBufferUpdate {
    private final List<Rectangle> rectangles = new ArrayList<>();

    public static class Rectangle {
        private final int xPosition;
        private final int yPosition;
        private final int width;
        private final int height;
        private final int encodingType;
        private final byte[] pixelData;

        public Rectangle(int xPosition, int yPosition, int width, int height, int encodingType, byte[] pixelData) {
            this.xPosition = xPosition;
            this.yPosition = yPosition;
            this.width = width;
            this.height = height;
            this.encodingType = encodingType;
            this.pixelData = pixelData;
        }

        // Getters for rectangle properties
        public int getXPosition() {
            return xPosition;
        }

        public int getYPosition() {
            return yPosition;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public int getEncodingType() {
            return encodingType;
        }

        public byte[] getPixelData() {
            return pixelData;
        }
    }

    // Method to read a FrameBufferUpdate from the input stream
    public static FrameBufferUpdate read(DataInputStream in, int bitsPerPixel) throws IOException {
        int messageType = in.readUnsignedByte(); // Should be 0 for FrameBufferUpdate
        in.readByte(); // Padding
        int numberOfRectangles = in.readUnsignedShort();
        FrameBufferUpdate frameBufferUpdate = new FrameBufferUpdate();

        for (int i = 0; i < numberOfRectangles; i++) {
            int x = in.readUnsignedShort();
            int y = in.readUnsignedShort();
            int width = in.readUnsignedShort();
            int height = in.readUnsignedShort();
            int encodingType = in.readInt();

            // Calculate the size of the pixel data array based on encodingType and dimensions
            // For RAW encoding, it will be width * height * (bitsPerPixel / 8)
            int bytesPerPixel = (bitsPerPixel + 7) / 8; // Ensure the division rounds up
            byte[] pixelData = new byte[width * height * bytesPerPixel];
            in.readFully(pixelData);

            Rectangle rectangle = new Rectangle(x, y, width, height, encodingType, pixelData);
            frameBufferUpdate.addRectangle(rectangle);
        }

        return frameBufferUpdate;
    }

    // Method to add a rectangle to the update
    public void addRectangle(Rectangle rectangle) {
        rectangles.add(rectangle);
    }

    // Getters for the list of rectangles
    public List<Rectangle> getRectangles() {
        return rectangles;
    }
}

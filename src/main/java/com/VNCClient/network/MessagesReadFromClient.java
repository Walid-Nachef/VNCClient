package com.VNCClient.network;

import com.VNCClient.GUI.FrameBufferUpdate;

import java.io.DataInputStream;
import java.io.IOException;

public class MessagesReadFromClient {

    public MessagesReadFromClient(VNCConnectionManager connectionManager) {
    }

    public FrameBufferUpdate readFrameBufferUpdate(DataInputStream in, int bitsPerPixel) throws IOException {

        FrameBufferUpdate update = new FrameBufferUpdate();
        int messageType = in.readUnsignedByte(); // Should be 0 for FrameBufferUpdate
        in.readByte(); // Padding
        int numberOfRectangles = in.readUnsignedShort();

        for (int i = 0; i < numberOfRectangles; i++) {
            int x = in.readUnsignedShort();
            int y = in.readUnsignedShort();
            int width = in.readUnsignedShort();
            int height = in.readUnsignedShort();
            int encodingType = in.readInt();

            // Determine the size of the pixel data based on encodingType and width*height
            // For RAW encoding, it will be width * height * (bitsPerPixel/8)
            byte[] pixelData = new byte[width * height * (bitsPerPixel / 8)]; // bitsPerPixel needs to be determined
            in.readFully(pixelData);

            update.addRectangle(new FrameBufferUpdate.Rectangle(x, y, width, height, encodingType, pixelData));
        }

        return update;
    }
}

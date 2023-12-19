package com.VNCClient.codec;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;

public class Decoder {

    // Method to decode pixel data according to encoding type
    public BufferedImage decode(byte[] encodedData, int encodingType, int width, int height) throws IOException {
        switch (encodingType) {
            case 0:
                return decodeRaw(encodedData, width, height);
            //Other cases can represent other encoding types
            default:
                throw new IllegalArgumentException("Unsupported encoding type: " + encodingType);
        }
    }

    public BufferedImage decodeRaw(byte[] rawData, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        int[] pixels = new int[width * height]; // Array to hold ARGB pixel values

        // Assuming rawData.length == pixels.length * 4 bytes per pixel
        ByteBuffer.wrap(rawData).asIntBuffer().get(pixels);

        // Convert BGRA to ARGB
        for (int i = 0; i < pixels.length; i++) {
            int bgra = pixels[i];
            int a = (bgra >> 24) & 0xff;
            int r = (bgra >> 16) & 0xff;
            int g = (bgra >> 8) & 0xff;
            int b = bgra & 0xff;

            pixels[i] = (a << 24) | (r << 16) | (g << 8) | b;
        }

        image.setRGB(0, 0, width, height, pixels, 0, width);
        return image;
    }

    // ... other decoding methods for each encoding type
}


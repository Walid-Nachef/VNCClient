package com.VNCClient.GUI;

public class PixelFormat {
    private final int bitsPerPixel;
    private final int depth;
    private final boolean bigEndianFlag;
    private final boolean trueColourFlag;
    private final int redMax;
    private final int greenMax;
    private final int blueMax;
    private final int redShift;
    private final int greenShift;
    private final int blueShift;

    public PixelFormat(int bitsPerPixel, int depth, boolean bigEndianFlag, boolean trueColourFlag,
                       int redMax, int greenMax, int blueMax, int redShift, int greenShift, int blueShift) {
        this.bitsPerPixel = bitsPerPixel;
        this.depth = depth;
        this.bigEndianFlag = bigEndianFlag;
        this.trueColourFlag = trueColourFlag;
        this.redMax = redMax;
        this.greenMax = greenMax;
        this.blueMax = blueMax;
        this.redShift = redShift;
        this.greenShift = greenShift;
        this.blueShift = blueShift;
    }


}

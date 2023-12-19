package com.VNCClient.network;

import java.io.DataOutputStream;
import java.io.IOException;

public class MessagesSentToClient {
    private DataOutputStream out;

    public MessagesSentToClient(DataOutputStream out) {
        this.out = out;
    }

    public void sendFrameBufferUpdateRequest(boolean incremental, int x, int y, int width, int height) throws IOException {
        out.write(new byte[]{(byte) 3, (byte) (incremental ? 1 : 0)}); // FrameBufferUpdateRequest message type
        out.writeShort(x);
        out.writeShort(y);
        out.writeShort(width);
        out.writeShort(height);
        out.flush();
    }

}

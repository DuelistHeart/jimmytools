package com.duelco.handlers;

import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

public class ScreenCaptureHandler {
//      TODO: Review later
//    public static BufferedImage captureFramebuffer(Framebuffer framebuffer) {
//        int width = framebuffer.viewportWidth;
//        int height = framebuffer.viewportHeight;
//
//        // Allocate buffer to store RGBA pixels
//        ByteBuffer buffer = ByteBuffer.allocateDirect(width * height * 4);
//
//        // Bind the FBO
//        GL11.glBindTexture(GL11.GL_TEXTURE_2D, framebuffer.getColorAttachment());
//        // Read pixels
//        GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
//
//        // Convert ByteBuffer to BufferedImage
//        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
//        for (int y = 0; y < height; y++) {
//            for (int x = 0; x < width; x++) {
//                int i = (x + (width * y)) * 4;
//                int r = buffer.get(i) & 0xFF;
//                int g = buffer.get(i + 1) & 0xFF;
//                int b = buffer.get(i + 2) & 0xFF;
//                int a = 255;
//                image.setRGB(x, height - (y + 1), (a << 24) | (r << 16) | (g << 8) | b);
//            }
//        }
//        return image;
//    }
//
//
//    /**
//     * Utility method to place the given BufferedImage onto the system clipboard.
//     */
//    public static void copyImageToClipboard(BufferedImage image) {
//        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
//        clipboard.setContents((Transferable) image, null);
//    }
}

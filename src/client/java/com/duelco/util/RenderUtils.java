package com.duelco.util;

import net.minecraft.client.gui.GuiGraphicsExtractor;

public class RenderUtils {
    public static void drawWithScale(GuiGraphicsExtractor context, float scaleX, float scaleY, float scaleZ, Runnable action) {
        context.pose().pushMatrix();
        context.pose().scale(scaleX, scaleY);
        // Draw the player's name next to their head
        action.run();
        context.pose().popMatrix();
    }
}

package com.duelco.util;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Colors;

public class RenderUtils {
    public static void drawWithScale(DrawContext context, float scaleX, float scaleY, float scaleZ, Runnable action) {
        context.getMatrices().push();
        context.getMatrices().scale(scaleX, scaleY, scaleZ);
        // Draw the player's name next to their head
        action.run();
        context.getMatrices().pop();
    }
}

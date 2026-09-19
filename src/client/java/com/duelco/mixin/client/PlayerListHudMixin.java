package com.duelco.mixin.client;
import com.duelco.config.ModConfig;
import com.duelco.util.ServerUtils;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerTabOverlay.class)
public class PlayerListHudMixin {

    @Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
    private void disableDefaultTab(GuiGraphicsExtractor context, int scaledWindowWidth, Scoreboard scoreboard, Objective objective, CallbackInfo ci) {
        // Other servers keep the vanilla tab list, since the custom one is built around the LoM2 layout
        if (ModConfig.isCustomTablistEnabled && ServerUtils.isLordsServer()) ci.cancel();
    }
}
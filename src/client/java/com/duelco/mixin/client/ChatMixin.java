package com.duelco.mixin.client;

import com.duelco.config.ModConfig;
import com.duelco.handlers.PlayerMessagerHandler;
import com.duelco.handlers.TransformationHelperHandler;
import com.duelco.managers.TransformationHelperManager;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public abstract class ChatMixin {
    @Inject(method = "sendMessage", at = @At("HEAD"))
    private void interceptCommand(String chatText, boolean addToHistory, CallbackInfo ci) {
        if (chatText.startsWith("/skin ") && TransformationHelperHandler.isSettingUpTransformation()) {
            System.out.println("Intercepted /skin command: " + chatText);
            ModConfig.areTransformationsEnabled = true;
            ModConfig.isTransformed = true;
            TransformationHelperManager.setTransformSkin(chatText.substring(6));
            PlayerMessagerHandler.sendMessage(Text.literal("After the skin changes, press the KEYBIND key to revert back."));
        }
    }
}
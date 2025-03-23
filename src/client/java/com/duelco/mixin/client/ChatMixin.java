package com.duelco.mixin.client;

import com.duelco.config.ModConfig;
import com.duelco.handlers.PlayerMessagerHandler;
import com.duelco.handlers.TransformationHelperHandler;
import com.duelco.managers.TransformationHelperManager;
import com.duelco.util.KeybindUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.option.KeyBinding;
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
            KeyBinding transformKeybind = KeybindUtils.getKeybind("keybinds.key.jimmytools.transform");

            PlayerMessagerHandler.sendMessage(Text.literal(
                    String.format("After the skin changes, press the [%s] key to revert back.", transformKeybind.getBoundKeyLocalizedText().getString())));
        }
    }
}
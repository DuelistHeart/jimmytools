package com.duelco.mixin.client;

import com.duelco.ui.screen.CharacterSelectScreen;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Gui.class)
public class GuiMixin {

    /** Swaps the server's plain "<name>'s Characters" chest for the roster screen, keeping the same menu underneath. */
    @ModifyVariable(method = "setScreen", at = @At("HEAD"), argsOnly = true)
    private Screen swapCharacterScreen(Screen screen) {
        return CharacterSelectScreen.wrapIfCharacterMenu(screen);
    }
}

package com.duelco.mixin.client;

import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Targets the private {@code SubtitleOverlay.Subtitle} inner class by
 * string name (since it's inaccessible by type from our own source) and
 * exposes its {@code component} field through {@link SubtitleAccessor}.
 */
@Mixin(targets = "net.minecraft.client.gui.components.SubtitleOverlay$Subtitle")
public class SubtitleMixin implements SubtitleAccessor {

    // NOTE: field name is a best guess based on Mojmap vanilla source.
    // If Mixin complains at launch ("Unable to locate field"), open
    // SubtitleOverlay$Subtitle via javap/decompiler and fix this name.
    @Final
    @Shadow
    private Component component;

    @Override
    public Component jimmytools$getComponent() {
        return this.component;
    }
}

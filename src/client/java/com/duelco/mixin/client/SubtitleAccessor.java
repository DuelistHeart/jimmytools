package com.duelco.mixin.client;

import net.minecraft.network.chat.Component;

/**
 * Public interface implemented (via Mixin) onto the private
 * {@code SubtitleOverlay.Subtitle} class so we can pull its
 * {@code Component} out without ever naming the private class
 * in our own source.
 */
public interface SubtitleAccessor {
    Component jimmytools$getComponent();
}


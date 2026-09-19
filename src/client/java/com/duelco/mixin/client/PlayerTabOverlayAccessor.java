package com.duelco.mixin.client;

import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Comparator;

@Mixin(PlayerTabOverlay.class)
public interface PlayerTabOverlayAccessor {

    /** The comparator vanilla uses to order the tab list, so we see entries in the same slots the server assigned. */
    @Accessor("PLAYER_COMPARATOR")
    static Comparator<PlayerInfo> getPlayerComparator() {
        throw new AssertionError();
    }
}

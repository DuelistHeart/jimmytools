package com.duelco.mixin.client;

import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Slot.class)
public interface SlotAccessor {

    /** Slot.x/y are final in vanilla; the character select screen moves the server's slots onto its own roster grid. */
    @Mutable
    @Accessor("x")
    void jimmytools$setX(int x);

    @Mutable
    @Accessor("y")
    void jimmytools$setY(int y);
}

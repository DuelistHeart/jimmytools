package com.duelco.ui.managers;

import com.duelco.config.ModConfig;
import com.duelco.obj.bingo.BingoItem;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.StackLayout;
import io.wispforest.owo.ui.core.*;
import net.minecraft.util.Identifier;

public class BingoItemUIManager {

    public static StackLayout buildItemSlot(BingoItem bingoItem) {
        StackLayout sampleSlot = (StackLayout) Containers.stack(Sizing.fixed(20), Sizing.fixed(20))
                .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
                .padding(Insets.of(2));

        sampleSlot.child(
                Components.box(Sizing.fill(), Sizing.fill())
                        .fill(true)
                        .color(Color.ofRgb(ModConfig.bingoBackgroundColor.getRGB()))
        );

        if (bingoItem.isFreeSpace()) {
            sampleSlot.child(
                    Components.texture(Identifier.of("jimmytools", "ui/free_space.png"), 1, 1, 256, 256)
                            .sizing(Sizing.fixed(12))
                            .zIndex(500)
            );
        } else {
            sampleSlot.child(
                    Components.item(bingoItem.getItem())
                            .sizing(Sizing.fixed(12))
                            .zIndex(25)
            );
        }

        return sampleSlot;
    }
}

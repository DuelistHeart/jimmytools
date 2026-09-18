package com.duelco.ui.managers;

import com.duelco.config.ModConfig;
import com.duelco.obj.BingoItem;
import io.wispforest.owo.ui.component.UIComponents;
import io.wispforest.owo.ui.container.StackLayout;
import io.wispforest.owo.ui.container.UIContainers;
import io.wispforest.owo.ui.core.*;
import net.minecraft.resources.Identifier;

public class BingoItemUIManager {

    public static StackLayout buildItemSlot(BingoItem bingoItem) {
        StackLayout sampleSlot = (StackLayout) UIContainers.stack(Sizing.fixed(24), Sizing.fixed(24))
                .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
                .padding(Insets.of(2));

        sampleSlot.child(
                UIComponents.box(Sizing.fill(), Sizing.fill())
                        .fill(true)
                        .color(Color.ofRgb(ModConfig.bingoBackgroundColor.getRGB()))
        );

        if (bingoItem.isFreeSpace()) {
            sampleSlot.child(
                    UIComponents.texture(Identifier.parse("jimmytools:ui/free_space.png"), 1, 1, 256, 256)
                            .sizing(Sizing.fixed(20))
//                            .zIndex(500)
            );
        } else {
            sampleSlot.child(
                    UIComponents.item(bingoItem.getItem())
//                            .zIndex(25) TODO: Check
            );
        }

        return sampleSlot;
    }
}

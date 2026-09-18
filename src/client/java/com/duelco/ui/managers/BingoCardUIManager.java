package com.duelco.ui.managers;

import com.duelco._enum.SoundEvent;
import com.duelco.config.ModConfig;
import com.duelco.managers.SoundManager;
import com.duelco.obj.bingo.BingoCard;
import com.duelco.obj.general.UiPosition;
import io.wispforest.owo.ui.component.UIComponents;
import io.wispforest.owo.ui.container.GridLayout;
import io.wispforest.owo.ui.container.StackLayout;
import io.wispforest.owo.ui.container.UIContainers;
import io.wispforest.owo.ui.core.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;

public class BingoCardUIManager {
    private static final int MARKER_SIZE = 14;

    public static StackLayout buildBingoCardComponent(Integer cardNumber, BingoCard bingoCard) {
        StackLayout bingoCardElement = (StackLayout) UIContainers.stack(Sizing.fixed(110), Sizing.fixed(140))
                .alignment(HorizontalAlignment.CENTER, VerticalAlignment.BOTTOM)
                .margins(Insets.of(2));
        StackLayout markerElement = (StackLayout) UIContainers.stack(Sizing.fixed(120), Sizing.fixed(165))
                .alignment(HorizontalAlignment.CENTER, VerticalAlignment.BOTTOM);
        StackLayout bingoGridContainerElement = (StackLayout) UIContainers.stack(Sizing.fixed(112), Sizing.fixed(112))
                .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
                .padding(Insets.of(4));
        GridLayout bingoGridElement = (GridLayout) UIContainers.grid(Sizing.fixed(100), Sizing.fixed(100), 5, 5)
                .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER);

        int fullIndex = 0;

        bingoCard.getItems().get(12).setFreeSpace(true);

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                bingoGridElement.child(
                        BingoItemUIManager.buildItemSlot(bingoCard.getItems().get(fullIndex)), i, j
                );
                fullIndex++;
            }
        }

        bingoGridContainerElement.child(
                UIComponents.box(Sizing.fill(), Sizing.fill())
                        .color(Color.ofRgb(ModConfig.bingoGridColor.getRGB()))
                        .fill(true)
        ).child(
                bingoGridElement
        );

        if (cardNumber != null && ModConfig.isDisplayBingoNumsEnabled) {
            Style style = Style.EMPTY.withColor(ModConfig.bingoGridColor.getRGB());

            bingoCardElement.child(
                    UIComponents.label(Component.literal(cardNumber.toString()).setStyle(style))
                            .positioning(Positioning.absolute(2, 2))
//                            .zIndex(100) TODO: Check
            );
        }

        bingoCardElement.child(
                UIComponents.box(Sizing.fixed(130), Sizing.fixed(160))
                        .color(Color.ofRgb(ModConfig.bingoBackgroundColor.getRGB()))
                        .fill(true)
        ).child(
                UIComponents.texture(Identifier.parse("jimmytools:ui/bingo_header.png"), 1, 1, 255, 256)
                        .sizing(Sizing.fixed(80), Sizing.fixed(16))
                        .positioning(Positioning.relative(50, 10))
//                        .zIndex(200) TODO: Check
        ).child(
                bingoGridContainerElement
        ).child(
                markerElement
        );

        markerElement.mouseDown().subscribe((mousePos, bool) -> {
            UiPosition markerPos = new UiPosition((int) (mousePos.x()-((double) MARKER_SIZE /2)), (int) (mousePos.y()-((double) MARKER_SIZE /2)));
            bingoCard.addMarker(markerPos);
            markerElement.child(buildMarkerComponent(bingoCard, markerPos));
            SoundManager.playSound(SoundEvent.PLACE_MARKER);
            return true;
        });

        if (!bingoCard.getBingoMarkerPositions().isEmpty()) {
            for (UiPosition markerPos : bingoCard.getBingoMarkerPositions()) {
                markerElement.child(buildMarkerComponent(bingoCard, markerPos));
            }
        }

        return bingoCardElement;
    }

    private static UIComponent buildMarkerComponent(BingoCard bingoCard, UiPosition markerPos) {
        UIComponent markerComponent = UIComponents.texture(Identifier.parse("jimmytools:ui/marker.png"), 1, 1, 256, 256)
                .sizing(Sizing.fixed(MARKER_SIZE))
                .positioning(Positioning.absolute(markerPos.getX(), markerPos.getY()));
//                .zIndex(30)

        markerComponent.mouseDown().subscribe((mousePos, bool) -> {
            markerComponent.remove();
            bingoCard.removeMarker(markerPos);
            SoundManager.playSound(SoundEvent.REMOVE_MARKER);
            return true;
        });

        return markerComponent;
    }
}

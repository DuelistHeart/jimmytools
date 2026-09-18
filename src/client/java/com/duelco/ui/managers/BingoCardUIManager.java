package com.duelco.ui.managers;

import com.duelco._enum.SoundEvent;
import com.duelco.config.ModConfig;
import com.duelco.managers.SoundManager;
import com.duelco.obj.bingo.BingoCard;
import com.duelco.obj.general.UiPosition;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.GridLayout;
import io.wispforest.owo.ui.container.StackLayout;
import io.wispforest.owo.ui.core.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class BingoCardUIManager {
    private static final int MARKER_SIZE = 14;

    public static StackLayout buildBingoCardComponent(Integer cardNumber, BingoCard bingoCard) {
        StackLayout bingoCardElement = (StackLayout) Containers.stack(Sizing.fixed(110), Sizing.fixed(140))
                .alignment(HorizontalAlignment.CENTER, VerticalAlignment.BOTTOM)
                .margins(Insets.of(2));
        StackLayout markerElement = (StackLayout) Containers.stack(Sizing.fixed(120), Sizing.fixed(165))
                .alignment(HorizontalAlignment.CENTER, VerticalAlignment.BOTTOM);
        StackLayout bingoGridContainerElement = (StackLayout) Containers.stack(Sizing.fixed(112), Sizing.fixed(112))
                .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
                .padding(Insets.of(4));
        GridLayout bingoGridElement = (GridLayout) Containers.grid(Sizing.fixed(100), Sizing.fixed(100), 5, 5)
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
                Components.box(Sizing.fill(), Sizing.fill())
                        .color(Color.ofRgb(ModConfig.bingoGridColor.getRGB()))
                        .fill(true)
        ).child(
                bingoGridElement
        );

        if (cardNumber != null && ModConfig.isDisplayBingoNumsEnabled) {
            Style style = Style.EMPTY.withColor(ModConfig.bingoGridColor.getRGB());

            bingoCardElement.child(
                    Components.label(Text.literal(cardNumber.toString()).setStyle(style))
                            .positioning(Positioning.absolute(2, 2))
                            .zIndex(100)
            );
        }

        bingoCardElement.child(
                Components.box(Sizing.fixed(130), Sizing.fixed(160))
                        .color(Color.ofRgb(ModConfig.bingoBackgroundColor.getRGB()))
                        .fill(true)
        ).child(
                Components.texture(Identifier.of("jimmytools","ui/bingo_header.png"), 1, 1, 255, 256)
                        .sizing(Sizing.fixed(80), Sizing.fixed(16))
                        .positioning(Positioning.relative(50, 10))
                        .zIndex(200)
        ).child(
                bingoGridContainerElement
        ).child(
                markerElement
        );

        markerElement.mouseDown().subscribe((x, y, btn) -> {
            UiPosition markerPos = new UiPosition((int) (x-((double) MARKER_SIZE /2)), (int) (y-((double) MARKER_SIZE /2)));
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

    private static Component buildMarkerComponent(BingoCard bingoCard, UiPosition markerPos) {
        Component markerComponent = Components.texture(Identifier.of("jimmytools", "ui/marker.png"), 1, 1, 256, 256)
                .sizing(Sizing.fixed(MARKER_SIZE))
                .zIndex(30)
                .positioning(Positioning.absolute(markerPos.getX(), markerPos.getY()));

        markerComponent.mouseDown().subscribe((x, y, btn) -> {
            markerComponent.remove();
            bingoCard.removeMarker(markerPos);
            SoundManager.playSound(SoundEvent.REMOVE_MARKER);
            return true;
        });

        return markerComponent;
    }
}

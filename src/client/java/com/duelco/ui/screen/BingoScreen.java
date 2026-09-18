package com.duelco.ui.screen;

import com.duelco._enum.Screen;
import com.duelco.handlers.ImageSelection;
import com.duelco.handlers.ScreenCaptureHandler;
import com.duelco.managers.BingoManager;
import com.duelco.managers.ToastManager;
import com.duelco.obj.BingoCard;
import com.duelco.ui.managers.BingoCardUIManager;
import com.duelco.ui.managers.BingoMarkerUIManager;
import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.component.UIComponents;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.StackLayout;
import io.wispforest.owo.ui.container.UIContainers;
import io.wispforest.owo.ui.core.*;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class BingoScreen extends BaseOwoScreen<FlowLayout> {
    private static final BingoMarkerUIManager bingoMarkerManager = new BingoMarkerUIManager();

    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, UIContainers::verticalFlow);
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        rootComponent
                .surface(Surface.VANILLA_TRANSLUCENT)
                .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER);

        StackLayout bingoCardsAndMarkerLayout = (StackLayout) UIContainers.stack(Sizing.fixed(500), Sizing.fixed(220))
                .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER);

        FlowLayout bingoCardsLayout = (FlowLayout) UIContainers.horizontalFlow(Sizing.fixed(500), Sizing.fixed(220))
                .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER);

        bingoCardsAndMarkerLayout.child(bingoCardsLayout)
                .child(bingoMarkerManager.bingoMarkersLayout);

        bingoCardsLayout.mouseDown().subscribe((btn, bool) -> {
                    bingoMarkerManager.addMarker((int) btn.x(), (int) btn.y());

                    return true;
                });

        for (BingoCard bingoCard : BingoManager.getCards()) {
            bingoCardsLayout.child(BingoCardUIManager.buildBingoCardComponent(bingoCard));
        }

        FlowLayout buttonGroup = (FlowLayout) UIContainers.horizontalFlow(Sizing.fill(), Sizing.fixed(22))
                .horizontalAlignment(HorizontalAlignment.CENTER);

        buttonGroup.child(
                UIComponents.button(Component.literal("\uD83D\uDCF7"), buttonComponent -> {
//                    ImageSelection.copyImageToClipboard(ScreenCaptureHandler.captureFramebuffer(Minecraft.getInstance().gameRenderer)); TODO: Re-integrate
                    ToastManager.displayToast("Copied Bingo Cards", "Your bingo cards were copied to clipboard");
                }).margins(Insets.of(2))
        ).child(
                UIComponents.button(Component.translatable("buttons.jimmytools.bingo.clear_marks"),buttonComponent -> {
                    bingoMarkerManager.clearMarkers();
                    ScreenHandler.displayScreen(Screen.BINGO_CARDS_SCREEN, Minecraft.getInstance());
                }).margins(Insets.of(2))
        ).child(
                UIComponents.button(Component.translatable("buttons.jimmytools.bingo.generate_cards"), buttonComponent -> {
                    BingoManager.generateCard();
                    ScreenHandler.displayScreen(Screen.BINGO_CARDS_SCREEN, Minecraft.getInstance());
                }).margins(Insets.of(2))
        ).child(
                UIComponents.button(Component.translatable("buttons.jimmytools.bingo.reset"), buttonComponent -> {
                    ScreenHandler.displayConfirmationScreen(Minecraft.getInstance(),"Are you sure you want to reset (delete) your bingo cards?",
                    () -> {
                        bingoMarkerManager.clearMarkers();
                        BingoManager.resetCards();
                        ScreenHandler.displayScreen(Screen.BINGO_CARDS_SCREEN, Minecraft.getInstance());
                    }, () -> {
                        ScreenHandler.displayScreen(Screen.BINGO_CARDS_SCREEN, Minecraft.getInstance());
                    });
                }).margins(Insets.of(2))
        ).child(
                UIComponents.button(Component.translatable("buttons.jimmytools.bingo.item_list"), buttonComponent -> {
                    ScreenHandler.displayScreen(Screen.BINGO_ITEMS_SCREEN, Minecraft.getInstance());
                }).margins(Insets.of(2))
        );

        rootComponent.child(
                UIComponents.label(Component.translatable("screen.jimmytools.bingo.title"))
        ).child(
                bingoCardsAndMarkerLayout
        ).child(
                buttonGroup
        );
    }
}
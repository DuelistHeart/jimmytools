package com.duelco.ui.screen;

import com.duelco._enum.Screen;
import com.duelco.obj.BingoItem;
import com.duelco.obj.BingoPossibleItemsList;
import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.component.UIComponents;
import io.wispforest.owo.ui.container.UIContainers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.ScrollContainer;
import io.wispforest.owo.ui.core.*;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BingoItemsScreen extends BaseOwoScreen<FlowLayout> {

    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, UIContainers::verticalFlow);
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        rootComponent
                .surface(Surface.VANILLA_TRANSLUCENT)
                .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER);

        List<BingoItem> bingoItems = BingoPossibleItemsList.getListOfPossibleItems();

        FlowLayout bingoPossibleItemsContainer = (FlowLayout) UIContainers.verticalFlow(Sizing.fixed(500), Sizing.content()).allowOverflow(true);

        for (BingoItem item : bingoItems) {
            FlowLayout bingoPossibleItemElement = (FlowLayout) UIContainers.horizontalFlow(Sizing.fixed(500), Sizing.fixed(25))
                    .verticalAlignment(VerticalAlignment.CENTER);
            bingoPossibleItemElement.child(
                    UIComponents.item(item.getItem())
                            .margins(Insets.of(2))
            ).child(
                    UIComponents.label(Component.literal(item.getName()))
                            .margins(Insets.of(2))
            );
            bingoPossibleItemsContainer.child(bingoPossibleItemElement);
        }

        rootComponent.child(
                UIComponents.label(Component.literal("screen.jimmytools.bingoitems.title"))
        ).child(
                UIContainers.verticalScroll(Sizing.fixed(500), Sizing.fixed(200), bingoPossibleItemsContainer)
                        .scrollbar(ScrollContainer.Scrollbar.vanilla())
                        .scrollbarThiccness(5)
                        .scrollStep(25)
        ).child(
                UIComponents.button(Component.literal("Cards"), buttonComponent -> {
                    ScreenHandler.displayScreen(Screen.BINGO_CARDS_SCREEN, Minecraft.getInstance());
                })
        );
    }
}
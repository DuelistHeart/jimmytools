package com.duelco.ui.screen;

import com.duelco._enum.Screen;
import com.duelco.obj.bingo.BingoItem;
import com.duelco.obj.bingo.BingoPossibleItemsList;
import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.component.UIComponents;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.ScrollContainer;
import io.wispforest.owo.ui.container.UIContainers;
import io.wispforest.owo.ui.core.*;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class BingoItemsScreen extends BaseOwoScreen<FlowLayout> {
    private static FlowLayout bingoPossibleItemsContainer = (FlowLayout) UIContainers.verticalFlow(Sizing.fixed(500), Sizing.content()).allowOverflow(true);

    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, UIContainers::verticalFlow);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        rootComponent
                .surface(Surface.VANILLA_TRANSLUCENT)
                .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER);

        updateItemList();

        TextBoxComponent searchFieldComponent = UIComponents.textBox(Sizing.fixed(250))
                .text(BingoPossibleItemsList.getFilter());

//        searchFieldComponent.setPlaceholder(Component.literal("Search...")); //TODO: Check?

        searchFieldComponent.onChanged().subscribe((text) -> {
            BingoPossibleItemsList.setFilter(text);
            updateItemList();
        });

        rootComponent.child(
                UIComponents.label(Component.translatable("screen.jimmytools.bingoitems.title"))
        ).child(
                searchFieldComponent
        ).child(
                UIContainers.verticalScroll(Sizing.fixed(340), Sizing.fixed(200), bingoPossibleItemsContainer)
                        .scrollbar(ScrollContainer.Scrollbar.vanillaFlat())
                        .scrollbarThiccness(5)
                        .scrollStep(25)
        ).child(
                UIComponents.button(Component.literal("Cards"), buttonComponent -> {
                    ScreenHandler.displayScreen(Screen.BINGO_CARDS_SCREEN, Minecraft.getInstance());
                })
        );
    }

    private void updateItemList() {
        bingoPossibleItemsContainer.clearChildren();

        for (BingoItem item : BingoPossibleItemsList.getListOfPossibleItems()) {
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
    }
}
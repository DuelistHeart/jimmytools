package com.duelco.ui.screen;

import com.duelco._enum.Screen;
import com.duelco.obj.bingo.BingoItem;
import com.duelco.obj.bingo.BingoPossibleItemsList;
import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.ScrollContainer;
import io.wispforest.owo.ui.core.*;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public class BingoItemsScreen extends BaseOwoScreen<FlowLayout> {
    private static FlowLayout bingoPossibleItemsContainer = (FlowLayout) Containers.verticalFlow(Sizing.fixed(500), Sizing.content()).allowOverflow(true);

    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, Containers::verticalFlow);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        rootComponent
                .surface(Surface.VANILLA_TRANSLUCENT)
                .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER);

        updateItemList();

        TextBoxComponent searchFieldComponent = Components.textBox(Sizing.fixed(250))
                .text(BingoPossibleItemsList.getFilter());

        searchFieldComponent.setPlaceholder(Text.of("Search..."));

        searchFieldComponent.onChanged().subscribe((text) -> {
            BingoPossibleItemsList.setFilter(text);
            updateItemList();
        });

        rootComponent.child(
                Components.label(Text.translatable("screen.jimmytools.bingoitems.title"))
        ).child(
                searchFieldComponent
        ).child(
                Containers.verticalScroll(Sizing.fixed(340), Sizing.fixed(200), bingoPossibleItemsContainer)
                        .scrollbar(ScrollContainer.Scrollbar.vanillaFlat())
                        .scrollbarThiccness(5)
                        .scrollStep(25)
        ).child(
                Components.button(Text.of("Cards"), buttonComponent -> {
                    ScreenHandler.displayScreen(Screen.BINGO_CARDS_SCREEN, client);
                })
        );
    }

    private void updateItemList() {
        bingoPossibleItemsContainer.clearChildren();

        for (BingoItem item : BingoPossibleItemsList.getListOfPossibleItems()) {
            FlowLayout bingoPossibleItemElement = (FlowLayout) Containers.horizontalFlow(Sizing.fixed(500), Sizing.fixed(25))
                    .verticalAlignment(VerticalAlignment.CENTER);
            bingoPossibleItemElement.child(
                    Components.item(item.getItem())
                            .margins(Insets.of(2))
            ).child(
                    Components.label(Text.of(item.getName()))
                            .margins(Insets.of(2))
            );
            bingoPossibleItemsContainer.child(bingoPossibleItemElement);
        }
    }
}
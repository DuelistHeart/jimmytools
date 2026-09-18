package com.duelco.ui.screen;

import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.component.UIComponents;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.UIContainers;
import io.wispforest.owo.ui.core.*;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class ConfirmationScreen extends BaseOwoScreen<FlowLayout> {

    private final Runnable onConfirm;
    private final Runnable onReturn;

    ConfirmationScreen(String title, Runnable onConfirm, Runnable onReturn) {
        super(Component.literal(title));
        this.onConfirm = onConfirm;
        this.onReturn = onReturn;
    }

    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, UIContainers::verticalFlow);
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        rootComponent
                .surface(Surface.VANILLA_TRANSLUCENT)
                .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER);

        FlowLayout buttonGroup = (FlowLayout) UIContainers.horizontalFlow(Sizing.fill(), Sizing.fixed(22))
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .margins(Insets.of(5));

        buttonGroup.child(
                UIComponents.button(Component.literal("Confirm"), buttonComponent -> onConfirm.run())
                        .margins(Insets.of(2))
        ).child(
                UIComponents.button(Component.literal("Return"), buttonComponent -> onReturn.run())
                        .margins(Insets.of(2))
        );

        rootComponent.child(
                UIComponents.label(this.title)
        ).child(
                buttonGroup
        );
    }
}

package com.duelco.ui.managers;

import io.wispforest.owo.ui.component.UIComponents;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.UIContainers;
import io.wispforest.owo.ui.core.*;
import net.minecraft.resources.Identifier;

public class BingoMarkerUIManager {
    private static final int MARKER_SIZE = 20;
    private static final int MARKER_INDEX = 30;

    public final FlowLayout bingoMarkersLayout;

    public BingoMarkerUIManager() {
        this.bingoMarkersLayout =  (FlowLayout) UIContainers.horizontalFlow(Sizing.fill(), Sizing.fixed(220))
                .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
    }

    public void addMarker(int x, int y) {
        UIComponent markerComponent = getMarkerComponent().positioning(Positioning.absolute(x-(MARKER_SIZE/2), y-(MARKER_SIZE/2)));
        markerComponent.mouseDown().subscribe((btn, bool) -> {
            markerComponent.remove();
            return true;
        });

        bingoMarkersLayout.child(
                markerComponent
        );
    }

    public void clearMarkers() {
        bingoMarkersLayout.clearChildren();
    }

    private UIComponent getMarkerComponent() {
        return UIComponents.texture(Identifier.parse("jimmytools:ui/marker.png"), 1, 1, 256, 256)
                .sizing(Sizing.fixed(MARKER_SIZE));
//                .zIndex(MARKER_INDEX); TODO: Check
    }
}

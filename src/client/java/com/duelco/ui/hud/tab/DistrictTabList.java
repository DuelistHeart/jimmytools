package com.duelco.ui.hud.tab;

import com.duelco._enum.District;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.Objects;

public class DistrictTabList extends TabListRenderer<Component> {
    private static final int FOOTER_SWAP_TICKS = 10; // 20 client ticks per second
    private static final Component[] FOOTERS = {
            Component.literal("Powered by the ").append(Component.literal("rawb.tv").withStyle(ChatFormatting.AQUA)).append(Component.literal(" Commmunity!")),
            Component.literal("Powered by the ").append(Component.literal("rawb.tv").withStyle(ChatFormatting.LIGHT_PURPLE)).append(Component.literal(" Commmunity!")),
    };

    private String districtName;
    private int tickCount;

    public DistrictTabList(String id) {
        super(id);
        this.scrollTexture = Identifier.fromNamespaceAndPath("jimmytools", "ui/scroll_roll_nearby.png");
        setColumns(1, 190);
        tabHeight = DISTRICT_HEIGHT;
    }

    /** Advances the footer rotation; call once per client tick. */
    public void tick() {
        tickCount++;
    }

    public void setDistrictName(String districtName) {
        if (!Objects.equals(districtName, this.districtName)) {
            this.scrollTexture = this.getDistrictScroll(District.fromName(districtName));
            this.districtName = districtName;
        }
    }

    public Identifier getDistrictScroll(District district) {
        if (district == null) return Identifier.fromNamespaceAndPath("jimmytools", "ui/scroll_roll_nearby.png");

        switch (district) {
            case DWARVEN:
                return Identifier.fromNamespaceAndPath("jimmytools", "ui/scroll_roll_dwarven.png");
            case MOONBAY:
                return Identifier.fromNamespaceAndPath("jimmytools", "ui/scroll_roll_moonbay.png");
            case GOATTOWN:
                return Identifier.fromNamespaceAndPath("jimmytools", "ui/scroll_roll_goattown.png");
            case ROYAL:
                return Identifier.fromNamespaceAndPath("jimmytools", "ui/scroll_roll_royal.png");
            case SOUTHSHIRE:
                return Identifier.fromNamespaceAndPath("jimmytools", "ui/scroll_roll_southshire.png");
            case GROVE:
                return Identifier.fromNamespaceAndPath("jimmytools", "ui/scroll_roll_grove.png");
            case BRICKTON:
                return Identifier.fromNamespaceAndPath("jimmytools", "ui/scroll_roll_brickton.png");
            case DARKVALE:
                return Identifier.fromNamespaceAndPath("jimmytools", "ui/scroll_roll_darkvale.png");
            case SLUMS:
                return Identifier.fromNamespaceAndPath("jimmytools", "ui/scroll_roll_slums.png");
            case STICKY:
                return Identifier.fromNamespaceAndPath("jimmytools", "ui/scroll_roll_sticky.png");
            default:
                return Identifier.fromNamespaceAndPath("jimmytools", "ui/scroll_roll_nearby.png");
        }
    }

    @Override
    Component getHeaderText() {
        return districtName == null ? null : Component.literal(districtName);
    }

    @Override
    Component getFooterText() {
        return FOOTERS[(tickCount / FOOTER_SWAP_TICKS) % FOOTERS.length];
    }

    @Override
    void executeLoop(Component datum, GuiGraphicsExtractor context, int i) {
        // Line 0 is the plot name and line 1 its owner, each centred on the scroll.
        // The two-line block is centred vertically, so a taller scroll gains space above and below.
        int blockTop = (tabHeight - (2 * lineHeight)) / 2;
        int entryY = y + blockTop + (i * lineHeight);
        if (y + animatedHeight > entryY + lineHeight) {
            drawCentered(context, datum, entryY);
        }
    }

    @Override
    void setPosition() {
        // Centered below the player and character scrolls
        this.x = centeredLeft(tabWidth);
        this.y = listTop() + LIST_HEIGHT + (2 * GAP);
    }
}

package com.duelco.mixin.client;

import com.duelco.config.ModConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.SubtitleOverlay;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(SubtitleOverlay.class)
public class TitleMixin {
    @Unique
    private static final Logger LOGGER = LoggerFactory.getLogger("title-mixin");

    @Unique
    private static final List<String> pastLvlUpMessages = new ArrayList<>();

    @Final
    @Shadow
    private List<?> subtitles;

    @Inject(at = @At("HEAD"), method = "extractRenderState")
    private void onTitle(final GuiGraphicsExtractor graphics, CallbackInfo ci) {

        if (ModConfig.areLevelUpMessagesEnabled && !this.subtitles.isEmpty()) {
            Object latest = this.subtitles.get(this.subtitles.size() - 1);
            Component text = ((SubtitleAccessor) latest).jimmytools$getComponent();

            LOGGER.debug("receiving title: {}", text);
            Minecraft client = Minecraft.getInstance();

            Component lvlUpMsg = this.getLevelUpMessage(text.getString());

            if (client.player != null) {
                if (lvlUpMsg != null && !Objects.equals(lvlUpMsg.getString(), "[]") && !pastLvlUpMessages.contains(lvlUpMsg.getString())) {
                    client.player.sendSystemMessage(lvlUpMsg);
                    pastLvlUpMessages.add(lvlUpMsg.getString());
                }
            }
        }
    }

    @Unique
    private Component getLevelUpMessage(String lvlUpMsg) {
        // Define a regex pattern to match the input string and capture the desired parts
        String regex = "Your (\\w+) Level has increased to (\\d+)!!!";

        // Compile the regex pattern
        Pattern pattern = Pattern.compile(regex);

        // Create a matcher for the input string
        Matcher matcher = pattern.matcher(lvlUpMsg);

        // Check if the pattern matches
        if (matcher.find()) {
            // Extract the captured groups
            String activity = matcher.group(1);
            String level = matcher.group(2);

            // Print the extracted parts
            Component activityText = Component.literal(activity).withStyle(ChatFormatting.AQUA);
            LOGGER.debug("Activity: " + activity);
            Component levelText = Component.literal(level).withStyle(ChatFormatting.AQUA);
            LOGGER.debug("Level: " + level);

            return Component.literal("")
                    .append(Component.literal("[").withStyle(ChatFormatting.GREEN))
                    .append(Component.literal("JimmyTools").withStyle(ChatFormatting.GRAY))
                    .append(Component.literal("] ").withStyle(ChatFormatting.GREEN))
                    .append("Your ")
                    .append(activityText)
                    .append(" Level has increased to ")
                    .append(levelText)
                    .append("!!!").withStyle(ChatFormatting.GOLD);
        } else {
            LOGGER.debug("Subtitle was not a level up notification.");
            return null;
        }
    }
}

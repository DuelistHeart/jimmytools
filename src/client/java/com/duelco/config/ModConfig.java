package com.duelco.config;

import com.duelco._enum.NamesCmdOptions;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.awt.*;

public class ModConfig {
    public static ConfigClassHandler<ModConfig> HANDLER = ConfigClassHandler.createBuilder(ModConfig.class)
            .id(Identifier.parse("jimmytools:config"))
                    .serializer(config -> GsonConfigSerializerBuilder.create(config)
                            .setPath(FabricLoader.getInstance().getConfigDir().resolve("jimmytools.json5"))
                            .setJson5(true)
                            .build())
                    .build();

    @SerialEntry
    public static int bingoMaxCards = 3; // The isColor property adds a color chooser for a hexadecimal color
    @SerialEntry()
    public static Color bingoBackgroundColor = Color.decode("#e2d5c4"); // The isColor property adds a color chooser for a hexadecimal color
    @SerialEntry
    public static Color bingoGridColor = Color.decode("#d59989"); // The isColor property adds a color chooser for a hexadecimal color

    @SerialEntry
    public static boolean areTransformationsEnabled = false;
    @SerialEntry
    public static boolean isTransformed = false;
    @SerialEntry
    public static String regularSkin = "";
    @SerialEntry
    public static String transformationSkin = "";

    @SerialEntry
    public static boolean areLevelUpMessagesEnabled = false;
    @SerialEntry
    public static boolean startupCommandsNamesEnabled = false;
    @SerialEntry
    public static NamesCmdOptions startupCommandsNamesOption = NamesCmdOptions.NAMES_CHAR;

    public static YetAnotherConfigLib build() {
        return YetAnotherConfigLib.createBuilder()
                .title(Component.literal("JimmyTools Config"))
                .category(ConfigCategory.createBuilder()
                        .name(Component.literal("Bingo"))
                        .tooltip(Component.literal("Bingo config"))
                        .group(OptionGroup.createBuilder()
                                .name(Component.literal("Color Options"))
                                .description(OptionDescription.of(Component.literal("Color options for Bingo cards")))
                                .option(Option.<Color>createBuilder()
                                        .name(Component.literal("Background Color"))
                                        .description(OptionDescription.of(Component.literal("Background color of the bingo card.")))
                                        .binding(Color.BLACK, () -> bingoBackgroundColor, newVal -> bingoBackgroundColor = newVal)
                                        .controller(ColorControllerBuilder::create)
                                        .build())
                                .option(Option.<Color>createBuilder()
                                        .name(Component.literal("Grid Color"))
                                        .description(OptionDescription.of(Component.literal("Grid color of the bingo card.")))
                                        .binding(Color.BLACK, () -> bingoGridColor, newVal -> bingoGridColor = newVal)
                                        .controller(ColorControllerBuilder::create)
                                        .build())
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(Component.literal("Generation Options"))
                                .description(OptionDescription.of(Component.literal("Generation options for Bingo cards")))
                                .option(Option.<Integer>createBuilder()
                                        .name(Component.literal("Max Cards"))
                                        .description(OptionDescription.of(Component.literal("The max amount of cards that can be generated.")))
                                        .binding(3, () -> bingoMaxCards, newVal -> bingoMaxCards = newVal)
                                        .controller(integerOption -> IntegerSliderControllerBuilder.create(integerOption)
                                                .range(1, 3)
                                                .step(1)
                                                .formatValue(val -> Component.literal(val + " Card(s)")))
                                        .build())
                                .build())
                        .build())
                .category(ConfigCategory.createBuilder()
                        .name(Component.literal("ChatUtils"))
                        .tooltip(Component.literal("ChatUtils config"))
                        .group(OptionGroup.createBuilder()
                                .name(Component.literal("LevelUp Options"))
                                .description(OptionDescription.of(Component.literal("Options for the LevelUp Messages")))
                                .option(Option.<Boolean>createBuilder()
                                        .name(Component.literal("Enable LevelUp Messages"))
                                        .description(OptionDescription.of(Component.literal("Enables/Disables level up messages from appearing.")))
                                        .binding(true, () -> areLevelUpMessagesEnabled, newVal -> areLevelUpMessagesEnabled = newVal)
                                        .controller(BooleanControllerBuilder::create)
                                        .build())
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(Component.literal("StartUp Command Options"))
                                .description(OptionDescription.of(Component.literal("Options for startup commands")))
                                .option(Option.<Boolean>createBuilder()
                                        .name(Component.literal("Enable Names command"))
                                        .description(OptionDescription.of(Component.literal("Enables/Disables running /names on server join.")))
                                        .binding(true, () -> startupCommandsNamesEnabled, newVal -> startupCommandsNamesEnabled = newVal)
                                        .controller(BooleanControllerBuilder::create)
                                        .build())
                                .option(Option.<NamesCmdOptions>createBuilder()
                                        .name(Component.literal("Names command option"))
                                        .description(OptionDescription.of(Component.literal("The option that is ran for /names on startup.")))
                                        .binding(NamesCmdOptions.NAMES_OFF, () -> startupCommandsNamesOption, newVal -> startupCommandsNamesOption = newVal)
                                        .controller(opt -> EnumControllerBuilder.create(opt)
                                                .enumClass(NamesCmdOptions.class)
                                                .formatValue(v -> Component.translatable("jimmytools.config.startupcommands.namesoptions." + v.name().toLowerCase())))
                                        .build())
                                .build())
                        .build())
                .category(ConfigCategory.createBuilder()
                        .name(Component.literal("Transformation"))
                        .tooltip(Component.literal("Transformation config"))
                        .group(OptionGroup.createBuilder()
                                .name(Component.literal("Skin Settings"))
                                .description(OptionDescription.of(Component.literal("Skin settings for transformations")))
                                .option(Option.<Boolean>createBuilder()
                                        .name(Component.literal("Transformation Enabled"))
                                        .description(OptionDescription.of(Component.literal("Determines if transformations are enabled or not.")))
                                        .binding(false, () -> areTransformationsEnabled, newVal -> areTransformationsEnabled = newVal)
                                        .controller(BooleanControllerBuilder::create)
                                        .build())
                                .option(Option.<String>createBuilder()
                                        .name(Component.literal("Regular Skin"))
                                        .description(OptionDescription.of(Component.literal("The URL of the pre-transformation skin. (Auto-Updates)")))
                                        .binding("", () -> regularSkin, newVal -> regularSkin = newVal)
                                        .controller(StringControllerBuilder::create)
                                        .build())
                                .option(Option.<String>createBuilder()
                                        .name(Component.literal("Transformation Skin"))
                                        .description(OptionDescription.of(Component.literal("The URL of the transformation skin")))
                                        .binding("", () -> transformationSkin, newVal -> transformationSkin = newVal)
                                        .controller(StringControllerBuilder::create)
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Component.literal("Skin Transformed"))
                                        .description(OptionDescription.of(Component.literal("Denotes if a transformation is currently active")))
                                        .binding(true, () -> isTransformed, newVal -> isTransformed = newVal)
                                        .controller(BooleanControllerBuilder::create)
                                        .build())
                                .build())
                        .build())
                .build();
    }
}

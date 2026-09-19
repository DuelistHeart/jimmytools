package com.duelco.config;

import com.duelco._enum.NamesCmdOptions;
import com.duelco.handlers.FeatureFlagHandler;
import com.duelco.handlers.TransformationHelperHandler;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
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
    public static boolean isCustomTablistEnabled = false;
    @SerialEntry
    public static float openScrollSpeed = 3.0f;
    @SerialEntry
    public static float closeScrollSpeed = 8.0f;

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
        YetAnotherConfigLib.Builder config =  YetAnotherConfigLib.createBuilder()
                .title(Component.literal("JimmyTools Config"))
                .category(ConfigCategory.createBuilder()
                        .name(Component.literal("ChatUtils"))
                        .tooltip(Component.literal("ChatUtils config"))
                        .group(OptionGroup.createBuilder()
                                .name(Component.literal("LevelUp Options"))
                                .description(OptionDescription.of(Component.literal("Options for the LevelUp Messages")))
                                .option(Option.<Boolean>createBuilder()
                                        .name(Component.literal("Enable LevelUp Messages"))
                                        .description(OptionDescription.of(Component.literal("Enables/Disables level up messages from appearing.")))
                                        .binding(false, () -> areLevelUpMessagesEnabled, newVal -> areLevelUpMessagesEnabled = newVal)
                                        .controller(BooleanControllerBuilder::create)
                                        .build())
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(Component.literal("StartUp Command Options"))
                                .description(OptionDescription.of(Component.literal("Options for startup commands")))
                                .option(Option.<Boolean>createBuilder()
                                        .name(Component.literal("Enable Names command"))
                                        .description(OptionDescription.of(Component.literal("Enables/Disables running /names on server join.")))
                                        .binding(false, () -> startupCommandsNamesEnabled, newVal -> startupCommandsNamesEnabled = newVal)
                                        .controller(BooleanControllerBuilder::create)
                                        .build())
                                .option(Option.<NamesCmdOptions>createBuilder()
                                        .name(Component.literal("Names command option"))
                                        .description(OptionDescription.of(Component.literal("The option that is ran for /names on startup.")))
                                        .binding(NamesCmdOptions.NAMES_CHAR, () -> startupCommandsNamesOption, newVal -> startupCommandsNamesOption = newVal)
                                        .controller(opt -> EnumControllerBuilder.create(opt)
                                                .enumClass(NamesCmdOptions.class)
                                                .formatValue(v -> Component.translatable("jimmytools.config.startupcommands.namesoptions." + v.name().toLowerCase())))
                                        .build())
                                .build())
                        .build());

        config.category(ConfigCategory.createBuilder()
                .name(Component.literal("CustomTab"))
                .tooltip(Component.literal("Custom Tablist config"))
                .group(OptionGroup.createBuilder()
                        .name(Component.literal("Tab Options"))
                        .description(OptionDescription.of(Component.literal("Options for the custom tab list.")))
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.literal("Enable Custom Tablist"))
                                .description(OptionDescription.of(Component.literal("Enables/Disables the custom tablist.")))
                                .binding(false, () -> isCustomTablistEnabled, newVal -> isCustomTablistEnabled = newVal)
                                .controller(BooleanControllerBuilder::create)
                                .build())
                        .build())
                .option(Option.<Float>createBuilder()
                        .name(Component.literal("Scroll Open Speed"))
                        .description(OptionDescription.of(Component.literal("Determines how fast the scrolls open up.")))
                        .binding(3.0f, () -> openScrollSpeed, newVal -> openScrollSpeed = newVal)
                        .controller(floatOption -> FloatSliderControllerBuilder.create(floatOption)
                                .range(1.0f, 12.0f)
                                .step(1.0f))
                        .build())
                .option(Option.<Float>createBuilder()
                        .name(Component.literal("Scroll Close Speed"))
                        .description(OptionDescription.of(Component.literal("Determines how fast the scrolls close.")))
                        .binding(8.0f, () -> closeScrollSpeed, newVal -> closeScrollSpeed = newVal)
                        .controller(floatOption -> FloatSliderControllerBuilder.create(floatOption)
                                .range(1.0f, 12.0f)
                                .step(1.0f))
                        .build())
                .build());

        config.category(ConfigCategory.createBuilder()
                        .name(Component.literal("Transformation"))
                        .tooltip(Component.literal("Transformation config"))
                        .group(OptionGroup.createBuilder()
                                .name(Component.literal("Transformation Setup"))
                                .description(OptionDescription.of(Component.literal("Config options for setting up transformations.")))
                                .option(ButtonOption.createBuilder()
                                        .name(Component.literal("Set Up Character Transformation"))
                                        .description(OptionDescription.of(Component.literal("Starts a process for setting up a transformation for your current character.")))
                                        .action((yaclScreen, option) -> {
                                            TransformationHelperHandler.beginTransformationSetup();
                                            Minecraft.getInstance().gui.setScreen(null);
                                        })
                                        .build()
                                ).build()
                        )
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
                                        .binding(false, () -> isTransformed, newVal -> isTransformed = newVal)
                                        .controller(BooleanControllerBuilder::create)
                                        .build())
                                .build())
                        .build())
                .build();

        return config.build();
    }
}

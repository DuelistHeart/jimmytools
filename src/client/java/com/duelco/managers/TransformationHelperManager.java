package com.duelco.managers;

import com.duelco.config.ModConfig;
import com.duelco.handlers.TransformationHelperHandler;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransformationHelperManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("TransformationHelperManager");
    public String handleTransform(String currentSkin) {
        if (!ModConfig.transformationSkin.isBlank()) {
            if (ModConfig.isTransformed) {
                if (TransformationHelperHandler.isSettingUpTransformation()) {
                    TransformationHelperHandler.completeTransformationSetup();
                }

                ModConfig.isTransformed = false;
                return ModConfig.regularSkin;
            } else {
                ModConfig.regularSkin = currentSkin;
                ModConfig.isTransformed = true;
                return ModConfig.transformationSkin;
            }
        } else {
            ToastManager.displayToast(Text.of("Transformation Error"), Text.of("Transformation skin is not set in the config file."));
            return null;
        }
    }

    public static void setTransformSkin(String skinUrl) {
        ModConfig.transformationSkin = skinUrl;
    }
}

package com.duelco.managers;

import com.duelco.config.ModConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransformationHelperManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("TransformationHelperManager");
    public String handleTransform(String currentSkin) {
        if (!ModConfig.transformationSkin.isBlank()) {
            if (ModConfig.isTransformed) {
                ModConfig.isTransformed = false;
                return ModConfig.regularSkin;
            } else {
                ModConfig.regularSkin = currentSkin;
                ModConfig.isTransformed = true;
                return ModConfig.transformationSkin;
            }
        } else {
            ToastManager.displayToast("Transformation Error", "Transformation skin is not set in the config file.");
            return null;
        }
    }
}

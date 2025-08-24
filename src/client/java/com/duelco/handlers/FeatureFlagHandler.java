package com.duelco.handlers;

public class FeatureFlagHandler {
    public static boolean isCustomTablistEnabled() {
        // retrieve the environment variable "JIMMYTOOLS_CUSTOM_TABLIST_ENABLED"
        String flag = System.getenv("JIMMYTOOLS_CUSTOM_TABLIST_ENABLED");
        return flag != null && flag.equalsIgnoreCase("true");
    }
}

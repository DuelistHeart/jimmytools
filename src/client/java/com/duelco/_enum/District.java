package com.duelco._enum;

public enum District {
    SLUMS("The Slums"),
    DWARVEN("Dwarven District"),
    MOONBAY("Moon Bay"),
    DARKVALE("Darkvale"),
    STICKY("Sticky District"),
    BRICKTON("Brickton"),
    GROVE("The Grove"),
    SOUTHSHIRE("Southshire"),
    GOATTOWN("Goat Town"),
    ROYAL("Royal District");

    private final String districtName;

    private District(String districtName) {
        this.districtName = districtName;
    }

    /** Returns the district with this display name (ignoring case), or null if there is none. */
    public static District fromName(String name) {
        if (name == null) return null;

        String trimmed = name.trim();
        for (District district : District.values()) {
            if (district.districtName.equalsIgnoreCase(trimmed)) {
                return district;
            }
        }

        return null;
    }
}

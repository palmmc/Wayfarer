package com.wayfarer.config;

import com.wayfarer.platform.Services;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class WayfarerConfig {
    // General Configuration
    public static float waypointScale = 1.0f;
    public static String waypointLabelFormat = "%s (%dm)";
    public static int waypointViewDistance = -1;

    // Animations Configuration
    public static AnimationCurve waypointFadeInCurve = AnimationCurve.EXPO_OUT;
    public static int waypointFadeInDuration = 400;
    public static AnimationCurve waypointFadeOutCurve = AnimationCurve.EXPO_OUT;
    public static int waypointFadeOutDuration = 300;

    // Visibility Configuration
    public static VisibilityMode showWaypointLabels = VisibilityMode.ALWAYS;
    public static VisibilityMode showWaypointIcons = VisibilityMode.ALWAYS;
    public static LocatorVisibilityMode showLocatorIcons = LocatorVisibilityMode.ALWAYS;

    public enum VisibilityMode {
        ALWAYS, HOVER, NEVER
    }

    public enum LocatorVisibilityMode {
        ALWAYS, HOLD, NEVER
    }

    public enum AnimationCurve {
        LINEAR, EXPO_IN, EXPO_OUT, SPRING, BOUNCE
    }

    private static final String FILE_NAME = "wayfarer.toml";

    public static void load() {
        Path configPath = Services.PLATFORM.getConfigFolder().resolve(FILE_NAME);
        if (!Files.exists(configPath)) {
            save();
            return;
        }

        try {
            List<String> lines = Files.readAllLines(configPath, StandardCharsets.UTF_8);
            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#"))
                    continue;

                String[] parts = line.split("=", 2);
                if (parts.length < 2)
                    continue;

                String key = parts[0].trim();
                String value = parts[1].trim();

                if (value.startsWith("\"") && value.endsWith("\"")) {
                    value = value.substring(1, value.length() - 1);
                }

                try {
                    switch (key) {
                        case "waypointScale" -> waypointScale = Float.parseFloat(value);
                        case "waypointLabelFormat" -> waypointLabelFormat = value;
                        case "waypointViewDistance" -> waypointViewDistance = Integer.parseInt(value);
                        case "waypointFadeInCurve" -> waypointFadeInCurve = AnimationCurve.valueOf(value.toUpperCase());
                        case "waypointFadeInDuration" -> waypointFadeInDuration = Integer.parseInt(value);
                        case "waypointFadeOutCurve" ->
                            waypointFadeOutCurve = AnimationCurve.valueOf(value.toUpperCase());
                        case "waypointFadeOutDuration" -> waypointFadeOutDuration = Integer.parseInt(value);
                        case "showWaypointLabels" -> showWaypointLabels = VisibilityMode.valueOf(value.toUpperCase());
                        case "showWaypointIcons" -> showWaypointIcons = VisibilityMode.valueOf(value.toUpperCase());
                        case "showLocatorIcons" ->
                            showLocatorIcons = LocatorVisibilityMode.valueOf(value.toUpperCase());
                    }
                } catch (Exception ex) {
                    System.err.println("Failed to parse config key '" + key + "': " + ex.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to load Wayfarer config: " + e.getMessage());
        }
    }

    public static void save() {
        Path configPath = Services.PLATFORM.getConfigFolder().resolve(FILE_NAME);
        List<String> lines = new ArrayList<>();

        lines.add("# Wayfarer Configuration File");
        lines.add("");
        lines.add("# --- General ---");
        lines.add("# Modifies the scale of waypoint rendering (0.5 to 2.0)");
        lines.add("waypointScale = " + waypointScale);
        lines.add("");
        lines.add("# Customizes the format used for waypoint labels.");
        lines.add("# %s = Name, %d = Distance");
        lines.add("waypointLabelFormat = \"" + waypointLabelFormat + "\"");
        lines.add("");
        lines.add("# The maximum distance in blocks before a waypoint is hidden. Set to -1 for no limit.");
        lines.add("waypointViewDistance = " + waypointViewDistance);
        lines.add("");
        lines.add("# --- Animations ---");
        lines.add("# Options: LINEAR, EXPO_IN, EXPO_OUT, SPRING, BOUNCE");
        lines.add("waypointFadeInCurve = \"" + waypointFadeInCurve.name() + "\"");
        lines.add("waypointFadeInDuration = " + waypointFadeInDuration);
        lines.add("waypointFadeOutCurve = \"" + waypointFadeOutCurve.name() + "\"");
        lines.add("waypointFadeOutDuration = " + waypointFadeOutDuration);
        lines.add("");
        lines.add("# --- Visibility ---");
        lines.add("# For Labels and Icons: ALWAYS, HOVER (labels only), NEVER");
        lines.add("showWaypointLabels = \"" + showWaypointLabels.name() + "\"");
        lines.add("showWaypointIcons = \"" + showWaypointIcons.name() + "\"");
        lines.add("# For Locator icons: ALWAYS, HOLD, NEVER");
        lines.add("showLocatorIcons = \"" + showLocatorIcons.name() + "\"");

        try {
            Files.write(configPath, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Failed to save Wayfarer config: " + e.getMessage());
        }
    }
}

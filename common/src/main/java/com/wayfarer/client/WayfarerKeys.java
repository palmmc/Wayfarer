package com.wayfarer.client;

import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.resources.Identifier;

public class WayfarerKeys {
    public static final KeyMapping.Category WAYFARER_CATEGORY = KeyMapping.Category
            .register(Identifier.fromNamespaceAndPath("wayfarer", "main"));

    public static final KeyMapping LOCATOR_BAR_KEY = new KeyMapping(
            "key.wayfarer.locator_bar",
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_TAB,
            WAYFARER_CATEGORY);

    public static float locatorBarAlpha = 0.0f;
}

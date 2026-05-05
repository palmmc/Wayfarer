package com.wayfarer.client;

import org.lwjgl.glfw.GLFW;
import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

public class WayfarerKeys {
        public static final KeyMapping LOCATOR_BAR_KEY = new KeyMapping(
                        "key.wayfarer.locator_bar",
                        InputConstants.Type.KEYSYM,
                        InputConstants.KEY_TAB,
                        "key.category.wayfarer.main");

        private static java.lang.reflect.Field keyField;

        public static boolean isDownCustom() {
                if (LOCATOR_BAR_KEY.isUnbound())
                        return false;
                try {
                        if (keyField == null) {
                                try {
                                        keyField = KeyMapping.class.getDeclaredField("key");
                                } catch (NoSuchFieldException e) {
                                        keyField = KeyMapping.class.getDeclaredField("field_1653");
                                }
                                keyField.setAccessible(true);
                        }
                        InputConstants.Key k = (InputConstants.Key) keyField.get(LOCATOR_BAR_KEY);
                        long window = Minecraft.getInstance().getWindow().getWindow();
                        int value = k.getValue();
                        if (k.getType() == InputConstants.Type.MOUSE) {
                                return GLFW.glfwGetMouseButton(window, value) == GLFW.GLFW_PRESS;
                        }
                        return InputConstants.isKeyDown(window, value);
                } catch (Exception e) {
                        return LOCATOR_BAR_KEY.isDown();
                }
        }

        public static float locatorBarAlpha = 0.0f;
}

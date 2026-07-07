package com.wayfarer.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.wayfarer.api.WayfarerRegistry;
import com.wayfarer.api.WayfarerRegistry.Waypoint;
import com.wayfarer.config.WayfarerConfig;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;

public class LocatorBarRenderer {
    private static final ResourceLocation DOT_ICON = ResourceLocation.fromNamespaceAndPath("wayfarer",
            "hud/locator_bar_dot/default_0");
    private final Minecraft minecraft;

    public LocatorBarRenderer(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        if (this.minecraft.player == null || this.minecraft.player.isCreative() || this.minecraft.player.isSpectator())
            return;

        float partialTick = deltaTracker.getGameTimeDeltaTicks();
        int width = graphics.guiWidth();
        int height = graphics.guiHeight();
        int y = height - 29;
        int centerX = width / 2;
        int barWidth = 182;

        Collection<Waypoint> waypoints = WayfarerRegistry.getWaypoints();
        float playerYaw = this.minecraft.player.getViewYRot(partialTick);
        Vec3 cameraPos = this.minecraft.player.getEyePosition(partialTick);

        boolean showIcons = WayfarerConfig.showLocatorIcons == WayfarerConfig.LocatorVisibilityMode.ALWAYS ||
                (WayfarerConfig.showLocatorIcons == WayfarerConfig.LocatorVisibilityMode.HOLD
                        && WayfarerKeys.locatorBarAlpha > 0);

        graphics.pose().pushPose();
        graphics.pose().translate(0, 0, 1000);

        for (Waypoint wp : waypoints) {
            if (wp.locatorType == WayfarerRegistry.LocatorType.HIDDEN)
                continue;

            double dx = wp.pos.getX() + 0.5 - cameraPos.x;
            double dz = wp.pos.getZ() + 0.5 - cameraPos.z;

            float angle = (float) (Math.atan2(dz, dx) * (180 / Math.PI)) - 90.0f;
            float relativeYaw = Mth.wrapDegrees(angle - playerYaw);

            if (Math.abs(relativeYaw) < 90) {
                double distance = Math.sqrt(dx * dx + dz * dz);
                float distFactor;
                if (distance < 179)
                    distFactor = 1.0f;
                else if (distance < 230)
                    distFactor = 0.8f;
                else if (distance < 281)
                    distFactor = 0.7f;
                else
                    distFactor = 0.5f;

                float xOffset = (relativeYaw / 90.0f) * (barWidth / 2.0f);
                int iconX = (int) (centerX + xOffset) - 4;
                int iconY = y - 2;

                boolean hasIcon = wp.icon != null;
                boolean showIcon = showIcons && hasIcon;

                float dotAlpha = distFactor;
                float iconAlpha = 0.0f;

                if (showIcon) {
                    if (WayfarerConfig.showLocatorIcons == WayfarerConfig.LocatorVisibilityMode.ALWAYS) {
                        dotAlpha = 0.0f;
                        iconAlpha = distFactor;
                    } else if (WayfarerConfig.showLocatorIcons == WayfarerConfig.LocatorVisibilityMode.HOLD) {
                        dotAlpha = distFactor * (1.0f - WayfarerKeys.locatorBarAlpha);
                        iconAlpha = distFactor * WayfarerKeys.locatorBarAlpha;
                    }
                }

                if (dotAlpha > 0.0f) {
                    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, dotAlpha);
                    graphics.blitSprite(DOT_ICON, iconX, iconY, 8, 8);
                    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                }

                if (iconAlpha > 0.0f && wp.icon != null) {
                    if (wp.icon.getNamespace().equals("wayfarer") && wp.icon.getPath().equals("player")) {
                        renderPlayerHead(graphics, wp.name, iconX, iconY, iconAlpha);
                    } else {
                        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, iconAlpha);
                        graphics.blit(wp.icon, iconX, iconY, 8, 8, 0.0f, 0.0f, 16, 16, 16, 16);
                        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                    }
                }
            }
        }

        graphics.pose().popPose();
    }

    private void renderPlayerHead(GuiGraphics graphics, String playerName, int x, int y, float alpha) {
        if (this.minecraft.getConnection() == null)
            return;

        net.minecraft.client.multiplayer.PlayerInfo playerInfo = this.minecraft.getConnection()
                .getPlayerInfo(playerName);
        if (playerInfo != null) {
            ResourceLocation skin = playerInfo.getSkin().texture();
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
            graphics.blit(skin, x, y, 8.0f, 8.0f, 8, 8, 64, 64);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        }
    }
}

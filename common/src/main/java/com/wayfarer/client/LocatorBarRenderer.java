package com.wayfarer.client;

import com.wayfarer.api.WayfarerRegistry;
import com.wayfarer.api.WayfarerRegistry.Waypoint;
import com.wayfarer.config.WayfarerConfig;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
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
        if (this.minecraft.player == null)
            return;

        float partialTick = deltaTracker.getGameTimeDeltaTicks();
        int width = graphics.guiWidth();
        int height = graphics.guiHeight();
        int y = height - 29;
        int centerX = width / 2;
        int barWidth = 182;

        Collection<Waypoint> waypoints = WayfarerRegistry.getWaypoints();
        float playerYaw = this.minecraft.player.getYRot(partialTick);
        Vec3 cameraPos = this.minecraft.player.getEyePosition(partialTick);

        boolean showHeads = WayfarerConfig.showLocatorIcons == WayfarerConfig.LocatorVisibilityMode.ALWAYS ||
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
                int dotColor = ((int) (255 * distFactor) << 24) | 0xFFFFFF;
                graphics.blitSprite(RenderType::guiTextured, DOT_ICON, iconX, iconY, 8, 8, dotColor);

                if (showHeads && wp.icon != null && wp.icon.getNamespace().equals("wayfarer")
                        && wp.icon.getPath().equals("player")) {
                    float headAlpha = distFactor * WayfarerKeys.locatorBarAlpha;
                    renderPlayerHead(graphics, wp.name, iconX, iconY, headAlpha);
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
            int color = ((int) (alpha * 255) << 24) | 0xFFFFFF;
            graphics.blit(RenderType::guiTextured, skin, x, y, 8.0f, 8.0f, 8, 8, 8, 8, 64, 64, color);
        }
    }
}

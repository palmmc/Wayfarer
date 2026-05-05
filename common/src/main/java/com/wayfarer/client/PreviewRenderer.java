package com.wayfarer.client;

import com.wayfarer.util.AnimationHelper;
import com.wayfarer.config.WayfarerConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class PreviewRenderer {
    private static long lastResetTime = -1L;

    public static void resetAnimation() {
        lastResetTime = System.currentTimeMillis();
    }

    public static void render(GuiGraphicsExtractor graphics, int panelX, int panelY, int panelWidth) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen == null)
            return;

        if (lastResetTime == -1)
            return;

        long now = System.currentTimeMillis();
        long elapsed = now - lastResetTime;

        int inDur = Math.max(50, WayfarerConfig.waypointFadeInDuration);
        int outDur = Math.max(50, WayfarerConfig.waypointFadeOutDuration);
        int pause = 2000;
        int total = inDur + outDur + pause;

        long posInCycle = elapsed % total;
        float fade;

        if (posInCycle < inDur) {
            float p = (float) posInCycle / inDur;
            fade = AnimationHelper.applyCurve(p, WayfarerConfig.waypointFadeInCurve);
        } else if (posInCycle < inDur + pause) {
            fade = 1.0f;
        } else {
            float p = (float) (posInCycle - inDur - pause) / outDur;
            fade = AnimationHelper.applyCurve(1.0f - p, WayfarerConfig.waypointFadeOutCurve);
        }

        if (fade <= 1e-3f)
            return;

        float configScale = WayfarerConfig.waypointScale;
        float zoom = 1.0f / Math.max(1.0f, configScale / 1.35f);
        float finalWPScale = configScale * zoom;

        int alpha = (int) (255 * fade);
        int color = (alpha << 24) | 0xFFFFFF;
        int bgColor = (int) (0xB0 * fade) << 24;

        int centerX = panelX + panelWidth / 2;
        int baseY = panelY + 50;

        graphics.pose().pushMatrix();
        graphics.pose().translate((float) centerX, (float) baseY);
        graphics.pose().scale(finalWPScale, finalWPScale);

        // Draw player head icon for preview (default to steve if clientplayer skin is
        // inaccessible)
        Identifier skinPath = Identifier.fromNamespaceAndPath("minecraft", "textures/entity/player/wide/steve.png");
        if (mc.player instanceof net.minecraft.client.player.AbstractClientPlayer clientPlayer) {
            try {
                skinPath = clientPlayer.getSkin().body().texturePath();
            } catch (Exception e) {
            }
        }

        graphics.blit(RenderPipelines.GUI_TEXTURED, skinPath, -7, -20, 8.0f, 8.0f, 14, 14, 8, 8, 64, 64, color);

        // Draw waypoint preview label
        String labelText = String.format(WayfarerConfig.waypointLabelFormat, "Preview", 123);
        Component label = Component.literal(labelText);
        int labelWidth = mc.font.width(label);
        graphics.fill(-labelWidth / 2 - 4, -2, labelWidth / 2 + 4, 11, bgColor);
        graphics.text(mc.font, label, -labelWidth / 2, 0, color, false);

        graphics.pose().popMatrix();

        // Gotta have a chicken for scale
        int chickenSize = (int) (24 * zoom);
        int chickenX = panelX + panelWidth - chickenSize - 15;
        int chickenY = panelY + 100 - chickenSize - 20;

        Identifier chickenSprite = Identifier.fromNamespaceAndPath("wayfarer", "chicken");
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, chickenSprite, chickenX, chickenY, chickenSize, chickenSize,
                color);
    }
}

package com.wayfarer.mixin;

import com.wayfarer.api.WayfarerRegistry;
import com.wayfarer.api.WayfarerRegistry.LocatorType;
import com.wayfarer.api.WayfarerRegistry.Waypoint;
import com.wayfarer.config.WayfarerConfig;
import com.wayfarer.config.WayfarerConfig.LocatorVisibilityMode;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.contextualbar.LocatorBar;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.Identifier;
import com.wayfarer.client.LocatorBarRendererState;
import com.wayfarer.client.WayfarerKeys;

import java.util.Iterator;
import java.util.Map.Entry;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocatorBar.class)
public abstract class MixinLocatorBarRenderer {
    @Shadow(remap = false)
    @Final
    private Minecraft minecraft;

    @Shadow(remap = false)
    private static int VISIBLE_DEGREE_RANGE;

    @Unique
    private static final java.util.Map<String, LocatorBarRendererState> LOCATOR_STATES = new java.util.HashMap<>();
    @Unique
    private static long lastFrameTime_locator = 0;

    @Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true, remap = false)
    private void wayfarer$onExtractRenderState(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker,
            CallbackInfo ci) {
        Entity cameraEntity = this.minecraft.getCameraEntity();
        if (cameraEntity == null || this.minecraft.level == null)
            return;

        if (this.minecraft.player != null && (this.minecraft.player.isCreative() || this.minecraft.player.isSpectator())) {
            ci.cancel();
            return;
        }

        long now = System.currentTimeMillis();
        float deltaTime = lastFrameTime_locator == 0 ? 0 : (now - lastFrameTime_locator) / 1000f;
        lastFrameTime_locator = now;

        int left = ((LocatorBar) (Object) this).left(this.minecraft.getWindow());
        int top = ((LocatorBar) (Object) this).top(this.minecraft.getWindow());
        int barWidth = 182;
        int centerX = left + (barWidth / 2);

        float playerYaw = cameraEntity.getViewYRot(deltaTracker.getGameTimeDeltaTicks());
        float halfRange = 30.0F;

        for (LocatorBarRendererState state : LOCATOR_STATES.values()) {
            state.active = false;
        }

        for (Player player : this.minecraft.level.players()) {
            if (player == cameraEntity)
                continue;
            String key = "player_" + player.getUUID().toString();
            LocatorBarRendererState state = LOCATOR_STATES.computeIfAbsent(key, k -> new LocatorBarRendererState());
            state.update(player.getX(), player.getY(), player.getZ(), 0xFFFFFF, null, player,
                    LocatorType.STANDARD);
        }

        for (Waypoint waypoint : WayfarerRegistry.getWaypoints()) {
            if (waypoint.icon != null && waypoint.icon.getNamespace().equals("wayfarer")
                    && waypoint.icon.getPath().equals("player")) {
                continue;
            }
            String key = "wp_" + waypoint.name + "_" + waypoint.pos.toString() + "_"
                    + (waypoint.icon != null ? waypoint.icon.toString() : "");
            LocatorBarRendererState state = LOCATOR_STATES.computeIfAbsent(key, k -> new LocatorBarRendererState());
            state.update(waypoint.pos.getX() + 0.5, waypoint.pos.getY() + 0.5, waypoint.pos.getZ() + 0.5,
                    waypoint.color, waypoint.icon, null, waypoint.locatorType);
        }

        Iterator<Entry<String, LocatorBarRendererState>> it = LOCATOR_STATES.entrySet()
                .iterator();
        while (it.hasNext()) {
            Entry<String, LocatorBarRendererState> entry = it.next();
            LocatorBarRendererState state = entry.getValue();

            if (state.active) {
                state.alpha = Math.min(1.0f, state.alpha + deltaTime * 4.0f);
            } else {
                state.alpha = Math.max(0.0f, state.alpha - deltaTime * 3.0f);
            }

            if (state.alpha > 0) {
                wayfarer$renderPoint(graphics, deltaTracker, cameraEntity, playerYaw, halfRange, centerX, top, barWidth,
                        state.x, state.y, state.z,
                        state.color, state.icon, state.player, state.locatorType, state.alpha);
            }

            if (state.alpha <= 0 && !state.active) {
                it.remove();
            }
        }

        ci.cancel();
    }

    @Unique
    private static final Identifier SQUARE_SPRITE = Identifier.fromNamespaceAndPath("minecraft",
            "hud/locator_bar_dot/default_0");

    @Unique
    private void wayfarer$renderPoint(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, Entity camera,
            float playerYaw, float halfRange, int centerX, int top, int barWidth,
            double tx, double ty, double tz,
            int color, Identifier icon, Player player,
            LocatorType locatorType, float visibilityAlpha) {

        double dx = tx - camera.getX();
        double dz = tz - camera.getZ();
        double distance = Math.sqrt(dx * dx + dz * dz);

        if (WayfarerConfig.waypointViewDistance != -1 && distance > WayfarerConfig.waypointViewDistance) {
            return;
        }

        float modeAlpha;
        if (WayfarerConfig.showLocatorIcons == LocatorVisibilityMode.ALWAYS) {
            modeAlpha = 1.0f;
        } else if (WayfarerConfig.showLocatorIcons == LocatorVisibilityMode.HOLD) {
            modeAlpha = WayfarerKeys.locatorBarAlpha;
        } else {
            modeAlpha = 0.0f;
        }

        if (modeAlpha <= 0 || locatorType == LocatorType.HIDDEN) {
            return;
        }

        float targetYaw = (float) (Math.atan2(dz, dx) * (180 / Math.PI)) - 90.0F;
        float relativeYaw = Mth.wrapDegrees(targetYaw - playerYaw);

        if (Math.abs(relativeYaw) <= halfRange) {
            float normalized = relativeYaw / halfRange;
            int screenX = centerX + (int) (normalized * (barWidth / 2.0F));

            float distFactor;
            if (distance < 179)
                distFactor = 1.0f;
            else if (distance < 230)
                distFactor = 0.8f;
            else if (distance < 281)
                distFactor = 0.7f;
            else
                distFactor = 0.5f;

            distFactor *= (visibilityAlpha * modeAlpha);

            float dotAlphaMult = 1.0f;
            float iconAlphaMult = WayfarerKeys.locatorBarAlpha;
            int iconSize = 5;

            if (locatorType == LocatorType.HOVER) {
                dotAlphaMult = 1.0f - WayfarerKeys.locatorBarAlpha;
                iconAlphaMult = WayfarerKeys.locatorBarAlpha;
                iconSize = 9;
            } else if (locatorType == LocatorType.ICON) {
                dotAlphaMult = 0.0f;
                iconAlphaMult = 1.0f;
                iconSize = 9;
            }

            graphics.pose().pushMatrix();

            int alpha = (int) (255 * distFactor * dotAlphaMult);
            if (alpha > 0) {
                int baseColor = (alpha << 24) | (color & 0x00FFFFFF);
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SQUARE_SPRITE,
                        screenX - 4, top - 2, 9, 9, baseColor);
            }

            if (iconAlphaMult > 0) {
                int animatedAlpha = (int) (255 * distFactor * iconAlphaMult);
                int iconColor = (animatedAlpha << 24) | 0xFFFFFF;

                boolean isPlayerType = (icon != null && icon.getNamespace().equals("wayfarer")
                        && icon.getPath().equals("player"));

                int drawSize = (player != null || isPlayerType) ? 8 : iconSize;
                int halfSize = drawSize / 2;
                int offset = (drawSize == 9 || drawSize == 8) ? -2 : 0;
                int renderY = top + offset;

                if (player instanceof AbstractClientPlayer clientPlayer) {
                    Identifier skinPath = clientPlayer.getSkin().body().texturePath();
                    graphics.blit(RenderPipelines.GUI_TEXTURED, skinPath,
                            screenX - halfSize, renderY, 8.0f, 8.0f,
                            drawSize, drawSize, 8, 8, 64, 64, iconColor);
                } else if (isPlayerType
                        && this.minecraft.player instanceof AbstractClientPlayer localPlayer) {
                    Identifier skinPath = localPlayer.getSkin().body().texturePath();
                    graphics.blit(RenderPipelines.GUI_TEXTURED, skinPath,
                            screenX - halfSize, renderY, 8.0f, 8.0f,
                            drawSize, drawSize, 8, 8, 64, 64, iconColor);
                } else if (icon != null) {
                    graphics.blit(RenderPipelines.GUI_TEXTURED, icon,
                            screenX - halfSize, renderY, 0.0F, 0.0F,
                            iconSize, iconSize, 16, 16, 16, 16, iconColor);
                }
            }

            graphics.pose().popMatrix();
        }
    }
}

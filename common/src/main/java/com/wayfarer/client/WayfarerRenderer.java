package com.wayfarer.client;

import com.wayfarer.api.WayfarerRegistry;
import com.wayfarer.api.WayfarerRegistry.Waypoint;
import com.wayfarer.api.WayfarerRegistry.WaypointType;
import com.wayfarer.config.WayfarerConfig;
import com.wayfarer.config.WayfarerConfig.VisibilityMode;
import com.wayfarer.util.AnimationHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Camera;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Vector3fc;
import org.jetbrains.annotations.Nullable;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class WayfarerRenderer {
    private static final float BASE_SCALE_FACTOR = 0.006f;
    private static final Map<String, WaypointState> WAYPOINT_STATES = new HashMap<>();
    private static long lastFrameTime = 0;

    private static class WaypointState {
        Waypoint waypoint;
        float fadeProgress = 0f;
        float lookProgress = 0f;
        boolean active = false;

        WaypointState(Waypoint waypoint) {
            this.waypoint = waypoint;
        }
    }

    public static void render(PoseStack poseStack, Camera camera, MultiBufferSource bufferSource) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null)
            return;

        Vec3 cameraPos = camera.position();
        Vector3fc lookVec = camera.forwardVector();

        long currentTime = System.currentTimeMillis();
        float deltaTime = lastFrameTime == 0 ? 0 : (currentTime - lastFrameTime) / 1000f;
        lastFrameTime = currentTime;

        if (WayfarerConfig.showWaypointIcons == VisibilityMode.NEVER &&
                WayfarerConfig.showWaypointLabels == VisibilityMode.NEVER) {
            return;
        }

        for (WaypointState state : WAYPOINT_STATES.values()) {
            state.active = false;
        }

        for (Waypoint waypoint : WayfarerRegistry.getWaypoints()) {
            String key = waypoint.name + waypoint.pos.toString()
                    + (waypoint.icon != null ? waypoint.icon.toString() : "");
            WaypointState state = WAYPOINT_STATES.computeIfAbsent(key, k -> new WaypointState(waypoint));
            state.waypoint = waypoint;
            state.active = true;
        }

        float bScale = WayfarerConfig.waypointScale;

        Iterator<Map.Entry<String, WaypointState>> it = WAYPOINT_STATES.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, WaypointState> entry = it.next();
            WaypointState state = entry.getValue();
            Waypoint waypoint = state.waypoint;

            double dx = (waypoint.pos.getX() + 0.5) - cameraPos.x;
            double dy = (waypoint.pos.getY() + 2.0) - cameraPos.y;
            double dz = (waypoint.pos.getZ() + 0.5) - cameraPos.z;
            double distSq = dx * dx + dy * dy + dz * dz;
            double dist = Math.sqrt(distSq);

            if (state.active
                    && (WayfarerConfig.waypointViewDistance == -1 || dist <= WayfarerConfig.waypointViewDistance)) {
                float step = deltaTime * (1000f / Math.max(1, WayfarerConfig.waypointFadeInDuration));
                state.fadeProgress = Math.min(1.0f, state.fadeProgress + step);
            } else {
                float step = deltaTime * (1000f / Math.max(1, WayfarerConfig.waypointFadeOutDuration));
                state.fadeProgress = Math.max(0.0f, state.fadeProgress - step);
            }

            if (state.fadeProgress <= 0 && !state.active) {
                it.remove();
                continue;
            }

            float visualFade = AnimationHelper.applyCurve(state.fadeProgress,
                    state.active ? WayfarerConfig.waypointFadeInCurve : WayfarerConfig.waypointFadeOutCurve);

            if (visualFade <= 0)
                continue;

            double hitX = dx;
            double hitY = dy;
            double hitZ = dz;
            double threshold = 0.9985;

            if (waypoint.type == WaypointType.ICON
                    || waypoint.type == WaypointType.FOLDED) {
                hitY += (8.0 * dist * BASE_SCALE_FACTOR * bScale);
                threshold = 0.9997;
            }

            boolean isLookingAt = false;
            if (WayfarerKeys.LOCATOR_BAR_KEY.isDown()) {
                isLookingAt = true;
            } else if (dist > 0) {
                double hitDist = Math.sqrt(hitX * hitX + hitY * hitY + hitZ * hitZ);
                double dot = (hitX / hitDist) * lookVec.x() + (hitY / hitDist) * lookVec.y()
                        + (hitZ / hitDist) * lookVec.z();
                if (dot > threshold)
                    isLookingAt = true;
            }

            if (isLookingAt)
                state.lookProgress = Math.min(1.0f, state.lookProgress + deltaTime * 6.0f);
            else
                state.lookProgress = Math.max(0.0f, state.lookProgress - deltaTime * 4.0f);

            renderWaypointTag(poseStack, camera, bufferSource, waypoint.name, dx, dy, dz, distSq, waypoint.color,
                    waypoint.icon, state.lookProgress, visualFade, waypoint.type);
        }
    }

    private static void renderWaypointTag(PoseStack poseStack, Camera camera, MultiBufferSource consumers,
            String label, double dx, double dy, double dz, double distanceSq,
            int textColor, @Nullable Identifier icon, float lookProgress, float visualFade,
            WaypointType type) {

        Minecraft client = Minecraft.getInstance();
        Font font = client.font;

        double realDistance = Math.sqrt(distanceSq);
        double maxRenderDist = client.options.renderDistance().get() * 8.0;

        double rDx = dx, rDy = dy, rDz = dz, rScaleDist = realDistance;
        if (realDistance > maxRenderDist) {
            double multiplier = maxRenderDist / realDistance;
            rDx *= multiplier;
            rDy *= multiplier;
            rDz *= multiplier;
            rScaleDist = maxRenderDist;
        }

        poseStack.pushPose();
        poseStack.translate(rDx, rDy, rDz);
        poseStack.mulPose(Axis.YP.rotationDegrees(-camera.yRot()));
        poseStack.mulPose(Axis.XP.rotationDegrees(camera.xRot()));

        float bScale = WayfarerConfig.waypointScale;
        float curve = lookProgress * lookProgress * (3 - 2 * lookProgress);

        float scale = (float) (BASE_SCALE_FACTOR * Math.max(1.0, rScaleDist)) * bScale
                * Mth.lerp(curve, 1.0f, 1.33f)
                * Mth.lerp(visualFade, 0.75f, 1.0f);
        poseStack.scale(-scale, -scale, scale);

        if (icon != null && WayfarerConfig.showWaypointIcons != VisibilityMode.NEVER) {
            poseStack.pushPose();
            poseStack.translate(0, -8.0f, 0);
            renderIcon(poseStack, consumers, icon, 14f, curve, visualFade);
            poseStack.popPose();
        }

        if (type != WaypointType.ICON
                && WayfarerConfig.showWaypointLabels != VisibilityMode.NEVER) {
            boolean shouldRenderText = true;
            float textAlphaMultiplier = visualFade;
            float textYOffset = 0;

            if (WayfarerConfig.showWaypointLabels == VisibilityMode.HOVER) {
                if (lookProgress <= 0)
                    shouldRenderText = false;
                textAlphaMultiplier *= lookProgress;
            }

            if (type == WayfarerRegistry.WaypointType.FOLDED) {
                if (lookProgress <= 0)
                    shouldRenderText = false;
                textAlphaMultiplier *= lookProgress;
                textYOffset = Mth.lerp(curve, -8.0f, 0.0f);
            }

            if (shouldRenderText) {
                int r = (textColor >> 16) & 0xFF;
                int g = (textColor >> 8) & 0xFF;
                int b = textColor & 0xFF;
                int fR = (int) Mth.lerp(curve, r, Math.min(255, r + 60));
                int fG = (int) Mth.lerp(curve, g, Math.min(255, g + 60));
                int fB = (int) Mth.lerp(curve, b, Math.min(255, b + 60));

                int alphaText = (int) (255 * textAlphaMultiplier);
                int alphaDim = (int) (Mth.lerp(curve, 0x64, 0xA0) * textAlphaMultiplier);
                int alphaBg = (int) (0x80 * textAlphaMultiplier);

                int finalColor = (alphaText << 24) | (fR << 16) | (fG << 8) | fB;
                int dimColor = (alphaDim << 24) | (fR << 16) | (fG << 8) | fB;
                int finalBgColor = (alphaBg << 24);

                String textStr = (icon == null ? "⬥ " : "")
                        + String.format(WayfarerConfig.waypointLabelFormat, label, (int) realDistance);
                float hPath = -font.width(textStr) / 2f;
                Component text = Component.literal(textStr);

                poseStack.pushPose();
                poseStack.translate(0, textYOffset, 0);
                Matrix4f textMat = poseStack.last().pose();
                font.drawInBatch(text, hPath, 0, dimColor, false, textMat, consumers, Font.DisplayMode.SEE_THROUGH,
                        finalBgColor, 15728880);
                font.drawInBatch(text, hPath, 0, finalColor, false, textMat, consumers, Font.DisplayMode.NORMAL, 0,
                        15728880);
                poseStack.popPose();
            }
        }

        poseStack.popPose();
    }

    private static void renderIcon(PoseStack ps, MultiBufferSource consumers, Identifier icon, float size, float curve,
            float fadeProgress) {
        Minecraft client = Minecraft.getInstance();
        float half = size / 2f;
        int alphaSolid = (int) (255 * fadeProgress);

        Identifier finalIcon = icon;
        float u1 = 0, v1 = 0, u2 = 1, v2 = 1;

        if (icon.getNamespace().equals("wayfarer") && icon.getPath().equals("player") && client.player != null) {
            finalIcon = client.player.getSkin().body().texturePath();
            u1 = 8 / 64f;
            v1 = 8 / 64f;
            u2 = 16 / 64f;
            v2 = 16 / 64f;
        }

        Matrix4f mat = ps.last().pose();
        int alpha = (int) (Mth.lerp(curve, 100, 160) * fadeProgress);
        VertexConsumer b1 = consumers.getBuffer(RenderTypes.textSeeThrough(finalIcon));
        drawQuad(b1, mat, -half, half, 0, u1, v2, alpha, 255, 255, 255);
        drawQuad(b1, mat, half, half, 0, u2, v2, alpha, 255, 255, 255);
        drawQuad(b1, mat, half, -half, 0, u2, v1, alpha, 255, 255, 255);
        drawQuad(b1, mat, -half, -half, 0, u1, v1, alpha, 255, 255, 255);

        VertexConsumer b2 = consumers.getBuffer(RenderTypes.entityTranslucent(finalIcon));
        drawQuad(b2, mat, -half, half, 0, u1, v2, alphaSolid, 255, 255, 255);
        drawQuad(b2, mat, half, half, 0, u2, v2, alphaSolid, 255, 255, 255);
        drawQuad(b2, mat, half, -half, 0, u2, v1, alphaSolid, 255, 255, 255);
        drawQuad(b2, mat, -half, -half, 0, u1, v1, alphaSolid, 255, 255, 255);
    }

    private static void drawQuad(VertexConsumer b, Matrix4f mat, float x, float y, float z, float u, float v, int alpha,
            int r, int g, int ob) {
        b.addVertex(mat, x, y, z).setColor(r, g, ob, alpha).setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(15728880).setNormal(0f, 1f, 0f);
    }
}

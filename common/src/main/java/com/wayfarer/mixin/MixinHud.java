package com.wayfarer.mixin;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.contextualbar.LocatorBar;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.wayfarer.client.WayfarerKeys;

@Mixin(Hud.class)
public abstract class MixinHud {
    @Shadow(remap = false)
    @Final
    private Minecraft minecraft;
    @Unique
    private LocatorBar wayfarer$forcedLocatorBar;

    @Unique
    private long wayfarer$lastAnimationTime = -1L;

    // Handle locator bar rendering.
    @Inject(method = "extractRenderState", at = @At("TAIL"), remap = false)
    private void wayfarer$onExtractRenderState(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker,
            CallbackInfo ci) {
        long now = System.currentTimeMillis();
        if (this.wayfarer$lastAnimationTime == -1)
            this.wayfarer$lastAnimationTime = now;
        float delta = (now - this.wayfarer$lastAnimationTime) / 200.0f;
        this.wayfarer$lastAnimationTime = now;

        if (WayfarerKeys.LOCATOR_BAR_KEY.isDown()) {
            WayfarerKeys.locatorBarAlpha = Math.min(1.0f, WayfarerKeys.locatorBarAlpha + delta);
        } else {
            WayfarerKeys.locatorBarAlpha = Math.max(0.0f, WayfarerKeys.locatorBarAlpha - delta);
        }

        if (this.minecraft.level != null) {
            if (this.wayfarer$forcedLocatorBar == null) {
                this.wayfarer$forcedLocatorBar = new LocatorBar(this.minecraft);
            }

            if (WayfarerKeys.locatorBarAlpha > 0 && this.minecraft.player != null
                    && (this.minecraft.player.isCreative() || this.minecraft.player.isSpectator())) {
                this.wayfarer$forcedLocatorBar.extractBackground(graphics, deltaTracker);
            }

            this.wayfarer$forcedLocatorBar.extractRenderState(graphics, deltaTracker);
        }
    }
}

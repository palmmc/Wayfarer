package com.wayfarer.mixin;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.contextualbar.LocatorBarRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.wayfarer.client.WayfarerKeys;

@Mixin(Gui.class)
public abstract class MixinGui {
    @Shadow
    @Final
    private Minecraft minecraft;
    @Unique
    private LocatorBarRenderer wayfarer$forcedLocatorBar;

    @Unique
    private long wayfarer$lastAnimationTime = -1L;

    // Handle locator bar rendering.
    @Inject(method = "render", at = @At("TAIL"))
    private void wayfarer$onRender(net.minecraft.client.gui.GuiGraphics graphics, DeltaTracker deltaTracker,
            CallbackInfo ci) {
        long now = System.currentTimeMillis();
        if (this.wayfarer$lastAnimationTime == -1)
            this.wayfarer$lastAnimationTime = now;
        float delta = (now - this.wayfarer$lastAnimationTime) / 200.0f;
        this.wayfarer$lastAnimationTime = now;

        if (WayfarerKeys.isDownCustom()) {
            WayfarerKeys.locatorBarAlpha = Math.min(1.0f, WayfarerKeys.locatorBarAlpha + delta);
        } else {
            WayfarerKeys.locatorBarAlpha = Math.max(0.0f, WayfarerKeys.locatorBarAlpha - delta);
        }

        if (this.minecraft.level != null) {
            if (this.wayfarer$forcedLocatorBar == null) {
                this.wayfarer$forcedLocatorBar = new LocatorBarRenderer(this.minecraft);
            }

            if (WayfarerKeys.locatorBarAlpha > 0 && this.minecraft.player != null
                    && (this.minecraft.player.isCreative() || this.minecraft.player.isSpectator())) {
                try {
                    this.wayfarer$forcedLocatorBar.getClass()
                            .getMethod("renderBackground", graphics.getClass(), DeltaTracker.class)
                            .invoke(this.wayfarer$forcedLocatorBar, graphics, deltaTracker);
                } catch (Exception e) {
                }
            }

            try {
                this.wayfarer$forcedLocatorBar.getClass()
                        .getMethod("render", graphics.getClass(), DeltaTracker.class)
                        .invoke(this.wayfarer$forcedLocatorBar, graphics, deltaTracker);
            } catch (Exception e) {
            }
        }
    }
}

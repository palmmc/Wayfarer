package com.wayfarer.mixin;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.wayfarer.client.LocatorBarRenderer;
import com.wayfarer.client.WayfarerKeys;

@Mixin(value = Gui.class, priority = 2000)
public abstract class MixinGui {
    @Shadow
    @Final
    private Minecraft minecraft;
    @Unique
    private LocatorBarRenderer wayfarer$locatorBar;

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
            if (this.wayfarer$locatorBar == null) {
                this.wayfarer$locatorBar = new LocatorBarRenderer(this.minecraft);
            }

            this.wayfarer$locatorBar.render(graphics, deltaTracker);
        }
    }
}

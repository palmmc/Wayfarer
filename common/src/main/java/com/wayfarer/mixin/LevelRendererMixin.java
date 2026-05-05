package com.wayfarer.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import com.wayfarer.client.WayfarerRenderer;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {

    @Shadow
    @Final
    private RenderBuffers renderBuffers;

    @Inject(method = "renderLevel", at = @At("TAIL"))
    private void onRenderLevelTail(
            GraphicsResourceAllocator resourceAllocator,
            DeltaTracker deltaTracker,
            boolean renderBlockOutline,
            Camera camera,
            Matrix4f modelViewMatrix,
            Matrix4f projectionMatrix,
            Matrix4f positionMatrix,
            GpuBufferSlice gpuBufferSlice,
            Vector4f fogColor,
            boolean renderEntityOutlines,
            CallbackInfo ci) {

        PoseStack matrices = new PoseStack();
        matrices.last().pose().set(modelViewMatrix);
        MultiBufferSource.BufferSource bufferSource = this.renderBuffers.bufferSource();
        WayfarerRenderer.render(matrices, camera, bufferSource);
        bufferSource.endBatch();
    }
}

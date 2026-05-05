package com.wayfarer.mixin;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.state.BlockState;
import com.wayfarer.api.WayfarerRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin {

    @Inject(method = "setBlock", at = @At("HEAD"))
    private void onBlockUpdate(BlockPos pos, BlockState state, int flags, int maxUpdateDepth,
            CallbackInfoReturnable<Boolean> cir) {
        if (WayfarerRegistry.hasWaypointAt(pos)) {
            if (!(state.getBlock() instanceof BannerBlock)) {
                WayfarerRegistry.removeWaypointAt(pos);
            }
        }
    }
}

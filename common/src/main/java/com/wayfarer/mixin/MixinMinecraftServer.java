package com.wayfarer.mixin;

import com.wayfarer.server.PlayerLocatorManager;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {
    @Inject(method = "tickChildren", at = @At("TAIL"))
    private void wayfarer$onTick(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
        PlayerLocatorManager.tick((MinecraftServer) (Object) this);
    }
}

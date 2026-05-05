package com.wayfarer.platform;

import com.wayfarer.platform.services.IPlatformHelper;
import net.fabricmc.loader.api.FabricLoader;
import java.nio.file.Path;

public class FabricPlatformHelper implements IPlatformHelper {
    @Override
    public Path getConfigFolder() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public void sendToPlayer(net.minecraft.server.level.ServerPlayer player,
            net.minecraft.network.protocol.common.custom.CustomPacketPayload payload) {
        if (net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.canSend(player, payload.type())) {
            net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.send(player, payload);
        }
    }
}

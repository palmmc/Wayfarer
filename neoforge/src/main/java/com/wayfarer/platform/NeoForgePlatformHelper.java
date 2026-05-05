package com.wayfarer.platform;

import com.wayfarer.platform.services.IPlatformHelper;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.loading.FMLPaths;
import java.nio.file.Path;

public class NeoForgePlatformHelper implements IPlatformHelper {
    @Override
    public Path getConfigFolder() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public void sendToPlayer(ServerPlayer player,
            CustomPacketPayload payload) {
        if (player.connection.hasChannel(payload.type())) {
            player.connection.send(payload);
        }
    }
}

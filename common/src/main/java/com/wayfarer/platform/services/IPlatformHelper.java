package com.wayfarer.platform.services;

import java.nio.file.Path;

public interface IPlatformHelper {

    Path getConfigFolder();

    void sendToPlayer(net.minecraft.server.level.ServerPlayer player,
            net.minecraft.network.protocol.common.custom.CustomPacketPayload payload);
}

package com.wayfarer.platform;

import com.wayfarer.network.S2CWaypointPacket;
import com.wayfarer.platform.services.INetworkHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

import com.wayfarer.api.WayfarerRegistry.LocatorType;
import com.wayfarer.api.WayfarerRegistry.WaypointType;

public class NeoForgeNetworkHelper implements INetworkHelper {
    @Override
    public void sendWaypoint(ServerPlayer player, String name, BlockPos pos, ResourceLocation icon, int color,
            WaypointType type, LocatorType locatorType) {
        if (player.connection.hasChannel(S2CWaypointPacket.TYPE)) {
            PacketDistributor.sendToPlayer(player,
                    new S2CWaypointPacket(name, pos, icon, color, type, locatorType, false));
        }
    }

    @Override
    public void clearWaypoints(ServerPlayer player) {
        if (player.connection.hasChannel(S2CWaypointPacket.TYPE)) {
            PacketDistributor.sendToPlayer(player, new S2CWaypointPacket("", BlockPos.ZERO,
                    ResourceLocation.fromNamespaceAndPath("minecraft", "air"), 0, WaypointType.STANDARD,
                    LocatorType.STANDARD,
                    true));
        }
    }
}

package com.wayfarer.platform.services;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public interface INetworkHelper {

    void sendWaypoint(ServerPlayer player, String name, BlockPos pos, ResourceLocation icon, int color,
            com.wayfarer.api.WayfarerRegistry.WaypointType type,
            com.wayfarer.api.WayfarerRegistry.LocatorType locatorType);

    void clearWaypoints(ServerPlayer player);
}

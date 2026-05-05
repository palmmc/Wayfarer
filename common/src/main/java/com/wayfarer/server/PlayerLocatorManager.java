package com.wayfarer.server;

import com.wayfarer.api.WayfarerRegistry;
import com.wayfarer.api.WayfarerRegistry.LocatorType;
import com.wayfarer.api.WayfarerRegistry.WaypointType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.Random;

public class PlayerLocatorManager {
    private static final ResourceLocation PLAYER_ICON = ResourceLocation.fromNamespaceAndPath("wayfarer", "player");
    private static int tickCounter = 0;

    public static void tick(MinecraftServer server) {
        if (++tickCounter % 20 != 0)
            return;

        for (ServerPlayer viewer : server.getPlayerList().getPlayers()) {
            for (ServerPlayer target : server.getPlayerList().getPlayers()) {
                if (viewer == target)
                    continue;
                if (viewer.level() != target.level())
                    continue;

                BlockPos fuzzyPos = getFuzzyPos(target, viewer);
                WayfarerRegistry.sendWaypoint(viewer, target.getGameProfile().getName(), fuzzyPos,
                        PLAYER_ICON, 0xFFFFFF, WaypointType.LOCATOR_ONLY, LocatorType.STANDARD);
            }
        }
    }

    private static BlockPos getFuzzyPos(ServerPlayer target, ServerPlayer viewer) {
        double dx = target.getX() - viewer.getX();
        double dy = target.getY() - viewer.getY();
        double dz = target.getZ() - viewer.getZ();
        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
        double errorRadius = dist * 0.05;

        long seed = target.getUUID().getMostSignificantBits() ^ viewer.getUUID().getMostSignificantBits();
        Random random = new Random(seed);

        double time = target.getServer().getTickCount() / 2400.0;
        double offsetX = Math.sin(time + random.nextDouble() * 10) * errorRadius;
        double offsetZ = Math.cos(time + random.nextDouble() * 10) * errorRadius;

        return new BlockPos((int) (target.getX() + offsetX), (int) target.getY(), (int) (target.getZ() + offsetZ));
    }
}

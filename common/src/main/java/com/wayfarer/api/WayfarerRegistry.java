package com.wayfarer.api;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

import com.wayfarer.network.S2CWaypointPacket;
import com.wayfarer.platform.Services;

public class WayfarerRegistry {
    private static final List<WaypointProvider> PROVIDERS = new ArrayList<>();
    private static final List<WaypointTransformer> TRANSFORMERS = new ArrayList<>();

    public enum WaypointType {
        STANDARD,
        ICON,
        FOLDED
    }

    public enum LocatorType {
        STANDARD,
        HOVER,
        ICON,
        HIDDEN
    }

    public static void registerProvider(WaypointProvider provider) {
        PROVIDERS.add(provider);
    }

    public static void registerTransformer(WaypointTransformer transformer) {
        TRANSFORMERS.add(transformer);
    }

    public static final SimpleProvider STATIC_PROVIDER = new SimpleProvider();
    public static final SimpleProvider NETWORK_PROVIDER = new SimpleProvider();
    public static final SimpleProvider AUTOMATED_PROVIDER = new SimpleProvider();
    public static final SimpleProvider PREVIEW_PROVIDER = new SimpleProvider();

    static {
        PROVIDERS.add(STATIC_PROVIDER);
        PROVIDERS.add(NETWORK_PROVIDER);
        PROVIDERS.add(AUTOMATED_PROVIDER);
        PROVIDERS.add(PREVIEW_PROVIDER);
    }

    public static void registerWaypoint(String name, BlockPos pos, Identifier iconId, int color) {
        registerWaypoint(name, pos, iconId, color, WaypointType.STANDARD, LocatorType.STANDARD);
    }

    public static void registerWaypoint(String name, BlockPos pos, Identifier iconId, int color, WaypointType type) {
        registerWaypoint(name, pos, iconId, color, type, LocatorType.STANDARD);
    }

    public static void registerWaypoint(String name, BlockPos pos, Identifier iconId, int color, WaypointType type,
            LocatorType locatorType) {
        STATIC_PROVIDER.add(new Waypoint(name, pos, iconId, color, type, locatorType));
    }

    public static void sendWaypoint(ServerPlayer player, String name, BlockPos pos,
            Identifier icon, int color) {
        sendWaypoint(player, name, pos, icon, color, WaypointType.STANDARD, LocatorType.STANDARD);
    }

    public static void sendWaypoint(ServerPlayer player, String name, BlockPos pos,
            Identifier icon, int color, WaypointType type) {
        sendWaypoint(player, name, pos, icon, color, type, LocatorType.STANDARD);
    }

    public static void sendWaypoint(ServerPlayer player, String name, BlockPos pos,
            Identifier icon, int color, WaypointType type, LocatorType locatorType) {
        Services.PLATFORM.sendToPlayer(player,
                new S2CWaypointPacket(name, pos, icon, color, type, locatorType, false));
    }

    public static void clearWaypoints(ServerPlayer player) {
        Services.PLATFORM.sendToPlayer(player, new S2CWaypointPacket("", BlockPos.ZERO,
                Identifier.fromNamespaceAndPath("minecraft", "air"), 0, WaypointType.STANDARD, LocatorType.STANDARD,
                true));
    }

    public static void clearWaypoints() {
        STATIC_PROVIDER.clear();
        NETWORK_PROVIDER.clear();
    }

    public static Collection<Waypoint> getWaypoints() {
        List<Waypoint> all = new ArrayList<>();
        for (WaypointProvider provider : PROVIDERS) {
            for (Waypoint wp : provider.getWaypoints()) {
                for (WaypointTransformer transformer : TRANSFORMERS) {
                    wp = transformer.transform(wp);
                    if (wp == null)
                        break;
                }
                if (wp != null)
                    all.add(wp);
            }
        }
        return all;
    }

    public interface WaypointTransformer {
        Waypoint transform(Waypoint waypoint);
    }

    public interface WaypointProvider {
        Collection<Waypoint> getWaypoints();
    }

    public static class SimpleProvider implements WaypointProvider {
        private final List<Waypoint> waypoints = new ArrayList<>();

        public void add(Waypoint wp) {
            waypoints.add(wp);
        }

        public void clear() {
            waypoints.clear();
        }

        @Override
        public Collection<Waypoint> getWaypoints() {
            return waypoints;
        }
    }

    public static class Waypoint {
        public final String name;
        public final BlockPos pos;
        public final Identifier icon;
        public final int color;
        public final WaypointType type;
        public final LocatorType locatorType;

        public Waypoint(String name, BlockPos pos, Identifier icon, int color) {
            this(name, pos, icon, color, WaypointType.STANDARD, LocatorType.STANDARD);
        }

        public Waypoint(String name, BlockPos pos, Identifier icon, int color, WaypointType type) {
            this(name, pos, icon, color, type, LocatorType.STANDARD);
        }

        public Waypoint(String name, BlockPos pos, Identifier icon, int color, WaypointType type,
                LocatorType locatorType) {
            this.name = name;
            this.pos = pos;
            this.icon = icon;
            this.color = color;
            this.type = type;
            this.locatorType = locatorType;
        }
    }
}

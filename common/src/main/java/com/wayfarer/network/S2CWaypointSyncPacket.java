package com.wayfarer.network;

import com.wayfarer.api.WayfarerRegistry.Waypoint;
import com.wayfarer.api.WayfarerRegistry.WaypointType;
import com.wayfarer.api.WayfarerRegistry.LocatorType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import java.util.ArrayList;
import java.util.List;

public record S2CWaypointSyncPacket(List<Waypoint> waypoints) implements CustomPacketPayload {
    public static final Type<S2CWaypointSyncPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath("wayfarer", "waypoint_sync"));

    public static final StreamCodec<FriendlyByteBuf, S2CWaypointSyncPacket> STREAM_CODEC = StreamCodec.ofMember(
            S2CWaypointSyncPacket::write, S2CWaypointSyncPacket::decode);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeInt(waypoints.size());
        for (Waypoint wp : waypoints) {
            buf.writeUtf(wp.name);
            buf.writeBlockPos(wp.pos);
            buf.writeResourceLocation(wp.icon);
            buf.writeInt(wp.color);
            buf.writeEnum(wp.type);
            buf.writeEnum(wp.locatorType);
        }
    }

    public static S2CWaypointSyncPacket decode(FriendlyByteBuf buf) {
        int size = buf.readInt();
        List<Waypoint> waypoints = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            String name = buf.readUtf();
            BlockPos pos = buf.readBlockPos();
            ResourceLocation icon = buf.readResourceLocation();
            int color = buf.readInt();
            WaypointType type = buf.readEnum(WaypointType.class);
            LocatorType locType = buf.readEnum(LocatorType.class);
            waypoints.add(new Waypoint(name, pos, icon, color, type, locType));
        }
        return new S2CWaypointSyncPacket(waypoints);
    }
}

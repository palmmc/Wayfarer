package com.wayfarer.network;

import com.wayfarer.api.WayfarerRegistry.LocatorType;
import com.wayfarer.api.WayfarerRegistry.WaypointType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record S2CWaypointPacket(String name, BlockPos pos, ResourceLocation icon, int color, WaypointType waypointType,
        LocatorType locatorType, boolean clear) implements CustomPacketPayload {
    public static final Type<S2CWaypointPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath("wayfarer", "waypoint"));

    public static final StreamCodec<FriendlyByteBuf, S2CWaypointPacket> STREAM_CODEC = StreamCodec.ofMember(
            S2CWaypointPacket::write, S2CWaypointPacket::decode);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBoolean(clear);
        if (!clear) {
            buf.writeUtf(name);
            buf.writeBlockPos(pos);
            buf.writeResourceLocation(icon);
            buf.writeInt(color);
            buf.writeEnum(waypointType);
            buf.writeEnum(locatorType);
        }
    }

    public static S2CWaypointPacket decode(FriendlyByteBuf buf) {
        boolean clear = buf.readBoolean();
        if (clear)
            return new S2CWaypointPacket("", BlockPos.ZERO, ResourceLocation.fromNamespaceAndPath("minecraft", "air"),
                    0,
                    WaypointType.STANDARD, LocatorType.STANDARD, true);

        String name = buf.readUtf();
        BlockPos pos = buf.readBlockPos();
        ResourceLocation icon = buf.readResourceLocation();
        int color = buf.readInt();
        WaypointType type = buf.readEnum(WaypointType.class);
        LocatorType locType = buf.readEnum(LocatorType.class);
        return new S2CWaypointPacket(name, pos, icon, color, type, locType, false);
    }
}

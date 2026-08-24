package me.linstar.illusion.network;

import me.linstar.illusion.Illusion;
import net.caffeinemc.mods.sodium.client.render.SodiumWorldRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BoundIllusionDataS2CPacket implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<BoundIllusionDataS2CPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Illusion.MOD_ID, "bid"));

    static final StreamCodec<RegistryFriendlyByteBuf, List<IllusionDataS2CPacket>> PACKETS_CODEC =
            StreamCodec.of((buf, packets) -> packets.forEach((packet) -> packet.writeTo(buf)), (buf)->{
                List<IllusionDataS2CPacket> list = new ArrayList<>();
                while (buf.isReadable()){
                    list.add(new IllusionDataS2CPacket(buf));
                }

                return list;
            });

    public static final StreamCodec<RegistryFriendlyByteBuf, BoundIllusionDataS2CPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_LONG,
            (packet) -> packet.chunkPos.toLong(),
            PACKETS_CODEC,
            (packet) -> packet.packets,
            (l, p) -> new BoundIllusionDataS2CPacket(new ChunkPos(l), p)
    );

    private final ChunkPos chunkPos;
    private final List<IllusionDataS2CPacket> packets = new ArrayList<>();

    public BoundIllusionDataS2CPacket(ChunkPos chunkPos, List<IllusionDataS2CPacket> packets) {
        this.chunkPos = chunkPos;
        this.packets.addAll(packets);
    }

    public BoundIllusionDataS2CPacket(ChunkPos chunkPos) {
        this.chunkPos = chunkPos;
    }

    public void appendPacket(IllusionDataS2CPacket packet) {
        this.packets.add(packet);
    }


    public static void handle(final BoundIllusionDataS2CPacket payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            payload.packets.forEach((packet) -> packet.execute(false));

            ClientLevel level = Minecraft.getInstance().level;
            if (level == null) return;
            for (int y = level.getMinSection(); y < level.getMaxSection(); ++y) {
                SodiumWorldRenderer.instance().scheduleRebuildForChunk(payload.chunkPos.x, y, payload.chunkPos.z, true);
            }
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

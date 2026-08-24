package me.linstar.illusion.network;

import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import io.netty.buffer.ByteBuf;
import me.linstar.illusion.Illusion;
import me.linstar.illusion.attachment.IllusionChunkData;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record BlockEntityRequestC2SPayload(ChunkPos chunkPos) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<BlockEntityRequestC2SPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Illusion.MOD_ID, "beq"));

    public static final StreamCodec<ByteBuf, BlockEntityRequestC2SPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_LONG,
            (p) -> p.chunkPos().toLong(),
            (l) -> new BlockEntityRequestC2SPayload(new ChunkPos(l))
    );

    public static void handle(final BlockEntityRequestC2SPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();
            ServerLevel level = player.serverLevel();
            ChunkPos pos = payload.chunkPos;
            if (!level.hasChunk(pos.x, pos.z)) return;

            LevelChunk chunk = level.getChunk(pos.x, pos.z);
            IllusionChunkData chunkData = chunk.getData(Illusion.CHUNK_DATA);

            var packet = new BoundIllusionDataS2CPacket(pos);
            var keys = chunkData.getKeys();
            if (keys.isEmpty()) return;
            keys.forEach(key -> packet.appendPacket(new IllusionDataS2CPacket(key, chunkData.getData(key))));

            PacketDistributor.sendToPlayer(player, packet);
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

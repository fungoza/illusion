package me.linstar.illusion.network;

import me.linstar.illusion.Illusion;
import me.linstar.illusion.data.IllusionData;
import net.caffeinemc.mods.sodium.client.render.SodiumWorldRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record RemoveDataS2CPacket(BlockPos pos) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<RemoveDataS2CPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Illusion.MOD_ID, "rd"));
    public static final StreamCodec<FriendlyByteBuf, RemoveDataS2CPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_LONG,
            (p) -> p.pos().asLong(),
            (l) -> new RemoveDataS2CPacket(BlockPos.of(l))
    );

    public static void handle(final RemoveDataS2CPacket payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            var level = Minecraft.getInstance().level;
            if (level == null || !level.isClientSide) return;

            var blockEntity = level.getBlockEntity(payload.pos);
            if (blockEntity == null) return;

            var tag = blockEntity.getPersistentData();
            tag.remove(IllusionData.NAME);

            ChunkPos chunkPos = level.getChunkAt(blockEntity.getBlockPos()).getPos();
            for (int y = level.getMinSection(); y < level.getMaxSection(); ++y) {
                SodiumWorldRenderer.instance().scheduleRebuildForChunk(chunkPos.x, y, chunkPos.z, false);
            }
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

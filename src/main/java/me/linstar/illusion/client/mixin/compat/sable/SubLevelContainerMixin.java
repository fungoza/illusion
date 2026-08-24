package me.linstar.illusion.client.mixin.compat.sable;

import com.llamalad7.mixinextras.sugar.Local;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.platform.SablePlotPlatform;
import dev.ryanhcode.sable.sublevel.plot.ServerLevelPlot;
import me.linstar.illusion.network.BlockEntityRequestC2SPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ServerLevelPlot.class, remap = false)
public class SubLevelContainerMixin {
    @Redirect(
            method = "save",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/ryanhcode/sable/platform/SablePlotPlatform;writeChunkAttachments(Lnet/minecraft/nbt/CompoundTag;Lnet/minecraft/core/RegistryAccess;Lnet/minecraft/world/level/chunk/LevelChunk;)V"
            )
    )
    private void fixWriteChunkAttachments(SablePlotPlatform instance, CompoundTag error, RegistryAccess registryAccess, LevelChunk chunk, @Local(name = "chunkTag") CompoundTag chunkTag) {
        SablePlotPlatform.INSTANCE.writeChunkAttachments(chunkTag, registryAccess, chunk);
    }

    @Redirect(
            method = "save",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/ryanhcode/sable/platform/SablePlotPlatform;writeLightData(Lnet/minecraft/nbt/CompoundTag;Lnet/minecraft/core/RegistryAccess;Lnet/minecraft/world/level/chunk/LevelChunk;)V"
            )
    )
    private void fixWriteLightData(SablePlotPlatform instance, CompoundTag error, RegistryAccess registryAccess, LevelChunk chunk, @Local(name = "chunkTag") CompoundTag chunkTag) {
        SablePlotPlatform.INSTANCE.writeLightData(chunkTag, registryAccess, chunk);
    }
}

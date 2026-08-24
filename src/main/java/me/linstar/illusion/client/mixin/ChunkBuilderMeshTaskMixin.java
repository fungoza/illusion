package me.linstar.illusion.client.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import me.linstar.illusion.data.IllusionData;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.tasks.ChunkBuilderMeshingTask;
import net.caffeinemc.mods.sodium.client.world.LevelSlice;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ChunkBuilderMeshingTask.class, remap = false)
public class ChunkBuilderMeshTaskMixin {

    @WrapOperation(method = "execute(Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/ChunkBuildContext;Lnet/caffeinemc/mods/sodium/client/util/task/CancellationToken;)Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/ChunkBuildOutput;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getRenderShape()Lnet/minecraft/world/level/block/RenderShape;"))
    private RenderShape onGetRenderShape(BlockState instance, Operation<RenderShape> original, @Local(name = "slice") LevelSlice slice, @Local(name = "blockPos") BlockPos.MutableBlockPos blockPos){
        BlockEntity blockEntity = slice.getBlockEntity(blockPos);
        if (blockEntity != null){
            CompoundTag persistentData = blockEntity.getPersistentData();
            if (persistentData.contains(IllusionData.NAME)) {
                return RenderShape.MODEL;
            }
        }

        return original.call(instance);
    }
}

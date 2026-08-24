package me.linstar.illusion.client.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.linstar.illusion.Illusion;
import me.linstar.illusion.data.IllusionData;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;
import net.caffeinemc.mods.sodium.client.render.frapi.render.AbstractBlockRenderContext;
import net.caffeinemc.mods.sodium.client.services.PlatformModelAccess;
import net.caffeinemc.mods.sodium.client.services.SodiumModelData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(value = BlockRenderer.class, remap = false)
public abstract class SodiumBlockRendererMixin extends AbstractBlockRenderContext {

    @Shadow
    public abstract void renderModel(BakedModel model, BlockState state, BlockPos pos, BlockPos origin);

    @Unique
    @Nullable
    private IllusionData illusion$currentData;

    @Inject(method = "renderModel", at = @At("HEAD"), cancellable = true)
    private void onRenderStarted(BakedModel model, BlockState state, BlockPos pos, BlockPos origin, CallbackInfo ci){
        try {
            if (illusion$currentData != null) return;
            BlockEntity blockEntity = this.level.getBlockEntity(pos);
            if (blockEntity == null) return;
            CompoundTag persistentData = blockEntity.getPersistentData();
            if (!persistentData.contains(IllusionData.NAME)) return;
            this.illusion$currentData = new IllusionData(persistentData.getCompound(IllusionData.NAME), Minecraft.getInstance().level.registryAccess());
            this.renderModel(illusion$currentData.getModelData().getModel(), illusion$currentData.getModelData().getState(), pos, origin);
            ci.cancel();
        }catch (Exception e){
            Illusion.LOGGER.error("Error when rendering block", e);
        }
    }

    @Inject(method = "renderModel", at = @At("TAIL"))
    private void onRenderFinished(BakedModel model, BlockState state, BlockPos pos, BlockPos origin, CallbackInfo ci){
        this.illusion$currentData = null;
    }

    @WrapOperation(method = "renderModel", at = @At(value = "INVOKE", target = "Lnet/caffeinemc/mods/sodium/client/services/PlatformModelAccess;getModelRenderTypes(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/util/RandomSource;Lnet/caffeinemc/mods/sodium/client/services/SodiumModelData;)Ljava/lang/Iterable;"))
    private Iterable<RenderType> onGetRenderType(PlatformModelAccess instance, BlockAndTintGetter blockAndTintGetter, BakedModel model, BlockState state, BlockPos pos, RandomSource randomSource, SodiumModelData sodiumModelData, Operation<Iterable<RenderType>> original){
        if (this.illusion$currentData != null &&
                (this.illusion$currentData.getType().equals(IllusionData.DataType.CUSTOM_ITEM) || this.illusion$currentData.getType().equals(IllusionData.DataType.CUSTOM))){
            return new ArrayList<>(){{
                add(RenderType.CUTOUT);
            }};
        }
        return original.call(instance, blockAndTintGetter, model, state, pos, randomSource, sodiumModelData);
    }

    @WrapOperation(method = "renderModel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;hasOffsetFunction()Z"), remap = true)
    public boolean hasOffsetFunction(BlockState instance, Operation<Boolean> original){
        if (illusion$currentData != null) return true;
        return original.call(instance);
    }

    // Return the custom offset stored in the block entity.
    @WrapOperation(method = "renderModel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getOffset(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/Vec3;"), remap = true)
    public Vec3 getOffset(BlockState instance, BlockGetter blockGetter, BlockPos pos, Operation<Vec3> original){
        try {
            IllusionData data = this.illusion$currentData;
            if (data != null) return data.getOffset();
        }catch (Exception ignore){}

        return original.call(instance, blockGetter, pos);
    }
}

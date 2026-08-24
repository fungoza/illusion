package me.linstar.illusion.client;

import me.linstar.illusion.Illusion;
import me.linstar.illusion.item.MovementTool;
import me.linstar.illusion.network.BlockEntityRequestC2SPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Objects;

@EventBusSubscriber(value = Dist.CLIENT)
public class IllusionClient {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(()-> ItemProperties.register(Illusion.MOVEMENT_TOOL.get(), Objects.requireNonNull(ResourceLocation.tryBuild(Illusion.MOD_ID, MovementTool.STATE)), (itemstack, world, entity, idk) -> itemstack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getInt(MovementTool.STATE)));
    }

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event){
        ChunkPos pos = event.getChunk().getPos();
        if (Minecraft.getInstance().getConnection() == null) return;
        PacketDistributor.sendToServer(new BlockEntityRequestC2SPayload(pos));
    }
}

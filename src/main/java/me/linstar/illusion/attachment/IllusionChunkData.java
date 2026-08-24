package me.linstar.illusion.attachment;

import me.linstar.illusion.data.IllusionData;
import net.minecraft.core.BlockPos;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class IllusionChunkData{
    public static final String NAME = "illusion_chunk_data";

    final Map<BlockPos, IllusionData> data = new ConcurrentHashMap<>();

    public void updateData(BlockPos pos, IllusionData data) {
        this.data.put(pos, data);
    }

    public void deleteData(BlockPos pos) {
        this.data.remove(pos);
    }

    public IllusionData getData(BlockPos pos) {
        return this.data.get(pos);
    }

    public Collection<BlockPos> getKeys() {
        return data.keySet();
    }
//
//    @SubscribeEvent
//    public static void onChunkSave(ChunkDataEvent.Save event) {
//        ChunkAccess chunk = event.getChunk();
//
//        IllusionChunkData posData = chunk.hasData(Illusion.CHUNK_DATA.get())
//                ? chunk.getData(Illusion.CHUNK_DATA.get())
//                : chunk.setData(Illusion.CHUNK_DATA.get(), new IllusionChunkData());
//
//        CompoundTag chunkNBT = event.getData();
//        CompoundTag modData = posData.serializeNBT();
//        chunkNBT.put("yourmodid_chunk_pos_data", modData);
//    }
}

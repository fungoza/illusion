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
}

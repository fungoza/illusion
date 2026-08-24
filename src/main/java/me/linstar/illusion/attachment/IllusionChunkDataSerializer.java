/*
 * Copyright (c) Linstar 2026.
 */

package me.linstar.illusion.attachment;

import me.linstar.illusion.data.IllusionData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class IllusionChunkDataSerializer implements IAttachmentSerializer<CompoundTag, IllusionChunkData> {
    static final String POS = "Pos";
    static final String DATA = "Data";


    @Override
    public @NotNull IllusionChunkData read(@NotNull IAttachmentHolder iAttachmentHolder, @NotNull CompoundTag compoundTag, HolderLookup.@NotNull Provider provider) {
        IllusionChunkData chunkData = new IllusionChunkData();
        ListTag listTag = compoundTag.getList(DATA, ListTag.TAG_COMPOUND);
        listTag.forEach(tag -> {
            CompoundTag data = (CompoundTag) tag;
            BlockPos pos = BlockPos.of(data.getLong(POS));
            IllusionData illusionData = new IllusionData(data.getCompound(DATA), provider);
            chunkData.updateData(pos, illusionData);
        });

        return chunkData;
    }

    @Override
    public @Nullable CompoundTag write(@NotNull IllusionChunkData data, HolderLookup.@NotNull Provider provider) {
        CompoundTag nbt = new CompoundTag();

        ListTag listTag = new ListTag();
        Collection<BlockPos> posSet = data.getKeys();
        posSet.forEach(pos -> {
            CompoundTag tag = new CompoundTag();
            tag.putLong(POS, pos.asLong());
            tag.put(DATA, data.getData(pos).save(provider));
            listTag.add(tag);
        });

        nbt.put(DATA, listTag);
        return nbt;
    }
}

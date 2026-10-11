package top.dext.centurybridge.shims.v26_2;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.storage.TagValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** The compound a tag-backed ValueOutput writes into (CbNbtStorage). */
@Mixin(TagValueOutput.class)
public interface TagValueOutputAccessor {

    @Accessor("output")
    CompoundTag cbOutput();
}

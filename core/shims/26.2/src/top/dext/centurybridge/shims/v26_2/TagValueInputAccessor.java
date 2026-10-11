package top.dext.centurybridge.shims.v26_2;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.storage.TagValueInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** The compound a tag-backed ValueInput reads from (CbNbtStorage). */
@Mixin(TagValueInput.class)
public interface TagValueInputAccessor {

    @Accessor("input")
    CompoundTag cbInput();
}

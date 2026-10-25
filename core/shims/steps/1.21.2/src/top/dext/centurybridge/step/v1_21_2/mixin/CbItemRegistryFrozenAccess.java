package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_2370;
import net.minecraft.class_6862;
import net.minecraft.class_6885;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemRegistries;

/**
 * MappedRegistry's frozen flag: which lookup resolves a tag now (CbItemTags.lookup); its private createTag(tag) for
 * tags a mod's bindTags mixin adds (CbItemRegistries.bindTags)
 */
@Mixin(class_2370.class)
public interface CbItemRegistryFrozenAccess extends CbItemRegistries.CreateTag {

    @Accessor("field_36463")
    boolean cb$frozen();

    @Override
    @SuppressWarnings("rawtypes")
    @Invoker("method_40562")
    class_6885.class_6888 cb$createTag(class_6862 tag);
}

package top.dext.centurybridge.step.v1_21_2.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import top.dext.centurybridge.step.v1_21_2.rt.CbLevelCarving;

/** ImposterProtoChunk.allowWrites, which 1.21.1's carving masks of an imposter checked (CbLevelCarving) */
@Mixin(net.minecraft.class_2821.class)
public interface CbLevelCarvingImposterAccess extends CbLevelCarving.ImposterState {

    @Override
    @Accessor("field_34554")
    boolean cb$allowWrites();
}

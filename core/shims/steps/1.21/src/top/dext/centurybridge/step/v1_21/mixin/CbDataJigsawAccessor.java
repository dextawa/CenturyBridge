package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** JigsawStructure.poolAliases: the field 1.21 kept, the getter 1.20.6 had (CbData.method_54521) */
@Mixin(net.minecraft.class_5434.class)
public interface CbDataJigsawAccessor {

    @Accessor("field_46829")
    java.util.List<net.minecraft.class_8889> cb$poolAliases();
}

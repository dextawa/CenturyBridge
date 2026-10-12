package top.dext.centurybridge.step.v1_20_3.mixin;

import com.google.common.collect.Multimap;
import net.minecraft.class_58;
import net.minecraft.class_8942;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_20_3.rt.CbWorldLoot;

/** 1.20.2's ValidationContext.getProblems() (a cover): the problems reported so far (CbWorldLoot.problemsOf) */
@Mixin(class_58.class)
public abstract class CbWorldValidationContextMixin {

    @Shadow
    @Final
    private class_8942 field_47427;

    public Multimap<String, String> method_361() {
        return CbWorldLoot.problemsOf(this.field_47427);
    }
}

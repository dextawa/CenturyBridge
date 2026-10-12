package top.dext.centurybridge.step.v1_20_3.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import top.dext.centurybridge.step.v1_20_3.rt.CbGameTest;

/**
 * StructureBlockEntity's author field, written by the 1.20.2 structure loading CbGameTest ports
 * (1.20.2 set it from the template; 1.20.3 has no setter for it).
 */
@Mixin(net.minecraft.class_2633.class)
public interface CbGameTestStructureBlockAccessor extends CbGameTest.StructureAuthor {

    @Override
    @Accessor("field_12104")
    void cb$setAuthor(String author);
}

package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * A provided tag builder's TagBuilder (private in vanilla; Fabric API's FabricTagBuilder shares its parent's). The
 * builder class is protected in TagProvider, so the target is named, not referenced.
 */
@Mixin(targets = "net.minecraft.class_2474$class_5124")
public interface CbFapiProvidedTagBuilderAccess {

    @Accessor("field_23960")
    net.minecraft.class_3495 cb$builder();
}

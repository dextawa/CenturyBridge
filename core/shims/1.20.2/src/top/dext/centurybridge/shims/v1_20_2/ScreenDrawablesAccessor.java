package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Screen's drawable list, for the widgets-only super.render bridge (see Statics.cbRenderWidgets). */
@Mixin(net.minecraft.class_437.class)
public interface ScreenDrawablesAccessor {
    @Accessor("field_33816")
    java.util.List<net.minecraft.class_4068> cbDrawables();
}

package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** DataComponentMap.Builder's map: what an Item.Properties has been given so far (rt/CbItemProps) */
@Mixin(net.minecraft.class_9323.class_9324.class)
public interface CbComponentBuilderAccessor {

    /** map */
    @Accessor("field_49587")
    it.unimi.dsi.fastutil.objects.Reference2ObjectMap<net.minecraft.class_9331<?>, Object> cbS4$map();
}

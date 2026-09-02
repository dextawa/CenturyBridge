package top.dext.centurybridge.shims.v1_21_4;

import it.unimi.dsi.fastutil.objects.Object2IntSortedMap;
import net.minecraft.class_1792;
import net.minecraft.class_9895;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** FuelValues.Builder's value map: vanilla only removes by tag, the old FuelRegistry removed single items (see CbFuelRegistry). */
@Mixin(class_9895.class_9896.class)
public interface FuelBuilderAccessor {

    @Accessor("field_52638")
    Object2IntSortedMap<class_1792> cbValues();
}

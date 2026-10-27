package cb.s11d.mixin;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.minecraft.class_2960;
import net.minecraft.class_5294;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** 1.21.10's private DimensionSpecialEffects.EFFECTS (field_24609) */
@Mixin(class_5294.class)
public interface EffectsAccess {
    @Accessor("field_24609")
    static Object2ObjectMap<class_2960, class_5294> cb$effects() {
        throw new AssertionError();
    }
}

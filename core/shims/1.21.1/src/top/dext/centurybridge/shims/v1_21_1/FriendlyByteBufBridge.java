package top.dext.centurybridge.shims.v1_21_1;

import net.minecraft.class_2487;
import net.minecraft.class_2520;
import net.minecraft.class_2540;
import org.spongepowered.asm.mixin.Mixin;

/**
 * 1.20.1 -> 1.20.2: writeNbt narrowed-parameter overload.
 * Old: method_10794(CompoundTag); new: method_10794(Tag). Work order: L2 x44 mods.
 */
@Mixin(class_2540.class)
public abstract class FriendlyByteBufBridge {

    public class_2540 method_10794(class_2487 nbt) {
        return ((class_2540) (Object) this).method_10794((class_2520) nbt);
    }

    /** removed unlimited-size NBT read; delegates to the standard read (size-cap approximation) */
    public class_2487 method_30617() {
        return ((class_2540) (Object) this).method_10798();
    }
}

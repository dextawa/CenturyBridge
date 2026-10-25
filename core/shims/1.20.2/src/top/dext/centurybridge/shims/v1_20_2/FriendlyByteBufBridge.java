package top.dext.centurybridge.shims.v1_20_2;

import net.minecraft.class_2487;
import net.minecraft.class_2520;
import net.minecraft.class_2540;
import org.spongepowered.asm.mixin.Mixin;

/**
 * 1.20.1 -> 1.20.2: writeNbt narrowed-parameter overload.
 * Old: method_10794(CompoundTag); new: method_10794(Tag). Work order: L2 x44 mods.
 */
@Mixin(value = class_2540.class, priority = top.dext.centurybridge.rt.v1_20_2.CbVanillaReads.PRIORITY)
public abstract class FriendlyByteBufBridge {

    public class_2540 method_10794(class_2487 nbt) {
        return ((class_2540) (Object) this).method_10794((class_2520) nbt);
    }

    /**
     * readUnlimitedNbt(): 1.20.1 read with NbtTagSizeTracker.EMPTY (no size limit); 1.20.2's
     * unlimited accounting is NbtTagSizeTracker.ofUnlimitedBytes(), and its read answers any root tag,
     * held here to 1.20.1's compound-or-null.
     */
    public class_2487 method_30617() {
        return top.dext.centurybridge.rt.v1_20_2.CbNet.cbNbtCompound(
            ((class_2540) (Object) this).method_30616(net.minecraft.class_2505.method_53898()));
    }
}

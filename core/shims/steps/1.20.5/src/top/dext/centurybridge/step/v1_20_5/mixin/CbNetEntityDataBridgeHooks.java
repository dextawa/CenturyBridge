package top.dext.centurybridge.step.v1_20_5.mixin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.class_2945;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * The injectors into 1.20.5's own SynchedEntityData loops that go with {@link CbNetEntityDataBridge} (its restored
 * 1.20.4 members stay there, at {@code CbVanillaReads.PRIORITY}, so a mod's 1.20.4 injectors can anchor inside them):
 * an id that is allocated but not (yet) defined is taken the way 1.20.4's map did.
 */
@Mixin(class_2945.class)
public abstract class CbNetEntityDataBridgeHooks {

    /** the item array (CbNetEntityDataBridge's define grows it, hence @Mutable on both) */
    @Shadow
    @Final
    @Mutable
    private class_2945.class_2946<?>[] field_13331;

    /** packDirty(): an undefined id is never dirty (1.20.4 iterated defined items only) */
    @Redirect(method = "method_12781", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_2945$class_2946;method_12796()Z"))
    private boolean cb$dirtyIfDefined(class_2945.class_2946<?> item) {
        return item != null && item.method_12796();
    }

    /** getNonDefaultValues(): an undefined id has no value to send (1.20.4 iterated defined items only) */
    @Redirect(method = "method_46357", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_2945$class_2946;method_46358()Z"))
    private boolean cb$defaultIfUndefined(class_2945.class_2946<?> item) {
        return item == null || item.method_46358();
    }

    /**
     * assignValues(values): 1.20.4 skipped a value for an id this side has not defined -- no assignment,
     * no per-item notification -- and still told the entity about the whole list. Only the per-item
     * loop sees the filtered values; the closing onSyncedDataUpdated(list) gets the list as handed in.
     */
    @Redirect(method = "method_12779", at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;"))
    private Iterator<class_2945.class_7834<?>> cb$definedValues(List<class_2945.class_7834<?>> values) {
        List<class_2945.class_7834<?>> defined = new ArrayList<>(values.size());
        for (class_2945.class_7834<?> v : values) {
            if (v.comp_1115() < this.field_13331.length && this.field_13331[v.comp_1115()] != null) {
                defined.add(v);
            }
        }
        return defined.iterator();
    }
}

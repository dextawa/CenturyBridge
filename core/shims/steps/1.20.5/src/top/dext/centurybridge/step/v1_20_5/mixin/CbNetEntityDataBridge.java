package top.dext.centurybridge.step.v1_20_5.mixin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.minecraft.class_2940;
import net.minecraft.class_2945;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_20_5.rt.CbNetEntityData;
import top.dext.centurybridge.step.v1_20_5.rt.CbNetEntityDataAccess;
import top.dext.centurybridge.step.v1_20_5.rt.CbNetEntityDataBuilderAccess;

/**
 * SynchedEntityData: 1.20.4's define / hasItem / isEmpty come back, and 1.20.5's own loops over
 * the item array take an id that is allocated but not (yet) defined the way 1.20.4's map did --
 * see {@link CbNetEntityData} for the whole contract. A slot is empty only for such an id: 1.20.5
 * itself never leaves one.
 */
@Mixin(class_2945.class)
public abstract class CbNetEntityDataBridge implements CbNetEntityDataAccess {

    @Shadow
    @Final
    @Mutable
    private class_2945.class_2946<?>[] field_13331;

    /** while the entity is being constructed: the builder this object shares its item array with */
    @Unique
    private class_2945.class_9222 cb$builder;

    @Override
    public void cb$attachBuilder(class_2945.class_9222 builder) {
        this.cb$builder = builder;
    }

    @Override
    public void cb$detachBuilder() {
        this.cb$builder = null;
    }

    /**
     * was define(accessor, value): 1.20.4's checks, then the item -- not dirty, nobody told, as 1.20.4
     * put it in its map. An id past the slots allocated for the entity's class grows the array
     * (1.20.4's map took any id up to 254); during construction the builder goes on with the grown one.
     */
    public <T> void method_12784(class_2940<T> accessor, T value) {
        CbNetEntityData.cbCheckDefine(this.field_13331, accessor);
        int id = accessor.comp_2327();
        if (id >= this.field_13331.length) {
            this.field_13331 = Arrays.copyOf(this.field_13331, id + 1);
            if (this.cb$builder != null) {
                ((CbNetEntityDataBuilderAccess) this.cb$builder).cb$replaceItems(this.field_13331);
            }
        }
        this.field_13331[id] = new class_2945.class_2946<>(accessor, value);
    }

    /** was hasItem(accessor): whether the id is defined */
    public <T> boolean method_51696(class_2940<T> accessor) {
        int id = accessor.comp_2327();
        return id < this.field_13331.length && this.field_13331[id] != null;
    }

    /** was isEmpty(): no id defined */
    public boolean method_12790() {
        for (class_2945.class_2946<?> item : this.field_13331) {
            if (item != null) {
                return false;
            }
        }
        return true;
    }

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

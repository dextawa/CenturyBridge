package top.dext.centurybridge.shims.v1_20_2;

import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.class_18;
import net.minecraft.class_2487;
import net.minecraft.class_26;
import org.spongepowered.asm.mixin.Mixin;

/**
 * 1.20.1 -> 1.20.2: DimensionDataStorage.computeIfAbsent now takes a
 * SavedData.Factory record. Old (load, ctor, id) overload wraps into the
 * record with a null DataFixTypes -- matching the old no-datafix semantics.
 * Work order: L2 x23.
 */
@Mixin(class_26.class)
public abstract class DimensionDataStorageBridge {

    public <T extends class_18> T method_17924(Function<class_2487, T> load, Supplier<T> ctor, String id) {
        return ((class_26) (Object) this).method_17924(new class_18.class_8645<>(ctor, load, null), id);
    }

    /** legacy get(load, id): the new Factory wants a constructor too, but the get path never uses it */
    public <T extends class_18> T method_20786(Function<class_2487, T> load, String id) {
        return ((class_26) (Object) this).method_20786(new class_18.class_8645<>(() -> {
            throw new IllegalStateException("CenturyBridge: legacy get() cannot construct fresh state for " + id);
        }, load, null), id);
    }
}

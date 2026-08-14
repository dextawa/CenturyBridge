package cb;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import org.spongepowered.asm.mixin.Mixin;

/**
 * CenturyBridge signature bridges for the 1.20.4->1.20.5+ / 1.21.5 CompoundTag
 * rework. Each method re-adds a pre-components-era overload; the JVM resolves
 * old callsites by descriptor, so retargeted 1.20.1 jars link without edits.
 */
@Mixin(CompoundTag.class)
public abstract class CompoundTagBridge {

    private CompoundTag cb$self() {
        return (CompoundTag) (Object) this;
    }

    public String getString(String key) {
        return cb$self().getStringOr(key, "");
    }

    public boolean getBoolean(String key) {
        return cb$self().getBooleanOr(key, false);
    }

    public CompoundTag getCompound(String key) {
        return cb$self().getCompoundOrEmpty(key);
    }

    // old signature carried an NBT type id; modern getList is untyped
    public ListTag getList(String key, int elementType) {
        return cb$self().getListOrEmpty(key);
    }

    // old remove() returned void; modern returns the removed Tag
    public void remove(String key) {
        cb$self().remove(key);
    }

    // intermediary-era typed contains (method_10573) died before 1.21.11 and
    // keeps its raw name in retargeted jars; semantics approximated (presence only)
    public boolean method_10573(String key, int type) {
        return cb$self().contains(key);
    }
}

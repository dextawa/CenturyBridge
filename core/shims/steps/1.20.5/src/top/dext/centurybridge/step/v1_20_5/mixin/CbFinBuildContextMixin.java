package top.dext.centurybridge.step.v1_20_5.mixin;

import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.class_2378;
import net.minecraft.class_5321;
import net.minecraft.class_7157;
import net.minecraft.class_7225;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_20_5.rt.CbFinLookups;

/**
 * CommandBuildContext: 1.20.4's holderLookup(key) back as a default answering 1.20.5's
 * lookupOrThrow(key) (1.20.5's contexts already filter by the enabled features, as 1.20.4's did in
 * holderLookup), and defaults for the Provider methods a 1.20.4 implementation -- which declares only
 * holderLookup -- lacks, answered through its holderLookup (CbFinLookups). 1.20.5's own contexts
 * implement those methods and keep theirs.
 */
@Mixin(class_7157.class)
public interface CbFinBuildContextMixin {

    /** was holderLookup(key) */
    default <T> class_7225<T> method_41699(class_5321<? extends class_2378<T>> key) {
        return ((class_7157) this).method_46762(key);
    }

    /** Provider.listRegistries() of a 1.20.4 context */
    default Stream<class_5321<? extends class_2378<?>>> method_55282() {
        return CbFinLookups.cbRegistryKeys();
    }

    /** Provider.lookup(key) of a 1.20.4 context: its holderLookup(key) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    default <T> Optional<class_7225.class_7226<T>> method_46759(class_5321<? extends class_2378<? extends T>> key) {
        return CbFinLookups.cbLookup(key, (class_7225<T>) this.method_41699((class_5321) key));
    }
}

package top.dext.centurybridge.step.v1_21_9.mixin;

import java.util.Optional;
import net.minecraft.class_2378;
import net.minecraft.class_5819;
import net.minecraft.class_6862;
import net.minecraft.class_6880;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_9.rt.CbVanillaReads;

/**
 * 1.21.8's Registry.getRandomElementOf(tag, random), a default method 1.21.9 dropped (javap: absent from 1.21.9's
 * class_2378), restored as a default method of Registry with 1.21.8's body: getTag(tag).flatMap(set ->
 * set.getRandomElement(random)), both still in 1.21.9 (method_46733, HolderSet.Named.getRandomElement method_40243).
 * A 1.21.8 call links to it, a 1.21.8 registry class overrides it and reaches it by super. 1.21.9 never calls it.
 */
@Mixin(value = class_2378.class, priority = CbVanillaReads.PRIORITY)
public interface CbRegistryRandomRestoredMixin<T> {

    /** Registry.getRandomElementOf(tag, random) */
    default Optional<class_6880<T>> method_56159(class_6862<T> tag, class_5819 random) {
        @SuppressWarnings("unchecked")
        class_2378<T> self = (class_2378<T>) (Object) this;
        return self.method_46733(tag).flatMap(set -> set.method_40243(random));
    }
}

package top.dext.centurybridge.step.v1_21_9.rt;

import java.util.Optional;
import net.minecraft.class_2378;
import net.minecraft.class_5819;
import net.minecraft.class_6862;
import net.minecraft.class_6880;

/**
 * Registry.getRandomElementOf(tag, random), a default method 1.21.9 dropped (javap: absent from 1.21.9's class_2378):
 * 1.21.8's body is getTag(tag).flatMap(set -> set.getRandomElement(random)), both still in 1.21.9 (method_46733,
 * HolderSet.Named.getRandomElement method_40243). A layer cannot add a default method to a vanilla interface, so calls
 * come here (interfaceCallRedirects); a converted mod's override of it stays open.
 */
public final class CbRegistryRandom {

    private CbRegistryRandom() {
    }

    /** Registry.getRandomElementOf(tag, random) */
    public static <T> Optional<class_6880<T>> method_56159(class_2378<T> registry, class_6862<T> tag, class_5819 random) {
        return registry.method_46733(tag).flatMap(set -> set.method_40243(random));
    }
}

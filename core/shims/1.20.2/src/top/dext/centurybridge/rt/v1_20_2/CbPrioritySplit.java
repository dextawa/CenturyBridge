package top.dext.centurybridge.rt.v1_20_2;

/**
 * What a restoring layer mixin (at {@link CbVanillaReads#PRIORITY}) shares with the injectors into 1.20.2's own
 * methods that were split off it into a default-priority {@code <name>Hooks} mixin: the state both use, reached
 * through the restorer (its {@code @Unique} state is the restorer's own).
 */
public final class CbPrioritySplit {
    private CbPrioritySplit() {
    }

    /** CbFapiSimpleRegistryAliases: the alias map 1.20.1's lookups resolve through */
    public interface RegistryAliases {
        java.util.Map<net.minecraft.class_2960, net.minecraft.class_2960> cb$v1_20_2$aliases();
    }
}

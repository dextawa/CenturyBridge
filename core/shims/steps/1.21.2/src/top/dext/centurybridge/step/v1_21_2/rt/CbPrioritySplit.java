package top.dext.centurybridge.step.v1_21_2.rt;

/**
 * What a restoring layer mixin (at {@link CbVanillaReads#PRIORITY}) shares with the injectors into 1.21.2's own
 * methods that were split off it into a default-priority {@code <name>Hooks} mixin: the state and restored members both
 * use, reached through the restorer (its {@code @Unique} state is the restorer's own; its restored members are called
 * virtually, so a subclass's override still answers). Client-side ones: {@link CbPrioritySplitClient}.
 */
public final class CbPrioritySplit {
    private CbPrioritySplit() {
    }

    /** CbItemFoodDataMixin: 1.21.1's lastFoodLevel, set at the head of 1.21.2's tick */
    public interface FoodData {
        void cb$v1_21_2$lastFoodLevel(int level);
    }

    /** CbRestFapiRegistryAliasMixin: the alias map 0.116's lookups resolve through */
    public interface RegistryAliases {
        java.util.Map<net.minecraft.class_2960, net.minecraft.class_2960> cb$v1_21_2$aliases();
    }

    /** CbItemArmorMixin: the material and type 1.21.2's ArmorItem constructor was given (1.21.2 keeps neither) */
    public interface ArmorKeep {
        void cb$v1_21_2$keepArmor(net.minecraft.class_1741 material, net.minecraft.class_8051 type);
    }

    /** CbItemAnimalArmorMixin: the material and body type 1.21.2's AnimalArmorItem constructor was given */
    public interface AnimalArmorKeep {
        void cb$v1_21_2$keepAnimalArmor(net.minecraft.class_1741 material, net.minecraft.class_4059.class_9076 bodyType);
    }

    /** CbRestMinecraftServerMixin: 1.21.1's isSpawningAnimals, called virtually */
    public interface ServerSpawn {
        boolean cb$v1_21_2$isSpawningAnimals();
    }
}

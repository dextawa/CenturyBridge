package top.dext.centurybridge.step.v1_20_5.rt;

import top.dext.centurybridge.step.v1_20_5.facade.LootDataManager;

/**
 * 1.20.4's loot data entry points of the server on 1.20.5 (the loot data facades and their conversions
 * are CbLootData's): the server's and a resource set's LootDataManager (the covers of
 * mixin/CbRestMinecraftServerMixin and mixin/CbRestReloadableResourcesMixin), and the advancement manager
 * a 1.20.4 mod builds over a LootDataManager.
 */
public final class CbRestLoot {

    /**
     * new ServerAdvancementManager(lootData): 1.20.4 parsed advancements against the game's registries
     * and validated their loot references against the manager it was given; 1.20.5 takes one registry
     * lookup for both (createSerializationContext to parse, asGetterLookup to validate). So the lookup is
     * the running world's registries, except that the validation's getters of the loot registries
     * (loot tables, predicates, item modifiers) answer from the manager.
     */
    public static net.minecraft.class_7225.class_7874 advancementRegistries(LootDataManager manager) {
        net.minecraft.class_7225.class_7874 world = CbRegistries.current();
        net.minecraft.class_7871.class_7872 loot = CbLootData.lookup(manager);
        return new net.minecraft.class_7225.class_7874() {
            @Override
            public java.util.stream.Stream<net.minecraft.class_5321<? extends net.minecraft.class_2378<?>>> method_55282() {
                return world.method_55282();
            }

            @Override
            public <T> java.util.Optional<net.minecraft.class_7225.class_7226<T>> method_46759(
                    net.minecraft.class_5321<? extends net.minecraft.class_2378<? extends T>> key) {
                return world.method_46759(key);
            }

            @Override
            public net.minecraft.class_7871.class_7872 method_46758() {
                net.minecraft.class_7871.class_7872 getters = world.method_46758();
                return new net.minecraft.class_7871.class_7872() {
                    @Override
                    public <T> java.util.Optional<net.minecraft.class_7871<T>> method_46750(
                            net.minecraft.class_5321<? extends net.minecraft.class_2378<? extends T>> key) {
                        return isLoot(key) ? loot.method_46750(key) : getters.method_46750(key);
                    }
                };
            }
        };
    }

    /** whether a registry is one of the loot data types' (LootDataType.registryKey()) */
    private static boolean isLoot(net.minecraft.class_5321<?> registry) {
        return net.minecraft.class_8490.method_51215().anyMatch(type -> type.comp_2519().equals(registry));
    }

    private CbRestLoot() {
    }
}

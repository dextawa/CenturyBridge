package top.dext.centurybridge.step.v1_21_6.rt;

/**
 * 1.21.5's tag forms of the player's and containers' save data, restored on the classes with 1.21.5's bodies
 * (CbContainerSaveRestoredMixin, CbEnderSaveRestoredMixin, CbInventorySaveRestoredMixin, CbAbilitiesSaveRestoredMixin):
 * SimpleContainer.fromTag(ListTag, Provider) / createTag(Provider) (PlayerEnderChestContainer overrides both with
 * slots), Inventory.save(ListTag) / load(ListTag), Abilities.addSaveData(CompoundTag) / loadSaveData(CompoundTag).
 * 1.21.6 saves the same data through ValueOutput / ValueInput typed lists and Abilities.Packed (javap of both; the
 * same keys and entries), and never calls the 1.21.5 members. The routes (CbPlayerSaveRouteMixin,
 * CbInventoryCarrierRouteMixin) let 1.21.6's save and load run, then -- only when a converted mod mixed the restored
 * member ({@link CbMixedMembers}) or the object is a subclass (whose override 1.21.6 never asks) -- let the restored
 * member write its 1.21.5 form under the same key, or read the saved data again, as 1.21.5's callers did.
 */
public final class CbSaveData {

    private CbSaveData() {
    }

    public static final String CONTAINER_FROM_TAG = "net/minecraft/class_1277.method_7659(Lnet/minecraft/class_2499;Lnet/minecraft/class_7225$class_7874;)V";
    public static final String CONTAINER_CREATE_TAG = "net/minecraft/class_1277.method_7660(Lnet/minecraft/class_7225$class_7874;)Lnet/minecraft/class_2499;";
    public static final String ENDER_FROM_TAG = "net/minecraft/class_1730.method_7659(Lnet/minecraft/class_2499;Lnet/minecraft/class_7225$class_7874;)V";
    public static final String ENDER_CREATE_TAG = "net/minecraft/class_1730.method_7660(Lnet/minecraft/class_7225$class_7874;)Lnet/minecraft/class_2499;";
    public static final String INVENTORY_SAVE = "net/minecraft/class_1661.method_7384(Lnet/minecraft/class_2499;)Lnet/minecraft/class_2499;";
    public static final String INVENTORY_LOAD = "net/minecraft/class_1661.method_7397(Lnet/minecraft/class_2499;)V";
    public static final String ABILITIES_SAVE = "net/minecraft/class_1656.method_7251(Lnet/minecraft/class_2487;)V";
    public static final String ABILITIES_LOAD = "net/minecraft/class_1656.method_7249(Lnet/minecraft/class_2487;)V";

    /** SimpleContainer's restored members (PlayerEnderChestContainer's overrides dispatch through the same names) */
    public interface LegacyContainer {
        void method_7659(net.minecraft.class_2499 list, net.minecraft.class_7225.class_7874 provider);

        net.minecraft.class_2499 method_7660(net.minecraft.class_7225.class_7874 provider);
    }

    /** Inventory's restored members */
    public interface LegacyInventory {
        net.minecraft.class_2499 method_7384(net.minecraft.class_2499 list);

        void method_7397(net.minecraft.class_2499 list);
    }

    /** Abilities' restored members */
    public interface LegacyAbilities {
        void method_7251(net.minecraft.class_2487 tag);

        void method_7249(net.minecraft.class_2487 tag);
    }

    /** a SimpleContainer's save asks createTag: mixed (SimpleContainer's or the ender chest's), or a mod's subclass */
    public static boolean routeCreateTag(net.minecraft.class_1277 c) {
        return subclass(c) || CbMixedMembers.mixed(CONTAINER_CREATE_TAG)
            || (c instanceof net.minecraft.class_1730 && CbMixedMembers.mixed(ENDER_CREATE_TAG));
    }

    public static boolean routeFromTag(net.minecraft.class_1277 c) {
        return subclass(c) || CbMixedMembers.mixed(CONTAINER_FROM_TAG)
            || (c instanceof net.minecraft.class_1730 && CbMixedMembers.mixed(ENDER_FROM_TAG));
    }

    /** a class below the vanilla one 1.21.6 constructs (SimpleContainer / PlayerEnderChestContainer) */
    private static boolean subclass(net.minecraft.class_1277 c) {
        Class<?> k = c.getClass();
        return k != net.minecraft.class_1277.class && k != net.minecraft.class_1730.class;
    }

    public static boolean routeInventorySave(net.minecraft.class_1661 inv) {
        return inv.getClass() != net.minecraft.class_1661.class || CbMixedMembers.mixed(INVENTORY_SAVE);
    }

    public static boolean routeInventoryLoad(net.minecraft.class_1661 inv) {
        return inv.getClass() != net.minecraft.class_1661.class || CbMixedMembers.mixed(INVENTORY_LOAD);
    }

    public static boolean routeAbilitiesSave(net.minecraft.class_1656 a) {
        return a.getClass() != net.minecraft.class_1656.class || CbMixedMembers.mixed(ABILITIES_SAVE);
    }

    public static boolean routeAbilitiesLoad(net.minecraft.class_1656 a) {
        return a.getClass() != net.minecraft.class_1656.class || CbMixedMembers.mixed(ABILITIES_LOAD);
    }
}

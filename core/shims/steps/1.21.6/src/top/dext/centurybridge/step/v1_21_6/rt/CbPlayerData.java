package top.dext.centurybridge.step.v1_21_6.rt;

/**
 * Player data 1.21.5 saved into CompoundTag / ListTag and 1.21.6 saves through records and ValueInput / ValueOutput
 * (javap of both):
 * <ul>
 * <li>Abilities.addSaveData(tag) put an "abilities" compound of invulnerable, flying, mayfly, instabuild, mayBuild,
 *     flySpeed, walkSpeed; loadSaveData(tag) read it back from tag.getCompoundOrEmpty("abilities") with defaults false,
 *     false, false, false, 0.05, 0.1, true. Both are 1.21.5's bodies on 1.21.6's public fields and speed accessors.</li>
 * <li>Inventory.save(ListTag) appended a compound per non-empty main slot (its "Slot" byte and the stack, saved with
 *     the player's registries) and answered the list; load(ListTag) cleared the main slots and set each entry's stack
 *     (an unparsable one as empty) at its slot when in range. 1.21.6's save / load(TypedOutputList / TypedInputList of
 *     ItemStackWithSlot) do the same through ItemStackWithSlot.CODEC (slot + stack), so the list is read and written
 *     through a TagValueInput / TagValueOutput over the same ListTag.</li>
 * </ul>
 */
public final class CbPlayerData {

    private CbPlayerData() {
    }

    /** Abilities.addSaveData(CompoundTag) */
    public static void method_7251(net.minecraft.class_1656 a, net.minecraft.class_2487 tag) {
        net.minecraft.class_2487 t = new net.minecraft.class_2487();
        t.method_10556("invulnerable", a.field_7480);
        t.method_10556("flying", a.field_7479);
        t.method_10556("mayfly", a.field_7478);
        t.method_10556("instabuild", a.field_7477);
        t.method_10556("mayBuild", a.field_7476);
        t.method_10548("flySpeed", a.method_7252());
        t.method_10548("walkSpeed", a.method_7253());
        tag.method_10566("abilities", t);
    }

    /** Abilities.loadSaveData(CompoundTag) */
    public static void method_7249(net.minecraft.class_1656 a, net.minecraft.class_2487 tag) {
        net.minecraft.class_2487 t = tag.method_68568("abilities");
        a.field_7480 = t.method_68566("invulnerable", false);
        a.field_7479 = t.method_68566("flying", false);
        a.field_7478 = t.method_68566("mayfly", false);
        a.field_7477 = t.method_68566("instabuild", false);
        a.method_7248(t.method_66563("flySpeed", 0.05F));
        a.method_7250(t.method_66563("walkSpeed", 0.1F));
        a.field_7476 = t.method_68566("mayBuild", true);
    }

    /** Inventory.save(ListTag) */
    public static net.minecraft.class_2499 method_7384(net.minecraft.class_1661 inventory, net.minecraft.class_2499 list) {
        net.minecraft.class_8942.class_11340 reporter = CbValueIo.reporter();
        try {
            net.minecraft.class_11362 out = CbValueIo.fresh(inventory.field_7546.method_56673(), reporter);
            inventory.method_7384(out.method_71467("Inventory", net.minecraft.class_11343.field_60354));
            if (CbValueIo.result(out).method_10580("Inventory") instanceof net.minecraft.class_2499 written) {
                list.addAll(written);
            }
            return list;
        } finally {
            CbValueIo.close(reporter);
        }
    }

    /** Inventory.load(ListTag) */
    public static void method_7397(net.minecraft.class_1661 inventory, net.minecraft.class_2499 list) {
        net.minecraft.class_2487 holder = new net.minecraft.class_2487();
        holder.method_10566("Inventory", list);
        net.minecraft.class_8942.class_11340 reporter = CbValueIo.reporter();
        try {
            inventory.method_7397(CbValueIo.input(holder, inventory.field_7546.method_56673(), reporter)
                .method_71437("Inventory", net.minecraft.class_11343.field_60354));
        } finally {
            CbValueIo.close(reporter);
        }
    }
}

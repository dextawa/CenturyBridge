package top.dext.centurybridge.step.v1_20_5.rt;

/**
 * 1.20.4's loot table ids on 1.20.5, which names a loot table by a ResourceKey&lt;LootTable&gt;
 * where 1.20.4 used its ResourceLocation: the key of the loot_table registry with that location,
 * and back its location (a key carries nothing else). Null stays null both ways -- "no loot
 * table" in both releases.
 */
public final class CbBlockLoot {

    /** a block entity's lootTable field (RandomizableContainerBlockEntity, DecoratedPotBlockEntity) */
    public interface Field {
        net.minecraft.class_5321<net.minecraft.class_52> cb$lootTable();

        void cb$setLootTable(net.minecraft.class_5321<net.minecraft.class_52> key);
    }

    /** location -> ResourceKey&lt;LootTable&gt; */
    public static net.minecraft.class_5321<net.minecraft.class_52> key(net.minecraft.class_2960 location) {
        return location == null ? null : net.minecraft.class_5321.method_29179(net.minecraft.class_7924.field_50079, location);
    }

    /** ResourceKey&lt;LootTable&gt; -> location */
    public static net.minecraft.class_2960 location(net.minecraft.class_5321<net.minecraft.class_52> key) {
        return key == null ? null : key.method_29177();
    }

    /** a block's read of its protected drops field */
    public static net.minecraft.class_2960 field_23156(net.minecraft.class_4970 self) {
        return location(((CbBlockAccess) self).cb$drops());
    }

    /** a block's write of its protected drops field */
    public static void field_23156(net.minecraft.class_4970 self, net.minecraft.class_2960 location) {
        ((CbBlockAccess) self).cb$setDrops(key(location));
    }

    /** RandomizableContainerBlockEntity.lootTable read */
    public static net.minecraft.class_2960 field_12037(net.minecraft.class_2621 self) {
        return location(((Field) self).cb$lootTable());
    }

    /** RandomizableContainerBlockEntity.lootTable write */
    public static void field_12037(net.minecraft.class_2621 self, net.minecraft.class_2960 location) {
        ((Field) self).cb$setLootTable(key(location));
    }

    /** DecoratedPotBlockEntity.lootTable read */
    public static net.minecraft.class_2960 field_47156(net.minecraft.class_8172 self) {
        return location(((Field) self).cb$lootTable());
    }

    /** DecoratedPotBlockEntity.lootTable write */
    public static void field_47156(net.minecraft.class_8172 self, net.minecraft.class_2960 location) {
        ((Field) self).cb$setLootTable(key(location));
    }

    private CbBlockLoot() {
    }
}

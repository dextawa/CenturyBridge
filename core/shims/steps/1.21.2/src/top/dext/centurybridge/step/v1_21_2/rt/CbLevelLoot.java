package top.dext.centurybridge.step.v1_21_2.rt;

import java.util.Optional;
import net.minecraft.class_10162;
import net.minecraft.class_1767;
import net.minecraft.class_2248;
import net.minecraft.class_2960;
import net.minecraft.class_39;
import net.minecraft.class_4970;
import net.minecraft.class_5321;
import net.minecraft.class_52;
import net.minecraft.class_7924;

/**
 * 1.21.1's loot table keys on 1.21.2.
 *
 * <p>BuiltInLootTables.EMPTY (minecraft:empty) is gone: 1.21.2 says "no loot table" with an empty Optional
 * (BlockBehaviour.getLootTable, Properties.noLootTable). 1.21.1's EMPTY was the key ResourceKey.create
 * (LOOT_TABLE, "minecraft:empty") (BuiltInLootTables.&lt;clinit&gt;), and 1.21.1's code compared against it by
 * identity; the same key is built here once, so a 1.21.1 mod's comparison of a key it got from this layer
 * still holds, and a key it hands 1.21.2 names no table, as it did. The per-colour SHEEP_* keys are 1.21.2's
 * SHEEP_BY_DYE entries for that colour: the same "entities/sheep/&lt;colour&gt;" keys 1.21.1 declared one by one.
 *
 * <p>BlockBehaviour.getLootTable() answered the key, or EMPTY for a block without loot table; 1.21.2 answers
 * an Optional ({@link #lootTable}). Properties.dropsLike(block) copied the block's key into the properties
 * (1.21.2 has the same effect as overrideLootTable(block.getLootTable())).
 */
public final class CbLevelLoot {

    private static final class_5321<class_52> EMPTY =
        class_5321.method_29179(class_7924.field_50079, class_2960.method_60656("empty"));

    public static class_5321<class_52> field_844() {
        return EMPTY;
    }

    /** a 1.21.2 loot table answer as 1.21.1's key: EMPTY for none */
    public static class_5321<class_52> fromOptional(Optional<class_5321<class_52>> key) {
        return key.isPresent() ? key.get() : EMPTY;
    }

    /** a 1.21.1 key as 1.21.2's answer: none for EMPTY (or null) */
    public static Optional<class_5321<class_52>> toOptional(class_5321<class_52> key) {
        return key == null || key == EMPTY || EMPTY.equals(key) ? Optional.empty() : Optional.of(key);
    }

    /** BlockBehaviour.getLootTable() called on any block */
    public static class_5321<class_52> method_26162(class_4970 self) {
        return fromOptional(self.method_26162());
    }

    /** BlockBehaviour's loot table field (protected, final since 1.21.2), read for 1.21.1's drops field (CbLevelBlockInvoker) */
    public interface Fields {
        Optional<class_5321<class_52>> cb$dropsField();
    }

    /** a read of 1.21.1's BlockBehaviour.drops (a key, EMPTY for none) */
    public static class_5321<class_52> field_23156(class_4970 self) {
        return fromOptional(((Fields) self).cb$dropsField());
    }

    /** a write of 1.21.1's BlockBehaviour.drops: the block's loot table from then on (CbPendingIdsBlockMixin's setter) */
    public static void field_23156(class_4970 self, class_5321<class_52> key) {
        ((CbPendingIdsItems.BlockFields) self).cb$setDrops(toOptional(key));
    }

    /**
     * Properties.drops on 1.21.2. 1.21.1's field held a key: null by default (BlockBehaviour.getLootTable then worked out
     * "blocks/&lt;id&gt;" from the registered block), EMPTY after noLootTable(), the block's key after dropsLike(block); every
     * copy took it along (ofFullCopy). 1.21.2 holds a DependantName of the block id instead (field_54007): its constructor
     * puts a function deriving the same "blocks/&lt;id&gt;" key, noLootTable() a fixed empty Optional, overrideLootTable(o)
     * a fixed o; copies take it along as 1.21.1's did. A fixed value ignores the id, the default derives from it: the
     * function answers what a fresh Properties' function answers for a probe id exactly when it is that default.
     */
    private static final class DefaultDrops {
        static final class_5321<class_2248> PROBE =
            class_5321.method_29179(class_7924.field_41254, class_2960.method_60655("centurybridge", "drops_probe"));
        static final class_10162<class_2248, Optional<class_5321<class_52>>> FN = class_4970.class_2251.method_9637().field_54007;
    }

    /** a read of 1.21.1's Properties.drops: null for the default (the block's own table), EMPTY for none, else the key */
    public static class_5321<class_52> field_10666(class_4970.class_2251 self) {
        Optional<class_5321<class_52>> v = self.field_54007.get(DefaultDrops.PROBE);
        if (v.equals(DefaultDrops.FN.get(DefaultDrops.PROBE))) return null;
        return fromOptional(v);
    }

    /** a write of 1.21.1's Properties.drops: null puts the default back, EMPTY (or its equal) means none */
    public static void field_10666(class_4970.class_2251 self, class_5321<class_52> key) {
        self.field_54007 = key == null ? DefaultDrops.FN : class_10162.fixed(toOptional(key));
    }

    /** Properties.dropsLike(block): the properties take the block's loot table */
    public static class_4970.class_2251 method_16228(class_4970.class_2251 self, class_2248 block) {
        return self.method_63502(block.method_26162());
    }

    private static class_5321<class_52> sheep(class_1767 colour) {
        return class_39.field_52692.get(colour);
    }

    public static class_5321<class_52> field_869() {
        return sheep(class_1767.field_7952);
    }

    public static class_5321<class_52> field_814() {
        return sheep(class_1767.field_7946);
    }

    public static class_5321<class_52> field_224() {
        return sheep(class_1767.field_7958);
    }

    public static class_5321<class_52> field_461() {
        return sheep(class_1767.field_7951);
    }

    public static class_5321<class_52> field_385() {
        return sheep(class_1767.field_7947);
    }

    public static class_5321<class_52> field_702() {
        return sheep(class_1767.field_7961);
    }

    public static class_5321<class_52> field_629() {
        return sheep(class_1767.field_7954);
    }

    public static class_5321<class_52> field_878() {
        return sheep(class_1767.field_7944);
    }

    public static class_5321<class_52> field_806() {
        return sheep(class_1767.field_7967);
    }

    public static class_5321<class_52> field_365() {
        return sheep(class_1767.field_7955);
    }

    public static class_5321<class_52> field_285() {
        return sheep(class_1767.field_7945);
    }

    public static class_5321<class_52> field_394() {
        return sheep(class_1767.field_7966);
    }

    public static class_5321<class_52> field_489() {
        return sheep(class_1767.field_7957);
    }

    public static class_5321<class_52> field_607() {
        return sheep(class_1767.field_7942);
    }

    public static class_5321<class_52> field_716() {
        return sheep(class_1767.field_7964);
    }

    public static class_5321<class_52> field_778() {
        return sheep(class_1767.field_7963);
    }

    private CbLevelLoot() {
    }
}

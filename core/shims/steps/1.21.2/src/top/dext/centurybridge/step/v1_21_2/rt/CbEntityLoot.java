package top.dext.centurybridge.step.v1_21_2.rt;

import java.util.Optional;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1308;
import net.minecraft.class_1309;
import net.minecraft.class_1472;
import net.minecraft.class_39;
import net.minecraft.class_52;
import net.minecraft.class_5321;
import net.minecraft.class_7923;
import net.minecraft.class_7924;
import top.dext.centurybridge.step.v1_21_2.mixin.CbEntityTypeAccess;

/**
 * 1.21.1's entity loot-table API on 1.21.2.
 *
 * <p>1.21.1: EntityType.getDefaultLootTable() -> ResourceKey, never empty (getKey(this).withPrefix("entities/"),
 * lazily); LivingEntity.getLootTable() -> ResourceKey, overridable, LivingEntity's = the type's; Mob made it final
 * (its lootTable field, else the overridable protected getDefaultLootTable(), Mob's = LivingEntity.getLootTable,
 * Sheep's = the type's when sheared, else entities/sheep/&lt;color&gt;).
 * 1.21.2: EntityType.getDefaultLootTable() -> Optional (empty for a builder's noLootTable); getLootTable()
 * -> Optional moved up to Entity (method_5991, Entity's = the type's), final on Mob (its lootTable field, else
 * Entity's); Sheep's colour tables are SHEEP_BY_DYE, chosen by the loot table itself.
 *
 * <p>An empty Optional answers what 1.21.1 answered there: the type's entities/&lt;path&gt; key.
 */
public final class CbEntityLoot {

    /** a mod Mob's 1.21.1 getDefaultLootTable() (adapter hook) */
    public interface LegacyMob {
        class_5321<class_52> method_5991();
    }

    /** 1.21.1's never-empty answer from 1.21.2's Optional of the type */
    public static class_5321<class_52> key(class_1299<?> type, Optional<class_5321<class_52>> loot) {
        if (loot.isPresent()) {
            return loot.get();
        }
        return class_5321.method_29179(class_7924.field_50079, class_7923.field_41177.method_10221(type).method_45138("entities/"));
    }

    /** an old override's answer as 1.21.2's Optional (hook unwrap) */
    public static Optional<class_5321<class_52>> wrap(class_5321<class_52> key) {
        return Optional.ofNullable(key);
    }

    // ------------------------------------------------------------------------------------------ EntityType

    /** 1.21.1 EntityType.getDefaultLootTable() */
    public static class_5321<class_52> defaultLootTable(class_1299<?> type) {
        return key(type, type.method_16351());
    }

    /** a mod EntityType's super.getDefaultLootTable(): EntityType's own body, the type's field */
    public static class_5321<class_52> superDefaultLootTable(class_1299<?> type) {
        return key(type, ((CbEntityTypeAccess) type).cb$lootTable());
    }

    // ------------------------------------------------------------------------------------------ LivingEntity / Mob

    /** 1.21.1 LivingEntity.getLootTable() (Mob's final one included): 1.21.2's getLootTable, by dispatch */
    public static class_5321<class_52> lootTable(class_1309 entity) {
        return key(entity.method_5864(), entity.method_5991());
    }

    /** a mod LivingEntity's super.getLootTable(): LivingEntity's body, the type's default */
    public static class_5321<class_52> superLootTable(class_1309 entity) {
        return defaultLootTable(entity.method_5864());
    }

    /** 1.21.1 Mob.getDefaultLootTable(), called: the mob class's own override, else the vanilla body */
    public static class_5321<class_52> mobDefaultLootTable(class_1308 mob) {
        if (mob instanceof LegacyMob legacy) {
            return legacy.method_5991();
        }
        return superMobDefaultLootTable(mob);
    }

    /** a mod Mob's super.getDefaultLootTable(): the 1.21.1 body of the vanilla class above it (Mob's or Sheep's) */
    public static class_5321<class_52> superMobDefaultLootTable(class_1308 mob) {
        if (mob instanceof class_1472 sheep && !sheep.method_6629()) {
            return class_39.field_52692.get(sheep.method_6633());
        }
        return defaultLootTable(mob.method_5864());
    }

    /** a mod Sheep's super.getDefaultLootTable(): 1.21.1 Sheep's body */
    public static class_5321<class_52> superSheepDefaultLootTable(class_1472 sheep) {
        return superMobDefaultLootTable(sheep);
    }

    /**
     * From the mixin on Mob's final 1.21.2 getLootTable, when the mob holds no loot table of its own: a mod
     * Mob's 1.21.1 getDefaultLootTable answers, as 1.21.1's final getLootTable asked it. A Sheep of another type
     * than SHEEP (a mod subclass) answers 1.21.1 Sheep's colour tables: 1.21.2 dispatches on the colour inside the
     * SHEEP type's own table, which a mod type does not have. Null for vanilla's answer.
     */
    public static Optional<class_5321<class_52>> mobLootTable(class_1297 mob) {
        if (mob instanceof LegacyMob legacy) {
            return Optional.ofNullable(legacy.method_5991());
        }
        if (mob instanceof class_1472 sheep && sheep.method_5864() != class_1299.field_6115) {
            return Optional.ofNullable(superMobDefaultLootTable(sheep));
        }
        return null;
    }

    private CbEntityLoot() {
    }
}

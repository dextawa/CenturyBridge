package top.dext.centurybridge.step.v1_20_5.rt;

/**
 * 1.20.4's finalizeSpawn tag on 1.20.5.
 * <p>
 * 1.20.4 handed {@code Mob.finalizeSpawn(level, difficulty, reason, groupData, tag)} the NBT the mob came
 * with -- the spawning item's tag (spawn egg, bucket: EntityType.spawn(level, stack, ..) passed
 * {@code stack.getTag()} on), a structure template entity's NBT, or whatever a caller passed -- and mods
 * read it in their overrides (bucket variants...). 1.20.5 dropped the parameter. The tag rides along
 * beside the call instead:
 * <ul>
 * <li>a 1.20.4 call site passes it through {@link #cbWithTag} (callReshapes), which pairs it with
 *     that call's DifficultyInstance -- an object made for the call -- and the synthesized 1.20.5
 *     shape of a mod's override hands it back ({@link #cbFinalizeSpawn}) only for the same difficulty
 *     object, so another mob's finalizeSpawn never sees it;</li>
 * <li>vanilla 1.20.5 code where 1.20.4 passed a tag (EntityType.create, StructureTemplate's entity
 *     placement) pairs it the same way around its own call and clears the pairing after
 *     (mixin/CbRestEntityTypeMixin, CbRestStructureTemplateMixin);</li>
 * <li>a 1.20.4 call of EntityType.spawn / create with a tag hands the tag to the create it leads to
 *     ({@link #cbSpawnTag}); that create takes it at its start ({@link #cbBeginCreate}) and pairs it with
 *     the finalizeSpawn it makes.</li>
 * </ul>
 * Everything is per thread.
 */
public final class CbRestSpawn {

    /** the 1.20.4 finalizeSpawn shape a mod's override implements (it is public, as 1.20.4's was) */
    public interface CbLegacyFinalizeSpawn {
        net.minecraft.class_1315 method_5943(net.minecraft.class_5425 level, net.minecraft.class_1266 difficulty,
                                             net.minecraft.class_3730 reason, net.minecraft.class_1315 groupData,
                                             net.minecraft.class_2487 tag);
    }

    /** the 1.20.4 EntityType.spawn / create shapes a mod's EntityType subclass implements */
    public interface CbLegacyEntityTypeSpawn {
        net.minecraft.class_1297 method_5899(net.minecraft.class_3218 level, net.minecraft.class_2487 tag,
                                             java.util.function.Consumer<?> consumer, net.minecraft.class_2338 pos,
                                             net.minecraft.class_3730 reason, boolean alignPosition, boolean invertY);

        net.minecraft.class_1297 method_5888(net.minecraft.class_3218 level, net.minecraft.class_2487 tag,
                                             java.util.function.Consumer<?> consumer, net.minecraft.class_2338 pos,
                                             net.minecraft.class_3730 reason, boolean alignPosition, boolean invertY);
    }

    /** {difficulty, tag} of the finalizeSpawn call under way */
    private static final ThreadLocal<Object[]> PAIRED = new ThreadLocal<>();
    /** the tag a 1.20.4 spawn / create call hands to the create it leads to */
    private static final ThreadLocal<net.minecraft.class_2487> SPAWN_TAG = new ThreadLocal<>();
    /** the tag the running EntityType.create took at its start */
    private static final ThreadLocal<net.minecraft.class_2487> CREATE_TAG = new ThreadLocal<>();

    /** a 1.20.4 finalizeSpawn call's tag, paired with its difficulty (null: no tag, which clears) */
    public static net.minecraft.class_1266 cbWithTag(net.minecraft.class_1266 difficulty, net.minecraft.class_2487 tag) {
        if (tag == null) {
            PAIRED.remove();
        } else {
            PAIRED.set(new Object[] {difficulty, tag});
        }
        return difficulty;
    }

    /** the tag paired with this difficulty, taken; null when none is */
    static net.minecraft.class_2487 take(net.minecraft.class_1266 difficulty) {
        Object[] p = PAIRED.get();
        if (p != null && p[0] == difficulty) {
            PAIRED.remove();
            return (net.minecraft.class_2487) p[1];
        }
        return null;
    }

    /** the pairing made for this difficulty, cleared after its call (a vanilla target never takes it) */
    public static void cbEndCall(net.minecraft.class_1266 difficulty) {
        Object[] p = PAIRED.get();
        if (p != null && p[0] == difficulty) {
            PAIRED.remove();
        }
    }

    /** 1.20.5 finalizeSpawn of a mod class overriding the 1.20.4 one: its override, with the tag */
    public static net.minecraft.class_1315 cbFinalizeSpawn(CbLegacyFinalizeSpawn self, net.minecraft.class_5425 level,
                                                          net.minecraft.class_1266 difficulty, net.minecraft.class_3730 reason,
                                                          net.minecraft.class_1315 groupData) {
        return self.method_5943(level, difficulty, reason, groupData, take(difficulty));
    }

    /** a 1.20.4 EntityType.spawn / create call's tag, for the create it leads to */
    public static void cbSpawnTag(net.minecraft.class_2487 tag) {
        if (tag == null) {
            SPAWN_TAG.remove();
        } else {
            SPAWN_TAG.set(tag);
        }
    }

    /** EntityType.create starts: it takes the tag a 1.20.4 call handed over (or none) */
    public static void cbBeginCreate() {
        net.minecraft.class_2487 tag = SPAWN_TAG.get();
        SPAWN_TAG.remove();
        if (tag == null) {
            CREATE_TAG.remove();
        } else {
            CREATE_TAG.set(tag);
        }
    }

    /** the tag the running create took, taken */
    public static net.minecraft.class_2487 cbCreateTag() {
        net.minecraft.class_2487 tag = CREATE_TAG.get();
        CREATE_TAG.remove();
        return tag;
    }

    /** 1.20.5 EntityType.spawn(level, consumer, ..) of a mod type overriding the 1.20.4 one */
    public static net.minecraft.class_1297 cbSpawn(CbLegacyEntityTypeSpawn self, net.minecraft.class_3218 level,
                                                  java.util.function.Consumer<?> consumer, net.minecraft.class_2338 pos,
                                                  net.minecraft.class_3730 reason, boolean alignPosition, boolean invertY) {
        net.minecraft.class_2487 tag = SPAWN_TAG.get();
        SPAWN_TAG.remove();
        return self.method_5899(level, tag, consumer, pos, reason, alignPosition, invertY);
    }

    /** 1.20.5 EntityType.create(level, consumer, ..) of a mod type overriding the 1.20.4 one */
    public static net.minecraft.class_1297 cbCreate(CbLegacyEntityTypeSpawn self, net.minecraft.class_3218 level,
                                                   java.util.function.Consumer<?> consumer, net.minecraft.class_2338 pos,
                                                   net.minecraft.class_3730 reason, boolean alignPosition, boolean invertY) {
        net.minecraft.class_2487 tag = SPAWN_TAG.get();
        SPAWN_TAG.remove();
        return self.method_5888(level, tag, consumer, pos, reason, alignPosition, invertY);
    }

    /** the structure template entity's NBT the placement is about to make a mob from */
    private static final ThreadLocal<net.minecraft.class_2487> STRUCTURE_TAG = new ThreadLocal<>();

    public static void cbStructureEntity(net.minecraft.class_2487 nbt) {
        if (nbt == null) {
            STRUCTURE_TAG.remove();
        } else {
            STRUCTURE_TAG.set(nbt);
        }
    }

    public static net.minecraft.class_2487 cbStructureTag() {
        net.minecraft.class_2487 tag = STRUCTURE_TAG.get();
        STRUCTURE_TAG.remove();
        return tag;
    }

    private CbRestSpawn() {
    }
}

package top.dext.centurybridge.step.v1_20_5.rt;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * 1.20.4's trial spawner API on 1.20.5.
 *
 * <p>PlayerDetector: 1.20.4's detect(level, pos, range) enumerated the level itself and knew no line of
 * sight; 1.20.5's detect(level, selector, pos, range, requireLineOfSight) takes the candidates from a
 * selector (vanilla passes SELECT_FROM_LEVEL, the level) and checks line of sight when asked.
 * 1.20.4's PLAYERS is 1.20.5's NO_CREATIVE_PLAYERS (survival players within range); a 1.20.4 call
 * asks with SELECT_FROM_LEVEL and no line of sight -- exactly 1.20.4's detection. A 1.20.4 detector
 * asked 1.20.5's question picks its entities as in 1.20.4; the selector (when it is not the level) and
 * the line-of-sight requirement then apply to them as they do to 1.20.5's own detectors.
 *
 * <p>TrialSpawnerConfig: 1.20.5 moved requiredPlayerRange and targetCooldownLength to the spawner,
 * took the eject loot tables as keys and added the loot dropped when ominous. A config a 1.20.4
 * constructor (or 1.20.4's MAP_CODEC) built keeps its range and cooldown here, by identity; any other
 * config answers 1.20.4's defaults, 14 and 36000 -- 1.20.5's spawner defaults too. Its ominous loot
 * is 1.20.5's default (DEFAULT's, which 1.20.5's codec also fills in when the field is absent).
 *
 * <p>TrialSpawnerData: 1.20.4 kept the spawn potentials on the data, set from the config when the
 * spawner was built (setSpawnPotentialsFromConfig); 1.20.5 reads them from the spawner's config
 * when it needs them. The data knows its spawner here (CbBlockTrialSpawnerMixin), so what 1.20.4 code
 * reads of the data is what 1.20.4 had: potentials the 1.20.4 API set stay in force for spawning
 * until the spawner turns ominous (1.20.4 had no ominous state), else the config's. The old shapes
 * of hasMobToSpawn, tryDetectPlayers, isReadyToOpenShutter and isReadyToEjectItems keep 1.20.4's
 * bodies; 1.20.5 asking a data class overriding them reaches the override with what 1.20.4's
 * spawner passed (its detector and required range; its config).
 *
 * <p>Ominous: 1.20.5 turns a spawner ominous by setting its block's OMINOUS property when a player
 * carrying Bad or Trial Omen is detected. A 1.20.4 trial spawner block has no such property (1.20.4
 * knew no omen interplay), so for a spawner whose block lacks it the omens are not seen
 * (CbBlockTrialDataMixin): 1.20.4's behavior, and no state change its block cannot hold.
 */
public final class CbBlockTrial {

    // ---------------------------------------------------------------- PlayerDetector

    /** 1.20.4's PlayerDetector.detect(level, pos, range) a mod detector implements */
    public interface LegacyDetector {
        List<UUID> detect(net.minecraft.class_3218 level, net.minecraft.class_2338 pos, int range);
    }

    /** PlayerDetector.PLAYERS: survival players within range, 1.20.5's NO_CREATIVE_PLAYERS */
    public static net.minecraft.class_8962 field_47356() {
        return net.minecraft.class_8962.field_48860;
    }

    /** detect(level, pos, range) called on any detector: 1.20.4's question, from the level and without line of sight */
    public static List<UUID> detect(net.minecraft.class_8962 self, net.minecraft.class_3218 level, net.minecraft.class_2338 pos, int range) {
        if (self instanceof LegacyDetector legacy) {
            return legacy.detect(level, pos, range);
        }
        return self.detect(level, net.minecraft.class_8962.class_9198.field_48862, pos, range, false);
    }

    /** a 1.20.4 detector lambda as 1.20.5's PlayerDetector (lambdaAdapters) */
    public static net.minecraft.class_8962 detector(LegacyDetector legacy) {
        return (level, selector, pos, range, lineOfSight) -> detect(legacy, level, selector, pos, range, lineOfSight);
    }

    /** 1.20.5's detect asked of a 1.20.4 detector (overrideHooks bridge, lambda adapter): see the class comment */
    public static List<UUID> detect(LegacyDetector legacy, net.minecraft.class_3218 level, net.minecraft.class_8962.class_9198 selector,
            net.minecraft.class_2338 pos, double range, boolean lineOfSight) {
        List<UUID> found = legacy.detect(level, pos, (int) range);
        boolean fromLevel = selector == net.minecraft.class_8962.class_9198.field_48862;
        if (fromLevel && !lineOfSight) {
            return found;
        }
        Set<UUID> selectable = null;
        if (!fromLevel) {
            Set<UUID> wanted = new HashSet<>(found);
            selectable = new HashSet<>();
            for (net.minecraft.class_1657 p : selector.method_56725(level, p -> wanted.contains(p.method_5667()))) {
                selectable.add(p.method_5667());
            }
            for (net.minecraft.class_1297 e : selector.method_56724(level, net.minecraft.class_5575.method_31795(net.minecraft.class_1297.class),
                    new net.minecraft.class_238(pos).method_1014(range), e -> wanted.contains(e.method_5667()))) {
                selectable.add(e.method_5667());
            }
        }
        List<UUID> out = new ArrayList<>();
        for (UUID id : found) {
            if (selectable != null && !selectable.contains(id)) {
                continue;
            }
            if (lineOfSight) {
                net.minecraft.class_1297 e = level.method_14190(id);
                if (e == null || !inLineOfSight(level, pos.method_46558(), e.method_33571())) {
                    continue;
                }
            }
            out.add(id);
        }
        return out;
    }

    /** 1.20.5's PlayerDetector.inLineOfSight (private there), the same test */
    private static boolean inLineOfSight(net.minecraft.class_1937 level, net.minecraft.class_243 from, net.minecraft.class_243 to) {
        net.minecraft.class_3965 hit = level.method_17742(new net.minecraft.class_3959(to, from, net.minecraft.class_3959.class_3960.field_23142,
            net.minecraft.class_3959.class_242.field_1348, net.minecraft.class_3726.method_16194()));
        return hit.method_17777().equals(net.minecraft.class_2338.method_49638(from)) || hit.method_17783() == net.minecraft.class_239.class_240.field_1333;
    }

    // ---------------------------------------------------------------- TrialSpawnerConfig

    /** 1.20.4 TrialSpawnerConfig.DEFAULT's requiredPlayerRange and targetCooldownLength (1.20.5's spawner defaults too) */
    static final int DEFAULT_RANGE = 14;
    static final int DEFAULT_COOLDOWN = 36000;

    /** config -> {requiredPlayerRange, targetCooldownLength}, by identity (a config is a value record), weakly */
    private static final Map<net.minecraft.class_8965, int[]> LEGACY = new com.google.common.collect.MapMaker().weakKeys().makeMap();

    /** after a 1.20.4 constructor (superCtorReshapes post): the range and cooldown the config was built with */
    public static void remember(net.minecraft.class_8965 config, int range, int cooldown) {
        LEGACY.put(config, new int[] {range, cooldown});
    }

    /** TrialSpawnerConfig.requiredPlayerRange() */
    public static int comp_2102(net.minecraft.class_8965 config) {
        int[] v = LEGACY.get(config);
        return v != null ? v[0] : DEFAULT_RANGE;
    }

    /** TrialSpawnerConfig.targetCooldownLength() */
    public static int comp_2109(net.minecraft.class_8965 config) {
        int[] v = LEGACY.get(config);
        return v != null ? v[1] : DEFAULT_COOLDOWN;
    }

    /** a 1.20.4 config's eject loot table ids as the keys 1.20.5 takes (a key passes) */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static net.minecraft.class_6005<net.minecraft.class_5321<net.minecraft.class_52>> lootKeys(net.minecraft.class_6005 tables) {
        net.minecraft.class_6005.class_6006 b = net.minecraft.class_6005.method_34971();
        for (Object o : tables.method_34994()) {
            net.minecraft.class_6008.class_6010 w = (net.minecraft.class_6008.class_6010) o;
            Object t = w.comp_2542();
            b.method_34975(t instanceof net.minecraft.class_2960 rl ? CbBlockLoot.key(rl) : t, w.method_34979().method_34976());
        }
        return b.method_34974();
    }

    /** a 1.20.5 config's eject loot table keys as 1.20.4's ids (an id passes): lootTablesToEject() */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static net.minecraft.class_6005<net.minecraft.class_2960> lootLocations(net.minecraft.class_6005 tables) {
        net.minecraft.class_6005.class_6006 b = net.minecraft.class_6005.method_34971();
        for (Object o : tables.method_34994()) {
            net.minecraft.class_6008.class_6010 w = (net.minecraft.class_6008.class_6010) o;
            Object t = w.comp_2542();
            b.method_34975(t instanceof net.minecraft.class_5321 key ? key.method_29177() : t, w.method_34979().method_34976());
        }
        return b.method_34974();
    }

    /** the loot a 1.20.4 config drops when ominous: 1.20.5's default */
    public static net.minecraft.class_5321<net.minecraft.class_52> ominousLoot() {
        return net.minecraft.class_8965.field_47367.comp_2552();
    }

    /** 1.20.4's TrialSpawnerConfig.MAP_CODEC: 1.20.4's fields and defaults, building 1.20.5 configs that keep their range and cooldown */
    private static MapCodec<net.minecraft.class_8965> legacyMapCodec() {
        Codec<net.minecraft.class_6005<net.minecraft.class_2960>> lootCodec = net.minecraft.class_6005.method_39521(net.minecraft.class_2960.field_25139);
        return RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.intRange(1, 128).optionalFieldOf("required_player_range", DEFAULT_RANGE).forGetter(CbBlockTrial::comp_2102),
            Codec.intRange(1, 128).optionalFieldOf("spawn_range", 4).forGetter(net.minecraft.class_8965::comp_2103),
            Codec.floatRange(0.0f, Float.MAX_VALUE).optionalFieldOf("total_mobs", 6.0f).forGetter(net.minecraft.class_8965::comp_2104),
            Codec.floatRange(0.0f, Float.MAX_VALUE).optionalFieldOf("simultaneous_mobs", 2.0f).forGetter(net.minecraft.class_8965::comp_2105),
            Codec.floatRange(0.0f, Float.MAX_VALUE).optionalFieldOf("total_mobs_added_per_player", 2.0f)
                .forGetter(net.minecraft.class_8965::comp_2106),
            Codec.floatRange(0.0f, Float.MAX_VALUE).optionalFieldOf("simultaneous_mobs_added_per_player", 1.0f)
                .forGetter(net.minecraft.class_8965::comp_2107),
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("ticks_between_spawn", 40).forGetter(net.minecraft.class_8965::comp_2108),
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("target_cooldown_length", DEFAULT_COOLDOWN).forGetter(CbBlockTrial::comp_2109),
            net.minecraft.class_1952.field_34461.optionalFieldOf("spawn_potentials", net.minecraft.class_6005.<net.minecraft.class_1952>method_38062())
                .forGetter(net.minecraft.class_8965::comp_2110),
            lootCodec.optionalFieldOf("loot_tables_to_eject", net.minecraft.class_6005.<net.minecraft.class_2960>method_38062())
                .forGetter(c -> lootLocations(c.comp_2111()))
        ).apply(i, (range, spawnRange, total, simultaneous, totalPer, simultaneousPer, ticks, cooldown, potentials, loot) -> {
            net.minecraft.class_8965 c = new net.minecraft.class_8965(spawnRange, total, simultaneous, totalPer, simultaneousPer, ticks,
                potentials, lootKeys(loot), ominousLoot());
            remember(c, range, cooldown);
            return c;
        }));
    }

    /** what TrialSpawnerConfig.MAP_CODEC holds: built on first read (1.20.4's), or what a mod stored */
    private static volatile MapCodec<net.minecraft.class_8965> mapCodec;
    private static volatile boolean mapCodecSet;

    /** TrialSpawnerConfig.MAP_CODEC (1.20.4's, a MapCodec; 1.20.5's CODEC is a Codec of other fields) */
    public static MapCodec<net.minecraft.class_8965> field_47368() {
        MapCodec<net.minecraft.class_8965> c = mapCodec;
        if (c == null) {
            synchronized (CbBlockTrial.class) {
                c = mapCodec;
                if (c == null) {
                    c = legacyMapCodec();
                    mapCodec = c;
                }
            }
        }
        return c;
    }

    /** TrialSpawnerConfig.MAP_CODEC = codec (a mutable static in 1.20.4): spawners (de)serialize their configs with it */
    public static void field_47368(MapCodec<net.minecraft.class_8965> codec) {
        mapCodec = codec;
        mapCodecSet = true;
    }

    /** the config codec TrialSpawner's codec uses (CbBlockTrialSpawnerMixin): 1.20.5's, or the one a mod put in MAP_CODEC */
    public static Codec<net.minecraft.class_8965> spawnerConfigCodec() {
        return mapCodecSet ? field_47368().codec() : net.minecraft.class_8965.field_47368;
    }

    // ---------------------------------------------------------------- TrialSpawner

    /** the entity selector 1.20.5's own trial spawners use: the level, as 1.20.4's detectors searched */
    public static net.minecraft.class_8962.class_9198 selectFromLevel() {
        return net.minecraft.class_8962.class_9198.field_48862;
    }

    /** TrialSpawnerConfig.DEFAULT, as the 1.20.4 constructors' default config and 1.20.5's default ominous one */
    public static net.minecraft.class_8965 defaultConfig() {
        return net.minecraft.class_8965.field_47367;
    }

    /** a fresh TrialSpawnerData, as 1.20.4's two-argument constructor made */
    public static net.minecraft.class_8966 newData() {
        return new net.minecraft.class_8966();
    }

    /** the 1.20.4 default config's targetCooldownLength (TrialSpawnerConfig.DEFAULT was mutable in 1.20.4) */
    public static int defaultCooldown() {
        return comp_2109(net.minecraft.class_8965.field_47367);
    }

    /** the 1.20.4 default config's requiredPlayerRange */
    public static int defaultRange() {
        return comp_2102(net.minecraft.class_8965.field_47367);
    }

    /** after TrialSpawner(config, data, accessor, detector) (superCtorReshapes post): its data's potentials set from the config, as 1.20.4's constructor did */
    public static void legacySpawner(net.minecraft.class_8963 spawner, net.minecraft.class_8965 config, net.minecraft.class_8966 data) {
        method_55191(data, config);
    }

    /** the isOminous 1.20.5 passes its spawner and state ticks: the block's OMINOUS, false where it has none (what 1.20.5's own ticker passes) */
    public static boolean ominousAt(net.minecraft.class_1937 level, net.minecraft.class_2338 pos) {
        return level.method_8320(pos).method_28500(net.minecraft.class_2741.field_50193).orElse(false);
    }

    /** {@link #ominousAt(net.minecraft.class_1937, net.minecraft.class_2338)} for a server level argument */
    public static boolean ominousAt(net.minecraft.class_3218 level, net.minecraft.class_2338 pos) {
        return ominousAt((net.minecraft.class_1937) level, pos);
    }

    /** addSpawnParticles: 1.20.4's flame */
    public static net.minecraft.class_2400 flame() {
        return net.minecraft.class_2398.field_11240;
    }

    /** addDetectPlayerParticles: 1.20.4's detection particle */
    public static net.minecraft.class_2394 detectedPlayer() {
        return net.minecraft.class_2398.field_47493;
    }

    // ---------------------------------------------------------------- TrialSpawnerData

    /** the data's spawner and the state 1.20.4 code reads and writes (CbBlockTrialDataMixin) */
    public interface Data {
        net.minecraft.class_8963 cb$spawner();

        void cb$setSpawner(net.minecraft.class_8963 spawner);

        /** potentials the 1.20.4 API set, null when none */
        net.minecraft.class_6005<net.minecraft.class_1952> cb$legacyPotentials();

        void cb$setLegacyPotentials(net.minecraft.class_6005<net.minecraft.class_1952> potentials);

        Set<UUID> cb$detectedPlayers();

        long cb$nextMobSpawnsAt();

        void cb$setNextMobSpawnsAt(long t);

        long cb$cooldownEndsAt();

        Optional<net.minecraft.class_1952> cb$nextSpawnData();

        Optional<net.minecraft.class_5321<net.minecraft.class_52>> cb$ejectingLootTable();

        void cb$setEjectingLootTable(Optional<net.minecraft.class_5321<net.minecraft.class_52>> table);
    }

    /** 1.20.4's setSpawnPotentialsFromConfig(config) a mod data class overrides */
    public interface LegacySetPotentials {
        void method_55191(net.minecraft.class_8965 config);
    }

    /** 1.20.4's hasMobToSpawn() a mod data class overrides */
    public interface LegacyHasMob {
        boolean method_55197();
    }

    /** 1.20.4's tryDetectPlayers(level, pos, detector, range) a mod data class overrides */
    public interface LegacyDetect {
        void method_55186(net.minecraft.class_3218 level, net.minecraft.class_2338 pos, net.minecraft.class_8962 detector, int range);
    }

    /** 1.20.4's isReadyToOpenShutter(level, config, delay) a mod data class overrides */
    public interface LegacyShutter {
        boolean method_55184(net.minecraft.class_3218 level, net.minecraft.class_8965 config, float delay);
    }

    /** 1.20.4's isReadyToEjectItems(level, config, delay) a mod data class overrides */
    public interface LegacyEject {
        boolean method_55198(net.minecraft.class_3218 level, net.minecraft.class_8965 config, float delay);
    }

    /** 1.20.4's setSpawnPotentialsFromConfig: the config's potentials, or -- with none -- the next spawn data (a new one without) alone */
    private static net.minecraft.class_6005<net.minecraft.class_1952> potentialsFrom(net.minecraft.class_8966 data, net.minecraft.class_8965 config) {
        net.minecraft.class_6005<net.minecraft.class_1952> list = config.comp_2110();
        return list.method_34993() ? net.minecraft.class_6005.method_38061(((Data) data).cb$nextSpawnData().orElseGet(net.minecraft.class_1952::new)) : list;
    }

    /** the potentials 1.20.5's spawning reads (CbBlockTrialDataMixin): those the 1.20.4 API set, unless the spawner turned ominous; else the config's */
    public static net.minecraft.class_6005<net.minecraft.class_1952> potentialsInForce(net.minecraft.class_8966 data, net.minecraft.class_8965 config) {
        Data d = (Data) data;
        net.minecraft.class_6005<net.minecraft.class_1952> legacy = d.cb$legacyPotentials();
        net.minecraft.class_8963 spawner = d.cb$spawner();
        return legacy != null && (spawner == null || !spawner.method_58705()) ? legacy : config.comp_2110();
    }

    /** setSpawnPotentialsFromConfig(config) called on any data */
    public static void method_55191(net.minecraft.class_8966 data, net.minecraft.class_8965 config) {
        if (data instanceof LegacySetPotentials legacy) {
            legacy.method_55191(config);
        } else {
            superSetSpawnPotentials(data, config);
        }
    }

    /** 1.20.4's setSpawnPotentialsFromConfig body (a mod's super call) */
    public static void superSetSpawnPotentials(net.minecraft.class_8966 data, net.minecraft.class_8965 config) {
        ((Data) data).cb$setLegacyPotentials(potentialsFrom(data, config));
    }

    /** spawnPotentials (protected in 1.20.4): what the 1.20.4 API set, else what 1.20.4's spawner set from its config; null outside a spawner, as unset in 1.20.4 */
    public static net.minecraft.class_6005<net.minecraft.class_1952> field_47378(net.minecraft.class_8966 data) {
        Data d = (Data) data;
        if (d.cb$legacyPotentials() != null) {
            return d.cb$legacyPotentials();
        }
        net.minecraft.class_8963 spawner = d.cb$spawner();
        return spawner != null ? potentialsFrom(data, spawner.method_55170()) : null;
    }

    /** spawnPotentials = list */
    public static void field_47378(net.minecraft.class_8966 data, net.minecraft.class_6005<net.minecraft.class_1952> potentials) {
        ((Data) data).cb$setLegacyPotentials(potentials);
    }

    /** hasMobToSpawn() called on any data */
    public static boolean method_55197(net.minecraft.class_8966 data) {
        return data instanceof LegacyHasMob legacy ? legacy.method_55197() : superHasMobToSpawn(data);
    }

    /** 1.20.4's hasMobToSpawn body (a mod's super call): a next spawn data naming an entity, or potentials to pick one from */
    public static boolean superHasMobToSpawn(net.minecraft.class_8966 data) {
        Optional<net.minecraft.class_1952> next = ((Data) data).cb$nextSpawnData();
        if (next.isPresent() && next.get().method_38093().method_10573("id", 8)) {
            return true;
        }
        net.minecraft.class_6005<net.minecraft.class_1952> potentials = field_47378(data);
        return potentials != null && !potentials.method_34993();
    }

    /** 1.20.5's hasMobToSpawn(spawner, random) asked of a data overriding 1.20.4's */
    public static boolean hasMobToSpawn(LegacyHasMob self, net.minecraft.class_8963 spawner, net.minecraft.class_5819 random) {
        return self.method_55197();
    }

    /** tryDetectPlayers(level, pos, detector, range) called on any data */
    public static void method_55186(net.minecraft.class_8966 data, net.minecraft.class_3218 level, net.minecraft.class_2338 pos,
            net.minecraft.class_8962 detector, int range) {
        if (data instanceof LegacyDetect legacy) {
            legacy.method_55186(level, pos, detector, range);
        } else {
            superTryDetectPlayers(data, level, pos, detector, range);
        }
    }

    /** 1.20.4's tryDetectPlayers body: newly detected players make the next mob come no sooner than 40 ticks on, and are announced (level event 3013) */
    public static void superTryDetectPlayers(net.minecraft.class_8966 data, net.minecraft.class_3218 level, net.minecraft.class_2338 pos,
            net.minecraft.class_8962 detector, int range) {
        Data d = (Data) data;
        List<UUID> found = detect(detector, level, pos, range);
        if (d.cb$detectedPlayers().addAll(found)) {
            d.cb$setNextMobSpawnsAt(Math.max(level.method_8510() + 40L, d.cb$nextMobSpawnsAt()));
            level.method_20290(3013, pos, d.cb$detectedPlayers().size());
        }
    }

    /** 1.20.5's tryDetectPlayers(level, pos, spawner) asked of a data overriding 1.20.4's: the detector and range 1.20.4's spawner passed */
    public static void tryDetectPlayers(LegacyDetect self, net.minecraft.class_3218 level, net.minecraft.class_2338 pos, net.minecraft.class_8963 spawner) {
        self.method_55186(level, pos, spawner.method_55177(), spawner.method_58707());
    }

    /** isReadyToOpenShutter(level, config, delay) called on any data */
    public static boolean method_55184(net.minecraft.class_8966 data, net.minecraft.class_3218 level, net.minecraft.class_8965 config, float delay) {
        return data instanceof LegacyShutter legacy ? legacy.method_55184(level, config, delay)
            : data.method_55184(level, delay, comp_2109(config));
    }

    /** 1.20.4's isReadyToOpenShutter body (1.20.5's, with the config's cooldown) */
    public static boolean superIsReadyToOpenShutter(net.minecraft.class_8966 data, net.minecraft.class_3218 level, net.minecraft.class_8965 config, float delay) {
        long start = ((Data) data).cb$cooldownEndsAt() - comp_2109(config);
        return (float) level.method_8510() >= (float) start + delay;
    }

    /** 1.20.5's isReadyToOpenShutter(level, delay, cooldown) asked of a data overriding 1.20.4's: the spawner's config */
    public static boolean isReadyToOpenShutter(LegacyShutter self, net.minecraft.class_3218 level, float delay, int cooldown) {
        return self.method_55184(level, configFor((net.minecraft.class_8966) self, cooldown), delay);
    }

    /** isReadyToEjectItems(level, config, delay) called on any data */
    public static boolean method_55198(net.minecraft.class_8966 data, net.minecraft.class_3218 level, net.minecraft.class_8965 config, float delay) {
        return data instanceof LegacyEject legacy ? legacy.method_55198(level, config, delay)
            : data.method_55198(level, delay, comp_2109(config));
    }

    /** 1.20.4's isReadyToEjectItems body (1.20.5's, with the config's cooldown) */
    public static boolean superIsReadyToEjectItems(net.minecraft.class_8966 data, net.minecraft.class_3218 level, net.minecraft.class_8965 config, float delay) {
        long start = ((Data) data).cb$cooldownEndsAt() - comp_2109(config);
        return (float) (level.method_8510() - start) % delay == 0.0f;
    }

    /** 1.20.5's isReadyToEjectItems(level, delay, cooldown) asked of a data overriding 1.20.4's: the spawner's config */
    public static boolean isReadyToEjectItems(LegacyEject self, net.minecraft.class_3218 level, float delay, int cooldown) {
        return self.method_55198(level, configFor((net.minecraft.class_8966) self, cooldown), delay);
    }

    /**
     * The config 1.20.4's spawner handed its data: the spawner's (DEFAULT outside one), answering the
     * spawner's range and the cooldown 1.20.5 passed -- a copy carrying them where the config itself
     * answers others (1.20.5 keeps range and cooldown on the spawner).
     */
    static net.minecraft.class_8965 configFor(net.minecraft.class_8966 data, int cooldown) {
        net.minecraft.class_8963 spawner = ((Data) data).cb$spawner();
        net.minecraft.class_8965 c = spawner != null ? spawner.method_55170() : net.minecraft.class_8965.field_47367;
        int range = spawner != null ? spawner.method_58707() : comp_2102(c);
        if (comp_2102(c) == range && comp_2109(c) == cooldown) {
            return c;
        }
        net.minecraft.class_8965 copy = new net.minecraft.class_8965(c.comp_2103(), c.comp_2104(), c.comp_2105(), c.comp_2106(), c.comp_2107(),
            c.comp_2108(), c.comp_2110(), c.comp_2111(), c.comp_2552());
        remember(copy, range, cooldown);
        return copy;
    }

    /** ejectingLootTable (protected; 1.20.4 held the id): the key's location */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Optional field_47377(net.minecraft.class_8966 data) {
        return ((Data) data).cb$ejectingLootTable().map(k -> k.method_29177());
    }

    /** ejectingLootTable = an id (a key passes) */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void field_47377(net.minecraft.class_8966 data, Optional table) {
        ((Data) data).cb$setEjectingLootTable(lootKeyOf(table));
    }

    /** TrialSpawnerData(.., ejectingLootTable): 1.20.4's optional id as the optional key 1.20.5 holds (a key passes) */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Optional lootKeyOf(Optional table) {
        return table == null ? null : table.map(t -> t instanceof net.minecraft.class_2960 rl ? CbBlockLoot.key(rl) : t);
    }

    private CbBlockTrial() {
    }
}

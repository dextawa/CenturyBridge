package top.dext.centurybridge.step.v1_20_3.rt;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import net.minecraft.class_1657;
import net.minecraft.class_1792;
import net.minecraft.class_1922;
import net.minecraft.class_1937;
import net.minecraft.class_1959;
import net.minecraft.class_2248;
import net.minecraft.class_2283;
import net.minecraft.class_2338;
import net.minecraft.class_2378;
import net.minecraft.class_2394;
import net.minecraft.class_2398;
import net.minecraft.class_2400;
import net.minecraft.class_2527;
import net.minecraft.class_2537;
import net.minecraft.class_2680;
import net.minecraft.class_2960;
import net.minecraft.class_4538;
import net.minecraft.class_5620;
import net.minecraft.class_6016;
import net.minecraft.class_6017;
import net.minecraft.class_7923;
import net.minecraft.class_8177;
import net.minecraft.class_8813;

/**
 * Blocks: the 1.20.2 block API on 1.20.3.
 *
 * Every member here is wired from core/segments/steps/1.20.2__1.20.3.json.d/s2world.json: converters
 * and suppliers the engine's constructor/call reshapes run at a converted mod's call site, and the
 * statics its redirects call instead of a member 1.20.3 removed. Each says which 1.20.2 behaviour it
 * keeps and how, read off the 1.20.2 and 1.20.3 bytecode.
 */
public final class CbWorldBlocks {

    // ------------------------------------------------------------------ getCloneItemStack

    /**
     * Block.getCloneItemStack(BlockGetter, pos, state) narrowed its world to a LevelReader in 1.20.3.
     * Every world vanilla 1.20.2 handed it is a Level (a LevelReader) and passes unchanged; any other
     * BlockGetter is viewed as a LevelReader that answers the BlockGetter surface from the getter
     * itself and registry access from the built-in registries -- exactly what the 1.20.3
     * implementations read from it (the stems resolve their seed item by key, the others read block
     * entities) -- and refuses, by name, the world queries a plain BlockGetter has no answer for.
     */
    public static class_4538 levelReader(class_1922 getter) {
        if (getter == null || getter instanceof class_4538) {
            return (class_4538) getter;
        }
        return new CbWorldGetterReader(getter);
    }

    // ------------------------------------------------------------------ playerWillDestroy

    /**
     * Block.playerWillDestroy returns, since 1.20.3, the state the break goes on with (ServerPlayerGameMode
     * judges the tool and calls destroy / mineBlock / playerDestroy with it); 1.20.2's returned nothing and
     * its caller went on with the state it had passed. The 1.20.3 shape synthesized in a 1.20.2 block runs the block's override and answers
     * that state. The override's own super call reaches the vanilla 1.20.3 method (its returned state is
     * dropped there, as the 1.20.2 call had none): of the 1.20.3 implementations only DecoratedPotBlock
     * returns another state than its argument (the cracked pot), so a 1.20.2 subclass of it breaks with the
     * state it was broken in, as in 1.20.2.
     */
    public static class_2680 playerWillDestroy(CbWorldPlayerWillDestroy block, class_1937 level, class_2338 pos,
                                               class_2680 state, class_1657 player) {
        block.method_9576(level, pos, state, player);
        return state;
    }

    // ------------------------------------------------------------------ AmethystClusterBlock

    /** AmethystClusterBlock(int height, int aabbOffset, ..) became (float, float, ..): the same values. */
    public static float intToFloat(int v) {
        return v;
    }

    // ------------------------------------------------------------------ buttons, pressure plates, block set types

    private record ButtonKey(class_8177 type, boolean arrows) {
    }

    private record PlateKey(class_8177 type, class_8177.class_2441 sensitivity) {
    }

    private static final Map<ButtonKey, class_8177> BUTTON_TYPES = new ConcurrentHashMap<>();
    private static final Map<PlateKey, class_8177> PLATE_TYPES = new ConcurrentHashMap<>();

    /**
     * ButtonBlock(props, type, ticks, arrowsCanPress) lost its arrow flag in 1.20.3: the button asks
     * its BlockSetType. A button whose flag agrees with its type keeps the type; otherwise it gets a
     * copy of the type that differs only in that flag, so the button behaves as 1.20.2 built it.
     */
    public static class_8177 buttonType(class_8177 type, boolean arrowsCanPress) {
        if (type.comp_1972() == arrowsCanPress) {
            return type;
        }
        return BUTTON_TYPES.computeIfAbsent(new ButtonKey(type, arrowsCanPress), k -> new class_8177(
            type.comp_1289(), type.comp_1471(), type.comp_2112(), arrowsCanPress, type.comp_1973(),
            type.comp_1290(), type.comp_1291(), type.comp_1292(), type.comp_1293(), type.comp_1294(),
            type.comp_1295(), type.comp_1296(), type.comp_1297(), type.comp_1298()));
    }

    /**
     * PressurePlateBlock(sensitivity, props, type): 1.20.3 reads the sensitivity off the BlockSetType.
     * Same treatment as {@link #buttonType}: the type itself when it agrees, else a copy with the
     * sensitivity the 1.20.2 constructor was given.
     */
    public static class_8177 plateType(class_8177.class_2441 sensitivity, class_8177 type) {
        if (type.comp_1973() == sensitivity) {
            return type;
        }
        return PLATE_TYPES.computeIfAbsent(new PlateKey(type, sensitivity), k -> new class_8177(
            type.comp_1289(), type.comp_1471(), type.comp_2112(), type.comp_1972(), sensitivity,
            type.comp_1290(), type.comp_1291(), type.comp_1292(), type.comp_1293(), type.comp_1294(),
            type.comp_1295(), type.comp_1296(), type.comp_1297(), type.comp_1298()));
    }

    /**
     * The full 1.20.2 BlockSetType constructor knew nothing of pressure plate sensitivity (the plate
     * carried it, see {@link #plateType}); 1.20.3's record needs one. 1.20.2's vanilla blocks paired
     * hand-openable sets with plates any entity triggers (the wooden ones) and the others with
     * mob-only plates (stone), which is the default a 1.20.2 set gets here.
     */
    public static class_8177.class_2441 plateSensitivityFor(boolean canOpenByHand) {
        return canOpenByHand ? class_8177.class_2441.field_11361 : class_8177.class_2441.field_11362;
    }

    // ------------------------------------------------------------------ torches

    private static final ThreadLocal<class_2394> PENDING_TORCH_FLAME = new ThreadLocal<>();

    /**
     * TorchBlock / WallTorchBlock(props, ParticleOptions flame) became (SimpleParticleType, props).
     * A simple particle type passes as itself. Any other ParticleOptions (a dust colour, a mod's
     * own options) has no place in the 1.20.3 field: the torch is built with FLAME there and the
     * real particle is handed to the torch being constructed (CbWorldTorchMixin takes it when the
     * constructor returns), which then emits it where 1.20.2 emitted its flame.
     */
    public static class_2400 torchFlame(class_2394 flame) {
        if (flame instanceof class_2400 simple) {
            PENDING_TORCH_FLAME.remove();
            return simple;
        }
        PENDING_TORCH_FLAME.set(flame);
        return class_2398.field_11240;
    }

    /** the flame particle a 1.20.2 constructor gave the torch under construction, once */
    public static class_2394 takePendingTorchFlame() {
        class_2394 p = PENDING_TORCH_FLAME.get();
        PENDING_TORCH_FLAME.remove();
        return p;
    }

    /** a 1.20.2 read of the protected TorchBlock.flameParticle (typed ParticleOptions then) */
    public static class_2394 flameParticle(class_2527 torch) {
        return ((CbWorldTorchAccess) torch).cb$flame();
    }

    // ------------------------------------------------------------------ simple constructor defaults

    /** WitherRoseBlock(effect, props) passed an effect duration of 8 seconds to FlowerBlock in 1.20.2. */
    public static int witherRoseEffectSeconds() {
        return 8;
    }

    /** DropExperienceBlock(props) was this(props, ConstantInt.of(0)) in 1.20.2. */
    public static class_6017 noExperience() {
        return class_6016.method_34998(0);
    }

    /**
     * MangrovePropaguleBlock(props) grew a MangroveTreeGrower(0.85F) in 1.20.2; 1.20.3's
     * TreeGrower.MANGROVE is the same choice (the tall mangrove with probability 0.85).
     */
    public static class_8813 mangroveGrower() {
        return class_8813.field_46513;
    }

    // ------------------------------------------------------------------ Blocks.register(ResourceLocation, Block)

    /** 1.20.2's body: register the block under the id (1.20.3 takes a ResourceKey instead). */
    public static class_2248 register(class_2960 id, class_2248 block) {
        return (class_2248) class_2378.method_10230(class_7923.field_41175, id, block);
    }

    // ------------------------------------------------------------------ ChorusPlantBlock

    /**
     * ChorusPlantBlock.getStateForPlacement(BlockGetter, pos) was an instance method building on the
     * block's own default state; 1.20.3 made it static, taking that state as a third argument.
     */
    public static class_2680 getStateForPlacement(class_2283 self, class_1922 level, class_2338 pos) {
        return class_2283.method_9759(level, pos, self.method_9564());
    }

    // ------------------------------------------------------------------ TripWireHookBlock

    /**
     * TripWireHookBlock.calculateState became static in 1.20.3 and reads the hook block from the state
     * it is given instead of from `this`; vanilla 1.20.2 always passed a state of the hook itself.
     */
    public static void calculateState(class_2537 self, class_1937 level, class_2338 pos, class_2680 state,
                                      boolean attaching, boolean shouldNotifyNeighbours, int searchRange,
                                      class_2680 tripwire) {
        class_2537.method_10776(level, pos, state, attaching, shouldNotifyNeighbours, searchRange, tripwire);
    }

    // ------------------------------------------------------------------ cauldrons

    /** LayeredCauldronBlock.RAIN / SNOW: the fill predicates 1.20.2 shipped (gone in 1.20.3) */
    public static final Predicate<class_1959.class_1963> LAYERED_RAIN = p -> p == class_1959.class_1963.field_9382;
    public static final Predicate<class_1959.class_1963> LAYERED_SNOW = p -> p == class_1959.class_1963.field_9383;

    /** what a cauldron built through a 1.20.2 constructor fills on, and whether it is a powder snow one */
    public record LegacyCauldron(Predicate<class_1959.class_1963> fill, boolean powderSnow) {
    }

    private static final ThreadLocal<LegacyCauldron> PENDING_CAULDRON = new ThreadLocal<>();

    /**
     * LayeredCauldronBlock(props, Predicate fill, interactions) became (Precipitation, InteractionMap,
     * props): 1.20.3 fills on exactly one precipitation and lets that one decide the fire behaviour
     * too (SNOW melts into a water cauldron, which only PowderSnowCauldronBlock did in 1.20.2). The
     * 1.20.2 predicate is handed to the cauldron under construction (CbWorldLayeredCauldronMixin),
     * which answers precipitation, stalactite drips and fire the 1.20.2 way; the precipitation
     * returned is the one the predicate accepts alone (NONE when it accepts neither), for the code
     * that only reads the 1.20.3 field.
     */
    public static class_1959.class_1963 legacyLayeredFill(Predicate<class_1959.class_1963> fill) {
        PENDING_CAULDRON.set(new LegacyCauldron(fill, false));
        return onlyPrecipitation(fill);
    }

    /** the same for the 1.20.2 PowderSnowCauldronBlock constructor (the facade's), keeping its fire behaviour */
    public static class_1959.class_1963 legacyPowderSnowFill(Predicate<class_1959.class_1963> fill) {
        PENDING_CAULDRON.set(new LegacyCauldron(fill, true));
        return onlyPrecipitation(fill);
    }

    private static class_1959.class_1963 onlyPrecipitation(Predicate<class_1959.class_1963> fill) {
        boolean rain = fill.test(class_1959.class_1963.field_9382);
        boolean snow = fill.test(class_1959.class_1963.field_9383);
        if (rain && !snow) {
            return class_1959.class_1963.field_9382;
        }
        if (snow && !rain) {
            return class_1959.class_1963.field_9383;
        }
        return rain ? class_1959.class_1963.field_9382 : class_1959.class_1963.field_9384;
    }

    /** the 1.20.2 fill predicate handed to the cauldron under construction, once */
    public static LegacyCauldron takePendingCauldron() {
        LegacyCauldron c = PENDING_CAULDRON.get();
        PENDING_CAULDRON.remove();
        return c;
    }

    private static int legacyMaps;

    /**
     * 1.20.2 cauldrons took their interactions as a bare Map; 1.20.3 wants a named InteractionMap.
     * A map that is the content of a 1.20.3 InteractionMap (CauldronInteraction.WATER and friends,
     * as a 1.20.2 mod reads them) is that InteractionMap; a mod's own map gets one of its own,
     * registered in CauldronInteraction.INTERACTIONS so the cauldron codec can name it.
     */
    public static synchronized class_5620.class_8821 interactionMap(Map<class_1792, class_5620> map) {
        if (map == null) {
            return null;
        }
        for (class_5620.class_8821 im : class_5620.field_46589.values()) {
            if (im.comp_1982() == map) {
                return im;
            }
        }
        String name;
        do {
            name = "centurybridge_legacy_" + (legacyMaps++);
        } while (class_5620.field_46589.containsKey(name));
        class_5620.class_8821 im = new class_5620.class_8821(name, map);
        class_5620.field_46589.put(name, im);
        return im;
    }

    private CbWorldBlocks() {
    }
}

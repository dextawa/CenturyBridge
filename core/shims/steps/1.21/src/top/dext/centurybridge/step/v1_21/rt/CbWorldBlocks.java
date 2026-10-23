package top.dext.centurybridge.step.v1_21.rt;

import java.util.Optional;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_1937;
import net.minecraft.class_2269;
import net.minecraft.class_2338;
import net.minecraft.class_2370;
import net.minecraft.class_2378;
import net.minecraft.class_2401;
import net.minecraft.class_2601;
import net.minecraft.class_2619;
import net.minecraft.class_2680;
import net.minecraft.class_2960;
import net.minecraft.class_3715;
import net.minecraft.class_3924;
import net.minecraft.class_5321;
import net.minecraft.class_7923;
import net.minecraft.class_7924;
import net.minecraft.class_8173;
import net.minecraft.class_8566;
import net.minecraft.class_8886;
import net.minecraft.class_9766;
import top.dext.centurybridge.step.v1_21.mixin.CbWorldButtonAccessor;
import top.dext.centurybridge.step.v1_21.mixin.CbWorldLeverInvoker;

/**
 * Blocks and block entities: the 1.20.6 members 1.21 reshaped, with 1.20.6's behaviour. Wired from s6world.json.
 */
public final class CbWorldBlocks {

    // ------------------------------------------------------------------------------------------------ button, lever
    /** the 1.20.6 press(state, level, pos) a 1.20.6 button subclass overrides */
    public interface LegacyPress {
        void method_21845(class_2680 state, class_1937 level, class_2338 pos);
    }

    /** the 1.20.6 pull(state, level, pos) a 1.20.6 lever subclass overrides */
    public interface LegacyPull {
        class_2680 method_21846(class_2680 state, class_1937 level, class_2338 pos);
    }

    /** the player of a press / pull 1.21 asked a 1.20.6 override for, for its super call (innermost first) */
    private static final ThreadLocal<java.util.ArrayDeque<Object[]>> USERS = ThreadLocal.withInitial(java.util.ArrayDeque::new);

    private static class_1657 userFor(Object block, class_2338 pos) {
        for (Object[] frame : USERS.get()) {
            if (frame[0] == block && pos.equals(frame[1])) {
                return (class_1657) frame[2];
            }
        }
        return null;
    }

    private static boolean inUse(Object block, class_2338 pos) {
        for (Object[] frame : USERS.get()) {
            if (frame[0] == block && pos.equals(frame[1])) {
                return true;
            }
        }
        return false;
    }

    /**
     * 1.20.6 button.press(state, level, pos): power the button, update its neighbours, schedule the release. 1.21's
     * press(state, level, pos, player) also plays the click and emits the activation event, which 1.20.6's use
     * did after press; a 1.20.6 caller pressing a button got neither.
     */
    public static void press(class_2269 button, class_2680 state, class_1937 level, class_2338 pos) {
        if (button instanceof LegacyPress legacy) {
            legacy.method_21845(state, level, pos);
        } else {
            legacyPress(button, state, level, pos);
        }
    }

    private static void legacyPress(class_2269 button, class_2680 state, class_1937 level, class_2338 pos) {
        level.method_8652(pos, state.method_11657(class_2269.field_10729, Boolean.TRUE), 3);
        ((CbWorldButtonAccessor) button).cb$updateNeighbours(state, level, pos);
        level.method_39279(pos, button, ((CbWorldButtonAccessor) button).cb$ticksToStayPressed());
    }

    /** 1.21's press(state, level, pos, player) on a 1.20.6 override: its press, with the player kept for its super call */
    public static void press(LegacyPress self, class_2680 state, class_1937 level, class_2338 pos, class_1657 player) {
        java.util.ArrayDeque<Object[]> users = USERS.get();
        users.push(new Object[] {self, pos, player});
        try {
            self.method_21845(state, level, pos);
        } finally {
            users.pop();
        }
    }

    /**
     * A 1.20.6 super.press(state, level, pos): inside 1.21's call (a player pressed it) 1.21's own press with that
     * player -- which now plays the click 1.20.6's use played after press; outside one, 1.20.6's body.
     */
    public static void superPress(class_2269 button, class_2680 state, class_1937 level, class_2338 pos) {
        if (inUse(button, pos)) {
            class_1657 player = userFor(button, pos);
            Guard.cbEnter(button);
            try {
                button.method_21845(state, level, pos, player);
            } finally {
                Guard.cbExit(button);
            }
        } else {
            legacyPress(button, state, level, pos);
        }
    }

    /**
     * 1.20.6 lever.pull(state, level, pos): toggle the lever, update its neighbours, answer the new state. 1.21's
     * pull(state, level, pos, player) also plays the click and emits the event 1.20.6's use did afterwards.
     */
    public static class_2680 pull(class_2401 lever, class_2680 state, class_1937 level, class_2338 pos) {
        if (lever instanceof LegacyPull legacy) {
            return legacy.method_21846(state, level, pos);
        }
        return legacyPull(lever, state, level, pos);
    }

    private static class_2680 legacyPull(class_2401 lever, class_2680 state, class_1937 level, class_2338 pos) {
        class_2680 pulled = state.method_28493(class_2401.field_11265);
        level.method_8652(pos, pulled, 3);
        ((CbWorldLeverInvoker) lever).cb$updateNeighbours(pulled, level, pos);
        return pulled;
    }

    /** 1.21's pull(state, level, pos, player) on a 1.20.6 override */
    public static void pull(LegacyPull self, class_2680 state, class_1937 level, class_2338 pos, class_1657 player) {
        java.util.ArrayDeque<Object[]> users = USERS.get();
        users.push(new Object[] {self, pos, player});
        try {
            self.method_21846(state, level, pos);
        } finally {
            users.pop();
        }
    }

    /** a 1.20.6 super.pull(state, level, pos): as for press -- 1.21's inside its call, 1.20.6's body outside */
    public static class_2680 superPull(class_2401 lever, class_2680 state, class_1937 level, class_2338 pos) {
        if (inUse(lever, pos)) {
            class_1657 player = userFor(lever, pos);
            Guard.cbEnter(lever);
            try {
                lever.method_21846(state, level, pos, player);
            } finally {
                Guard.cbExit(lever);
            }
            return state.method_28493(class_2401.field_11265);
        }
        return legacyPull(lever, state, level, pos);
    }

    // ------------------------------------------------------------------------------------------------ entities that act
    /** 1.20.6's Entity where 1.21 takes a LivingEntity: the entity when it is one, else none (1.21 records no source then) */
    public static class_1309 living(class_1297 entity) {
        return entity instanceof class_1309 living ? living : null;
    }

    /** 1.20.6 LecternBlock.tryPlaceBook(entity, ...): 1.21's with the entity as the event's source when it lives */
    public static boolean tryPlaceBook(class_1297 entity, class_1937 level, class_2338 pos, class_2680 state, class_1799 book) {
        return class_3715.method_17472(living(entity), level, pos, state, book);
    }

    // ------------------------------------------------------------------------------------------------ crafter
    /** 1.20.6 CrafterBlock.getPotentialResults(level, container): 1.21's on the container's crafting input */
    public static Optional<?> getPotentialResults(class_1937 level, class_8566 container) {
        return class_8886.method_54475(level, CbItemRecipes.cbCraftingInput(container));
    }

    // ------------------------------------------------------------------------------------------------ beacon beam
    /** 1.20.6 new BeaconBeamSection(float[] rgb): 1.21's takes the colour as an opaque ARGB int */
    public static net.minecraft.class_2580.class_2581 newBeamSection(float[] rgb) {
        return new net.minecraft.class_2580.class_2581(beamColor(rgb));
    }

    /** 1.20.6's float[] {r, g, b} (0 to 1) as 1.21's opaque ARGB int */
    public static int beamColor(float[] rgb) {
        return net.minecraft.class_5253.class_5254.method_59554(1.0F, rgb[0], rgb[1], rgb[2]);
    }

    /** 1.20.6 section.getColor(): the colour as 1.20.6's float[] {r, g, b} (0 to 1) */
    public static float[] beamColor(int argb) {
        return new float[] {
            net.minecraft.class_5253.class_5254.method_27765(argb) / 255.0F,
            net.minecraft.class_5253.class_5254.method_27766(argb) / 255.0F,
            net.minecraft.class_5253.class_5254.method_27767(argb) / 255.0F};
    }

    // ------------------------------------------------------------------------------------------------ dispenser
    /** the 1.20.6 addItem(stack) a 1.20.6 dispenser block entity subclass overrides */
    public interface LegacyAddItem {
        int method_11075(class_1799 stack);
    }

    /** 1.20.6 dispenser.addItem(stack): the whole stack into the first empty slot, its index; -1 when none is empty */
    public static int addItem(class_2601 dispenser, class_1799 stack) {
        if (dispenser instanceof LegacyAddItem legacy) {
            return legacy.method_11075(stack);
        }
        return superAddItem(dispenser, stack);
    }

    public static int superAddItem(class_2601 dispenser, class_1799 stack) {
        for (int i = 0; i < dispenser.method_5439(); i++) {
            if (dispenser.method_5438(i).method_7960()) {
                dispenser.method_5447(i, stack);
                return i;
            }
        }
        return -1;
    }

    /**
     * 1.21's insertItem(stack) on a 1.20.6 override of addItem: what is left of the stack -- nothing when the
     * override took it into a slot (1.20.6's addItem took the whole stack), the stack itself when it found none.
     */
    public static class_1799 insertItem(LegacyAddItem self, class_1799 stack) {
        return self.method_11075(stack) >= 0 ? class_1799.field_8037 : stack;
    }

    // ------------------------------------------------------------------------------------------------ jukebox
    /** the 1.20.6 isRecordPlaying() a 1.20.6 jukebox block entity subclass overrides */
    public interface LegacyRecordPlaying {
        boolean method_44373();
    }

    public static boolean isRecordPlaying(class_2619 jukebox) {
        if (jukebox instanceof LegacyRecordPlaying legacy) {
            return legacy.method_44373();
        }
        return superIsRecordPlaying(jukebox);
    }

    /** 1.20.6 JukeboxBlockEntity.isRecordPlaying(): a disc in it and its song playing (1.21's song player) */
    public static boolean superIsRecordPlaying(class_2619 jukebox) {
        return !jukebox.method_54079().method_7960() && jukebox.method_60784().method_60754();
    }

    // ------------------------------------------------------------------------------------------------ campfire
    /** 1.20.6 campfire.placeFood(entity, stack, cookTime): 1.21's with the entity as the event's source when it lives */
    public static boolean placeFood(class_3924 campfire, class_1297 entity, class_1799 stack, int cookTime) {
        return campfire.method_17503(living(entity), stack, cookTime);
    }

    /** placeFood's entity for a 1.20.6 override entered directly: the one 1.20.6 named, as a living entity */
    public static class_1309 placingEntity(class_3924 campfire, class_1297 entity, class_1799 stack, int cookTime) {
        return living(entity);
    }

    // ------------------------------------------------------------------------------------------------ decorated pots
    /**
     * 1.20.6's DecoratedPotPatterns.BASE: the pot's base, a key of its own registry whose name was the texture's
     * ("decorated_pot_base"). 1.21 made patterns a registry of records and has no pattern for the base: the key is
     * 1.21's registry with 1.20.6's name, which location() answers as 1.20.6 did.
     */
    public static class_5321<?> field_42787() {
        return class_5321.method_29179(class_7924.field_42941, class_2960.method_60656("decorated_pot_base"));
    }

    /**
     * 1.20.6 DecoratedPotPatterns.location(key): the pattern's texture, entity/decorated_pot/&lt;name&gt; -- 1.20.6's keys
     * were named after their texture. A 1.21 key (getResourceKey answers those now) names its pattern's asset
     * instead; a key 1.21's registry does not know is a 1.20.6 name.
     */
    public static class_2960 location(class_5321<?> key) {
        return patternAsset(key).method_45138("entity/decorated_pot/");
    }

    /** the texture name a pattern key stands for, 1.21's pattern asset or 1.20.6's key name (also for s6client's Sheets) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static class_2960 patternAsset(class_5321<?> key) {
        class_9766 pattern = (class_9766) ((class_2378) class_7923.field_42940).method_29107((class_5321) key);
        return pattern != null ? pattern.comp_2801() : key.method_29177();
    }

    /**
     * 1.20.6 DecoratedPotPatterns.bootstrap(registry of String): every pattern under a key named after its texture,
     * the texture's name as the value, the base last; answers the base's value.
     */
    public static String bootstrap(class_2378<String> registry) {
        class_2370<class_9766> patterns = new class_2370<>(class_7924.field_42941, com.mojang.serialization.Lifecycle.stable());
        class_8173.method_49207(patterns);
        for (class_9766 pattern : patterns) {
            class_2960 asset = pattern.comp_2801();
            class_2378.method_39197(registry, class_5321.method_29179(registry.method_30517(), asset), asset.method_12832());
        }
        return class_2378.method_39197(registry, class_5321.method_29179(registry.method_30517(),
            class_2960.method_60656("decorated_pot_base")), "decorated_pot_base");
    }

    private static volatile PotPatternNames potPatternNames;

    /**
     * 1.20.6 BuiltInRegistries.DECORATED_POT_PATTERNS, a registry of the patterns' texture names, which 1.21 made a
     * registry of DecoratedPotPattern records under the same key: a registry of names over 1.21's (PotPatternNames).
     */
    @SuppressWarnings("unchecked")
    public static class_2378<String> field_42940() {
        PotPatternNames names = potPatternNames;
        if (names == null) {
            synchronized (CbWorldBlocks.class) {
                names = potPatternNames;
                if (names == null) {
                    potPatternNames = names = new PotPatternNames();
                }
            }
        }
        return names;
    }

    /**
     * The names view: 1.21's patterns as 1.20.6 registered them -- each pattern's key with the name of its texture
     * as the value (1.21's vanilla keys stay 1.21's; 1.20.6's own were named after the texture, and the texture is
     * what location(key) answers either way). A 1.20.6 mod's registration goes into 1.21's registry as well, a
     * pattern whose asset is the key's name -- the texture 1.20.6 drew it with, entity/decorated_pot/&lt;name&gt;.
     */
    static final class PotPatternNames extends class_2370<String> {
        @SuppressWarnings({"unchecked", "rawtypes"})
        PotPatternNames() {
            super((class_5321) class_7924.field_42941, com.mojang.serialization.Lifecycle.stable());
            for (java.util.Map.Entry<class_5321<class_9766>, class_9766> entry : class_7923.field_42940.method_29722()) {
                super.method_10272((class_5321) entry.getKey(), entry.getValue().comp_2801().method_12832(),
                    net.minecraft.class_9248.field_49136);
            }
        }

        @Override
        @SuppressWarnings({"unchecked", "rawtypes"})
        public net.minecraft.class_6880.class_6883<String> method_10272(class_5321<String> key, String value, net.minecraft.class_9248 info) {
            net.minecraft.class_6880.class_6883<String> holder = super.method_10272(key, value, info);
            class_2378 patterns = class_7923.field_42940;
            if (!patterns.method_35842((class_5321) key)) {
                class_2378.method_39197(patterns, (class_5321) key, new class_9766(key.method_29177()));
            }
            return holder;
        }
    }

    private CbWorldBlocks() {
    }
}

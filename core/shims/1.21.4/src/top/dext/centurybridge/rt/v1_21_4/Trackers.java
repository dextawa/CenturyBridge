package top.dext.centurybridge.rt.v1_21_4;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.class_2960;
import net.minecraft.class_8779;

/**
 * Identity maps rebuilt on each datapack (re)load. 1.20.2 moved ids out of
 * Recipe and Advancement into wrapper entries; legacy getId()-style calls are
 * answered from here (populated by RecipeManagerBridge / AdvancementLoaderBridge).
 */
public final class Trackers {

    public static final Map<Object, class_2960> RECIPE_IDS =
        Collections.synchronizedMap(new IdentityHashMap<>());

    public static final Map<Object, class_8779> ADVANCEMENT_ENTRIES =
        Collections.synchronizedMap(new IdentityHashMap<>());

    private Trackers() {
    }
}

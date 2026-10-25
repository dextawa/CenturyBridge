package top.dext.centurybridge.rt.v1_20_2;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.class_8779;

/**
 * Identity maps rebuilt on each datapack (re)load. 1.20.2 moved ids out of
 * Advancement into wrapper entries; legacy getId()-style calls are answered
 * from here (populated by AdvancementLoaderBridgeHooks). Recipe ids live in
 * CbRecipeIds.
 */
public final class Trackers {

    public static final Map<Object, class_8779> ADVANCEMENT_ENTRIES =
        Collections.synchronizedMap(new IdentityHashMap<>());

    private Trackers() {
    }
}

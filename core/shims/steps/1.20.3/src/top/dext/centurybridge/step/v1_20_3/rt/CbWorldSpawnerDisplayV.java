package top.dext.centurybridge.step.v1_20_3.rt;

import net.minecraft.class_1297;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_5819;

/**
 * Marks a BaseSpawner class converted from 1.20.2 that overrides 1.20.2's getOrCreateDisplayEntity(level, random,
 * pos) (its own method implements it): a 1.20.2 call reaches the override through this, as 1.20.2's virtual call
 * did (CbWorldEntities.getOrCreateDisplayEntity).
 */
public interface CbWorldSpawnerDisplayV {
    class_1297 method_8283(class_1937 level, class_5819 random, class_2338 pos);
}

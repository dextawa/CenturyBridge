package top.dext.centurybridge.step.v1_20_3.facade;

import net.minecraft.class_1922;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import net.minecraft.class_2794;
import net.minecraft.class_2975;
import net.minecraft.class_3218;
import net.minecraft.class_5321;
import net.minecraft.class_5819;
import net.minecraft.class_6880;
import net.minecraft.class_7924;

/**
 * 1.20.2 AbstractMegaTreeGrower (gone in 1.20.3): a grower that first looks for a 2x2 of its sapling and
 * grows the mega feature there, else grows as an AbstractTreeGrower. 1.20.2's code, line for line.
 */
public abstract class AbstractMegaTreeGrower extends AbstractTreeGrower {

    public AbstractMegaTreeGrower() {
    }

    @Override
    public boolean method_11431(class_3218 level, class_2794 generator, class_2338 pos, class_2680 state, class_5819 random) {
        for (int x = 0; x >= -1; --x) {
            for (int z = 0; z >= -1; --z) {
                if (method_11442(state, level, pos, x, z)) {
                    return this.method_11444(level, generator, pos, state, random, x, z);
                }
            }
        }
        return super.method_11431(level, generator, pos, state, random);
    }

    public abstract class_5321<class_2975<?, ?>> method_11443(class_5819 random);

    public boolean method_11444(class_3218 level, class_2794 generator, class_2338 pos, class_2680 state, class_5819 random,
                                int x, int z) {
        class_5321<class_2975<?, ?>> key = this.method_11443(random);
        if (key == null) {
            return false;
        }
        class_6880<class_2975<?, ?>> holder = level.method_30349().method_30530(class_7924.field_41239).method_40264(key).orElse(null);
        if (holder == null) {
            return false;
        }
        class_2975<?, ?> feature = holder.comp_349();
        class_2680 air = class_2246.field_10124.method_9564();
        level.method_8652(pos.method_10069(x, 0, z), air, 4);
        level.method_8652(pos.method_10069(x + 1, 0, z), air, 4);
        level.method_8652(pos.method_10069(x, 0, z + 1), air, 4);
        level.method_8652(pos.method_10069(x + 1, 0, z + 1), air, 4);
        if (feature.method_12862(level, generator, random, pos.method_10069(x, 0, z))) {
            return true;
        }
        level.method_8652(pos.method_10069(x, 0, z), state, 4);
        level.method_8652(pos.method_10069(x + 1, 0, z), state, 4);
        level.method_8652(pos.method_10069(x, 0, z + 1), state, 4);
        level.method_8652(pos.method_10069(x + 1, 0, z + 1), state, 4);
        return false;
    }

    public static boolean method_11442(class_2680 state, class_1922 level, class_2338 pos, int x, int z) {
        class_2248 block = state.method_26204();
        return level.method_8320(pos.method_10069(x, 0, z)).method_27852(block)
            && level.method_8320(pos.method_10069(x + 1, 0, z)).method_27852(block)
            && level.method_8320(pos.method_10069(x, 0, z + 1)).method_27852(block)
            && level.method_8320(pos.method_10069(x + 1, 0, z + 1)).method_27852(block);
    }
}

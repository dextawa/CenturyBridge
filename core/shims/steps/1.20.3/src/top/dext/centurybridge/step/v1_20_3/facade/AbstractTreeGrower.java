package top.dext.centurybridge.step.v1_20_3.facade;

import java.util.Iterator;
import net.minecraft.class_1936;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import net.minecraft.class_2794;
import net.minecraft.class_2975;
import net.minecraft.class_3218;
import net.minecraft.class_3481;
import net.minecraft.class_5321;
import net.minecraft.class_5819;
import net.minecraft.class_6880;
import net.minecraft.class_7924;

/**
 * 1.20.2 AbstractTreeGrower (gone in 1.20.3, whose saplings take a TreeGrower record). 1.20.2's growTree,
 * line for line: the grower picks a configured feature for the random source and whether flowers are
 * near, the sapling is swapped for the fluid it stood in, and the feature is placed (or the sapling
 * put back). A sapling built through the 1.20.2 constructor grows through this (CbWorldSaplingMixin).
 */
public abstract class AbstractTreeGrower {

    public AbstractTreeGrower() {
    }

    public abstract class_5321<class_2975<?, ?>> method_11430(class_5819 random, boolean hasFlowers);

    public boolean method_11431(class_3218 level, class_2794 generator, class_2338 pos, class_2680 state, class_5819 random) {
        class_5321<class_2975<?, ?>> key = this.method_11430(random, this.method_24282(level, pos));
        if (key == null) {
            return false;
        }
        class_6880<class_2975<?, ?>> holder = level.method_30349().method_30530(class_7924.field_41239).method_40264(key).orElse(null);
        if (holder == null) {
            return false;
        }
        class_2975<?, ?> feature = holder.comp_349();
        class_2680 fluid = level.method_8316(pos).method_15759();
        level.method_8652(pos, fluid, 4);
        if (feature.method_12862(level, generator, random, pos)) {
            if (level.method_8320(pos) == fluid) {
                level.method_8413(pos, state, fluid, 2);
            }
            return true;
        }
        level.method_8652(pos, state, 4);
        return false;
    }

    /** 1.20.2: any flower in the 5x3x5 box around the sapling */
    public boolean method_24282(class_1936 level, class_2338 pos) {
        Iterator<class_2338> it = class_2338.method_10097(pos.method_10074().method_10076(2).method_10088(2),
            pos.method_10084().method_10077(2).method_10089(2)).iterator();
        while (it.hasNext()) {
            if (level.method_8320(it.next()).method_26164(class_3481.field_20339)) {
                return true;
            }
        }
        return false;
    }
}

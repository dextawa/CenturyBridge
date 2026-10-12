package top.dext.centurybridge.step.v1_20_3.facade;

import net.minecraft.class_2975;
import net.minecraft.class_5321;
import net.minecraft.class_5819;
import net.minecraft.class_6808;

/** 1.20.2 CherryTreeGrower (gone in 1.20.3, replaced by a TreeGrower constant): its feature choice, as 1.20.2 made it. */
public class CherryTreeGrower extends AbstractTreeGrower {

    public CherryTreeGrower() {
    }

    @Override
    public class_5321<class_2975<?, ?>> method_11430(class_5819 random, boolean hasFlowers) {
        return hasFlowers ? class_6808.field_42959 : class_6808.field_42960;
    }

}

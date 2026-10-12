package top.dext.centurybridge.step.v1_20_3.facade;

import net.minecraft.class_2975;
import net.minecraft.class_5321;
import net.minecraft.class_5819;
import net.minecraft.class_6808;

/** 1.20.2 SpruceTreeGrower (gone in 1.20.3, replaced by a TreeGrower constant): its feature choice, as 1.20.2 made it. */
public class SpruceTreeGrower extends AbstractMegaTreeGrower {

    public SpruceTreeGrower() {
    }

    @Override
    public class_5321<class_2975<?, ?>> method_11430(class_5819 random, boolean hasFlowers) {
        return class_6808.field_35909;
    }

    @Override
    public class_5321<class_2975<?, ?>> method_11443(class_5819 random) {
        return random.method_43056() ? class_6808.field_35915 : class_6808.field_35916;
    }
}

package top.dext.centurybridge.step.v1_20_3.facade;

import net.minecraft.class_2975;
import net.minecraft.class_5321;
import net.minecraft.class_5819;
import net.minecraft.class_6808;

/** 1.20.2 MangroveTreeGrower (gone in 1.20.3, replaced by a TreeGrower constant): its feature choice, as 1.20.2 made it. */
public class MangroveTreeGrower extends AbstractTreeGrower {

    /** 1.20.2: the chance of the tall mangrove (private there) */
    public final float field_38758;

    public MangroveTreeGrower(float tallProbability) {
        this.field_38758 = tallProbability;
    }

    @Override
    public class_5321<class_2975<?, ?>> method_11430(class_5819 random, boolean hasFlowers) {
        return random.method_43057() < this.field_38758 ? class_6808.field_38811 : class_6808.field_38810;
    }

}

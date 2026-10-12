package top.dext.centurybridge.step.v1_20_3.facade;

import net.minecraft.class_2195;
import net.minecraft.class_2248;
import net.minecraft.class_2513;
import net.minecraft.class_4970;

/**
 * 1.20.2 StemGrownBlock (gone in 1.20.3): the fruit of a stem, naming its stem and attached stem. 1.20.3's
 * stems name their fruit by registry key instead; a stem built through a 1.20.2 constructor around one of
 * these fruits (CbWorldStems) grows and attaches through these two methods, as in 1.20.2.
 */
public abstract class StemGrownBlock extends class_2248 {

    public StemGrownBlock(class_4970.class_2251 properties) {
        super(properties);
    }

    public abstract class_2513 method_10679();

    public abstract class_2195 method_10680();
}

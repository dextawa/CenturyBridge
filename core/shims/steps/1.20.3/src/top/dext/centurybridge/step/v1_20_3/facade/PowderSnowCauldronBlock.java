package top.dext.centurybridge.step.v1_20_3.facade;

import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.class_1792;
import net.minecraft.class_1937;
import net.minecraft.class_1959;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import net.minecraft.class_4970;
import net.minecraft.class_5556;
import net.minecraft.class_5620;
import top.dext.centurybridge.step.v1_20_3.rt.CbWorldBlocks;

/**
 * 1.20.2 PowderSnowCauldronBlock (gone in 1.20.3, whose powder snow cauldron is a LayeredCauldronBlock
 * filling on SNOW): a layered cauldron with the 1.20.2 fill predicate (see CbWorldBlocks.fillPrecipitation)
 * that melts into a water cauldron of the same level when a burning entity is put out in it.
 */
public class PowderSnowCauldronBlock extends class_5556 {

    public PowderSnowCauldronBlock(class_4970.class_2251 properties, Predicate<class_1959.class_1963> fill,
                                   Map<class_1792, class_5620> interactions) {
        super(CbWorldBlocks.fillPrecipitation(fill), CbWorldBlocks.interactionMap(interactions), properties);
        CbWorldBlocks.legacyPowderSnowCauldron(this, fill);
    }

    /**
     * 1.20.2's body: lower the level of a water cauldron standing in for this one. 1.20.3 made the
     * method private; the layer's access widener opens it (extendable) so this override is reached.
     */
    public void method_36994(class_2680 state, class_1937 level, class_2338 pos) {
        class_5556.method_31650(class_2246.field_27097.method_9564().method_11657(class_5556.field_27206,
            state.method_11654(class_5556.field_27206)), level, pos);
    }
}

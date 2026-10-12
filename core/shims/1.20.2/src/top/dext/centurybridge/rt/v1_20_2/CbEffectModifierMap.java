package top.dext.centurybridge.rt.v1_20_2;

import java.util.Map;
import net.minecraft.class_1320;
import net.minecraft.class_8630;

/**
 * An effect's own attribute-modifier map (1.20.2's field, attribute to template), added to
 * MobEffect by CbEffectModifierOwner: what 1.20.1's addAttributeModifiers /
 * removeAttributeModifiers read (its field, never the overridable getAttributeModifiers()).
 */
public interface CbEffectModifierMap {

    Map<class_1320, class_8630> cb$modifierMap();
}

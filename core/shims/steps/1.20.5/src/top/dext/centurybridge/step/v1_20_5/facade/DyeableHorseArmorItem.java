package top.dext.centurybridge.step.v1_20_5.facade;

/**
 * 1.20.4's DyeableHorseArmorItem (class_4058; classRenames): a horse armor item that is a DyeableLeatherItem
 * (1.20.4's leather horse armor). 1.20.5's is an AnimalArmorItem with an overlay texture in the
 * minecraft:dyeable tag; this one is a 1.20.4 HorseArmorItem (rt/CbFinItemHorseArmor) and joins the tag.
 */
public class DyeableHorseArmorItem extends net.minecraft.class_4059 implements DyeableLeatherItem {

    public DyeableHorseArmorItem(int protection, String identifier, net.minecraft.class_1792.class_1793 properties) {
        super(top.dext.centurybridge.step.v1_20_5.rt.CbFinItemHorseArmor.material(protection, identifier),
            net.minecraft.class_4059.class_9076.field_47825, false, properties);
        top.dext.centurybridge.step.v1_20_5.rt.CbFinItemHorseArmor.remember(this, protection, identifier);
    }
}

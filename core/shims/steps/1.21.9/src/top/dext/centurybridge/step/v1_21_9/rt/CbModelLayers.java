package top.dext.centurybridge.step.v1_21_9.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5601;
import net.minecraft.class_5602;

/**
 * 1.21.8's ModelLayers inner / outer armor locations (fieldRedirects of s9d-armorlayers.json), each the location
 * {@link CbArmorLayers} puts 1.21.8's whole armor mesh under. Paired by model name: 1.21.8's
 * registerInnerArmor(name) / registerOuterArmor(name) and 1.21.9's registerArmorSet(name) in ModelLayers' static
 * initializers (javap -c), one 1.21.9 set for every 1.21.8 pair, 22 of 22.
 */
@Environment(EnvType.CLIENT)
public final class CbModelLayers {
    private CbModelLayers() {
    }

    /** 1.21.8 armor_stand#inner_armor */
    public static final class_5601 field_27639 = CbArmorLayers.inner(class_5602.field_61719);

    /** 1.21.8 armor_stand#outer_armor */
    public static final class_5601 field_27677 = CbArmorLayers.outer(class_5602.field_61719);

    /** 1.21.8 armor_stand_small#inner_armor */
    public static final class_5601 field_53015 = CbArmorLayers.inner(class_5602.field_61720);

    /** 1.21.8 armor_stand_small#outer_armor */
    public static final class_5601 field_53016 = CbArmorLayers.outer(class_5602.field_61720);

    /** 1.21.8 bogged#inner_armor */
    public static final class_5601 field_49160 = CbArmorLayers.inner(class_5602.field_61684);

    /** 1.21.8 bogged#outer_armor */
    public static final class_5601 field_49161 = CbArmorLayers.outer(class_5602.field_61684);

    /** 1.21.8 drowned#inner_armor */
    public static final class_5601 field_27555 = CbArmorLayers.inner(class_5602.field_61687);

    /** 1.21.8 drowned#outer_armor */
    public static final class_5601 field_27556 = CbArmorLayers.outer(class_5602.field_61687);

    /** 1.21.8 drowned_baby#inner_armor */
    public static final class_5601 field_52972 = CbArmorLayers.inner(class_5602.field_61686);

    /** 1.21.8 drowned_baby#outer_armor */
    public static final class_5601 field_52973 = CbArmorLayers.outer(class_5602.field_61686);

    /** 1.21.8 giant#inner_armor */
    public static final class_5601 field_27570 = CbArmorLayers.inner(class_5602.field_61696);

    /** 1.21.8 giant#outer_armor */
    public static final class_5601 field_27571 = CbArmorLayers.outer(class_5602.field_61696);

    /** 1.21.8 husk#inner_armor */
    public static final class_5601 field_27605 = CbArmorLayers.inner(class_5602.field_61695);

    /** 1.21.8 husk#outer_armor */
    public static final class_5601 field_27606 = CbArmorLayers.outer(class_5602.field_61695);

    /** 1.21.8 husk_baby#inner_armor */
    public static final class_5601 field_52967 = CbArmorLayers.inner(class_5602.field_61694);

    /** 1.21.8 husk_baby#outer_armor */
    public static final class_5601 field_52968 = CbArmorLayers.outer(class_5602.field_61694);

    /** 1.21.8 piglin#inner_armor */
    public static final class_5601 field_27626 = CbArmorLayers.inner(class_5602.field_61700);

    /** 1.21.8 piglin#outer_armor */
    public static final class_5601 field_27574 = CbArmorLayers.outer(class_5602.field_61700);

    /** 1.21.8 piglin_baby#inner_armor */
    public static final class_5601 field_52977 = CbArmorLayers.inner(class_5602.field_61698);

    /** 1.21.8 piglin_baby#outer_armor */
    public static final class_5601 field_52978 = CbArmorLayers.outer(class_5602.field_61698);

    /** 1.21.8 piglin_brute#inner_armor */
    public static final class_5601 field_27624 = CbArmorLayers.inner(class_5602.field_61699);

    /** 1.21.8 piglin_brute#outer_armor */
    public static final class_5601 field_27625 = CbArmorLayers.outer(class_5602.field_61699);

    /** 1.21.8 player#inner_armor */
    public static final class_5601 field_27579 = CbArmorLayers.inner(class_5602.field_61701);

    /** 1.21.8 player#outer_armor */
    public static final class_5601 field_27580 = CbArmorLayers.outer(class_5602.field_61701);

    /** 1.21.8 player_slim#inner_armor */
    public static final class_5601 field_27582 = CbArmorLayers.inner(class_5602.field_61697);

    /** 1.21.8 player_slim#outer_armor */
    public static final class_5601 field_27583 = CbArmorLayers.outer(class_5602.field_61697);

    /** 1.21.8 skeleton#inner_armor */
    public static final class_5601 field_27651 = CbArmorLayers.inner(class_5602.field_61708);

    /** 1.21.8 skeleton#outer_armor */
    public static final class_5601 field_27652 = CbArmorLayers.outer(class_5602.field_61708);

    /** 1.21.8 stray#inner_armor */
    public static final class_5601 field_27661 = CbArmorLayers.inner(class_5602.field_61709);

    /** 1.21.8 stray#outer_armor */
    public static final class_5601 field_27662 = CbArmorLayers.outer(class_5602.field_61709);

    /** 1.21.8 wither_skeleton#inner_armor */
    public static final class_5601 field_27632 = CbArmorLayers.inner(class_5602.field_61712);

    /** 1.21.8 wither_skeleton#outer_armor */
    public static final class_5601 field_27633 = CbArmorLayers.outer(class_5602.field_61712);

    /** 1.21.8 zombie#inner_armor */
    public static final class_5601 field_27642 = CbArmorLayers.inner(class_5602.field_61714);

    /** 1.21.8 zombie#outer_armor */
    public static final class_5601 field_27643 = CbArmorLayers.outer(class_5602.field_61714);

    /** 1.21.8 zombie_baby#inner_armor */
    public static final class_5601 field_53009 = CbArmorLayers.inner(class_5602.field_61713);

    /** 1.21.8 zombie_baby#outer_armor */
    public static final class_5601 field_53010 = CbArmorLayers.outer(class_5602.field_61713);

    /** 1.21.8 zombie_villager#inner_armor */
    public static final class_5601 field_27645 = CbArmorLayers.inner(class_5602.field_61718);

    /** 1.21.8 zombie_villager#outer_armor */
    public static final class_5601 field_27646 = CbArmorLayers.outer(class_5602.field_61718);

    /** 1.21.8 zombie_villager_baby#inner_armor */
    public static final class_5601 field_53012 = CbArmorLayers.inner(class_5602.field_61717);

    /** 1.21.8 zombie_villager_baby#outer_armor */
    public static final class_5601 field_53013 = CbArmorLayers.outer(class_5602.field_61717);

    /** 1.21.8 zombified_piglin#inner_armor */
    public static final class_5601 field_27648 = CbArmorLayers.inner(class_5602.field_61711);

    /** 1.21.8 zombified_piglin#outer_armor */
    public static final class_5601 field_27649 = CbArmorLayers.outer(class_5602.field_61711);

    /** 1.21.8 zombified_piglin_baby#inner_armor */
    public static final class_5601 field_53002 = CbArmorLayers.inner(class_5602.field_61710);

    /** 1.21.8 zombified_piglin_baby#outer_armor */
    public static final class_5601 field_53003 = CbArmorLayers.outer(class_5602.field_61710);
}

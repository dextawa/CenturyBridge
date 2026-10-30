package cb.smx4b;

/**
 * Registers two 1.20.4 armor items of this mod's own (DyeableArmorItem and a plain ArmorItem subclass), so Driver's
 * "dyeable-classes" case can ask 1.20.4's instanceof DyeableLeatherItem of a mod subclass after the climb: the dyeable
 * one is, the plain one is not (CbFinItemArmor.declaresDyeable must not count the interface CbFinItemDyeableArmorMixin
 * gives vanilla's ArmorItem, wherever a later step's fold moves that mixin).
 */
public final class ModInit implements net.fabricmc.api.ModInitializer {
    public static net.minecraft.class_1792 dyeHelmet;
    public static net.minecraft.class_1792 plainHelmet;

    @Override
    public void onInitialize() {
        dyeHelmet = net.minecraft.class_2378.method_10230(net.minecraft.class_7923.field_41178,
            new net.minecraft.class_2960("cb_smx4b", "dye_helmet"), new DyeHelmet());
        plainHelmet = net.minecraft.class_2378.method_10230(net.minecraft.class_7923.field_41178,
            new net.minecraft.class_2960("cb_smx4b", "plain_helmet"), new PlainHelmet());
    }

    static final class DyeHelmet extends net.minecraft.class_4057 {
        DyeHelmet() {
            super(net.minecraft.class_1740.field_7897, net.minecraft.class_1738.class_8051.field_41934, new net.minecraft.class_1792.class_1793());
        }
    }

    static final class PlainHelmet extends net.minecraft.class_1738 {
        PlainHelmet() {
            super(net.minecraft.class_1740.field_7892, net.minecraft.class_1738.class_8051.field_41934, new net.minecraft.class_1792.class_1793());
        }
    }
}

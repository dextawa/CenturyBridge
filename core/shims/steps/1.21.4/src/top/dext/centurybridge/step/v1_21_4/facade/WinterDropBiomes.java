package top.dext.centurybridge.step.v1_21_4.facade;

import net.minecraft.class_1959;
import net.minecraft.class_1972;
import net.minecraft.class_2960;
import net.minecraft.class_5321;
import net.minecraft.class_5478;
import net.minecraft.class_7891;
import net.minecraft.class_7924;

/**
 * 1.21.3's WinterDropBiomes (net/minecraft/class_10351, classRenames), gone in 1.21.4 where the Winter Drop is
 * vanilla. 1.21.3's bodies: PALE_GARDEN is minecraft:pale_garden (1.21.4's Biomes.PALE_GARDEN), createKey and
 * register as they were, bootstrap registers the pale garden as 1.21.3 built it (OverworldBiomes.darkForest(..,
 * true), the same builder 1.21.4's own bootstrap uses for it).
 */
public class WinterDropBiomes {

    /** PALE_GARDEN */
    public static final class_5321<class_1959> field_54881 = class_1972.field_55052;

    public WinterDropBiomes() {
    }

    /** createKey(name) */
    public static class_5321<class_1959> method_64956(String name) {
        return class_5321.method_29179(class_7924.field_41236, class_2960.method_60656(name));
    }

    /** register(context, name, biome) */
    public static void method_64958(class_7891<class_1959> context, String name, class_1959 biome) {
        context.method_46838(method_64956(name), biome);
    }

    /** bootstrap(context) */
    public static void method_64957(class_7891<class_1959> context) {
        context.method_46838(field_54881, class_5478.method_30705(context.method_46799(class_7924.field_41245),
            context.method_46799(class_7924.field_41238), true));
    }
}

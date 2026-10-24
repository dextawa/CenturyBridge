package top.dext.centurybridge.step.v1_21_2.rt;

import net.minecraft.class_1799;
import net.minecraft.class_1852;
import net.minecraft.class_1856;
import net.minecraft.class_1865;
import net.minecraft.class_1874;
import net.minecraft.class_2378;
import net.minecraft.class_2960;
import net.minecraft.class_3859;
import net.minecraft.class_3861;
import net.minecraft.class_3862;
import net.minecraft.class_3920;
import net.minecraft.class_7709;
import net.minecraft.class_7923;
import top.dext.centurybridge.step.v1_21_2.facade.ShulkerBoxColoring;
import top.dext.centurybridge.step.v1_21_2.facade.SimpleCookingSerializer;
import top.dext.centurybridge.step.v1_21_2.facade.SuspiciousStewRecipe;

/**
 * 1.21.1's recipe serializers 1.21.2 dropped, and the members of its cooking serializer:
 * <ul>
 * <li>RecipeSerializer.SHULKER_BOX_COLORING / SUSPICIOUS_STEW: CustomRecipe serializers of the facades, registered
 *     under 1.21.1's ids while the built-in registries take entries ({@link #register}, CbItemInit), so a data pack
 *     naming them loads as on 1.21.1;</li>
 * <li>SimpleCookingSerializer (1.21.2's AbstractCookingRecipe.Serializer by classRenames): new builds the facade,
 *     which keeps the factory; create(..) on vanilla's four cooking serializers builds their recipe class, as their
 *     factories (the classes' constructors) did in 1.21.1.</li>
 * </ul>
 */
public final class CbItemRecipeSerializers {

    private CbItemRecipeSerializers() {
    }

    private static final class_1852.class_1866<ShulkerBoxColoring> SHULKER_BOX_COLORING =
        new class_1852.class_1866<>(ShulkerBoxColoring::new);
    private static final class_1852.class_1866<SuspiciousStewRecipe> SUSPICIOUS_STEW =
        new class_1852.class_1866<>(SuspiciousStewRecipe::new);

    /** RecipeSerializer.SHULKER_BOX_COLORING */
    public static class_1865<ShulkerBoxColoring> field_9041() {
        return SHULKER_BOX_COLORING;
    }

    /** RecipeSerializer.SUSPICIOUS_STEW */
    public static class_1865<SuspiciousStewRecipe> field_9030() {
        return SUSPICIOUS_STEW;
    }

    /** 1.21.1's ids for them (at mod initialization, before the built-in registries freeze) */
    static void register() {
        class_2378.method_10230(class_7923.field_41189, class_2960.method_60656("crafting_special_shulkerboxcoloring"),
            SHULKER_BOX_COLORING);
        class_2378.method_10230(class_7923.field_41189, class_2960.method_60656("crafting_special_suspiciousstew"),
            SUSPICIOUS_STEW);
    }

    /** new SimpleCookingSerializer(factory, defaultCookingTime): the facade, typed as 1.21.2's serializer */
    public static <T extends class_1874> class_1874.class_10285<T> newSimpleCookingSerializer(class_1874.class_3958<T> factory,
            int time) {
        return new SimpleCookingSerializer<>(factory, time);
    }

    /** SimpleCookingSerializer.create(..): the facade's factory, or vanilla's cooking class for its serializer */
    @SuppressWarnings("rawtypes")
    public static class_1874 method_55104(class_1874.class_10285 serializer, String group, class_7709 category,
            class_1856 ingredient, class_1799 result, float experience, int time) {
        if (serializer instanceof SimpleCookingSerializer s) {
            return s.method_55104(group, category, ingredient, result, experience, time);
        }
        if (serializer == class_1865.field_9042) {
            return new class_3861(group, category, ingredient, result, experience, time);
        }
        if (serializer == class_1865.field_17084) {
            return new class_3859(group, category, ingredient, result, experience, time);
        }
        if (serializer == class_1865.field_17085) {
            return new class_3862(group, category, ingredient, result, experience, time);
        }
        if (serializer == class_1865.field_17347) {
            return new class_3920(group, category, ingredient, result, experience, time);
        }
        throw new IllegalArgumentException("1.21.1's SimpleCookingSerializer.create: no factory known for " + serializer
            + " (a 1.21.2 serializer not built through SimpleCookingSerializer)");
    }
}

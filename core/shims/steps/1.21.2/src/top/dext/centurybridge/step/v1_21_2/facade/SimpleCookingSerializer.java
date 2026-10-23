package top.dext.centurybridge.step.v1_21_2.facade;

import net.minecraft.class_1799;
import net.minecraft.class_1856;
import net.minecraft.class_1874;
import net.minecraft.class_7709;

/**
 * 1.21.1's SimpleCookingSerializer (net/minecraft/class_3957), 1.21.2's AbstractCookingRecipe.Serializer with the
 * same codecs (compared on the bytecode) but without create(..): references are classRenamed to 1.21.2's class
 * (vanilla's cooking serializers are those); a converted subclass or construction gets this one, which keeps the
 * factory for create(..) (CbItemRecipeSerializers).
 */
public class SimpleCookingSerializer<T extends class_1874> extends class_1874.class_10285<T> {

    /** recipeFactory (public: an access widener never reaches a CenturyBridge class) */
    public final class_1874.class_3958<T> field_17552;

    public SimpleCookingSerializer(class_1874.class_3958<T> factory, int defaultCookingTime) {
        super(factory, defaultCookingTime);
        this.field_17552 = factory;
    }

    /** create(group, category, ingredient, result, experience, cookingTime): the factory's recipe */
    public T method_55104(String group, class_7709 category, class_1856 ingredient, class_1799 result, float experience, int time) {
        return field_17552.create(group, category, ingredient, result, experience, time);
    }
}

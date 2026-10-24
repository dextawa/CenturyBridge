package top.dext.centurybridge.step.v1_21_2.facade;

import net.minecraft.class_1799;
import net.minecraft.class_1856;
import net.minecraft.class_1874;
import net.minecraft.class_7709;

/**
 * 1.21.1's SimpleCookingSerializer (net/minecraft/class_3957), 1.21.2's AbstractCookingRecipe.Serializer: the same
 * JSON codec (compared on the bytecode), a stream codec that differs in one field (1.21.2 writes the cooking time as a
 * 4-byte int, 1.21.1 a VarInt; measured by cb-s7gear's byte round trip), and no create(..). References are
 * classRenamed to 1.21.2's class (vanilla's cooking serializers are those and keep 1.21.2's wire format); a converted
 * subclass or construction gets this one, which keeps the factory for create(..) and writes 1.21.1's bytes
 * (CbItemRecipeSerializers).
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

    private final net.minecraft.class_9139<net.minecraft.class_9129, T> legacyStreamCodec =
        net.minecraft.class_9139.method_56437(this::toNetwork, this::fromNetwork);

    /**
     * streamCodec(): 1.21.1's wire format -- group, category (enum ordinal), ingredient, result, experience, and the
     * cooking time as a VarInt where 1.21.2's AbstractCookingRecipe.Serializer writes a 4-byte int (the rest is the
     * same bytes, compared by cb-s7gear's round trip)
     */
    @Override
    public net.minecraft.class_9139<net.minecraft.class_9129, T> method_56104() {
        return legacyStreamCodec;
    }

    private void toNetwork(net.minecraft.class_9129 buf, T recipe) {
        buf.method_10814(recipe.method_8112());
        buf.method_10817(recipe.method_45438());
        class_1856.field_48355.encode(buf, recipe.method_64720());
        class_1799.field_48349.encode(buf, top.dext.centurybridge.step.v1_21_2.rt.CbItemRecipeClasses.field_9059(recipe));
        buf.writeFloat(recipe.method_8171());
        buf.method_10804(recipe.method_8167());
    }

    private T fromNetwork(net.minecraft.class_9129 buf) {
        String group = buf.method_19772();
        class_7709 category = buf.method_10818(class_7709.class);
        class_1856 ingredient = class_1856.field_48355.decode(buf);
        class_1799 result = class_1799.field_48349.decode(buf);
        float experience = buf.readFloat();
        int time = buf.method_10816();
        return field_17552.create(group, category, ingredient, result, experience, time);
    }
}

package top.dext.centurybridge.shims.v1_20_2;

import com.google.gson.JsonObject;
import net.minecraft.class_1860;
import net.minecraft.class_1866;
import net.minecraft.class_2960;
import net.minecraft.class_3518;
import net.minecraft.class_3955;
import net.minecraft.class_7710;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.rt.v1_20_2.CbRecipeJson;
import top.dext.centurybridge.rt.v1_20_2.CbRecipeSerializerSupport;
import top.dext.centurybridge.rt.v1_20_2.CbVanillaReads;

/**
 * SimpleCraftingRecipeSerializer's 1.20.1 read(id, json), 1.20.1's body: the category, then the serializer's
 * factory (1.20.2's create(category), run with the id as the one being decoded, which a converted 1.20.1 factory
 * takes; the id kept for getId()). 1.20.2's RecipeManager reads the special crafting recipes through it
 * (CbRecipeJsonVanillaMixin).
 */
@Mixin(value = class_1866.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRecipeJsonSpecial implements CbRecipeJson.Reader {

    @Shadow
    @Final
    private class_1866.class_7711<class_3955> field_9046;

    @CbVanillaReads("CbRecipeJsonVanillaMixin: RecipeManager.fromJson decodes special crafting recipes by calling it")
    public class_3955 method_45443(class_2960 id, JsonObject json) {
        class_7710 category = class_7710.field_40252.method_47920(class_3518.method_15253(json, "category", null), class_7710.field_40251);
        return CbRecipeSerializerSupport.withId(id, () -> this.field_9046.create(category));
    }

    @Override
    public class_1860<?> cb$readJson(class_2960 id, JsonObject json) {
        return this.method_45443(id, json);
    }
}

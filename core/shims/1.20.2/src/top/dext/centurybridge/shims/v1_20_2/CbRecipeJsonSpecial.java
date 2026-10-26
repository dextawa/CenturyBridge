package top.dext.centurybridge.shims.v1_20_2;

import com.google.gson.JsonObject;
import net.minecraft.class_1860;
import net.minecraft.class_1866;
import net.minecraft.class_2960;
import net.minecraft.class_3518;
import net.minecraft.class_3955;
import net.minecraft.class_7710;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.rt.v1_20_2.CbRecipeJson;
import top.dext.centurybridge.rt.v1_20_2.CbRecipeSerializerSupport;
import top.dext.centurybridge.rt.v1_20_2.CbVanillaReads;

/**
 * SimpleCraftingRecipeSerializer's 1.20.1 read(id, json), 1.20.1's body: the category, then the serializer's
 * factory, reached through the serializer's codec (CbRecipeJson.special: the codec reads exactly the category and calls
 * create(category)), run with the id as the one being decoded, which a converted 1.20.1 factory takes; the id kept for
 * getId(). Not a @Shadow of 1.20.2's private factory field: a later release drops it (1.20.5) and the mixin is stripped. 1.20.2's RecipeManager reads the special crafting recipes through it
 * (CbRecipeJsonVanillaMixin).
 */
@Mixin(value = class_1866.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRecipeJsonSpecial implements CbRecipeJson.Reader {

    @CbVanillaReads("CbRecipeJsonVanillaMixin: RecipeManager.fromJson consults it, against its unmixed copy cb$goldenJson, for every special crafting recipe the codec decoded")
    public class_3955 method_45443(class_2960 id, JsonObject json) {
        class_7710 category = class_7710.field_40252.method_47920(class_3518.method_15253(json, "category", null), class_7710.field_40251);
        return CbRecipeSerializerSupport.withId(id, () -> CbRecipeJson.special(this.cb$codec(), category));
    }

    /** the same 1.20.1 body under a CenturyBridge name: what method_45443 answers unmixed (CbRecipeJson.consult) */
    @Override
    public net.minecraft.class_1860<?> cb$goldenJson(class_2960 id, JsonObject json) {
        class_7710 category = class_7710.field_40252.method_47920(class_3518.method_15253(json, "category", null), class_7710.field_40251);
        return CbRecipeSerializerSupport.withId(id, () -> CbRecipeJson.special(this.cb$codec(), category));
    }

    @Override
    public class_1860<?> cb$readJson(class_2960 id, JsonObject json) {
        return this.method_45443(id, json);
    }

    /** the serializer's codec, asked through RecipeSerializer.codec() (later steps carry that call; see CbRecipeJsonCooking) */
    @SuppressWarnings("unchecked")
    @org.spongepowered.asm.mixin.Unique
    private com.mojang.serialization.Codec<class_3955> cb$codec() {
        return ((net.minecraft.class_1865<class_3955>) (Object) this).method_53736();
    }
}

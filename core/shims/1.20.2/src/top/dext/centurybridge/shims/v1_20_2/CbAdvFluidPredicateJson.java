package top.dext.centurybridge.shims.v1_20_2;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.class_2960;
import net.minecraft.class_3518;
import net.minecraft.class_3611;
import net.minecraft.class_4551;
import net.minecraft.class_4559;
import net.minecraft.class_6862;
import net.minecraft.class_7923;
import net.minecraft.class_7924;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.rt.v1_20_2.CbAdvAny;
import top.dext.centurybridge.rt.v1_20_2.CbAdvRecords;
import top.dext.centurybridge.rt.v1_20_2.CbVanillaReads;

/**
 * FluidPredicate's 1.20.1 fromJson(json), gone from 1.20.2, restored on FluidPredicate as a private static with 1.20.1's
 * body: ANY for null, the fluid looked up without a check (an unknown id is the registry's default, as 1.20.1 did), tag,
 * state through StatePropertiesPredicate's restored reader, the 1.20.1 constructor's values going into 1.20.2's record
 * (CbAdvRecords.fluidPredicate). The restored LocationPredicate reader calls it (CbAdvFluidPredicateAccess).
 */
@Mixin(value = class_4551.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbAdvFluidPredicateJson {

    /** 1.20.1 FluidPredicate.fromJson(json) */
    @CbVanillaReads("CbAdvLocationPredicateConsult: the restored LocationPredicate.fromJson calls it; 1.20.2's LocationPredicate.fromJson"
        + " consults that against its unmixed copy")
    private static class_4551 method_22474(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return CbAdvAny.field_20708;
        }
        JsonObject json = class_3518.method_15295(element, "fluid");
        class_3611 fluid = null;
        if (json.has("fluid")) {
            class_2960 id = new class_2960(class_3518.method_15265(json, "fluid"));
            fluid = class_7923.field_41173.method_10223(id);
        }
        class_6862<class_3611> tag = null;
        if (json.has("tag")) {
            class_2960 id = new class_2960(class_3518.method_15265(json, "tag"));
            tag = class_6862.method_40092(class_7924.field_41270, id);
        }
        class_4559 state = CbAdvStatePropertiesAccess.cb$fromJson(json.get("state"));
        return CbAdvRecords.fluidPredicate(tag, fluid, state);
    }
}

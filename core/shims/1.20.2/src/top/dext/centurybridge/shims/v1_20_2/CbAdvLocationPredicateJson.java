package top.dext.centurybridge.shims.v1_20_2;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.class_1937;
import net.minecraft.class_1959;
import net.minecraft.class_2090;
import net.minecraft.class_2096;
import net.minecraft.class_2960;
import net.minecraft.class_3195;
import net.minecraft.class_3518;
import net.minecraft.class_4550;
import net.minecraft.class_4551;
import net.minecraft.class_4552;
import net.minecraft.class_5321;
import net.minecraft.class_7924;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.rt.v1_20_2.CbAdvAny;
import top.dext.centurybridge.rt.v1_20_2.CbAdvPredicateJson;
import top.dext.centurybridge.rt.v1_20_2.CbAdvRecords;
import top.dext.centurybridge.rt.v1_20_2.CbVanillaReads;

/**
 * LocationPredicate's 1.20.1 fromJson(json), restored on LocationPredicate as a private static next to 1.20.2's
 * Optional answering one (another descriptor), with 1.20.1's body: ANY for null, the position bounds, dimension and
 * structure parsed leniently (an unparsable id logged and ignored), biome, smokey, and Light / Block / FluidPredicate
 * through those classes' restored readers (CbAdv*PredicateAccess), the 1.20.1 constructor's values going into 1.20.2's
 * record (CbAdvRecords.locationPredicate). 1.20.1's LOGGER is gone with the method; the log line goes to
 * CbAdvPredicateJson's. 1.20.2's LocationPredicate.fromJson consults it (CbAdvLocationPredicateConsult) against its
 * unmixable copy (CbAdvPredicateJson).
 */
@Mixin(value = class_2090.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbAdvLocationPredicateJson {

    /** 1.20.1 LocationPredicate.fromJson(json) */
    @CbVanillaReads("CbAdvLocationPredicateConsult: 1.20.2's LocationPredicate.fromJson consults it against its unmixed copy")
    private static class_2090 method_9021(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return CbAdvAny.field_9685;
        }
        JsonObject json = class_3518.method_15295(element, "location");
        JsonObject position = class_3518.method_15281(json, "position", new JsonObject());
        class_2096.class_2099 x = class_2096.class_2099.method_9051(position.get("x"));
        class_2096.class_2099 y = class_2096.class_2099.method_9051(position.get("y"));
        class_2096.class_2099 z = class_2096.class_2099.method_9051(position.get("z"));
        class_5321<class_1937> dimension = json.has("dimension")
            ? class_2960.field_25139.parse(JsonOps.INSTANCE, json.get("dimension")).resultOrPartial(CbAdvPredicateJson.LOGGER::error)
                .map(id -> class_5321.method_29179(class_7924.field_41223, id)).orElse(null)
            : null;
        class_5321<class_3195> structure = json.has("structure")
            ? class_2960.field_25139.parse(JsonOps.INSTANCE, json.get("structure")).resultOrPartial(CbAdvPredicateJson.LOGGER::error)
                .map(id -> class_5321.method_29179(class_7924.field_41246, id)).orElse(null)
            : null;
        class_5321<class_1959> biome = null;
        if (json.has("biome")) {
            class_2960 id = new class_2960(class_3518.method_15265(json, "biome"));
            biome = class_5321.method_29179(class_7924.field_41236, id);
        }
        Boolean smokey = json.has("smokey") ? json.get("smokey").getAsBoolean() : null;
        class_4552 light = CbAdvLightPredicateAccess.cb$fromJson(json.get("light"));
        class_4550 block = CbAdvBlockPredicateAccess.cb$fromJson(json.get("block"));
        class_4551 fluid = CbAdvFluidPredicateAccess.cb$fromJson(json.get("fluid"));
        return CbAdvRecords.locationPredicate(x, y, z, biome, structure, dimension, smokey, light, block, fluid);
    }
}

package top.dext.centurybridge.shims.v1_20_2;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Set;
import net.minecraft.class_2105;
import net.minecraft.class_2248;
import net.minecraft.class_2960;
import net.minecraft.class_3518;
import net.minecraft.class_4550;
import net.minecraft.class_4559;
import net.minecraft.class_6862;
import net.minecraft.class_7923;
import net.minecraft.class_7924;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.rt.v1_20_2.CbAdvAny;
import top.dext.centurybridge.rt.v1_20_2.CbAdvRecords;
import top.dext.centurybridge.rt.v1_20_2.CbVanillaReads;

/**
 * BlockPredicate's 1.20.1 fromJson(json), gone from 1.20.2 (its codec reads it inside LocationPredicate's), restored on
 * BlockPredicate as a private static with 1.20.1's body: ANY for null, nbt / blocks / tag / state in 1.20.1's order and
 * messages, NbtPredicate / StatePropertiesPredicate.fromJson being those classes' restored statics, the 1.20.1
 * constructor's values going into 1.20.2's record (CbAdvRecords.blockPredicate). The restored LocationPredicate reader
 * calls it (CbAdvBlockPredicateAccess); 1.20.2's LocationPredicate.fromJson consults that.
 */
@Mixin(value = class_4550.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbAdvBlockPredicateJson {

    /** 1.20.1 BlockPredicate.fromJson(json) */
    @CbVanillaReads("CbAdvLocationPredicateConsult: the restored LocationPredicate.fromJson calls it; 1.20.2's LocationPredicate.fromJson"
        + " consults that against its unmixed copy")
    private static class_4550 method_22453(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return CbAdvAny.field_20692;
        }
        JsonObject json = class_3518.method_15295(element, "block");
        class_2105 nbt = CbAdvNbtPredicateAccess.cb$fromJson(json.get("nbt"));
        Set<class_2248> blocks = null;
        JsonArray array = class_3518.method_15292(json, "blocks", null);
        if (array != null) {
            ImmutableSet.Builder<class_2248> builder = ImmutableSet.builder();
            for (JsonElement e : array) {
                class_2960 id = new class_2960(class_3518.method_15287(e, "block"));
                builder.add(class_7923.field_41175.method_17966(id).orElseThrow(() -> new JsonSyntaxException("Unknown block id '" + id + "'")));
            }
            blocks = builder.build();
        }
        class_6862<class_2248> tag = null;
        if (json.has("tag")) {
            class_2960 id = new class_2960(class_3518.method_15265(json, "tag"));
            tag = class_6862.method_40092(class_7924.field_41254, id);
        }
        class_4559 state = CbAdvStatePropertiesAccess.cb$fromJson(json.get("state"));
        return CbAdvRecords.blockPredicate(tag, blocks, state, nbt);
    }
}

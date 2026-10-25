package top.dext.centurybridge.shims.v1_20_2;

import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.class_2105;
import net.minecraft.class_2487;
import net.minecraft.class_2522;
import net.minecraft.class_3518;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.rt.v1_20_2.CbAdvAny;
import top.dext.centurybridge.rt.v1_20_2.CbVanillaReads;

/**
 * NbtPredicate's 1.20.1 fromJson(json), gone from 1.20.2 (its codec reads the tag), restored on NbtPredicate as a private
 * static with 1.20.1's body (ANY for null, the SNBT string parsed, 1.20.1's message). The restored 1.20.1 predicate
 * readers call it (CbAdvNbtPredicateAccess); 1.20.2's readers consult those.
 */
@Mixin(value = class_2105.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbAdvNbtPredicateJson {

    /** 1.20.1 NbtPredicate.fromJson(json) */
    @CbVanillaReads("CbAdvItemPredicateConsult: the restored ItemPredicate.fromJson calls it; 1.20.2's ItemPredicate.fromJson consults that against its unmixed copy")
    private static class_2105 method_9073(JsonElement json) {
        if (json == null || json.isJsonNull()) {
            return CbAdvAny.field_9716;
        }
        class_2487 tag;
        try {
            tag = class_2522.method_10718(class_3518.method_15287(json, "nbt"));
        } catch (CommandSyntaxException e) {
            throw new JsonSyntaxException("Invalid nbt tag: " + e.getMessage());
        }
        return new class_2105(tag);
    }
}

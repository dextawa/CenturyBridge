package top.dext.centurybridge.shims.v1_20_2;

import top.dext.centurybridge.rt.v1_20_2.Trackers;

import com.google.gson.JsonElement;
import net.minecraft.class_1856;
import org.spongepowered.asm.mixin.Mixin;

/**
 * 1.20.1 -> 1.20.2: Ingredient JSON methods moved to codecs.
 * - toJson() re-added delegating to toJson(boolean) (L2 x21); the permissive
 *   flag approximates the old unrestricted serialization
 *
 * NOTE: the dead STATIC fromJson (method_52177, L3 x29) cannot be revived via
 * Mixin (non-private statics are forbidden in mixins). Dead statics are
 * handled on the CALLER side instead: the converter redirects the
 * invokestatic to a plain runtime class (backlog B8).
 */
@Mixin(class_1856.class)
public abstract class IngredientBridge {

    public JsonElement method_8089() {
        return ((class_1856) (Object) this).method_8089(true);
    }
}

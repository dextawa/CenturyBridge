package top.dext.centurybridge.step.v1_21_6.mixin;

import com.mojang.datafixers.util.Pair;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.class_2487;
import net.minecraft.class_2540;
import net.minecraft.class_5411;
import net.minecraft.class_5421;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21_6.rt.CbPrioritySplit;
import top.dext.centurybridge.step.v1_21_6.rt.CbRecipeBooks;
import top.dext.centurybridge.step.v1_21_6.rt.CbVanillaReads;

/**
 * 1.21.5's RecipeBookSettings members, restored on RecipeBookSettings with 1.21.5's bodies (javap of both releases).
 * 1.21.5 kept the settings in a private EnumMap (field_25736, only non-default types present; getOrDefault DEFAULT)
 * with a static table of the CompoundTag keys (field_25735); 1.21.6 keeps four per-type fields (crafting, furnace,
 * blast furnace, smoker: RecipeBookType order) and dropped the map, the key table, write / read(FriendlyByteBuf)
 * (its STREAM_CODEC is a composite of four TypeSettings codecs: the same eight booleans in the same order), equals and
 * hashCode.
 * <ul>
 * <li>field_25736 is a live view of the four fields (CbRecipeBooks.SettingsView: get / put / remove / clear read and
 *     write them), so a mod's shadow or accessor on the map, and the restored bodies, see and change 1.21.6's state;
 *     field_25735 is 1.21.5's key table (from CbRecipeBooks.KEYS; its initializer is merged into &lt;clinit&gt;).</li>
 * <li>write(CompoundTag): 1.21.5's body (the key table's forEach writing each type's two booleans); the restored
 *     ServerRecipeBook.toNbt calls it, as 1.21.5's did.</li>
 * <li>read / write(FriendlyByteBuf): STREAM_CODEC is wrapped at &lt;clinit&gt; TAIL (CbRecipeBookSettingsRestoredMixinHooks, at
 *     the default priority, through CbPrioritySplit.RecipeBookSettingsNet); it decodes / encodes through the
 *     restored member only when a converted mod mixed it (CbMixedMembers), otherwise through 1.21.6's own codec
 *     untouched. Same wire format both ways. 1.21.5's private constructor RecipeBookSettings(Map) has no 1.21.6
 *     counterpart (a mixin adds no constructor): read builds the settings with the public one and fills the map.</li>
 * <li>equals / hashCode (1.21.5's map value semantics) are not restored: an override of Object's methods changes
 *     1.21.6 for everyone (the generated equals / hashCode of ClientboundRecipeBookSettingsPacket and
 *     ServerRecipeBook.Packed would change with no converted mod present), and an Object override cannot be gated by
 *     the mixed signal. A 1.21.5 mod comparing two settings by value sees identity (release_notes_known).</li>
 * </ul>
 * Priority below every mod's. The layer's own reads (CbRecipeBooks.LegacySettings: the same tag from the four fields,
 * a fresh book's all-false settings, the view's field access) touch the fields directly, so they add no call a mod
 * could mix.
 */
@Mixin(value = class_5411.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRecipeBookSettingsRestoredMixin implements CbRecipeBooks.LegacySettings, CbPrioritySplit.RecipeBookSettingsNet {

    /** the four types' settings, in RecipeBookType order (crafting, furnace, blast furnace, smoker; javap) */
    @Shadow
    private class_5411.class_5412 field_60336;

    @Shadow
    private class_5411.class_5412 field_60337;

    @Shadow
    private class_5411.class_5412 field_60338;

    @Shadow
    private class_5411.class_5412 field_60339;

    /** 1.21.5's TAG_FIELDS: each type's two CompoundTag keys (merged into &lt;clinit&gt;) */
    private static Map<class_5421, Pair<String, String>> field_25735 = CbRecipeBooks.tagFields();

    /** 1.21.5's states map: a live view of the four fields */
    private final Map<class_5421, class_5411.class_5412> field_25736 = new CbRecipeBooks.SettingsView(this);

    /** read(FriendlyByteBuf) */
    @CbVanillaReads("CbRecipeBookSettingsRestoredMixin: RecipeBookSettings.STREAM_CODEC (wrapped by CbRecipeBookSettingsRestoredMixinHooks) decodes "
        + "through it when a converted mod mixed it")
    private static class_5411 method_30186(class_2540 buf) {
        Map<class_5421, class_5411.class_5412> map = new EnumMap<>(class_5421.class);
        for (class_5421 type : class_5421.values()) {
            boolean open = buf.readBoolean();
            boolean filtering = buf.readBoolean();
            if (open || filtering) {
                map.put(type, new class_5411.class_5412(open, filtering));
            }
        }
        class_5411 settings = new class_5411();
        ((CbRecipeBookSettingsRestoredMixin) (Object) settings).field_25736.putAll(map);
        return settings;
    }

    /** write(FriendlyByteBuf) */
    @CbVanillaReads("CbRecipeBookSettingsRestoredMixin: RecipeBookSettings.STREAM_CODEC (wrapped by CbRecipeBookSettingsRestoredMixinHooks) encodes "
        + "through it when a converted mod mixed it")
    private void method_30190(class_2540 buf) {
        for (class_5421 type : class_5421.values()) {
            class_5411.class_5412 s = this.field_25736.getOrDefault(type, class_5411.class_5412.field_54549);
            buf.method_52964(s.comp_3247);
            buf.method_52964(s.comp_3248);
        }
    }

    /** write(CompoundTag) */
    @CbVanillaReads("CbRecipeBookRouteMixin: through the restored ServerRecipeBook.toNbt, which ServerPlayer's save asks")
    @Override
    public void method_30189(class_2487 tag) {
        field_25735.forEach((type, keys) -> this.cb$writeType(tag, type, keys));
    }

    /** 1.21.5's lambda of write(CompoundTag) (method_30184) */
    private void cb$writeType(class_2487 tag, class_5421 type, Pair<String, String> keys) {
        class_5411.class_5412 s = this.field_25736.getOrDefault(type, class_5411.class_5412.field_54549);
        tag.method_10556(keys.getFirst(), s.comp_3247);
        tag.method_10556(keys.getSecond(), s.comp_3248);
    }

    @Override
    public void cb$v1_21_6$netWrite(class_2540 buf) {
        this.method_30190(buf);
    }

    @Override
    public class_5411 cb$v1_21_6$netRead(class_2540 buf) {
        return method_30186(buf);
    }

    @Override
    public void cb$writeCopy(class_2487 tag) {
        class_5411.class_5412[] all = {field_60336, field_60337, field_60338, field_60339};
        for (int i = 0; i < all.length; i++) {
            tag.method_10556(CbRecipeBooks.KEYS[i][0], all[i].comp_3247());
            tag.method_10556(CbRecipeBooks.KEYS[i][1], all[i].comp_3248());
        }
    }

    @Override
    public void cb$clear() {
        field_60336 = field_60337 = field_60338 = field_60339 = class_5411.class_5412.field_54549;
    }

    @Override
    public class_5411.class_5412 cb$type(class_5421 type) {
        return type == class_5421.field_25763 ? field_60336 : type == class_5421.field_25764 ? field_60337
            : type == class_5421.field_25765 ? field_60338 : field_60339;
    }

    @Override
    public void cb$setType(class_5421 type, class_5411.class_5412 settings) {
        if (type == class_5421.field_25763) {
            field_60336 = settings;
        } else if (type == class_5421.field_25764) {
            field_60337 = settings;
        } else if (type == class_5421.field_25765) {
            field_60338 = settings;
        } else {
            field_60339 = settings;
        }
    }
}

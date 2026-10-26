package top.dext.centurybridge.step.v1_21_6.mixin;

import com.mojang.datafixers.util.Pair;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.class_2487;
import net.minecraft.class_2540;
import net.minecraft.class_5411;
import net.minecraft.class_5421;
import net.minecraft.class_9139;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
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
 *     field_25735 is 1.21.5's key table (set at &lt;clinit&gt; TAIL from CbRecipeBooks.KEYS, not in &lt;clinit&gt;'s body).</li>
 * <li>write(CompoundTag): 1.21.5's body (the key table's forEach writing each type's two booleans); the restored
 *     ServerRecipeBook.toNbt calls it, as 1.21.5's did.</li>
 * <li>read / write(FriendlyByteBuf): STREAM_CODEC is wrapped at &lt;clinit&gt; TAIL; it decodes / encodes through the
 *     restored member only when a converted mod mixed it (CbMixedMembers), otherwise through 1.21.6's own codec
 *     untouched. Same wire format both ways. 1.21.5's private constructor RecipeBookSettings(Map) has no 1.21.6
 *     counterpart (a mixin adds no constructor): read builds the settings with the public one and fills the map.</li>
 * <li>equals / hashCode: 1.21.5's (the maps'), overriding Object's as in 1.21.5. In 1.21.6 only the generated
 *     equals / hashCode of the two records holding one (ClientboundRecipeBookSettingsPacket, ServerRecipeBook.Packed)
 *     reach them (javap; release_notes_known).</li>
 * </ul>
 * Priority below every mod's. The layer's own reads (CbRecipeBooks.LegacySettings: the same tag from the four fields,
 * a fresh book's all-false settings, the view's field access) touch the fields directly, so they add no call a mod
 * could mix.
 */
@Mixin(value = class_5411.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRecipeBookSettingsRestoredMixin implements CbRecipeBooks.LegacySettings {

    /** the four types' settings, in RecipeBookType order (crafting, furnace, blast furnace, smoker; javap) */
    @Shadow
    private class_5411.class_5412 field_60336;

    @Shadow
    private class_5411.class_5412 field_60337;

    @Shadow
    private class_5411.class_5412 field_60338;

    @Shadow
    private class_5411.class_5412 field_60339;

    @Shadow
    @Final
    @Mutable
    public static class_9139<class_2540, class_5411> field_54548;

    /** 1.21.5's TAG_FIELDS: each type's two CompoundTag keys (set at &lt;clinit&gt; TAIL) */
    private static Map<class_5421, Pair<String, String>> field_25735;

    /** 1.21.5's states map: a live view of the four fields */
    private final Map<class_5421, class_5411.class_5412> field_25736 = new CbRecipeBooks.SettingsView(this);

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void cb$legacyMembers(CallbackInfo ci) {
        field_25735 = CbRecipeBooks.tagFields();
        class_9139<class_2540, class_5411> native16 = field_54548;
        field_54548 = class_9139.method_56438((settings, buf) -> {
            if (CbRecipeBooks.routeNetWrite()) {
                ((CbRecipeBookSettingsRestoredMixin) (Object) settings).method_30190(buf);
            } else {
                native16.encode(buf, settings);
            }
        }, buf -> CbRecipeBooks.routeNetRead() ? method_30186(buf) : native16.decode(buf));
    }

    /** read(FriendlyByteBuf) */
    @CbVanillaReads("CbRecipeBookSettingsRestoredMixin: RecipeBookSettings.STREAM_CODEC (wrapped at <clinit> TAIL) decodes "
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
    @CbVanillaReads("CbRecipeBookSettingsRestoredMixin: RecipeBookSettings.STREAM_CODEC (wrapped at <clinit> TAIL) encodes "
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
    public boolean equals(Object o) {
        return this == o || o instanceof class_5411 && this.field_25736.equals(((CbRecipeBookSettingsRestoredMixin) o).field_25736);
    }

    @Override
    public int hashCode() {
        return this.field_25736.hashCode();
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

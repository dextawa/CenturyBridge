package top.dext.centurybridge.step.v1_21_4.mixin;

import com.mojang.datafixers.util.Either;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21_4.rt.CbModelBlockModel;
import top.dext.centurybridge.step.v1_21_4.rt.CbVanillaReads;

/**
 * ItemModelGenerator's 1.21.3 generateBlockModel(spriteGetter, model): the BlockModel of an item's layer textures (one
 * element set per layer, from the same processFrames 1.21.4 keeps), which 1.21.4 bakes directly instead.
 */
@Environment(EnvType.CLIENT)
@Mixin(value = net.minecraft.class_801.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbModelItemGeneratorMixin {

    @Shadow
    private List<net.minecraft.class_785> method_3480(int tintIndex, String texture, net.minecraft.class_7764 sprite) {
        throw new AssertionError();
    }

    /** was generateBlockModel(spriteGetter, model) (method_3479): 1.21.3's body */
    public net.minecraft.class_793 method_3479(Function<net.minecraft.class_4730, net.minecraft.class_1058> sprites, net.minecraft.class_793 model) {
        CbModelBlockModel.CbLegacyBlockModel m = (CbModelBlockModel.CbLegacyBlockModel) model;
        Map<String, Either<net.minecraft.class_4730, String>> textures = new HashMap<>();
        List<net.minecraft.class_785> elements = new ArrayList<>();
        for (int i = 0; i < net.minecraft.class_801.field_4270.size(); i++) {
            String layer = net.minecraft.class_801.field_4270.get(i);
            if (!m.method_3432(layer)) {
                break;
            }
            net.minecraft.class_4730 material = m.method_24077(layer);
            textures.put(layer, Either.left(material));
            elements.addAll(this.method_3480(i, layer, sprites.apply(material).method_45851()));
        }
        textures.put("particle", m.method_3432("particle") ? Either.left(m.method_24077("particle")) : textures.get("layer0"));
        net.minecraft.class_793 generated = CbModelBlockModel.create(null, elements, textures, false, CbModelBlockModel.method_24298(model),
            m.method_3443(), m.method_3434());
        ((CbModelBlockModel.CbLegacyBlockModel) generated).cb$setName(m.cb$name());
        return generated;
    }
}

package top.dext.centurybridge.step.v1_21_4.mixin;

import com.google.gson.JsonObject;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_4.facade.ModelTemplateJsonFactory;
import top.dext.centurybridge.step.v1_21_4.rt.CbClientRest;

/**
 * ModelTemplate's 1.21.3 createBaseTemplate(id, textures) and create(id, mapping, output, jsonFactory), bodies as 1.21.3's.
 * 1.21.3's create(id, mapping, output) went through both virtually; a subclass (which may override either) creates as 1.21.3 did,
 * 1.21.4's own templates as 1.21.4 does.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_4942.class)
public abstract class CbDataModelTemplateMixin implements CbClientRest.CbLegacyModelTemplate {

    @Shadow
    @Final
    private Optional<net.minecraft.class_2960> field_22901;

    @Shadow
    private Map<net.minecraft.class_4945, net.minecraft.class_2960> method_25850(net.minecraft.class_4944 mapping) {
        throw new AssertionError();
    }

    /** was createBaseTemplate(id, textures) */
    @Override
    public JsonObject method_48524(net.minecraft.class_2960 id, Map<net.minecraft.class_4945, net.minecraft.class_2960> textures) {
        return CbClientRest.baseTemplate(this.field_22901, textures);
    }

    /** was create(id, mapping, output, jsonFactory) */
    public net.minecraft.class_2960 method_48525(net.minecraft.class_2960 id, net.minecraft.class_4944 mapping,
            BiConsumer<net.minecraft.class_2960, Supplier<com.google.gson.JsonElement>> output, ModelTemplateJsonFactory factory) {
        Map<net.minecraft.class_4945, net.minecraft.class_2960> textures = this.method_25850(mapping);
        net.minecraft.class_10411 json = () -> factory.create(id, textures);
        output.accept(id, json);
        return id;
    }

    @Inject(method = "method_25852", at = @At("HEAD"), cancellable = true)
    private void cb$legacyCreate(net.minecraft.class_2960 id, net.minecraft.class_4944 mapping,
            BiConsumer<net.minecraft.class_2960, net.minecraft.class_10411> output, CallbackInfoReturnable<net.minecraft.class_2960> cir) {
        if ((Object) this.getClass() != net.minecraft.class_4942.class) {
            @SuppressWarnings({"unchecked", "rawtypes"})
            BiConsumer<net.minecraft.class_2960, Supplier<com.google.gson.JsonElement>> legacy = (BiConsumer) output;
            cir.setReturnValue(this.method_48525(id, mapping, legacy, this::method_48524));
        }
    }
}

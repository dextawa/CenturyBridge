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
import top.dext.centurybridge.step.v1_21_4.facade.ModelTemplateJsonFactory;
import top.dext.centurybridge.step.v1_21_4.rt.CbClientRest;
import top.dext.centurybridge.step.v1_21_4.rt.CbPrioritySplitClient;
import top.dext.centurybridge.step.v1_21_4.rt.CbVanillaReads;

/**
 * ModelTemplate's 1.21.3 createBaseTemplate(id, textures) and create(id, mapping, output, jsonFactory), bodies as 1.21.3's.
 * 1.21.3's create(id, mapping, output) went through both virtually; a subclass (which may override either) creates as 1.21.3 did,
 * 1.21.4's own templates as 1.21.4 does (CbDataModelTemplateMixinHooks, default priority).
 */
@Environment(EnvType.CLIENT)
@Mixin(value = net.minecraft.class_4942.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbDataModelTemplateMixin implements CbClientRest.CbLegacyModelTemplate, CbPrioritySplitClient.ModelTemplateCreate {

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

    /** 1.21.3's create(id, mapping, output): both members virtually, for 1.21.4's create (CbDataModelTemplateMixinHooks) */
    @Override
    public net.minecraft.class_2960 cb$v1_21_4$legacyCreate(net.minecraft.class_2960 id, net.minecraft.class_4944 mapping,
            BiConsumer<net.minecraft.class_2960, Supplier<com.google.gson.JsonElement>> output) {
        return this.method_48525(id, mapping, output, this::method_48524);
    }
}

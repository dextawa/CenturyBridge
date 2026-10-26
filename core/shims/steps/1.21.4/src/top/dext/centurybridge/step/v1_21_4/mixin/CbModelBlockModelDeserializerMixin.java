package top.dext.centurybridge.step.v1_21_4.mixin;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_4.facade.ItemOverride;
import top.dext.centurybridge.step.v1_21_4.rt.CbModelBlockModel;
import top.dext.centurybridge.step.v1_21_4.rt.CbPrioritySplitClient;
import top.dext.centurybridge.step.v1_21_4.rt.CbVanillaReads;

/**
 * BlockModel.Deserializer's 1.21.3 getOverrides(context, json): the model file's item overrides, which 1.21.4 no longer
 * reads; 1.21.3's deserialize asked it (virtually: a subclass's override counts) and the model kept the list
 * (CbModelBlockModelDeserializerMixinHooks, default priority).
 */
@Environment(EnvType.CLIENT)
@Mixin(value = net.minecraft.class_793.class_795.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbModelBlockModelDeserializerMixin implements CbPrioritySplitClient.ModelOverrides {

    /** was getOverrides(context, json) (method_3452) */
    protected List<ItemOverride> method_3452(com.google.gson.JsonDeserializationContext context, com.google.gson.JsonObject json) {
        return CbModelBlockModel.overrides(json);
    }

    /** 1.21.3's getOverrides called virtually, for 1.21.4's deserialize (CbModelBlockModelDeserializerMixinHooks) */
    @Override
    public List<ItemOverride> cb$v1_21_4$getOverrides(com.google.gson.JsonDeserializationContext context, com.google.gson.JsonObject json) {
        return this.method_3452(context, json);
    }
}

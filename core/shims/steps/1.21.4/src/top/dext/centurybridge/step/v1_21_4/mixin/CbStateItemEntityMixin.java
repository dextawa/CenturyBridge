package top.dext.centurybridge.step.v1_21_4.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_4.rt.CbRenderStates;

/** the state's 1.21.3 item fields; 1.21.3 ItemEntityRenderer.extractRenderState: the item (a copy) and its model (see {@link CbRenderStates}) */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_10039.class)
public abstract class CbStateItemEntityMixin implements CbRenderStates.CbLegacyExtract {
    /** itemModel */
    public net.minecraft.class_1087 field_53436;
    /** item */
    public net.minecraft.class_1799 field_53437 = net.minecraft.class_1799.field_8037;

    @Override
    public void cb$extract(net.minecraft.class_1297 entity0) {
        net.minecraft.class_1542 entity = (net.minecraft.class_1542) entity0;
        this.field_53437 = entity.method_6983().method_7972();
        this.field_53436 = CbRenderStates.model(this.field_53437, entity.method_37908(), null, entity.method_5628());
    }
}

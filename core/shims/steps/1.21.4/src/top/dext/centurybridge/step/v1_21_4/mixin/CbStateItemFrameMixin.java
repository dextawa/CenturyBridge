package top.dext.centurybridge.step.v1_21_4.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_4.rt.CbRenderStates;

/** the state's 1.21.3 item fields; 1.21.3 ItemFrameRenderer.extractRenderState: the framed item (a copy) and, unless it is a map, its model (see {@link CbRenderStates}) */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_10040.class)
public abstract class CbStateItemFrameMixin implements CbRenderStates.CbLegacyExtract {
    /** itemStack */
    public net.minecraft.class_1799 field_53439 = net.minecraft.class_1799.field_8037;
    /** itemModel */
    public net.minecraft.class_1087 field_53442;

    @Override
    public void cb$extract(net.minecraft.class_1297 entity0) {
        net.minecraft.class_1533 entity = (net.minecraft.class_1533) entity0;
        this.field_53439 = entity.method_6940().method_7972();
        this.field_53442 = !this.field_53439.method_7960() && entity.method_43272(this.field_53439) == null
            ? CbRenderStates.model(this.field_53439, entity.method_37908(), null, entity.method_5628()) : null;
    }
}

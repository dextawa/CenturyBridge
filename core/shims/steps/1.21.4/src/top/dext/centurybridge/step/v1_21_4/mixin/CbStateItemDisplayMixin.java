package top.dext.centurybridge.step.v1_21_4.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_4.rt.CbRenderStates;

/** the state's 1.21.3 item fields; 1.21.3 DisplayRenderer.ItemDisplayRenderer.extractRenderState: the display's item render state and its item's model (see {@link CbRenderStates}) */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_10038.class)
public abstract class CbStateItemDisplayMixin implements CbRenderStates.CbLegacyExtract {
    /** itemRenderState */
    public net.minecraft.class_8113.class_8122.class_8226 field_53433;
    /** itemModel */
    public net.minecraft.class_1087 field_53434;

    @Override
    public void cb$extract(net.minecraft.class_1297 entity0) {
        net.minecraft.class_8113.class_8122 entity = (net.minecraft.class_8113.class_8122) entity0;
        net.minecraft.class_8113.class_8122.class_8226 item = entity.method_49782();
        this.field_53433 = item;
        this.field_53434 = item == null ? null : CbRenderStates.model(item.comp_1322(), entity.method_37908(), null, entity.method_5628());
    }
}

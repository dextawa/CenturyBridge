package top.dext.centurybridge.step.v1_21_4.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_4.rt.CbRenderStates;

/** the state's 1.21.3 item fields; 1.21.3 ThrownItemRenderer.extractRenderState: the item's model (none when empty) and the item (a copy) (see {@link CbRenderStates}) */
@Environment(EnvType.CLIENT)
@org.spongepowered.asm.mixin.Mixin(value = net.minecraft.class_10072.class, priority = top.dext.centurybridge.step.v1_21_4.rt.CbVanillaReads.PRIORITY)
public abstract class CbStateThrownItemMixin implements CbRenderStates.CbLegacyExtract {
    /** itemModel */
    public net.minecraft.class_1087 field_53590;
    /** item */
    public net.minecraft.class_1799 field_53591 = net.minecraft.class_1799.field_8037;

    @Override
    public void cb$extract(net.minecraft.class_1297 entity0) {
        net.minecraft.class_1297 entity = (net.minecraft.class_1297) entity0;
        net.minecraft.class_1799 stack = ((net.minecraft.class_3856) entity).method_7495();
        this.field_53590 = CbRenderStates.modelUnlessEmpty(stack, entity.method_37908(), entity.method_5628());
        this.field_53591 = stack.method_7972();
    }
}

package top.dext.centurybridge.step.v1_21_4.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_4.rt.CbRenderStates;

/**
 * FireworkRocketRenderState's 1.21.3 item fields; 1.21.3 FireworkEntityRenderer.extractRenderState filled the rocket's item
 * (a copy) and its model (none when empty). The item keeps its 1.21.3 name elsewhere (1.21.4's field of that name is its
 * render state; see {@link CbRenderStates}).
 */
@Environment(EnvType.CLIENT)
@org.spongepowered.asm.mixin.Mixin(value = net.minecraft.class_10025.class, priority = top.dext.centurybridge.step.v1_21_4.rt.CbVanillaReads.PRIORITY)
public abstract class CbStateFireworkMixin implements CbRenderStates.CbLegacyExtract, CbRenderStates.CbLegacyFireworkState {
    /** itemModel */
    public net.minecraft.class_1087 field_53368;

    private net.minecraft.class_1799 cb$item = net.minecraft.class_1799.field_8037;

    @Override
    public net.minecraft.class_1799 cb$item() {
        return this.cb$item;
    }

    @Override
    public void cb$setItem(net.minecraft.class_1799 stack) {
        this.cb$item = stack;
    }

    @Override
    public void cb$extract(net.minecraft.class_1297 entity0) {
        net.minecraft.class_1671 entity = (net.minecraft.class_1671) entity0;
        this.cb$item = entity.method_7495().method_7972();
        this.field_53368 = CbRenderStates.modelUnlessEmpty(this.cb$item, entity.method_37908(), entity.method_5628());
    }
}

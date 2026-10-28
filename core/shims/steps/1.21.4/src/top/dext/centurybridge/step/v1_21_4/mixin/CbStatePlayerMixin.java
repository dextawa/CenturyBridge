package top.dext.centurybridge.step.v1_21_4.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_4.facade.LegacyPlayerHandState;
import top.dext.centurybridge.step.v1_21_4.rt.CbRenderStates;

/** PlayerRenderState's 1.21.3 hand states (main hand, off hand), filled by the player renderer (CbStatePlayerRendererMixin) */
@Environment(EnvType.CLIENT)
@org.spongepowered.asm.mixin.Mixin(value = net.minecraft.class_10055.class, priority = top.dext.centurybridge.step.v1_21_4.rt.CbVanillaReads.PRIORITY)
public abstract class CbStatePlayerMixin implements CbRenderStates.CbLegacyExtract {
    /** mainHandState */
    public LegacyPlayerHandState field_53523 = new LegacyPlayerHandState();
    /** offhandState */
    public LegacyPlayerHandState field_53524 = new LegacyPlayerHandState();

    /** 1.21.3 PlayerRenderer.extractRenderState's hand part */
    @Override
    public void cb$extract(net.minecraft.class_1297 entity) {
        net.minecraft.class_742 player = (net.minecraft.class_742) entity;
        LegacyPlayerHandState.extract(player, this.field_53523, net.minecraft.class_1268.field_5808);
        LegacyPlayerHandState.extract(player, this.field_53524, net.minecraft.class_1268.field_5810);
    }
}

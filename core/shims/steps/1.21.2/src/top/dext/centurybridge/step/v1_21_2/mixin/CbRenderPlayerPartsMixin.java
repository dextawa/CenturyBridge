package top.dext.centurybridge.step.v1_21_2.mixin;

import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads;

/**
 * 1.21.1 PlayerModel.renderEars / renderCloak. 1.21.1's player mesh had an "ear" part (one 6x6x1 box, deformation 1)
 * and a "cloak" part (10x16x1, uv scale 1 x 0.5), drawn by Deadmau5EarsLayer / CapeLayer through these methods; 1.21.2
 * moved them to models of their own (PlayerEarsModel's left/right ear, PlayerCapeModel's cape: the same boxes, compared
 * in both jars). Here each is a part made of the 1.21.2 layer's box at a zero pose, as 1.21.1's part was: renderEars
 * poses it as the head with no offset (1.21.1: ear.copyFrom(head), x = y = 0) and draws it, renderCloak draws the cloak.
 */
@Environment(EnvType.CLIENT)
@Mixin(value = net.minecraft.class_591.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRenderPlayerPartsMixin {

    @Unique
    private net.minecraft.class_630 cb$ear;

    @Unique
    private net.minecraft.class_630 cb$cloak;

    @Unique
    private static net.minecraft.class_630 cb$part(net.minecraft.class_5601 layer, String parent, String name) {
        net.minecraft.class_630 source = net.minecraft.class_310.method_1551().method_31974().method_32072(layer).method_32086(parent).method_32086(name);
        return new net.minecraft.class_630(source.field_3663, Map.of());
    }

    /** 1.21.1 renderEars(poseStack, buffer, light, overlay) */
    public void method_2824(net.minecraft.class_4587 poseStack, net.minecraft.class_4588 buffer, int light, int overlay) {
        if (this.cb$ear == null) {
            this.cb$ear = cb$part(net.minecraft.class_5602.field_52979, "head", "left_ear");
        }
        this.cb$ear.method_17138(((net.minecraft.class_572<?>) (Object) this).field_3398);
        this.cb$ear.field_3657 = 0.0F;
        this.cb$ear.field_3656 = 0.0F;
        this.cb$ear.method_22698(poseStack, buffer, light, overlay);
    }

    /** 1.21.1 renderCloak(poseStack, buffer, light, overlay) */
    public void method_2823(net.minecraft.class_4587 poseStack, net.minecraft.class_4588 buffer, int light, int overlay) {
        if (this.cb$cloak == null) {
            this.cb$cloak = cb$part(net.minecraft.class_5602.field_52980, "body", "cape");
        }
        this.cb$cloak.method_22698(poseStack, buffer, light, overlay);
    }
}

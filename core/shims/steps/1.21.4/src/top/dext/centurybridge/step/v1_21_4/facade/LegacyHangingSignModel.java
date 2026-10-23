package top.dext.centurybridge.step.v1_21_4.facade;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.3 HangingSignRenderer.HangingSignModel (class_7761$class_7762, gone in 1.21.4, which has one model per attachment):
 * the model over 1.21.3's hanging sign layer, its plank and chain sets shown per block state. Body for body.
 */
@Environment(EnvType.CLIENT)
public final class LegacyHangingSignModel extends net.minecraft.class_3879 {
    /** plank */
    public final net.minecraft.class_630 field_40526;
    /** vChains */
    public final net.minecraft.class_630 field_40527;
    /** normalChains */
    public final net.minecraft.class_630 field_40528;

    public LegacyHangingSignModel(net.minecraft.class_630 root) {
        super(root, net.minecraft.class_1921::method_23578);
        this.field_40526 = root.method_32086("plank");
        this.field_40528 = root.method_32086("normalChains");
        this.field_40527 = root.method_32086("vChains");
    }

    /** evaluateVisibleParts(state): a wall sign shows its plank; a ceiling sign its V chains when attached, else its normal chains */
    public void method_45797(net.minecraft.class_2680 state) {
        boolean wall = !(state.method_26204() instanceof net.minecraft.class_7713);
        this.field_40526.field_3665 = wall;
        this.field_40527.field_3665 = false;
        this.field_40528.field_3665 = true;
        if (!wall) {
            boolean attached = state.method_11654(net.minecraft.class_2741.field_12493);
            this.field_40528.field_3665 = !attached;
            this.field_40527.field_3665 = attached;
        }
    }
}

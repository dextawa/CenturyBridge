package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.rt.v1_20_2.CbAdvLegacy;
import top.dext.centurybridge.rt.v1_20_2.CbAdvNodes;

/**
 * An AdvancementNode's children, for a 1.20.1 Advancement subclass overriding getChildren(): 1.20.1's tree,
 * visibility and layout code walked advancement.getChildren(); 1.20.2's walks node.children(). The node of
 * such an advancement answers the advancements its override names, as their nodes. cb$children reads the
 * node's own set (the subclass's super.getChildren()).
 */
@Mixin(net.minecraft.class_8781.class)
public abstract class CbAdvNodeChildren implements top.dext.centurybridge.rt.v1_20_2.CbAdvNodes.NodeChildren {

    @Shadow
    private java.util.Set<net.minecraft.class_8781> field_46079;

    @Inject(method = "method_53653()Ljava/lang/Iterable;", at = @At("HEAD"), cancellable = true)
    private void cb$legacyChildren(CallbackInfoReturnable<Iterable<net.minecraft.class_8781>> cir) {
        net.minecraft.class_161 a = ((net.minecraft.class_8781) (Object) this).method_53647();
        if ((Object) a instanceof CbAdvLegacy.AdvChildren own) {
            java.util.List<net.minecraft.class_8781> out = new java.util.ArrayList<>();
            for (net.minecraft.class_161 c : own.method_681()) {
                out.add(CbAdvNodes.nodeOf(c));
            }
            cir.setReturnValue(out);
        }
    }

    @Override
    public java.util.Set<net.minecraft.class_8781> cb$children() {
        return this.field_46079;
    }
}

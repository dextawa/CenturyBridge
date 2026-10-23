package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;

/**
 * 1.21.1 ShulkerModel.getLid() / getHead() / parts(): the lid and head parts 1.21.2 keeps under the same fields, and
 * 1.21.1's parts list (base, lid) -- 1.21.2 keeps the base as the root's "base" child (it no longer has a field).
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_602.class)
public abstract class CbRenderModelShulkerMixin {

    @Shadow
    @Final
    private net.minecraft.class_630 field_3555;

    @Shadow
    @Final
    private net.minecraft.class_630 field_3554;

    public net.minecraft.class_630 method_2829() {
        return this.field_3555;
    }

    public net.minecraft.class_630 method_2830() {
        return this.field_3554;
    }

    public Iterable<net.minecraft.class_630> method_22960() {
        return com.google.common.collect.ImmutableList.of(((net.minecraft.class_3879) (Object) this).method_63512().method_32086("base"), this.field_3555);
    }
}

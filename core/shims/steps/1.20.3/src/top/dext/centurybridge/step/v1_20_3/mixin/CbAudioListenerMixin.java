package top.dext.centurybridge.step.v1_20_3.mixin;

import org.spongepowered.asm.mixin.Mixin;

/**
 * com.mojang.blaze3d.audio.Listener's 1.20.2 setters and getter, which 1.20.3 folded into one transform
 * (position, forward, up) set at once: each sets its part and keeps the others as they are.
 */
@Mixin(net.minecraft.class_4227.class)
public abstract class CbAudioListenerMixin {

    private net.minecraft.class_4227 cb$self() {
        return (net.minecraft.class_4227) (Object) this;
    }

    /** was setListenerPosition(Vec3) (method_19671) */
    public void method_19671(net.minecraft.class_243 position) {
        net.minecraft.class_9054 t = cb$self().method_55585();
        cb$self().method_55584(new net.minecraft.class_9054(position, t.comp_2166(), t.comp_2167()));
    }

    /** was getListenerPosition() (method_27268) */
    public net.minecraft.class_243 method_27268() {
        return cb$self().method_55585().comp_2165();
    }

    /** was setListenerOrientation(Vector3f look, Vector3f up) (method_19672) */
    public void method_19672(org.joml.Vector3f look, org.joml.Vector3f up) {
        net.minecraft.class_9054 t = cb$self().method_55585();
        cb$self().method_55584(new net.minecraft.class_9054(t.comp_2165(), new net.minecraft.class_243(look), new net.minecraft.class_243(up)));
    }
}

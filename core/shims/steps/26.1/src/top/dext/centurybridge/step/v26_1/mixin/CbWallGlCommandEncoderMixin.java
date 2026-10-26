package top.dext.centurybridge.step.v26_1.mixin;

import com.mojang.blaze3d.opengl.GlCommandEncoder;
import com.mojang.blaze3d.opengl.GlDevice;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v26_1.rt.CbVanillaReads;
import org.spongepowered.asm.mixin.Shadow;

/** 1.21.11's GlCommandEncoder.getDevice() (protected): 26.1 keeps the device in the same field without a getter. */
@Environment(EnvType.CLIENT)
@Mixin(value = GlCommandEncoder.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbWallGlCommandEncoderMixin {

    @Shadow
    @Final
    private GlDevice device;

    protected GlDevice getDevice() {
        return device;
    }
}

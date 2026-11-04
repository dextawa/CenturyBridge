package top.dext.centurybridge.step.v1_21_11.mixin;

import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_12137;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_21_11.rt.CbSamplers;
import top.dext.centurybridge.step.v1_21_11.rt.CbVanillaReads;

/**
 * 1.21.10 GpuTexture's sampler state (javap -c -p both code jars): the protected fields addressModeU / addressModeV /
 * minFilter / magFilter / useMipmaps, set by its constructor to REPEAT, REPEAT, NEAREST, LINEAR, true, and the setters
 * setAddressMode(mode) -> setAddressMode(mode, mode), setAddressMode(u, v), setTextureFilter(filter, mip) ->
 * setTextureFilter(filter, filter, mip), setTextureFilter(min, mag, mip) (fields, then setUseMipmaps(mip)),
 * setUseMipmaps(mip), all gone from 1.21.11, which takes the state as a sampler beside each bound view. Restored with
 * 1.21.10's bodies; rt/CbSamplers turns the state into the sampler 1.21.11 binds (RenderPass.bindSampler). 1.21.10's
 * GlTexture overrides only marked its GL parameters dirty; 1.21.11 has no per-texture GL sampling state to flush.
 * 1.21.11 never calls these. Priority below every mod's.
 */
@Environment(EnvType.CLIENT)
@Mixin(value = GpuTexture.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbGpuTextureSamplerRestoredMixin implements CbSamplers.SamplerState {

    protected AddressMode addressModeU = AddressMode.REPEAT;
    protected AddressMode addressModeV = AddressMode.REPEAT;
    protected FilterMode minFilter = FilterMode.NEAREST;
    protected FilterMode magFilter = FilterMode.LINEAR;
    protected boolean useMipmaps = true;
    @Unique
    private boolean cb$v1_21_11$written;

    /** setAddressMode(mode) */
    @Override
    public void setAddressMode(AddressMode mode) {
        this.setAddressMode(mode, mode);
    }

    /** setAddressMode(u, v) */
    public void setAddressMode(AddressMode u, AddressMode v) {
        this.addressModeU = u;
        this.addressModeV = v;
        this.cb$v1_21_11$written = true;
    }

    /** setTextureFilter(filter, useMipmaps) */
    @Override
    public void setTextureFilter(FilterMode filter, boolean useMipmaps) {
        this.setTextureFilter(filter, filter, useMipmaps);
    }

    /** setTextureFilter(min, mag, useMipmaps) */
    public void setTextureFilter(FilterMode min, FilterMode mag, boolean useMipmaps) {
        this.minFilter = min;
        this.magFilter = mag;
        this.cb$v1_21_11$written = true;
        this.setUseMipmaps(useMipmaps);
    }

    /** setUseMipmaps(useMipmaps) */
    public void setUseMipmaps(boolean useMipmaps) {
        this.useMipmaps = useMipmaps;
        this.cb$v1_21_11$written = true;
    }

    @Override
    public boolean cb$v1_21_11$stateWritten() {
        return this.cb$v1_21_11$written;
    }

    @Override
    public class_12137 cb$v1_21_11$stateSampler() {
        return CbSamplers.of(this.addressModeU, this.addressModeV, this.minFilter, this.magFilter, this.useMipmaps);
    }
}

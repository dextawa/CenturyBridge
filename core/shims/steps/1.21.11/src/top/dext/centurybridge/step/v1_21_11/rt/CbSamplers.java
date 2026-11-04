package top.dext.centurybridge.step.v1_21_11.rt;

import com.google.common.collect.MapMaker;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.util.Map;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1044;
import net.minecraft.class_12137;
import net.minecraft.class_310;

/**
 * 1.21.10 RenderPass.bindSampler(name, view) on 1.21.11's bindTexture(name, view, sampler) (javap -c -p both code
 * jars). 1.21.10 sampled a bound texture with the state the GpuTexture itself carried (addressModeU / V, minFilter,
 * magFilter, useMipmaps; GlTexture flushed it to GL_TEXTURE_WRAP_S/T, MIN/MAG_FILTER before the draw); 1.21.11 dropped
 * that state and takes a sampler beside each view, from RenderSystem.getSamplerCache(). The state is given back on
 * GpuTexture (mixin/CbGpuTextureSamplerRestoredMixin, 1.21.10's fields and setters); the sampler a 1.21.10 call meant:
 * <ul>
 * <li>the texture's state when 1.21.10-style code set it (its setters, or vanilla's own 1.21.10 writes 1.21.11 no
 *     longer makes: RenderTarget / MainTarget buffers, the lightmap -- mixin/CbSamplerStateMixinHooks);</li>
 * <li>else, for a texture an AbstractTexture holds, that texture's sampler: 1.21.11 moved AbstractTexture's
 *     setClamp / setFilter / setUseMipmaps (which wrote the GpuTexture state in 1.21.10) into the sampler it keeps
 *     (AbstractTexture.sampler, set by each texture's load), so it is the state 1.21.10 would have held;</li>
 * <li>else the state as it stands: 1.21.10's GpuTexture defaults (repeat, nearest min / linear mag, mipmaps), or
 *     what a GpuTexture subclass of a mod wrote into the protected fields.</li>
 * </ul>
 * Mipmaps: the cache's sampler for useMipmaps has no max LOD (SamplerCache.method_75292: OptionalDouble.empty()
 * when mipmapped, 0 otherwise). A null view unbinds (1.21.10 removed the name; 1.21.11 does so for a null sampler).
 */
@Environment(EnvType.CLIENT)
public final class CbSamplers {
    private CbSamplers() {
    }

    /** the restored 1.21.10 sampler state of a GpuTexture (mixin/CbGpuTextureSamplerRestoredMixin) */
    public interface SamplerState {
        /** whether 1.21.10-style code set the state (a setter ran) */
        boolean cb$v1_21_11$stateWritten();

        /** the cache's sampler for the state as it stands */
        class_12137 cb$v1_21_11$stateSampler();

        /** 1.21.10 GpuTexture.setAddressMode(mode) */
        void setAddressMode(AddressMode mode);

        /** 1.21.10 GpuTexture.setTextureFilter(filter, useMipmaps) */
        void setTextureFilter(FilterMode filter, boolean useMipmaps);
    }

    private static final Map<GpuTexture, class_1044> OWNERS = new MapMaker().weakKeys().weakValues().makeMap();
    private static final Set<GpuTexture> UNOWNED = java.util.Collections.newSetFromMap(new MapMaker().weakKeys().makeMap());

    /** the sampler of the state values given */
    public static class_12137 of(AddressMode u, AddressMode v, FilterMode min, FilterMode mag, boolean mipmaps) {
        return RenderSystem.getSamplerCache().method_75293(u, v, min, mag, mipmaps);
    }

    /** the sampler 1.21.10's bindSampler(name, view) bound the view's texture with */
    public static class_12137 sampler(GpuTextureView view) {
        if (view == null) {
            return null;
        }
        GpuTexture texture = view.texture();
        SamplerState state = (SamplerState) (Object) texture;
        if (state.cb$v1_21_11$stateWritten()) {
            return state.cb$v1_21_11$stateSampler();
        }
        class_1044 owner = owner(texture);
        if (owner != null && owner.method_75484() != null) {
            return owner.method_75484();
        }
        return state.cb$v1_21_11$stateSampler();
    }

    /** a rewritten super.bindSampler(name, view) entered outside a hooked call: the sampler for the view */
    public static class_12137 frameSampler(RenderPass pass, String name, GpuTextureView view) {
        return sampler(view);
    }

    private static class_1044 owner(GpuTexture texture) {
        class_1044 known = OWNERS.get(texture);
        if (known != null && known.method_68004() == texture) {
            return known;
        }
        if (UNOWNED.contains(texture)) {
            return null;
        }
        class_310 client = class_310.method_1551();
        if (client != null && client.method_1531() != null) {
            for (class_1044 t : client.method_1531().field_5286.values()) {
                if (t.method_68004() == texture) {
                    OWNERS.put(texture, t);
                    return t;
                }
            }
        }
        UNOWNED.add(texture);
        return null;
    }

    /** 1.21.10's vanilla writes of a texture's state that 1.21.11 dropped, replayed (mixin/CbSamplerStateMixinHooks) */
    public static void vanillaState(GpuTexture texture, AddressMode address, FilterMode filter) {
        if (texture == null) {
            return;
        }
        SamplerState state = (SamplerState) (Object) texture;
        state.setTextureFilter(filter, false);
        if (address != null) {
            state.setAddressMode(address);
        }
    }
}

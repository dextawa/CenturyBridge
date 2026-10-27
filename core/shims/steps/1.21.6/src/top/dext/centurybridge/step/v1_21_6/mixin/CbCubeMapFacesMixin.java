package top.dext.centurybridge.step.v1_21_6.mixin;

import java.util.List;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_6.rt.CbPrioritySplit;
import top.dext.centurybridge.step.v1_21_6.rt.CbVanillaReads;

/**
 * 1.21.5's CubeMap.images (field_55505, private final List&lt;ResourceLocation&gt;), which 1.21.6 dropped (javap): 1.21.5's
 * constructor built the six face ids base.withPath(base.getPath() + "_" + i + ".png") for i 0..5 and loaded each face
 * from them; 1.21.6 keeps only the base id (field_60578) and loads the same six files through its CubeMapTexture. The
 * field is given back under its name, filled at the constructor's return with 1.21.5's ids from the constructor's
 * argument (CbCubeMapFacesMixinHooks, at the default priority), for the climbed 1.21.4 layer's accessor
 * (CbTailCubeMapAccess, the faces its CubeMap.preload loads) and 1.21.5 mods' accessors. Nothing in 1.21.6 reads it.
 * Priority below every mod's, so the field is there before any mixin naming it is applied.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
@Mixin(value = net.minecraft.class_751.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbCubeMapFacesMixin implements CbPrioritySplit.CubeMapFaces {

    /** images */
    private List<net.minecraft.class_2960> field_55505;

    @Override
    public void cb$v1_21_6$setFaces(List<net.minecraft.class_2960> faces) {
        this.field_55505 = faces;
    }
}

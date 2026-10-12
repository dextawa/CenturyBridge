package top.dext.centurybridge.rt.v1_20_2;

import java.io.IOException;

/**
 * A 1.20.1 GUI texture as the texture manager loads it on 1.20.2: the file when a resource pack still
 * ships the 1.20.1 composite, otherwise the composite rebuilt from 1.20.2's sprites (see
 * {@link CbGuiLegacyTextures}). Being a ResourceTexture, it reloads with the resource packs: the
 * texture manager calls load again on every reload, which composes again from the new resources.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
final class CbGuiLegacyTexture extends net.minecraft.class_1049 {

    private final CbGuiLegacyTextures.Spec spec;

    CbGuiLegacyTexture(net.minecraft.class_2960 id, CbGuiLegacyTextures.Spec spec) {
        super(id);
        this.spec = spec;
    }

    @Override
    protected net.minecraft.class_1049.class_4006 method_18153(net.minecraft.class_3300 manager) {
        try {
            if (spec.gone) {
                if (manager.method_14486(this.field_5224).isPresent()) {
                    return super.method_18153(manager); // a pack ships the 1.20.1 file: draw what it drew
                }
                net.minecraft.class_1011 img = spec.base != null ? CbGuiLegacyTextures.read(manager, spec.base) : null;
                if (img == null) {
                    img = new net.minecraft.class_1011(spec.width, spec.height, true);
                    img.method_4326(0, 0, spec.width, spec.height, 0);
                }
                CbGuiLegacyTextures.compose(manager, spec, img);
                return new net.minecraft.class_1049.class_4006(null, img);
            }
            net.minecraft.class_1049.class_4006 data = super.method_18153(manager);
            data.method_18158(); // the 1.20.2 file must load; its failure is the vanilla failure
            net.minecraft.class_1011 img = data.method_18157();
            CbGuiLegacyTextures.compose(manager, spec, img);
            return new net.minecraft.class_1049.class_4006(data.method_18155(), img);
        } catch (IOException e) {
            return new net.minecraft.class_1049.class_4006(e);
        }
    }
}

package top.dext.centurybridge.step.v26_1.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;

/**
 * 1.21.11's texture Identifiers against 26.1's sprite Materials (the id plus a forceTranslucent flag
 * 1.21.11 did not have): an id becomes the material it stood for (not force-translucent), a material its
 * id. Used by the generated reshapes (core/tools/gen_wall_material.py); the callers are all client
 * classes (model datagen lives under net.minecraft.client.data).
 */
@Environment(EnvType.CLIENT)
public final class CbWallMaterials {

    private CbWallMaterials() {
    }

    public static Material material(Identifier id) {
        return id == null ? null : new Material(id);
    }

    public static Identifier id(Material material) {
        return material == null ? null : material.sprite();
    }
}

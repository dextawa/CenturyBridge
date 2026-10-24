package top.dext.centurybridge.step.v1_21_2.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/** 1.21.1 layer-definition statics that 1.21.2 moved or removed (staticRedirects in s7render.json) */
@Environment(EnvType.CLIENT)
public final class CbRenderMeshes {
    private CbRenderMeshes() {
    }

    /**
     * PiglinModel.createMesh(deformation): 1.21.2's AbstractPiglinModel.createMesh (javap both: the player mesh, the
     * body, the head with ears; the hat is the head's child in 1.21.2's player mesh, emptied there, where 1.21.1 put an
     * empty "hat" at the root)
     */
    public static net.minecraft.class_5609 method_32026(net.minecraft.class_5605 deformation) {
        return net.minecraft.class_9939.method_62055(deformation);
    }

    /** PiglinModel.addHead(deformation, mesh): 1.21.2's AbstractPiglinModel.addHead, which also answers the head part */
    public static void method_47656(net.minecraft.class_5605 deformation, net.minecraft.class_5609 mesh) {
        net.minecraft.class_9939.method_62056(deformation, mesh);
    }

    /**
     * EndCrystalRenderer.createBodyLayer(): 1.21.1's layer -- glass, cube and base at the root (1.21.2's EndCrystalModel
     * nests outer_glass / inner_glass / cube under a base, with other names)
     */
    public static net.minecraft.class_5607 method_32164() {
        net.minecraft.class_5609 mesh = new net.minecraft.class_5609();
        net.minecraft.class_5610 root = mesh.method_32111();
        root.method_32117("glass", net.minecraft.class_5606.method_32108().method_32101(0, 0).method_32097(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F),
            net.minecraft.class_5603.field_27701);
        root.method_32117("cube", net.minecraft.class_5606.method_32108().method_32101(32, 0).method_32097(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F),
            net.minecraft.class_5603.field_27701);
        root.method_32117("base", net.minecraft.class_5606.method_32108().method_32101(0, 16).method_32097(-6.0F, 0.0F, -6.0F, 12.0F, 4.0F, 12.0F),
            net.minecraft.class_5603.field_27701);
        return net.minecraft.class_5607.method_32110(mesh, 64, 32);
    }
}

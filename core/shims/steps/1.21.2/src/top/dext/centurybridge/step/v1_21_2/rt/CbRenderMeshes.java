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

    /**
     * RabbitModel.createBodyLayer(): 1.21.1's layer, untransformed -- 1.21.1 scaled the rabbit when drawing (0.6 for an
     * adult, smaller for a baby), 1.21.2's createBodyLayer(baby) bakes the adult or baby scale into the layer it answers
     */
    public static net.minecraft.class_5607 method_32034() {
        net.minecraft.class_5609 mesh = new net.minecraft.class_5609();
        net.minecraft.class_5610 root = mesh.method_32111();
        root.method_32117("left_hind_foot", net.minecraft.class_5606.method_32108().method_32101(26, 24).method_32097(-1.0F, 5.5F, -3.7F, 2.0F, 1.0F, 7.0F),
            net.minecraft.class_5603.method_32090(3.0F, 17.5F, 3.7F));
        root.method_32117("right_hind_foot", net.minecraft.class_5606.method_32108().method_32101(8, 24).method_32097(-1.0F, 5.5F, -3.7F, 2.0F, 1.0F, 7.0F),
            net.minecraft.class_5603.method_32090(-3.0F, 17.5F, 3.7F));
        root.method_32117("left_haunch", net.minecraft.class_5606.method_32108().method_32101(30, 15).method_32097(-1.0F, 0.0F, 0.0F, 2.0F, 4.0F, 5.0F),
            net.minecraft.class_5603.method_32091(3.0F, 17.5F, 3.7F, -0.34906584F, 0.0F, 0.0F));
        root.method_32117("right_haunch", net.minecraft.class_5606.method_32108().method_32101(16, 15).method_32097(-1.0F, 0.0F, 0.0F, 2.0F, 4.0F, 5.0F),
            net.minecraft.class_5603.method_32091(-3.0F, 17.5F, 3.7F, -0.34906584F, 0.0F, 0.0F));
        root.method_32117("body", net.minecraft.class_5606.method_32108().method_32101(0, 0).method_32097(-3.0F, -2.0F, -10.0F, 6.0F, 5.0F, 10.0F),
            net.minecraft.class_5603.method_32091(0.0F, 19.0F, 8.0F, -0.34906584F, 0.0F, 0.0F));
        root.method_32117("left_front_leg", net.minecraft.class_5606.method_32108().method_32101(8, 15).method_32097(-1.0F, 0.0F, -1.0F, 2.0F, 7.0F, 2.0F),
            net.minecraft.class_5603.method_32091(3.0F, 17.0F, -1.0F, -0.17453292F, 0.0F, 0.0F));
        root.method_32117("right_front_leg", net.minecraft.class_5606.method_32108().method_32101(0, 15).method_32097(-1.0F, 0.0F, -1.0F, 2.0F, 7.0F, 2.0F),
            net.minecraft.class_5603.method_32091(-3.0F, 17.0F, -1.0F, -0.17453292F, 0.0F, 0.0F));
        root.method_32117("head", net.minecraft.class_5606.method_32108().method_32101(32, 0).method_32097(-2.5F, -4.0F, -5.0F, 5.0F, 4.0F, 5.0F),
            net.minecraft.class_5603.method_32090(0.0F, 16.0F, -1.0F));
        root.method_32117("right_ear", net.minecraft.class_5606.method_32108().method_32101(52, 0).method_32097(-2.5F, -9.0F, -1.0F, 2.0F, 5.0F, 1.0F),
            net.minecraft.class_5603.method_32091(0.0F, 16.0F, -1.0F, 0.0F, -0.2617994F, 0.0F));
        root.method_32117("left_ear", net.minecraft.class_5606.method_32108().method_32101(58, 0).method_32097(0.5F, -9.0F, -1.0F, 2.0F, 5.0F, 1.0F),
            net.minecraft.class_5603.method_32091(0.0F, 16.0F, -1.0F, 0.0F, 0.2617994F, 0.0F));
        root.method_32117("tail", net.minecraft.class_5606.method_32108().method_32101(52, 6).method_32097(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 2.0F),
            net.minecraft.class_5603.method_32091(0.0F, 20.0F, 7.0F, -0.3490659F, 0.0F, 0.0F));
        root.method_32117("nose", net.minecraft.class_5606.method_32108().method_32101(32, 9).method_32097(-0.5F, -2.5F, -5.5F, 1.0F, 1.0F, 1.0F),
            net.minecraft.class_5603.method_32090(0.0F, 16.0F, -1.0F));
        return net.minecraft.class_5607.method_32110(mesh, 64, 32);
    }
}

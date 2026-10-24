package top.dext.centurybridge.step.v26_1.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.11's model mesh builders and baby transformers, as 1.21.11 wrote them, over 26.1's mesh builder API
 * (MeshDefinition, PartDefinition, CubeListBuilder, PartPose, CubeDeformation, LayerDefinition, MeshTransformer,
 * BabyModelTransform). 26.1 rebuilt the baby models as models of their own and dropped or reshaped these; a 1.21.11
 * mod registering a layer with them gets 1.21.11's geometry. One holder per 1.21.11 model class, members named as
 * 1.21.11 named them; a mesh helper 1.21.11 shared (HumanoidModel/PlayerModel.createMesh, createBodyMesh) is
 * 1.21.11's own copy here, not 26.1's same-named method, so the geometry is 1.21.11's whatever 26.1 changed.
 * Translated from 1.21.11's bytecode; the branches (a baby flag choosing a transformer) and lambdas by hand.
 */
@Environment(EnvType.CLIENT)
public final class CbWallModelMeshes {

    private CbWallModelMeshes() {
    }

    /** 1.21.11 AbstractEquineModel */
    @Environment(EnvType.CLIENT)
    public static final class AbstractEquineModel {

        private AbstractEquineModel() {
        }

        /** 1.21.11 AbstractEquineModel.createBabyMesh (method_62053) */
        public static net.minecraft.client.model.geom.builders.MeshDefinition createBabyMesh(net.minecraft.client.model.geom.builders.CubeDeformation p0) {
            return AbstractEquineModel.BABY_TRANSFORMER.apply(AbstractEquineModel.createFullScaleBabyMesh(p0));
        }

        /** 1.21.11 AbstractEquineModel.createFullScaleBabyMesh (method_62054) */
        public static net.minecraft.client.model.geom.builders.MeshDefinition createFullScaleBabyMesh(net.minecraft.client.model.geom.builders.CubeDeformation p0) {
            var v1 = AbstractEquineModel.createBodyMesh(p0);
            var v2 = v1.getRoot();
            var v3 = p0.extend(0.0F, 5.5F, 0.0F);
            v2.addOrReplaceChild("left_hind_leg", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(48, 21).mirror().addBox(-3.0F, -1.01F, -1.0F, 4.0F, 11.0F, 4.0F, v3), net.minecraft.client.model.geom.PartPose.offset(4.0F, 14.0F, 7.0F));
            v2.addOrReplaceChild("right_hind_leg", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(48, 21).addBox(-1.0F, -1.01F, -1.0F, 4.0F, 11.0F, 4.0F, v3), net.minecraft.client.model.geom.PartPose.offset(-4.0F, 14.0F, 7.0F));
            v2.addOrReplaceChild("left_front_leg", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(48, 21).mirror().addBox(-3.0F, -1.01F, -1.9F, 4.0F, 11.0F, 4.0F, v3), net.minecraft.client.model.geom.PartPose.offset(4.0F, 14.0F, -10.0F));
            v2.addOrReplaceChild("right_front_leg", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(48, 21).addBox(-1.0F, -1.01F, -1.9F, 4.0F, 11.0F, 4.0F, v3), net.minecraft.client.model.geom.PartPose.offset(-4.0F, 14.0F, -10.0F));
            return v1;
        }

        /** 1.21.11 AbstractEquineModel.BABY_TRANSFORMER (field_52854) */
        public static final net.minecraft.client.model.geom.builders.MeshTransformer BABY_TRANSFORMER = new net.minecraft.client.model.BabyModelTransform(true, 16.2F, 1.36F, 2.7272F, 2.0F, 20.0F, java.util.Set.of("head_parts"));

        /** 1.21.11 AbstractEquineModel.createBodyMesh (method_32010) */
        public static net.minecraft.client.model.geom.builders.MeshDefinition createBodyMesh(net.minecraft.client.model.geom.builders.CubeDeformation p0) {
            var v1 = new net.minecraft.client.model.geom.builders.MeshDefinition();
            var v2 = v1.getRoot();
            var v3 = v2.addOrReplaceChild("body", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(0, 32).addBox(-5.0F, -8.0F, -17.0F, 10.0F, 10.0F, 22.0F, new net.minecraft.client.model.geom.builders.CubeDeformation(0.05F)), net.minecraft.client.model.geom.PartPose.offset(0.0F, 11.0F, 5.0F));
            var v4 = v2.addOrReplaceChild("head_parts", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(0, 35).addBox(-2.05F, -6.0F, -2.0F, 4.0F, 12.0F, 7.0F), net.minecraft.client.model.geom.PartPose.offsetAndRotation(0.0F, 4.0F, -12.0F, 0.5235988F, 0.0F, 0.0F));
            var v5 = v4.addOrReplaceChild("head", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(0, 13).addBox(-3.0F, -11.0F, -2.0F, 6.0F, 5.0F, 7.0F, p0), net.minecraft.client.model.geom.PartPose.ZERO);
            v4.addOrReplaceChild("mane", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(56, 36).addBox(-1.0F, -11.0F, 5.01F, 2.0F, 16.0F, 2.0F, p0), net.minecraft.client.model.geom.PartPose.ZERO);
            v4.addOrReplaceChild("upper_mouth", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(0, 25).addBox(-2.0F, -11.0F, -7.0F, 4.0F, 5.0F, 5.0F, p0), net.minecraft.client.model.geom.PartPose.ZERO);
            v2.addOrReplaceChild("left_hind_leg", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(48, 21).mirror().addBox(-3.0F, -1.01F, -1.0F, 4.0F, 11.0F, 4.0F, p0), net.minecraft.client.model.geom.PartPose.offset(4.0F, 14.0F, 7.0F));
            v2.addOrReplaceChild("right_hind_leg", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(48, 21).addBox(-1.0F, -1.01F, -1.0F, 4.0F, 11.0F, 4.0F, p0), net.minecraft.client.model.geom.PartPose.offset(-4.0F, 14.0F, 7.0F));
            v2.addOrReplaceChild("left_front_leg", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(48, 21).mirror().addBox(-3.0F, -1.01F, -1.9F, 4.0F, 11.0F, 4.0F, p0), net.minecraft.client.model.geom.PartPose.offset(4.0F, 14.0F, -10.0F));
            v2.addOrReplaceChild("right_front_leg", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(48, 21).addBox(-1.0F, -1.01F, -1.9F, 4.0F, 11.0F, 4.0F, p0), net.minecraft.client.model.geom.PartPose.offset(-4.0F, 14.0F, -10.0F));
            v3.addOrReplaceChild("tail", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(42, 36).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 14.0F, 4.0F, p0), net.minecraft.client.model.geom.PartPose.offsetAndRotation(0.0F, -5.0F, 2.0F, 0.5235988F, 0.0F, 0.0F));
            v5.addOrReplaceChild("left_ear", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(19, 16).addBox(0.55F, -13.0F, 4.0F, 2.0F, 3.0F, 1.0F, new net.minecraft.client.model.geom.builders.CubeDeformation(-0.001F)), net.minecraft.client.model.geom.PartPose.ZERO);
            v5.addOrReplaceChild("right_ear", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(19, 16).addBox(-2.55F, -13.0F, 4.0F, 2.0F, 3.0F, 1.0F, new net.minecraft.client.model.geom.builders.CubeDeformation(-0.001F)), net.minecraft.client.model.geom.PartPose.ZERO);
            return v1;
        }
    }

    /** 1.21.11 AbstractPiglinModel */
    @Environment(EnvType.CLIENT)
    public static final class AbstractPiglinModel {

        private AbstractPiglinModel() {
        }

        /** 1.21.11 AbstractPiglinModel.createMesh (method_62055) */
        public static net.minecraft.client.model.geom.builders.MeshDefinition createMesh(net.minecraft.client.model.geom.builders.CubeDeformation p0) {
            var v1 = PlayerModel.createMesh(p0, false);
            var v2 = v1.getRoot();
            v2.addOrReplaceChild("body", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, p0), net.minecraft.client.model.geom.PartPose.ZERO);
            var v3 = AbstractPiglinModel.addHead(p0, v1);
            v3.clearChild("hat");
            return v1;
        }

        /** 1.21.11 AbstractPiglinModel.addHead (method_62056) */
        public static net.minecraft.client.model.geom.builders.PartDefinition addHead(net.minecraft.client.model.geom.builders.CubeDeformation p0, net.minecraft.client.model.geom.builders.MeshDefinition p1) {
            var v2 = p1.getRoot();
            var v3 = v2.addOrReplaceChild("head", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -8.0F, -4.0F, 10.0F, 8.0F, 8.0F, p0).texOffs(31, 1).addBox(-2.0F, -4.0F, -5.0F, 4.0F, 4.0F, 1.0F, p0).texOffs(2, 4).addBox(2.0F, -2.0F, -5.0F, 1.0F, 2.0F, 1.0F, p0).texOffs(2, 0).addBox(-3.0F, -2.0F, -5.0F, 1.0F, 2.0F, 1.0F, p0), net.minecraft.client.model.geom.PartPose.ZERO);
            v3.addOrReplaceChild("left_ear", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(51, 6).addBox(0.0F, 0.0F, -2.0F, 1.0F, 5.0F, 4.0F, p0), net.minecraft.client.model.geom.PartPose.offsetAndRotation(4.5F, -6.0F, 0.0F, 0.0F, 0.0F, -0.5235988F));
            v3.addOrReplaceChild("right_ear", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(39, 6).addBox(-1.0F, 0.0F, -2.0F, 1.0F, 5.0F, 4.0F, p0), net.minecraft.client.model.geom.PartPose.offsetAndRotation(-4.5F, -6.0F, 0.0F, 0.0F, 0.0F, 0.5235988F));
            return v3;
        }
    }

    /** 1.21.11 ArmadilloModel */
    @Environment(EnvType.CLIENT)
    public static final class ArmadilloModel {

        private ArmadilloModel() {
        }

        /** 1.21.11 ArmadilloModel.createBodyLayer (method_55818) */
        public static net.minecraft.client.model.geom.builders.LayerDefinition createBodyLayer() {
            var v0 = new net.minecraft.client.model.geom.builders.MeshDefinition();
            var v1 = v0.getRoot();
            var v2 = v1.addOrReplaceChild("body", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(0, 20).addBox(-4.0F, -7.0F, -10.0F, 8.0F, 8.0F, 12.0F, new net.minecraft.client.model.geom.builders.CubeDeformation(0.3F)).texOffs(0, 40).addBox(-4.0F, -7.0F, -10.0F, 8.0F, 8.0F, 12.0F, new net.minecraft.client.model.geom.builders.CubeDeformation(0.0F)), net.minecraft.client.model.geom.PartPose.offset(0.0F, 21.0F, 4.0F));
            v2.addOrReplaceChild("tail", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(44, 53).addBox(-0.5F, -0.0865F, 0.0933F, 1.0F, 6.0F, 1.0F, new net.minecraft.client.model.geom.builders.CubeDeformation(0.0F)), net.minecraft.client.model.geom.PartPose.offsetAndRotation(0.0F, -3.0F, 1.0F, 0.5061F, 0.0F, 0.0F));
            var v3 = v2.addOrReplaceChild("head", net.minecraft.client.model.geom.builders.CubeListBuilder.create(), net.minecraft.client.model.geom.PartPose.offset(0.0F, -2.0F, -11.0F));
            v3.addOrReplaceChild("head_cube", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(43, 15).addBox(-1.5F, -1.0F, -1.0F, 3.0F, 5.0F, 2.0F, new net.minecraft.client.model.geom.builders.CubeDeformation(0.0F)), net.minecraft.client.model.geom.PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.3927F, 0.0F, 0.0F));
            var v4 = v3.addOrReplaceChild("right_ear", net.minecraft.client.model.geom.builders.CubeListBuilder.create(), net.minecraft.client.model.geom.PartPose.offset(-1.0F, -1.0F, 0.0F));
            v4.addOrReplaceChild("right_ear_cube", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(43, 10).addBox(-2.0F, -3.0F, 0.0F, 2.0F, 5.0F, 0.0F, new net.minecraft.client.model.geom.builders.CubeDeformation(0.0F)), net.minecraft.client.model.geom.PartPose.offsetAndRotation(-0.5F, 0.0F, -0.6F, 0.1886F, -0.3864F, -0.0718F));
            var v5 = v3.addOrReplaceChild("left_ear", net.minecraft.client.model.geom.builders.CubeListBuilder.create(), net.minecraft.client.model.geom.PartPose.offset(1.0F, -2.0F, 0.0F));
            v5.addOrReplaceChild("left_ear_cube", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(47, 10).addBox(0.0F, -3.0F, 0.0F, 2.0F, 5.0F, 0.0F, new net.minecraft.client.model.geom.builders.CubeDeformation(0.0F)), net.minecraft.client.model.geom.PartPose.offsetAndRotation(0.5F, 1.0F, -0.6F, 0.1886F, 0.3864F, 0.0718F));
            v1.addOrReplaceChild("right_hind_leg", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(51, 31).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, new net.minecraft.client.model.geom.builders.CubeDeformation(0.0F)), net.minecraft.client.model.geom.PartPose.offset(-2.0F, 21.0F, 4.0F));
            v1.addOrReplaceChild("left_hind_leg", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(42, 31).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, new net.minecraft.client.model.geom.builders.CubeDeformation(0.0F)), net.minecraft.client.model.geom.PartPose.offset(2.0F, 21.0F, 4.0F));
            v1.addOrReplaceChild("right_front_leg", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(51, 43).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, new net.minecraft.client.model.geom.builders.CubeDeformation(0.0F)), net.minecraft.client.model.geom.PartPose.offset(-2.0F, 21.0F, -4.0F));
            v1.addOrReplaceChild("left_front_leg", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(42, 43).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, new net.minecraft.client.model.geom.builders.CubeDeformation(0.0F)), net.minecraft.client.model.geom.PartPose.offset(2.0F, 21.0F, -4.0F));
            v1.addOrReplaceChild("cube", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -10.0F, -6.0F, 10.0F, 10.0F, 10.0F, new net.minecraft.client.model.geom.builders.CubeDeformation(0.0F)), net.minecraft.client.model.geom.PartPose.offset(0.0F, 24.0F, 0.0F));
            return net.minecraft.client.model.geom.builders.LayerDefinition.create(v0, 64, 64);
        }

        /** 1.21.11 ArmadilloModel.BABY_TRANSFORMER (field_52867) */
        public static final net.minecraft.client.model.geom.builders.MeshTransformer BABY_TRANSFORMER = net.minecraft.client.model.geom.builders.MeshTransformer.scaling(0.6F);
    }

    /** 1.21.11 AxolotlModel */
    @Environment(EnvType.CLIENT)
    public static final class AxolotlModel {

        private AxolotlModel() {
        }

        /** 1.21.11 AxolotlModel.BABY_TRANSFORMER (field_52869) */
        public static final net.minecraft.client.model.geom.builders.MeshTransformer BABY_TRANSFORMER = net.minecraft.client.model.geom.builders.MeshTransformer.scaling(0.5F);
    }

    /** 1.21.11 BeeModel */
    @Environment(EnvType.CLIENT)
    public static final class BeeModel {

        private BeeModel() {
        }

        /** 1.21.11 BeeModel.createBodyLayer (method_31981) */
        public static net.minecraft.client.model.geom.builders.LayerDefinition createBodyLayer() {
            var v0 = new net.minecraft.client.model.geom.builders.MeshDefinition();
            var v1 = v0.getRoot();
            var v2 = v1.addOrReplaceChild("bone", net.minecraft.client.model.geom.builders.CubeListBuilder.create(), net.minecraft.client.model.geom.PartPose.offset(0.0F, 19.0F, 0.0F));
            var v3 = v2.addOrReplaceChild("body", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(0, 0).addBox(-3.5F, -4.0F, -5.0F, 7.0F, 7.0F, 10.0F), net.minecraft.client.model.geom.PartPose.ZERO);
            v3.addOrReplaceChild("stinger", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(26, 7).addBox(0.0F, -1.0F, 5.0F, 0.0F, 1.0F, 2.0F), net.minecraft.client.model.geom.PartPose.ZERO);
            v3.addOrReplaceChild("left_antenna", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(2, 0).addBox(1.5F, -2.0F, -3.0F, 1.0F, 2.0F, 3.0F), net.minecraft.client.model.geom.PartPose.offset(0.0F, -2.0F, -5.0F));
            v3.addOrReplaceChild("right_antenna", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(2, 3).addBox(-2.5F, -2.0F, -3.0F, 1.0F, 2.0F, 3.0F), net.minecraft.client.model.geom.PartPose.offset(0.0F, -2.0F, -5.0F));
            var v4 = new net.minecraft.client.model.geom.builders.CubeDeformation(0.001F);
            v2.addOrReplaceChild("right_wing", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(0, 18).addBox(-9.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, v4), net.minecraft.client.model.geom.PartPose.offsetAndRotation(-1.5F, -4.0F, -3.0F, 0.0F, -0.2618F, 0.0F));
            v2.addOrReplaceChild("left_wing", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(0, 18).mirror().addBox(0.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, v4), net.minecraft.client.model.geom.PartPose.offsetAndRotation(1.5F, -4.0F, -3.0F, 0.0F, 0.2618F, 0.0F));
            v2.addOrReplaceChild("front_legs", net.minecraft.client.model.geom.builders.CubeListBuilder.create().addBox("front_legs", -5.0F, 0.0F, 0.0F, 7, 2, 0, 26, 1), net.minecraft.client.model.geom.PartPose.offset(1.5F, 3.0F, -2.0F));
            v2.addOrReplaceChild("middle_legs", net.minecraft.client.model.geom.builders.CubeListBuilder.create().addBox("middle_legs", -5.0F, 0.0F, 0.0F, 7, 2, 0, 26, 3), net.minecraft.client.model.geom.PartPose.offset(1.5F, 3.0F, 0.0F));
            v2.addOrReplaceChild("back_legs", net.minecraft.client.model.geom.builders.CubeListBuilder.create().addBox("back_legs", -5.0F, 0.0F, 0.0F, 7, 2, 0, 26, 5), net.minecraft.client.model.geom.PartPose.offset(1.5F, 3.0F, 2.0F));
            return net.minecraft.client.model.geom.builders.LayerDefinition.create(v0, 64, 64);
        }

        /** 1.21.11 BeeModel.BABY_TRANSFORMER (field_52871) */
        public static final net.minecraft.client.model.geom.builders.MeshTransformer BABY_TRANSFORMER = net.minecraft.client.model.geom.builders.MeshTransformer.scaling(0.5F);
    }

    /** 1.21.11 CamelModel */
    @Environment(EnvType.CLIENT)
    public static final class CamelModel {

        private CamelModel() {
        }

        /** 1.21.11 CamelModel.createBodyLayer (method_45707) */
        public static net.minecraft.client.model.geom.builders.LayerDefinition createBodyLayer() {
            return net.minecraft.client.model.geom.builders.LayerDefinition.create(CamelModel.createBodyMesh(), 128, 128);
        }

        /** 1.21.11 CamelModel.createBodyMesh (method_66836) */
        public static net.minecraft.client.model.geom.builders.MeshDefinition createBodyMesh() {
            var v0 = new net.minecraft.client.model.geom.builders.MeshDefinition();
            var v1 = v0.getRoot();
            var v2 = v1.addOrReplaceChild("body", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(0, 25).addBox(-7.5F, -12.0F, -23.5F, 15.0F, 12.0F, 27.0F), net.minecraft.client.model.geom.PartPose.offset(0.0F, 4.0F, 9.5F));
            v2.addOrReplaceChild("hump", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(74, 0).addBox(-4.5F, -5.0F, -5.5F, 9.0F, 5.0F, 11.0F), net.minecraft.client.model.geom.PartPose.offset(0.0F, -12.0F, -10.0F));
            v2.addOrReplaceChild("tail", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(122, 0).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 14.0F, 0.0F), net.minecraft.client.model.geom.PartPose.offset(0.0F, -9.0F, 3.5F));
            var v3 = v2.addOrReplaceChild("head", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(60, 24).addBox(-3.5F, -7.0F, -15.0F, 7.0F, 8.0F, 19.0F).texOffs(21, 0).addBox(-3.5F, -21.0F, -15.0F, 7.0F, 14.0F, 7.0F).texOffs(50, 0).addBox(-2.5F, -21.0F, -21.0F, 5.0F, 5.0F, 6.0F), net.minecraft.client.model.geom.PartPose.offset(0.0F, -3.0F, -19.5F));
            v3.addOrReplaceChild("left_ear", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(45, 0).addBox(-0.5F, 0.5F, -1.0F, 3.0F, 1.0F, 2.0F), net.minecraft.client.model.geom.PartPose.offset(2.5F, -21.0F, -9.5F));
            v3.addOrReplaceChild("right_ear", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(67, 0).addBox(-2.5F, 0.5F, -1.0F, 3.0F, 1.0F, 2.0F), net.minecraft.client.model.geom.PartPose.offset(-2.5F, -21.0F, -9.5F));
            v1.addOrReplaceChild("left_hind_leg", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(58, 16).addBox(-2.5F, 2.0F, -2.5F, 5.0F, 21.0F, 5.0F), net.minecraft.client.model.geom.PartPose.offset(4.9F, 1.0F, 9.5F));
            v1.addOrReplaceChild("right_hind_leg", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(94, 16).addBox(-2.5F, 2.0F, -2.5F, 5.0F, 21.0F, 5.0F), net.minecraft.client.model.geom.PartPose.offset(-4.9F, 1.0F, 9.5F));
            v1.addOrReplaceChild("left_front_leg", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, 2.0F, -2.5F, 5.0F, 21.0F, 5.0F), net.minecraft.client.model.geom.PartPose.offset(4.9F, 1.0F, -10.5F));
            v1.addOrReplaceChild("right_front_leg", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(0, 26).addBox(-2.5F, 2.0F, -2.5F, 5.0F, 21.0F, 5.0F), net.minecraft.client.model.geom.PartPose.offset(-4.9F, 1.0F, -10.5F));
            return v0;
        }

        /** 1.21.11 CamelModel.BABY_TRANSFORMER (field_52883) */
        public static final net.minecraft.client.model.geom.builders.MeshTransformer BABY_TRANSFORMER = net.minecraft.client.model.geom.builders.MeshTransformer.scaling(0.45F);
    }

    /** 1.21.11 ChickenModel */
    @Environment(EnvType.CLIENT)
    public static final class ChickenModel {

        private ChickenModel() {
        }

        /** 1.21.11 ChickenModel.createBodyLayer (method_31988) */
        public static net.minecraft.client.model.geom.builders.LayerDefinition createBodyLayer() {
            var v0 = ChickenModel.createBaseChickenModel();
            return net.minecraft.client.model.geom.builders.LayerDefinition.create(v0, 64, 32);
        }

        /** 1.21.11 ChickenModel.createBaseChickenModel (method_67546) */
        public static net.minecraft.client.model.geom.builders.MeshDefinition createBaseChickenModel() {
            var v0 = new net.minecraft.client.model.geom.builders.MeshDefinition();
            var v1 = v0.getRoot();
            var v2 = v1.addOrReplaceChild("head", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -6.0F, -2.0F, 4.0F, 6.0F, 3.0F), net.minecraft.client.model.geom.PartPose.offset(0.0F, 15.0F, -4.0F));
            v2.addOrReplaceChild("beak", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(14, 0).addBox(-2.0F, -4.0F, -4.0F, 4.0F, 2.0F, 2.0F), net.minecraft.client.model.geom.PartPose.ZERO);
            v2.addOrReplaceChild("red_thing", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(14, 4).addBox(-1.0F, -2.0F, -3.0F, 2.0F, 2.0F, 2.0F), net.minecraft.client.model.geom.PartPose.ZERO);
            v1.addOrReplaceChild("body", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(0, 9).addBox(-3.0F, -4.0F, -3.0F, 6.0F, 8.0F, 6.0F), net.minecraft.client.model.geom.PartPose.offsetAndRotation(0.0F, 16.0F, 0.0F, 1.5707964F, 0.0F, 0.0F));
            var v3 = net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(26, 0).addBox(-1.0F, 0.0F, -3.0F, 3.0F, 5.0F, 3.0F);
            v1.addOrReplaceChild("right_leg", v3, net.minecraft.client.model.geom.PartPose.offset(-2.0F, 19.0F, 1.0F));
            v1.addOrReplaceChild("left_leg", v3, net.minecraft.client.model.geom.PartPose.offset(1.0F, 19.0F, 1.0F));
            v1.addOrReplaceChild("right_wing", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(24, 13).addBox(0.0F, 0.0F, -3.0F, 1.0F, 4.0F, 6.0F), net.minecraft.client.model.geom.PartPose.offset(-4.0F, 13.0F, 0.0F));
            v1.addOrReplaceChild("left_wing", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(24, 13).addBox(-1.0F, 0.0F, -3.0F, 1.0F, 4.0F, 6.0F), net.minecraft.client.model.geom.PartPose.offset(4.0F, 13.0F, 0.0F));
            return v0;
        }

        /** 1.21.11 ChickenModel.BABY_TRANSFORMER (field_52891) */
        public static final net.minecraft.client.model.geom.builders.MeshTransformer BABY_TRANSFORMER = new net.minecraft.client.model.BabyModelTransform(false, 5.0F, 2.0F, 2.0F, 1.99F, 24.0F, java.util.Set.of("head", "beak", "red_thing"));

        /** 1.21.11 ChickenModel.RED_THING (field_32471) */
        public static final String RED_THING = "red_thing";
    }

    /** 1.21.11 DonkeyModel */
    @Environment(EnvType.CLIENT)
    public static final class DonkeyModel {

        private DonkeyModel() {
        }

        /** 1.21.11 DonkeyModel.createBabyLayer (method_62080) */
        public static net.minecraft.client.model.geom.builders.LayerDefinition createBabyLayer(float p0) {
            return net.minecraft.client.model.geom.builders.LayerDefinition.create(AbstractEquineModel.createFullScaleBabyMesh(net.minecraft.client.model.geom.builders.CubeDeformation.NONE), 64, 64).apply(DonkeyModel.DONKEY_TRANSFORMER).apply(AbstractEquineModel.BABY_TRANSFORMER).apply(net.minecraft.client.model.geom.builders.MeshTransformer.scaling(p0));
        }

        /** 1.21.11 DonkeyModel.createSaddleLayer (method_66838) */
        public static net.minecraft.client.model.geom.builders.LayerDefinition createSaddleLayer(float p0, boolean p1) {
            return EquineSaddleModel.createFullScaleSaddleLayer(p1).apply(DonkeyModel.DONKEY_TRANSFORMER)
                .apply(p1 ? AbstractEquineModel.BABY_TRANSFORMER : net.minecraft.client.model.geom.builders.MeshTransformer.IDENTITY)
                .apply(net.minecraft.client.model.geom.builders.MeshTransformer.scaling(p0));
        }

        /** 1.21.11 DonkeyModel.modifyMesh (method_62077) */
        public static void modifyMesh(net.minecraft.client.model.geom.builders.PartDefinition p0) {
            var v1 = p0.getChild("body");
            var v2 = net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(26, 21).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 8.0F, 3.0F);
            v1.addOrReplaceChild("left_chest", v2, net.minecraft.client.model.geom.PartPose.offsetAndRotation(6.0F, -8.0F, 0.0F, 0.0F, -1.5707964F, 0.0F));
            v1.addOrReplaceChild("right_chest", v2, net.minecraft.client.model.geom.PartPose.offsetAndRotation(-6.0F, -8.0F, 0.0F, 0.0F, 1.5707964F, 0.0F));
            var v3 = p0.getChild("head_parts").getChild("head");
            var v4 = net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(0, 12).addBox(-1.0F, -7.0F, 0.0F, 2.0F, 7.0F, 1.0F);
            v3.addOrReplaceChild("left_ear", v4, net.minecraft.client.model.geom.PartPose.offsetAndRotation(1.25F, -10.0F, 4.0F, 0.2617994F, 0.0F, 0.2617994F));
            v3.addOrReplaceChild("right_ear", v4, net.minecraft.client.model.geom.PartPose.offsetAndRotation(-1.25F, -10.0F, 4.0F, 0.2617994F, 0.0F, -0.2617994F));
        }

        /** 1.21.11 DonkeyModel.DONKEY_TRANSFORMER (field_56070): the mesh with modifyMesh applied to its root */
        public static final net.minecraft.client.model.geom.builders.MeshTransformer DONKEY_TRANSFORMER = mesh -> {
            modifyMesh(mesh.getRoot());
            return mesh;
        };
    }

    /** 1.21.11 EquineSaddleModel */
    @Environment(EnvType.CLIENT)
    public static final class EquineSaddleModel {

        private EquineSaddleModel() {
        }

        /** 1.21.11 EquineSaddleModel.createSaddleLayer (method_66840) */
        public static net.minecraft.client.model.geom.builders.LayerDefinition createSaddleLayer(boolean p0) {
            return EquineSaddleModel.createFullScaleSaddleLayer(p0)
                .apply(p0 ? AbstractEquineModel.BABY_TRANSFORMER : net.minecraft.client.model.geom.builders.MeshTransformer.IDENTITY);
        }

        /** 1.21.11 EquineSaddleModel.createFullScaleSaddleLayer (method_66841) */
        public static net.minecraft.client.model.geom.builders.LayerDefinition createFullScaleSaddleLayer(boolean p0) {
            var v1 = p0 ? AbstractEquineModel.createFullScaleBabyMesh(net.minecraft.client.model.geom.builders.CubeDeformation.NONE)
                : AbstractEquineModel.createBodyMesh(net.minecraft.client.model.geom.builders.CubeDeformation.NONE);
            var v2 = v1.getRoot();
            var v3 = v2.getChild("body");
            var v4 = v2.getChild("head_parts");
            v3.addOrReplaceChild("saddle", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(26, 0).addBox(-5.0F, -8.0F, -9.0F, 10.0F, 9.0F, 9.0F, new net.minecraft.client.model.geom.builders.CubeDeformation(0.5F)), net.minecraft.client.model.geom.PartPose.ZERO);
            v4.addOrReplaceChild("left_saddle_mouth", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(29, 5).addBox(2.0F, -9.0F, -6.0F, 1.0F, 2.0F, 2.0F), net.minecraft.client.model.geom.PartPose.ZERO);
            v4.addOrReplaceChild("right_saddle_mouth", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(29, 5).addBox(-3.0F, -9.0F, -6.0F, 1.0F, 2.0F, 2.0F), net.minecraft.client.model.geom.PartPose.ZERO);
            v4.addOrReplaceChild("left_saddle_line", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(32, 2).addBox(3.1F, -6.0F, -8.0F, 0.0F, 3.0F, 16.0F), net.minecraft.client.model.geom.PartPose.rotation(-0.5235988F, 0.0F, 0.0F));
            v4.addOrReplaceChild("right_saddle_line", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(32, 2).addBox(-3.1F, -6.0F, -8.0F, 0.0F, 3.0F, 16.0F), net.minecraft.client.model.geom.PartPose.rotation(-0.5235988F, 0.0F, 0.0F));
            v4.addOrReplaceChild("head_saddle", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(1, 1).addBox(-3.0F, -11.0F, -1.9F, 6.0F, 5.0F, 6.0F, new net.minecraft.client.model.geom.builders.CubeDeformation(0.22F)), net.minecraft.client.model.geom.PartPose.ZERO);
            v4.addOrReplaceChild("mouth_saddle_wrap", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(19, 0).addBox(-2.0F, -11.0F, -4.0F, 4.0F, 5.0F, 2.0F, new net.minecraft.client.model.geom.builders.CubeDeformation(0.2F)), net.minecraft.client.model.geom.PartPose.ZERO);
            return net.minecraft.client.model.geom.builders.LayerDefinition.create(v1, 64, 64);
        }
    }

    /** 1.21.11 FoxModel */
    @Environment(EnvType.CLIENT)
    public static final class FoxModel {

        private FoxModel() {
        }

        /** 1.21.11 FoxModel.createBodyLayer (method_31999) */
        public static net.minecraft.client.model.geom.builders.LayerDefinition createBodyLayer() {
            var v0 = new net.minecraft.client.model.geom.builders.MeshDefinition();
            var v1 = v0.getRoot();
            var v2 = v1.addOrReplaceChild("head", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(1, 5).addBox(-3.0F, -2.0F, -5.0F, 8.0F, 6.0F, 6.0F), net.minecraft.client.model.geom.PartPose.offset(-1.0F, 16.5F, -3.0F));
            v2.addOrReplaceChild("right_ear", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(8, 1).addBox(-3.0F, -4.0F, -4.0F, 2.0F, 2.0F, 1.0F), net.minecraft.client.model.geom.PartPose.ZERO);
            v2.addOrReplaceChild("left_ear", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(15, 1).addBox(3.0F, -4.0F, -4.0F, 2.0F, 2.0F, 1.0F), net.minecraft.client.model.geom.PartPose.ZERO);
            v2.addOrReplaceChild("nose", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(6, 18).addBox(-1.0F, 2.01F, -8.0F, 4.0F, 2.0F, 3.0F), net.minecraft.client.model.geom.PartPose.ZERO);
            var v3 = v1.addOrReplaceChild("body", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(24, 15).addBox(-3.0F, 3.999F, -3.5F, 6.0F, 11.0F, 6.0F), net.minecraft.client.model.geom.PartPose.offsetAndRotation(0.0F, 16.0F, -6.0F, 1.5707964F, 0.0F, 0.0F));
            var v4 = new net.minecraft.client.model.geom.builders.CubeDeformation(0.001F);
            var v5 = net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(4, 24).addBox(2.0F, 0.5F, -1.0F, 2.0F, 6.0F, 2.0F, v4);
            var v6 = net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(13, 24).addBox(2.0F, 0.5F, -1.0F, 2.0F, 6.0F, 2.0F, v4);
            v1.addOrReplaceChild("right_hind_leg", v6, net.minecraft.client.model.geom.PartPose.offset(-5.0F, 17.5F, 7.0F));
            v1.addOrReplaceChild("left_hind_leg", v5, net.minecraft.client.model.geom.PartPose.offset(-1.0F, 17.5F, 7.0F));
            v1.addOrReplaceChild("right_front_leg", v6, net.minecraft.client.model.geom.PartPose.offset(-5.0F, 17.5F, 0.0F));
            v1.addOrReplaceChild("left_front_leg", v5, net.minecraft.client.model.geom.PartPose.offset(-1.0F, 17.5F, 0.0F));
            v3.addOrReplaceChild("tail", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(30, 0).addBox(2.0F, 0.0F, -1.0F, 4.0F, 9.0F, 5.0F), net.minecraft.client.model.geom.PartPose.offsetAndRotation(-4.0F, 15.0F, -1.0F, -0.05235988F, 0.0F, 0.0F));
            return net.minecraft.client.model.geom.builders.LayerDefinition.create(v0, 48, 32);
        }

        /** 1.21.11 FoxModel.BABY_TRANSFORMER (field_52912) */
        public static final net.minecraft.client.model.geom.builders.MeshTransformer BABY_TRANSFORMER = new net.minecraft.client.model.BabyModelTransform(true, 8.0F, 3.35F, java.util.Set.of("head"));
    }

    /** 1.21.11 GoatModel */
    @Environment(EnvType.CLIENT)
    public static final class GoatModel {

        private GoatModel() {
        }

        /** 1.21.11 GoatModel.BABY_TRANSFORMER (field_52914) */
        public static final net.minecraft.client.model.geom.builders.MeshTransformer BABY_TRANSFORMER = new net.minecraft.client.model.BabyModelTransform(true, 19.0F, 1.0F, 2.5F, 2.0F, 24.0F, java.util.Set.of("head"));
    }

    /** 1.21.11 HoglinModel */
    @Environment(EnvType.CLIENT)
    public static final class HoglinModel {

        private HoglinModel() {
        }

        /** 1.21.11 HoglinModel.createBabyLayer (method_62091) */
        public static net.minecraft.client.model.geom.builders.LayerDefinition createBabyLayer() {
            var v0 = HoglinModel.createMesh();
            var v1 = v0.getRoot().getChild("body");
            v1.addOrReplaceChild("mane", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(90, 33).addBox(0.0F, 0.0F, -9.0F, 0.0F, 10.0F, 19.0F, new net.minecraft.client.model.geom.builders.CubeDeformation(0.001F)), net.minecraft.client.model.geom.PartPose.offset(0.0F, -14.0F, -3.0F));
            return net.minecraft.client.model.geom.builders.LayerDefinition.create(v0, 128, 64).apply(HoglinModel.BABY_TRANSFORMER);
        }

        /** 1.21.11 HoglinModel.BABY_TRANSFORMER (field_52916) */
        public static final net.minecraft.client.model.geom.builders.MeshTransformer BABY_TRANSFORMER = new net.minecraft.client.model.BabyModelTransform(true, 8.0F, 6.0F, 1.9F, 2.0F, 24.0F, java.util.Set.of("head"));

        /** 1.21.11 HoglinModel.createMesh (method_62092) */
        public static net.minecraft.client.model.geom.builders.MeshDefinition createMesh() {
            var v0 = new net.minecraft.client.model.geom.builders.MeshDefinition();
            var v1 = v0.getRoot();
            var v2 = v1.addOrReplaceChild("body", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(1, 1).addBox(-8.0F, -7.0F, -13.0F, 16.0F, 14.0F, 26.0F), net.minecraft.client.model.geom.PartPose.offset(0.0F, 7.0F, 0.0F));
            v2.addOrReplaceChild("mane", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(90, 33).addBox(0.0F, 0.0F, -9.0F, 0.0F, 10.0F, 19.0F, new net.minecraft.client.model.geom.builders.CubeDeformation(0.001F)), net.minecraft.client.model.geom.PartPose.offset(0.0F, -14.0F, -7.0F));
            var v3 = v1.addOrReplaceChild("head", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(61, 1).addBox(-7.0F, -3.0F, -19.0F, 14.0F, 6.0F, 19.0F), net.minecraft.client.model.geom.PartPose.offsetAndRotation(0.0F, 2.0F, -12.0F, 0.87266463F, 0.0F, 0.0F));
            v3.addOrReplaceChild("right_ear", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(1, 1).addBox(-6.0F, -1.0F, -2.0F, 6.0F, 1.0F, 4.0F), net.minecraft.client.model.geom.PartPose.offsetAndRotation(-6.0F, -2.0F, -3.0F, 0.0F, 0.0F, -0.6981317F));
            v3.addOrReplaceChild("left_ear", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(1, 6).addBox(0.0F, -1.0F, -2.0F, 6.0F, 1.0F, 4.0F), net.minecraft.client.model.geom.PartPose.offsetAndRotation(6.0F, -2.0F, -3.0F, 0.0F, 0.0F, 0.6981317F));
            v3.addOrReplaceChild("right_horn", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(10, 13).addBox(-1.0F, -11.0F, -1.0F, 2.0F, 11.0F, 2.0F), net.minecraft.client.model.geom.PartPose.offset(-7.0F, 2.0F, -12.0F));
            v3.addOrReplaceChild("left_horn", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(1, 13).addBox(-1.0F, -11.0F, -1.0F, 2.0F, 11.0F, 2.0F), net.minecraft.client.model.geom.PartPose.offset(7.0F, 2.0F, -12.0F));
            var v4 = 14;
            var v5 = 11;
            v1.addOrReplaceChild("right_front_leg", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(66, 42).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 14.0F, 6.0F), net.minecraft.client.model.geom.PartPose.offset(-4.0F, 10.0F, -8.5F));
            v1.addOrReplaceChild("left_front_leg", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(41, 42).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 14.0F, 6.0F), net.minecraft.client.model.geom.PartPose.offset(4.0F, 10.0F, -8.5F));
            v1.addOrReplaceChild("right_hind_leg", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(21, 45).addBox(-2.5F, 0.0F, -2.5F, 5.0F, 11.0F, 5.0F), net.minecraft.client.model.geom.PartPose.offset(-5.0F, 13.0F, 10.0F));
            v1.addOrReplaceChild("left_hind_leg", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(0, 45).addBox(-2.5F, 0.0F, -2.5F, 5.0F, 11.0F, 5.0F), net.minecraft.client.model.geom.PartPose.offset(5.0F, 13.0F, 10.0F));
            return v0;
        }
    }

    /** 1.21.11 HumanoidModel */
    @Environment(EnvType.CLIENT)
    public static final class HumanoidModel {

        private HumanoidModel() {
        }

        /** 1.21.11 HumanoidModel.createMesh (method_32011) */
        public static net.minecraft.client.model.geom.builders.MeshDefinition createMesh(net.minecraft.client.model.geom.builders.CubeDeformation p0, float p1) {
            var v2 = new net.minecraft.client.model.geom.builders.MeshDefinition();
            var v3 = v2.getRoot();
            var v4 = v3.addOrReplaceChild("head", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, p0), net.minecraft.client.model.geom.PartPose.offset(0.0F, (0.0F + p1), 0.0F));
            v4.addOrReplaceChild("hat", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, p0.extend(0.5F)), net.minecraft.client.model.geom.PartPose.ZERO);
            v3.addOrReplaceChild("body", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, p0), net.minecraft.client.model.geom.PartPose.offset(0.0F, (0.0F + p1), 0.0F));
            v3.addOrReplaceChild("right_arm", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(40, 16).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, p0), net.minecraft.client.model.geom.PartPose.offset(-5.0F, (2.0F + p1), 0.0F));
            v3.addOrReplaceChild("left_arm", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(40, 16).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, p0), net.minecraft.client.model.geom.PartPose.offset(5.0F, (2.0F + p1), 0.0F));
            v3.addOrReplaceChild("right_leg", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, p0), net.minecraft.client.model.geom.PartPose.offset(-1.9F, (12.0F + p1), 0.0F));
            v3.addOrReplaceChild("left_leg", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, p0), net.minecraft.client.model.geom.PartPose.offset(1.9F, (12.0F + p1), 0.0F));
            return v2;
        }
    }

    /** 1.21.11 LlamaModel */
    @Environment(EnvType.CLIENT)
    public static final class LlamaModel {

        private LlamaModel() {
        }

        /** 1.21.11 LlamaModel.BABY_TRANSFORMER (field_52920) */
        public static final net.minecraft.client.model.geom.builders.MeshTransformer BABY_TRANSFORMER = LlamaModel::babyMesh;

        /**
         * 1.21.11's lambda behind LlamaModel.BABY_TRANSFORMER: a mesh of the root's children, each re-posed -- the head
         * moved up and scaled by one set of factors, the body by another, everything else (legs, chests) by a third
         */
        private static net.minecraft.client.model.geom.builders.MeshDefinition babyMesh(net.minecraft.client.model.geom.builders.MeshDefinition p0) {
            java.util.function.UnaryOperator<net.minecraft.client.model.geom.PartPose> head =
                pose -> pose.translated(0.0F, 21.0F, 3.52F).scaled(0.71428573F, 0.64935064F, 0.7936508F);
            java.util.function.UnaryOperator<net.minecraft.client.model.geom.PartPose> body =
                pose -> pose.translated(0.0F, 33.0F, 0.0F).scaled(0.625F, 0.45454544F, 0.45454544F);
            java.util.function.UnaryOperator<net.minecraft.client.model.geom.PartPose> rest =
                pose -> pose.translated(0.0F, 33.0F, 0.0F).scaled(0.45454544F, 0.41322312F, 0.45454544F);
            var v7 = new net.minecraft.client.model.geom.builders.MeshDefinition();
            for (java.util.Map.Entry<String, net.minecraft.client.model.geom.builders.PartDefinition> entry : p0.getRoot().getChildren()) {
                String name = entry.getKey();
                java.util.function.UnaryOperator<net.minecraft.client.model.geom.PartPose> op = switch (name) {
                    case "head" -> head;
                    case "body" -> body;
                    default -> rest;
                };
                v7.getRoot().addOrReplaceChild(name, entry.getValue().transformed(op));
            }
            return v7;
        }
    }

    /** 1.21.11 PandaModel */
    @Environment(EnvType.CLIENT)
    public static final class PandaModel {

        private PandaModel() {
        }

        /** 1.21.11 PandaModel.BABY_TRANSFORMER (field_52924) */
        public static final net.minecraft.client.model.geom.builders.MeshTransformer BABY_TRANSFORMER = new net.minecraft.client.model.BabyModelTransform(true, 23.0F, 4.8F, 2.7F, 3.0F, 49.0F, java.util.Set.of("head"));
    }

    /** 1.21.11 PlayerModel */
    @Environment(EnvType.CLIENT)
    public static final class PlayerModel {

        private PlayerModel() {
        }

        /** 1.21.11 PlayerModel.createMesh (method_32028) */
        public static net.minecraft.client.model.geom.builders.MeshDefinition createMesh(net.minecraft.client.model.geom.builders.CubeDeformation p0, boolean p1) {
            var v2 = HumanoidModel.createMesh(p0, 0.0F);
            var v3 = v2.getRoot();
            var v4 = 0.25F;
            net.minecraft.client.model.geom.builders.PartDefinition v5;
            net.minecraft.client.model.geom.builders.PartDefinition v6;
            if (p1) {
                v5 = v3.addOrReplaceChild("left_arm", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(32, 48).addBox(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, p0), net.minecraft.client.model.geom.PartPose.offset(5.0F, 2.0F, 0.0F));
                v6 = v3.addOrReplaceChild("right_arm", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(40, 16).addBox(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, p0), net.minecraft.client.model.geom.PartPose.offset(-5.0F, 2.0F, 0.0F));
                v5.addOrReplaceChild("left_sleeve", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(48, 48).addBox(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, p0.extend(0.25F)), net.minecraft.client.model.geom.PartPose.ZERO);
                v6.addOrReplaceChild("right_sleeve", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(40, 32).addBox(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, p0.extend(0.25F)), net.minecraft.client.model.geom.PartPose.ZERO);
            } else {
                v5 = v3.addOrReplaceChild("left_arm", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(32, 48).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, p0), net.minecraft.client.model.geom.PartPose.offset(5.0F, 2.0F, 0.0F));
                v6 = v3.getChild("right_arm");
                v5.addOrReplaceChild("left_sleeve", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(48, 48).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, p0.extend(0.25F)), net.minecraft.client.model.geom.PartPose.ZERO);
                v6.addOrReplaceChild("right_sleeve", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(40, 32).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, p0.extend(0.25F)), net.minecraft.client.model.geom.PartPose.ZERO);
            }
            v5 = v3.addOrReplaceChild("left_leg", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(16, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, p0), net.minecraft.client.model.geom.PartPose.offset(1.9F, 12.0F, 0.0F));
            v6 = v3.getChild("right_leg");
            v5.addOrReplaceChild("left_pants", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(0, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, p0.extend(0.25F)), net.minecraft.client.model.geom.PartPose.ZERO);
            v6.addOrReplaceChild("right_pants", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(0, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, p0.extend(0.25F)), net.minecraft.client.model.geom.PartPose.ZERO);
            var v7 = v3.getChild("body");
            v7.addOrReplaceChild("jacket", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(16, 32).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, p0.extend(0.25F)), net.minecraft.client.model.geom.PartPose.ZERO);
            return v2;
        }
    }

    /** 1.21.11 PolarBearModel */
    @Environment(EnvType.CLIENT)
    public static final class PolarBearModel {

        private PolarBearModel() {
        }

        /** 1.21.11 PolarBearModel.createBodyLayer (method_32029) */
        public static net.minecraft.client.model.geom.builders.LayerDefinition createBodyLayer(boolean p0) {
            var v1 = new net.minecraft.client.model.geom.builders.MeshDefinition();
            var v2 = v1.getRoot();
            v2.addOrReplaceChild("head", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(0, 0).addBox(-3.5F, -3.0F, -3.0F, 7.0F, 7.0F, 7.0F).texOffs(0, 44).addBox("mouth", -2.5F, 1.0F, -6.0F, 5.0F, 3.0F, 3.0F).texOffs(26, 0).addBox("right_ear", -4.5F, -4.0F, -1.0F, 2.0F, 2.0F, 1.0F).texOffs(26, 0).mirror().addBox("left_ear", 2.5F, -4.0F, -1.0F, 2.0F, 2.0F, 1.0F), net.minecraft.client.model.geom.PartPose.offset(0.0F, 10.0F, -16.0F));
            v2.addOrReplaceChild("body", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(0, 19).addBox(-5.0F, -13.0F, -7.0F, 14.0F, 14.0F, 11.0F).texOffs(39, 0).addBox(-4.0F, -25.0F, -7.0F, 12.0F, 12.0F, 10.0F), net.minecraft.client.model.geom.PartPose.offsetAndRotation(-2.0F, 9.0F, 12.0F, 1.5707964F, 0.0F, 0.0F));
            var v3 = 10;
            var v4 = net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(50, 22).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 10.0F, 8.0F);
            v2.addOrReplaceChild("right_hind_leg", v4, net.minecraft.client.model.geom.PartPose.offset(-4.5F, 14.0F, 6.0F));
            v2.addOrReplaceChild("left_hind_leg", v4, net.minecraft.client.model.geom.PartPose.offset(4.5F, 14.0F, 6.0F));
            var v5 = net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(50, 40).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 10.0F, 6.0F);
            v2.addOrReplaceChild("right_front_leg", v5, net.minecraft.client.model.geom.PartPose.offset(-3.5F, 14.0F, -8.0F));
            v2.addOrReplaceChild("left_front_leg", v5, net.minecraft.client.model.geom.PartPose.offset(3.5F, 14.0F, -8.0F));
            return net.minecraft.client.model.geom.builders.LayerDefinition.create(v1, 128, 64)
                .apply(p0 ? PolarBearModel.BABY_TRANSFORMER : net.minecraft.client.model.geom.builders.MeshTransformer.IDENTITY)
                .apply(net.minecraft.client.model.geom.builders.MeshTransformer.scaling(1.2F));
        }

        /** 1.21.11 PolarBearModel.BABY_TRANSFORMER (field_52928) */
        public static final net.minecraft.client.model.geom.builders.MeshTransformer BABY_TRANSFORMER = new net.minecraft.client.model.BabyModelTransform(true, 16.0F, 4.0F, 2.25F, 2.0F, 24.0F, java.util.Set.of("head"));
    }

    /** 1.21.11 RabbitModel */
    @Environment(EnvType.CLIENT)
    public static final class RabbitModel {

        private RabbitModel() {
        }

        /** 1.21.11 RabbitModel.createBodyLayer (method_32034) */
        public static net.minecraft.client.model.geom.builders.LayerDefinition createBodyLayer(boolean p0) {
            var v1 = new net.minecraft.client.model.geom.builders.MeshDefinition();
            var v2 = v1.getRoot();
            var v3 = v2.addOrReplaceChild("left_haunch", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(30, 15).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 4.0F, 5.0F), net.minecraft.client.model.geom.PartPose.offsetAndRotation(3.0F, 17.5F, 3.7F, -0.36651915F, 0.0F, 0.0F));
            var v4 = v2.addOrReplaceChild("right_haunch", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(16, 15).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 4.0F, 5.0F), net.minecraft.client.model.geom.PartPose.offsetAndRotation(-3.0F, 17.5F, 3.7F, -0.36651915F, 0.0F, 0.0F));
            v3.addOrReplaceChild("left_hind_foot", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(26, 24).addBox(-1.0F, 5.5F, -3.7F, 2.0F, 1.0F, 7.0F), net.minecraft.client.model.geom.PartPose.rotation(0.36651915F, 0.0F, 0.0F));
            v4.addOrReplaceChild("right_hind_foot", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(8, 24).addBox(-1.0F, 5.5F, -3.7F, 2.0F, 1.0F, 7.0F), net.minecraft.client.model.geom.PartPose.rotation(0.36651915F, 0.0F, 0.0F));
            v2.addOrReplaceChild("body", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -2.0F, -10.0F, 6.0F, 5.0F, 10.0F), net.minecraft.client.model.geom.PartPose.offsetAndRotation(0.0F, 19.0F, 8.0F, -0.34906584F, 0.0F, 0.0F));
            v2.addOrReplaceChild("left_front_leg", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(8, 15).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 7.0F, 2.0F), net.minecraft.client.model.geom.PartPose.offsetAndRotation(3.0F, 17.0F, -1.0F, -0.19198622F, 0.0F, 0.0F));
            v2.addOrReplaceChild("right_front_leg", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(0, 15).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 7.0F, 2.0F), net.minecraft.client.model.geom.PartPose.offsetAndRotation(-3.0F, 17.0F, -1.0F, -0.19198622F, 0.0F, 0.0F));
            var v5 = v2.addOrReplaceChild("head", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(32, 0).addBox(-2.5F, -4.0F, -5.0F, 5.0F, 4.0F, 5.0F), net.minecraft.client.model.geom.PartPose.offset(0.0F, 16.0F, -1.0F));
            v5.addOrReplaceChild("right_ear", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(52, 0).addBox(-2.5F, -9.0F, -1.0F, 2.0F, 5.0F, 1.0F), net.minecraft.client.model.geom.PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.2617994F, 0.0F));
            v5.addOrReplaceChild("left_ear", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(58, 0).addBox(0.5F, -9.0F, -1.0F, 2.0F, 5.0F, 1.0F), net.minecraft.client.model.geom.PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.2617994F, 0.0F));
            v2.addOrReplaceChild("tail", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(52, 6).addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 2.0F), net.minecraft.client.model.geom.PartPose.offsetAndRotation(0.0F, 20.0F, 7.0F, -0.3490659F, 0.0F, 0.0F));
            v5.addOrReplaceChild("nose", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(32, 9).addBox(-0.5F, -2.5F, -5.5F, 1.0F, 1.0F, 1.0F), net.minecraft.client.model.geom.PartPose.ZERO);
            return net.minecraft.client.model.geom.builders.LayerDefinition.create(v1, 64, 32)
                .apply(p0 ? RabbitModel.BABY_TRANSFORMER : RabbitModel.ADULT_TRANSFORMER);
        }

        /** 1.21.11 RabbitModel.BABY_TRANSFORMER (field_52931) */
        public static final net.minecraft.client.model.geom.builders.MeshTransformer BABY_TRANSFORMER = new net.minecraft.client.model.BabyModelTransform(true, 22.0F, 2.0F, 2.65F, 2.5F, 36.0F, java.util.Set.of("head", "left_ear", "right_ear", "nose"));

        /** 1.21.11 RabbitModel.ADULT_TRANSFORMER (field_52930) */
        public static final net.minecraft.client.model.geom.builders.MeshTransformer ADULT_TRANSFORMER = net.minecraft.client.model.geom.builders.MeshTransformer.scaling(0.6F);
    }

    /** 1.21.11 SheepModel */
    @Environment(EnvType.CLIENT)
    public static final class SheepModel {

        private SheepModel() {
        }

        /** 1.21.11 SheepModel.BABY_TRANSFORMER (field_52936) */
        public static final net.minecraft.client.model.geom.builders.MeshTransformer BABY_TRANSFORMER = new net.minecraft.client.model.BabyModelTransform(false, 8.0F, 4.0F, 2.0F, 2.0F, 24.0F, java.util.Set.of("head"));
        /** 1.21.11 SheepModel.createBodyLayer (method_32038) */
        public static net.minecraft.client.model.geom.builders.LayerDefinition createBodyLayer() {
            var v0 = QuadrupedModel.createBodyMesh(12, false, true, net.minecraft.client.model.geom.builders.CubeDeformation.NONE);
            var v1 = v0.getRoot();
            v1.addOrReplaceChild("head", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -4.0F, -6.0F, 6.0F, 6.0F, 8.0F), net.minecraft.client.model.geom.PartPose.offset(0.0F, 6.0F, -8.0F));
            v1.addOrReplaceChild("body", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(28, 8).addBox(-4.0F, -10.0F, -7.0F, 8.0F, 16.0F, 6.0F), net.minecraft.client.model.geom.PartPose.offsetAndRotation(0.0F, 5.0F, 2.0F, 1.5707964F, 0.0F, 0.0F));
            return net.minecraft.client.model.geom.builders.LayerDefinition.create(v0, 64, 32);
        }

    }

    /** 1.21.11 SnifferModel */
    @Environment(EnvType.CLIENT)
    public static final class SnifferModel {

        private SnifferModel() {
        }

        /** 1.21.11 SnifferModel.BABY_TRANSFORMER (field_52938) */
        public static final net.minecraft.client.model.geom.builders.MeshTransformer BABY_TRANSFORMER = net.minecraft.client.model.geom.builders.MeshTransformer.scaling(0.5F);
    }

    /** 1.21.11 StriderModel */
    @Environment(EnvType.CLIENT)
    public static final class StriderModel {

        private StriderModel() {
        }

        /** 1.21.11 StriderModel.createBodyLayer (method_32058) */
        public static net.minecraft.client.model.geom.builders.LayerDefinition createBodyLayer() {
            var v0 = new net.minecraft.client.model.geom.builders.MeshDefinition();
            var v1 = v0.getRoot();
            v1.addOrReplaceChild("right_leg", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(0, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 16.0F, 4.0F), net.minecraft.client.model.geom.PartPose.offset(-4.0F, 8.0F, 0.0F));
            v1.addOrReplaceChild("left_leg", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(0, 55).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 16.0F, 4.0F), net.minecraft.client.model.geom.PartPose.offset(4.0F, 8.0F, 0.0F));
            var v2 = v1.addOrReplaceChild("body", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -6.0F, -8.0F, 16.0F, 14.0F, 16.0F), net.minecraft.client.model.geom.PartPose.offset(0.0F, 1.0F, 0.0F));
            v2.addOrReplaceChild("right_bottom_bristle", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(16, 65).addBox(-12.0F, 0.0F, 0.0F, 12.0F, 0.0F, 16.0F, true), net.minecraft.client.model.geom.PartPose.offsetAndRotation(-8.0F, 4.0F, -8.0F, 0.0F, 0.0F, -1.2217305F));
            v2.addOrReplaceChild("right_middle_bristle", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(16, 49).addBox(-12.0F, 0.0F, 0.0F, 12.0F, 0.0F, 16.0F, true), net.minecraft.client.model.geom.PartPose.offsetAndRotation(-8.0F, -1.0F, -8.0F, 0.0F, 0.0F, -1.134464F));
            v2.addOrReplaceChild("right_top_bristle", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(16, 33).addBox(-12.0F, 0.0F, 0.0F, 12.0F, 0.0F, 16.0F, true), net.minecraft.client.model.geom.PartPose.offsetAndRotation(-8.0F, -5.0F, -8.0F, 0.0F, 0.0F, -0.87266463F));
            v2.addOrReplaceChild("left_top_bristle", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(16, 33).addBox(0.0F, 0.0F, 0.0F, 12.0F, 0.0F, 16.0F), net.minecraft.client.model.geom.PartPose.offsetAndRotation(8.0F, -6.0F, -8.0F, 0.0F, 0.0F, 0.87266463F));
            v2.addOrReplaceChild("left_middle_bristle", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(16, 49).addBox(0.0F, 0.0F, 0.0F, 12.0F, 0.0F, 16.0F), net.minecraft.client.model.geom.PartPose.offsetAndRotation(8.0F, -2.0F, -8.0F, 0.0F, 0.0F, 1.134464F));
            v2.addOrReplaceChild("left_bottom_bristle", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(16, 65).addBox(0.0F, 0.0F, 0.0F, 12.0F, 0.0F, 16.0F), net.minecraft.client.model.geom.PartPose.offsetAndRotation(8.0F, 3.0F, -8.0F, 0.0F, 0.0F, 1.2217305F));
            return net.minecraft.client.model.geom.builders.LayerDefinition.create(v0, 64, 128);
        }

        /** 1.21.11 StriderModel.BABY_TRANSFORMER (field_55115) */
        public static final net.minecraft.client.model.geom.builders.MeshTransformer BABY_TRANSFORMER = net.minecraft.client.model.geom.builders.MeshTransformer.scaling(0.5F);
    }

    /** 1.21.11 TurtleModel */
    @Environment(EnvType.CLIENT)
    public static final class TurtleModel {

        private TurtleModel() {
        }

        /** 1.21.11 TurtleModel.createBodyLayer (method_32062) */
        public static net.minecraft.client.model.geom.builders.LayerDefinition createBodyLayer() {
            var v0 = new net.minecraft.client.model.geom.builders.MeshDefinition();
            var v1 = v0.getRoot();
            v1.addOrReplaceChild("head", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(3, 0).addBox(-3.0F, -1.0F, -3.0F, 6.0F, 5.0F, 6.0F), net.minecraft.client.model.geom.PartPose.offset(0.0F, 19.0F, -10.0F));
            v1.addOrReplaceChild("body", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(7, 37).addBox("shell", -9.5F, 3.0F, -10.0F, 19.0F, 20.0F, 6.0F).texOffs(31, 1).addBox("belly", -5.5F, 3.0F, -13.0F, 11.0F, 18.0F, 3.0F), net.minecraft.client.model.geom.PartPose.offsetAndRotation(0.0F, 11.0F, -10.0F, 1.5707964F, 0.0F, 0.0F));
            v1.addOrReplaceChild("egg_belly", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(70, 33).addBox(-4.5F, 3.0F, -14.0F, 9.0F, 18.0F, 1.0F), net.minecraft.client.model.geom.PartPose.offsetAndRotation(0.0F, 11.0F, -10.0F, 1.5707964F, 0.0F, 0.0F));
            var v2 = 1;
            v1.addOrReplaceChild("right_hind_leg", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(1, 23).addBox(-2.0F, 0.0F, 0.0F, 4.0F, 1.0F, 10.0F), net.minecraft.client.model.geom.PartPose.offset(-3.5F, 22.0F, 11.0F));
            v1.addOrReplaceChild("left_hind_leg", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(1, 12).addBox(-2.0F, 0.0F, 0.0F, 4.0F, 1.0F, 10.0F), net.minecraft.client.model.geom.PartPose.offset(3.5F, 22.0F, 11.0F));
            v1.addOrReplaceChild("right_front_leg", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(27, 30).addBox(-13.0F, 0.0F, -2.0F, 13.0F, 1.0F, 5.0F), net.minecraft.client.model.geom.PartPose.offset(-5.0F, 21.0F, -4.0F));
            v1.addOrReplaceChild("left_front_leg", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(27, 24).addBox(0.0F, 0.0F, -2.0F, 13.0F, 1.0F, 5.0F), net.minecraft.client.model.geom.PartPose.offset(5.0F, 21.0F, -4.0F));
            return net.minecraft.client.model.geom.builders.LayerDefinition.create(v0, 128, 64);
        }

        /** 1.21.11 TurtleModel.BABY_TRANSFORMER (field_52940) */
        public static final net.minecraft.client.model.geom.builders.MeshTransformer BABY_TRANSFORMER = new net.minecraft.client.model.BabyModelTransform(true, 120.0F, 0.0F, 9.0F, 6.0F, 120.0F, java.util.Set.of("head"));
    }

    /** 1.21.11 VillagerModel */
    @Environment(EnvType.CLIENT)
    public static final class VillagerModel {

        private VillagerModel() {
        }

        /** 1.21.11 VillagerModel.BABY_TRANSFORMER (field_55116) */
        public static final net.minecraft.client.model.geom.builders.MeshTransformer BABY_TRANSFORMER = net.minecraft.client.model.geom.builders.MeshTransformer.scaling(0.5F);
    }

    /** 1.21.11 WolfModel */
    @Environment(EnvType.CLIENT)
    public static final class WolfModel {

        private WolfModel() {
        }

        /** 1.21.11 WolfModel.createMeshDefinition (method_32068) */
        public static net.minecraft.client.model.geom.builders.MeshDefinition createMeshDefinition(net.minecraft.client.model.geom.builders.CubeDeformation p0) {
            var v1 = new net.minecraft.client.model.geom.builders.MeshDefinition();
            var v2 = v1.getRoot();
            var v3 = 13.5F;
            var v4 = v2.addOrReplaceChild("head", net.minecraft.client.model.geom.builders.CubeListBuilder.create(), net.minecraft.client.model.geom.PartPose.offset(-1.0F, 13.5F, -7.0F));
            v4.addOrReplaceChild("real_head", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -3.0F, -2.0F, 6.0F, 6.0F, 4.0F, p0).texOffs(16, 14).addBox(-2.0F, -5.0F, 0.0F, 2.0F, 2.0F, 1.0F, p0).texOffs(16, 14).addBox(2.0F, -5.0F, 0.0F, 2.0F, 2.0F, 1.0F, p0).texOffs(0, 10).addBox(-0.5F, -0.001F, -5.0F, 3.0F, 3.0F, 4.0F, p0), net.minecraft.client.model.geom.PartPose.ZERO);
            v2.addOrReplaceChild("body", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(18, 14).addBox(-3.0F, -2.0F, -3.0F, 6.0F, 9.0F, 6.0F, p0), net.minecraft.client.model.geom.PartPose.offsetAndRotation(0.0F, 14.0F, 2.0F, 1.5707964F, 0.0F, 0.0F));
            v2.addOrReplaceChild("upper_body", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(21, 0).addBox(-3.0F, -3.0F, -3.0F, 8.0F, 6.0F, 7.0F, p0), net.minecraft.client.model.geom.PartPose.offsetAndRotation(-1.0F, 14.0F, -3.0F, 1.5707964F, 0.0F, 0.0F));
            var v5 = net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(0, 18).addBox(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, p0);
            var v6 = net.minecraft.client.model.geom.builders.CubeListBuilder.create().mirror().texOffs(0, 18).addBox(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, p0);
            v2.addOrReplaceChild("right_hind_leg", v6, net.minecraft.client.model.geom.PartPose.offset(-2.5F, 16.0F, 7.0F));
            v2.addOrReplaceChild("left_hind_leg", v5, net.minecraft.client.model.geom.PartPose.offset(0.5F, 16.0F, 7.0F));
            v2.addOrReplaceChild("right_front_leg", v6, net.minecraft.client.model.geom.PartPose.offset(-2.5F, 16.0F, -4.0F));
            v2.addOrReplaceChild("left_front_leg", v5, net.minecraft.client.model.geom.PartPose.offset(0.5F, 16.0F, -4.0F));
            var v7 = v2.addOrReplaceChild("tail", net.minecraft.client.model.geom.builders.CubeListBuilder.create(), net.minecraft.client.model.geom.PartPose.offsetAndRotation(-1.0F, 12.0F, 8.0F, 0.62831855F, 0.0F, 0.0F));
            v7.addOrReplaceChild("real_tail", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(9, 18).addBox(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, p0), net.minecraft.client.model.geom.PartPose.ZERO);
            return v1;
        }

        /** 1.21.11 WolfModel.BABY_TRANSFORMER (field_52948) */
        public static final net.minecraft.client.model.geom.builders.MeshTransformer BABY_TRANSFORMER = new net.minecraft.client.model.BabyModelTransform(java.util.Set.of("head"));
    }

    /** 1.21.11 CamelSaddleModel */
    @Environment(EnvType.CLIENT)
    public static final class CamelSaddleModel {

        private CamelSaddleModel() {
        }

        /** 1.21.11 CamelSaddleModel.createSaddleLayer (method_66837) */
        public static net.minecraft.client.model.geom.builders.LayerDefinition createSaddleLayer() {
            var v0 = CamelModel.createBodyMesh();
            var v1 = v0.getRoot();
            var v2 = v1.getChild("body");
            var v3 = v2.getChild("head");
            var v4 = new net.minecraft.client.model.geom.builders.CubeDeformation(0.05F);
            v2.addOrReplaceChild("saddle", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(74, 64).addBox(-4.5F, -17.0F, -15.5F, 9.0F, 5.0F, 11.0F, v4).texOffs(92, 114).addBox(-3.5F, -20.0F, -15.5F, 7.0F, 3.0F, 11.0F, v4).texOffs(0, 89).addBox(-7.5F, -12.0F, -23.5F, 15.0F, 12.0F, 27.0F, v4), net.minecraft.client.model.geom.PartPose.offset(0.0F, 0.0F, 0.0F));
            v3.addOrReplaceChild("reins", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(98, 42).addBox(3.51F, -18.0F, -17.0F, 0.0F, 7.0F, 15.0F).texOffs(84, 57).addBox(-3.5F, -18.0F, -2.0F, 7.0F, 7.0F, 0.0F).texOffs(98, 42).addBox(-3.51F, -18.0F, -17.0F, 0.0F, 7.0F, 15.0F), net.minecraft.client.model.geom.PartPose.offset(0.0F, 0.0F, 0.0F));
            v3.addOrReplaceChild("bridle", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(60, 87).addBox(-3.5F, -7.0F, -15.0F, 7.0F, 8.0F, 19.0F, v4).texOffs(21, 64).addBox(-3.5F, -21.0F, -15.0F, 7.0F, 14.0F, 7.0F, v4).texOffs(50, 64).addBox(-2.5F, -21.0F, -21.0F, 5.0F, 5.0F, 6.0F, v4).texOffs(74, 70).addBox(2.5F, -19.0F, -18.0F, 1.0F, 2.0F, 2.0F).texOffs(74, 70).mirror().addBox(-3.5F, -19.0F, -18.0F, 1.0F, 2.0F, 2.0F), net.minecraft.client.model.geom.PartPose.offset(0.0F, 0.0F, 0.0F));
            return net.minecraft.client.model.geom.builders.LayerDefinition.create(v0, 128, 128);
        }
    }

    /** 1.21.11 ColdChickenModel */
    @Environment(EnvType.CLIENT)
    public static final class ColdChickenModel {

        private ColdChickenModel() {
        }

        /** 1.21.11 ColdChickenModel.createBodyLayer (method_67547) */
        public static net.minecraft.client.model.geom.builders.LayerDefinition createBodyLayer() {
            var v0 = ChickenModel.createBaseChickenModel();
            v0.getRoot().addOrReplaceChild("body", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(0, 9).addBox(-3.0F, -4.0F, -3.0F, 6.0F, 8.0F, 6.0F).texOffs(38, 9).addBox(0.0F, 3.0F, -1.0F, 0.0F, 3.0F, 5.0F), net.minecraft.client.model.geom.PartPose.offsetAndRotation(0.0F, 16.0F, 0.0F, 1.5707964F, 0.0F, 0.0F));
            v0.getRoot().addOrReplaceChild("head", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -6.0F, -2.0F, 4.0F, 6.0F, 3.0F).texOffs(44, 0).addBox(-3.0F, -7.0F, -2.015F, 6.0F, 3.0F, 4.0F), net.minecraft.client.model.geom.PartPose.offset(0.0F, 15.0F, -4.0F));
            return net.minecraft.client.model.geom.builders.LayerDefinition.create(v0, 64, 32);
        }
    }

    /** 1.21.11 ColdPigModel */
    @Environment(EnvType.CLIENT)
    public static final class ColdPigModel {

        private ColdPigModel() {
        }

        /** 1.21.11 ColdPigModel.createBodyLayer (method_66534) */
        public static net.minecraft.client.model.geom.builders.LayerDefinition createBodyLayer(net.minecraft.client.model.geom.builders.CubeDeformation p0) {
            var v1 = PigModel.createBasePigModel(p0);
            var v2 = v1.getRoot();
            v2.addOrReplaceChild("body", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(28, 8).addBox(-5.0F, -10.0F, -7.0F, 10.0F, 16.0F, 8.0F).texOffs(28, 32).addBox(-5.0F, -10.0F, -7.0F, 10.0F, 16.0F, 8.0F, new net.minecraft.client.model.geom.builders.CubeDeformation(0.5F)), net.minecraft.client.model.geom.PartPose.offsetAndRotation(0.0F, 11.0F, 2.0F, 1.5707964F, 0.0F, 0.0F));
            return net.minecraft.client.model.geom.builders.LayerDefinition.create(v1, 64, 64);
        }
    }

    /** 1.21.11 PigModel */
    @Environment(EnvType.CLIENT)
    public static final class PigModel {

        private PigModel() {
        }

        /** 1.21.11 PigModel.createBodyLayer (method_32025) */
        public static net.minecraft.client.model.geom.builders.LayerDefinition createBodyLayer(net.minecraft.client.model.geom.builders.CubeDeformation p0) {
            return net.minecraft.client.model.geom.builders.LayerDefinition.create(PigModel.createBasePigModel(p0), 64, 64);
        }

        /** 1.21.11 PigModel.BABY_TRANSFORMER (field_52925) */
        public static final net.minecraft.client.model.geom.builders.MeshTransformer BABY_TRANSFORMER = new net.minecraft.client.model.BabyModelTransform(false, 4.0F, 4.0F, java.util.Set.of("head"));

        /** 1.21.11 PigModel.createBasePigModel (method_66536) */
        public static net.minecraft.client.model.geom.builders.MeshDefinition createBasePigModel(net.minecraft.client.model.geom.builders.CubeDeformation p0) {
            var v1 = QuadrupedModel.createBodyMesh(6, true, false, p0);
            var v2 = v1.getRoot();
            v2.addOrReplaceChild("head", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -8.0F, 8.0F, 8.0F, 8.0F, p0).texOffs(16, 16).addBox(-2.0F, 0.0F, -9.0F, 4.0F, 3.0F, 1.0F, p0), net.minecraft.client.model.geom.PartPose.offset(0.0F, 12.0F, -6.0F));
            return v1;
        }
    }

    /** 1.21.11 QuadrupedModel */
    @Environment(EnvType.CLIENT)
    public static final class QuadrupedModel {

        private QuadrupedModel() {
        }

        /** 1.21.11 QuadrupedModel.createBodyMesh (method_32033) */
        public static net.minecraft.client.model.geom.builders.MeshDefinition createBodyMesh(int p0, boolean p1, boolean p2, net.minecraft.client.model.geom.builders.CubeDeformation p3) {
            var v4 = new net.minecraft.client.model.geom.builders.MeshDefinition();
            var v5 = v4.getRoot();
            v5.addOrReplaceChild("head", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -8.0F, 8.0F, 8.0F, 8.0F, p3), net.minecraft.client.model.geom.PartPose.offset(0.0F, ((float) (18 - p0)), -6.0F));
            v5.addOrReplaceChild("body", net.minecraft.client.model.geom.builders.CubeListBuilder.create().texOffs(28, 8).addBox(-5.0F, -10.0F, -7.0F, 10.0F, 16.0F, 8.0F, p3), net.minecraft.client.model.geom.PartPose.offsetAndRotation(0.0F, ((float) (17 - p0)), 2.0F, 1.5707964F, 0.0F, 0.0F));
            QuadrupedModel.createLegs(v5, p1, p2, p0, p3);
            return v4;
        }

        /** 1.21.11 QuadrupedModel.createLegs (method_70934) */
        public static void createLegs(net.minecraft.client.model.geom.builders.PartDefinition p0, boolean p1, boolean p2, int p3, net.minecraft.client.model.geom.builders.CubeDeformation p4) {
            var v5 = net.minecraft.client.model.geom.builders.CubeListBuilder.create().mirror(p2).texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, ((float) p3), 4.0F, p4);
            var v6 = net.minecraft.client.model.geom.builders.CubeListBuilder.create().mirror(p1).texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, ((float) p3), 4.0F, p4);
            p0.addOrReplaceChild("right_hind_leg", v5, net.minecraft.client.model.geom.PartPose.offset(-3.0F, ((float) (24 - p3)), 7.0F));
            p0.addOrReplaceChild("left_hind_leg", v6, net.minecraft.client.model.geom.PartPose.offset(3.0F, ((float) (24 - p3)), 7.0F));
            p0.addOrReplaceChild("right_front_leg", v5, net.minecraft.client.model.geom.PartPose.offset(-3.0F, ((float) (24 - p3)), -5.0F));
            p0.addOrReplaceChild("left_front_leg", v6, net.minecraft.client.model.geom.PartPose.offset(3.0F, ((float) (24 - p3)), -5.0F));
            return;
        }
    }
}

package top.dext.centurybridge.step.v1_21_2.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.1's QuadrupedModel(root, scaleHead, babyYHeadOffset, babyZHeadOffset, babyHeadScale, babyBodyScale, bodyYOffset)
 * for a 1.21.1 subclass (a mod's four-legged model).
 *
 * <p>1.21.1: QuadrupedModel was an AgeableListModel; it drew its head parts (the head) and body parts (body and four
 * legs), a young entity's scaled and offset by those six values. 1.21.2: QuadrupedModel(root) draws its root, and a baby
 * is another model baked from a scaled layer, which a 1.21.1 model has none of. The constructor keeps the root (1.21.2
 * finds the same named parts in it) and the six values go to the model ({@link #legacyBaby}, CbRenderQuadrupedMixin);
 * such a model draws as 1.21.1's AgeableListModel.renderToBuffer drew (CbRenderModelMixin): its head parts and body
 * parts (1.21.1's headParts() / bodyParts(), put back with 1.21.1's bodies), scaled while young (the young flag its
 * 1.21.1 renderer set, CbRenderModelFields).
 */
@Environment(EnvType.CLIENT)
public final class CbRenderQuadruped {

    /** what CbRenderQuadrupedMixin keeps of a 1.21.1 QuadrupedModel constructor */
    public interface Legacy {
        void cb$baby(boolean scaleHead, float babyYHeadOffset, float babyZHeadOffset, float babyHeadScale, float babyBodyScale, float bodyYOffset);

        /** 1.21.1's draw when the model was built by 1.21.1's constructor; false (1.21.2's root draw) otherwise */
        boolean cb$drawLegacy(net.minecraft.class_4587 pose, net.minecraft.class_4588 consumer, int light, int overlay, int color);
    }

    private CbRenderQuadruped() {
    }

    /** after super(root) of a 1.21.1 QuadrupedModel subclass (superCtorReshapes post) */
    public static void legacyBaby(net.minecraft.class_597<?> model, boolean scaleHead, float babyYHeadOffset, float babyZHeadOffset,
                                  float babyHeadScale, float babyBodyScale, int bodyYOffset) {
        ((Legacy) model).cb$baby(scaleHead, babyYHeadOffset, babyZHeadOffset, babyHeadScale, babyBodyScale, (float) bodyYOffset);
    }

    /** 1.21.1 AgeableListModel.renderToBuffer */
    public static void draw(boolean young, boolean scaleHead, float babyYHeadOffset, float babyZHeadOffset, float babyHeadScale,
                            float babyBodyScale, float bodyYOffset, Iterable<net.minecraft.class_630> headParts,
                            Iterable<net.minecraft.class_630> bodyParts, net.minecraft.class_4587 pose, net.minecraft.class_4588 consumer,
                            int light, int overlay, int color) {
        if (young) {
            pose.method_22903();
            if (scaleHead) {
                float s = 1.5F / babyHeadScale;
                pose.method_22905(s, s, s);
            }
            pose.method_46416(0.0F, babyYHeadOffset / 16.0F, babyZHeadOffset / 16.0F);
            headParts.forEach(part -> part.method_22699(pose, consumer, light, overlay, color));
            pose.method_22909();
            pose.method_22903();
            float s = 1.0F / babyBodyScale;
            pose.method_22905(s, s, s);
            pose.method_46416(0.0F, bodyYOffset / 16.0F, 0.0F);
            bodyParts.forEach(part -> part.method_22699(pose, consumer, light, overlay, color));
            pose.method_22909();
        } else {
            headParts.forEach(part -> part.method_22699(pose, consumer, light, overlay, color));
            bodyParts.forEach(part -> part.method_22699(pose, consumer, light, overlay, color));
        }
    }
}

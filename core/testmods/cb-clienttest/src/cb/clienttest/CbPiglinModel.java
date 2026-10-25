package cb.clienttest;

import net.minecraft.class_4836;
import net.minecraft.class_4840;
import net.minecraft.class_630;

/**
 * A 1.20.1 mod's piglin model subclass (swall plan item 17): its setupAnim override runs vanilla's, then
 * sets the left ear to 30 degrees.
 */
public class CbPiglinModel extends class_4840<class_4836> {
    static final float EAR = (float) Math.toRadians(30);
    int setups;

    public CbPiglinModel(class_630 root) {
        super(root);
    }

    @Override
    public void method_24803(class_4836 piglin, float limbSwing, float limbSwingAmount, float age, float headYaw,
            float headPitch) {
        super.method_24803(piglin, limbSwing, limbSwingAmount, age, headYaw, headPitch);
        setups++;
        leftEar().field_3674 = EAR;
    }

    /** the left ear as a 1.20.1 mod reaches it (PiglinModel.leftEar is private): the head's "left_ear" child */
    class_630 leftEar() {
        return this.field_3398.method_32086("left_ear");
    }
}

package cb.clienttest;

import net.minecraft.class_1921;
import net.minecraft.class_3883;
import net.minecraft.class_3887;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_4608;
import net.minecraft.class_591;
import net.minecraft.class_742;

/**
 * A 1.20.1 mod's feature renderer on the player (swall plan item 1): its render(..) override draws the
 * parent model again, slightly larger, tinted red and translucent, through the 1.20.1
 * Model.renderToBuffer(pose, consumer, light, overlay, r, g, b, a). It must run once per player drawn.
 */
public class CbLayer extends class_3887<class_742, class_591<class_742>> {
    static int calls;
    static Throwable first;

    public CbLayer(class_3883<class_742, class_591<class_742>> parent) {
        super(parent);
    }

    @Override
    public void method_4199(class_4587 pose, class_4597 buffers, int light, class_742 player, float limbSwing,
            float limbSwingAmount, float partialTick, float age, float headYaw, float headPitch) {
        calls++;
        pose.method_22903();
        try {
            pose.method_22905(1.08f, 1.04f, 1.08f);
            method_17165().method_2828(pose, buffers.getBuffer(class_1921.method_23580(player.method_3117())), light,
                class_4608.field_21444, 1.0f, 0.15f, 0.15f, 0.5f);
        } catch (Throwable t) {
            if (first == null) {
                first = t;
                t.printStackTrace();
            }
        } finally {
            pose.method_22909();
        }
    }
}

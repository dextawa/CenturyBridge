package cb.s9c;

import net.minecraft.class_10034;
import net.minecraft.class_1642;
import net.minecraft.class_1921;
import net.minecraft.class_2960;
import net.minecraft.class_3887;
import net.minecraft.class_3883;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_4608;
import net.minecraft.class_5602;
import net.minecraft.class_5617;
import net.minecraft.class_572;
import net.minecraft.class_922;

/** A 1.21.8 LivingEntityRenderer for zombies: super.render, then a mod-textured quad one block above the entity. */
public class CbZombieRenderer extends class_922<class_1642, class_10034, class_572<class_10034>> {
    static final class_2960 TEX = class_2960.method_60656("textures/entity/zombie/zombie.png");
    static final class_2960 QUAD_TEX = class_2960.method_60656("textures/block/diamond_block.png");

    public CbZombieRenderer(class_5617.class_5618 ctx) {
        super(ctx, new class_572<>(ctx.method_32167(class_5602.field_27638)), 0.5F);
        this.method_4046(new Layer(this));
    }

    @Override
    public class_10034 method_55269() {
        return new class_10034();
    }

    @Override
    public class_2960 method_3885(class_10034 state) {
        return TEX;
    }

    @Override
    public void method_4054(class_10034 state, class_4587 pose, class_4597 buffers, int light) {
        super.method_4054(state, pose, buffers, light);
        S9cClient.RENDERS.incrementAndGet();
        S9cClient.lastEntityLight = light;
        class_4588 vc = buffers.getBuffer(class_1921.method_23576(QUAD_TEX));
        class_4587.class_4665 p = pose.method_23760();
        float y = 2.9F;
        vc.method_56824(p, -0.5F, y, -0.5F).method_1336(255, 255, 255, 255).method_22913(0, 0).method_22922(class_4608.field_21444).method_60803(light).method_60831(p, 0, 1, 0);
        vc.method_56824(p, -0.5F, y, 0.5F).method_1336(255, 255, 255, 255).method_22913(0, 1).method_22922(class_4608.field_21444).method_60803(light).method_60831(p, 0, 1, 0);
        vc.method_56824(p, 0.5F, y, 0.5F).method_1336(255, 255, 255, 255).method_22913(1, 1).method_22922(class_4608.field_21444).method_60803(light).method_60831(p, 0, 1, 0);
        vc.method_56824(p, 0.5F, y, -0.5F).method_1336(255, 255, 255, 255).method_22913(1, 0).method_22922(class_4608.field_21444).method_60803(light).method_60831(p, 0, 1, 0);
    }

    /** a 1.21.8 RenderLayer drawing the model again with a translucent red tint */
    public static class Layer extends class_3887<class_10034, class_572<class_10034>> {
        public Layer(class_3883<class_10034, class_572<class_10034>> parent) {
            super(parent);
        }

        @Override
        public void method_4199(class_4587 pose, class_4597 buffers, int light, class_10034 state, float yRot, float xRot) {
            S9cClient.LAYERS.incrementAndGet();
            class_4588 vc = buffers.getBuffer(class_1921.method_23580(TEX));
            this.method_17165().method_62100(pose, vc, light, class_4608.field_21444, 0x80FF4040);
        }
    }
}

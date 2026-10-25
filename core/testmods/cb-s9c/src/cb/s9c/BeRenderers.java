package cb.s9c;

import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1921;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2586;
import net.minecraft.class_310;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_4608;
import net.minecraft.class_5614;
import net.minecraft.class_811;
import net.minecraft.class_826;
import net.minecraft.class_827;
import net.minecraft.class_9974;
import org.joml.Quaternionf;

/** s9's legacy block entity renderers (1.21.8 shape): (i) a direct BlockEntityRenderer, (ii) a ChestRenderer subclass. */
public final class BeRenderers {
    private BeRenderers() {
    }

    /** (i) for barrels: a cube turning with game time + partialTick, a line box around it, a held item above */
    public static class Cube implements class_827<class_2586> {
        public Cube(class_5614.class_5615 ctx) {
        }

        @Override
        public void method_3569(class_2586 be, float partialTick, class_4587 pose, class_4597 buffers, int light, int overlay, class_243 cam) {
            S9cClient.BE_CUBE.incrementAndGet();
            S9cClient.notePartial(partialTick);
            S9cClient.lastBeLight = light;
            long t = be.method_10997() == null ? 0 : be.method_10997().method_8510();
            pose.method_22903();
            pose.method_46416(0.5F, 1.5F, 0.5F);
            pose.method_22907(new Quaternionf().rotationY((float) Math.toRadians((t + partialTick) * 4.0)));
            pose.method_46416(-0.25F, -0.25F, -0.25F);
            class_4588 vc = buffers.getBuffer(class_1921.method_23576(CbZombieRenderer.QUAD_TEX));
            S9cClient.cubeFaces(pose.method_23760(), vc, 0.5F, light);
            class_9974.method_62295(pose, buffers.getBuffer(class_1921.method_23594()), new class_238(0, 0, 0, 0.5, 0.5, 0.5), 1F, 0F, 0F, 1F);
            pose.method_22909();
            pose.method_22903();
            pose.method_46416(0.5F, 2.3F, 0.5F);
            class_310.method_1551().method_1480().method_23178(new class_1799(class_1802.field_8802), class_811.field_4319, light,
                class_4608.field_21444, pose, buffers, be.method_10997(), 0);
            pose.method_22909();
        }
    }

    /** (ii) a ChestRenderer subclass: vanilla's chest, then a quad above it */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static class Chest extends class_826 {
        public Chest(class_5614.class_5615 ctx) {
            super(ctx);
        }

        @Override
        public void method_3569(class_2586 be, float partialTick, class_4587 pose, class_4597 buffers, int light, int overlay, class_243 cam) {
            super.method_3569(be, partialTick, pose, buffers, light, overlay, cam);
            S9cClient.BE_CHEST.incrementAndGet();
            pose.method_22903();
            pose.method_46416(0.25F, 1.2F, 0.25F);
            S9cClient.cubeFaces(pose.method_23760(), buffers.getBuffer(class_1921.method_23576(CbZombieRenderer.QUAD_TEX)), 0.5F, light);
            pose.method_22909();
        }
    }

    /** (iii) for furnaces: a lambda */
    public static class_827<class_2586> lambda(class_5614.class_5615 ctx) {
        return (be, pt, pose, buffers, light, overlay, cam) -> {
            S9cClient.BE_LAMBDA.incrementAndGet();
            pose.method_22903();
            pose.method_46416(0.25F, 1.2F, 0.25F);
            S9cClient.cubeFaces(pose.method_23760(), buffers.getBuffer(class_1921.method_23576(CbZombieRenderer.QUAD_TEX)), 0.5F, light);
            pose.method_22909();
        };
    }
}

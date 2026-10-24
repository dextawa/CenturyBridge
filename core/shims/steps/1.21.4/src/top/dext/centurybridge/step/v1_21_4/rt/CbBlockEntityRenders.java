package top.dext.centurybridge.step.v1_21_4.rt;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.3 block entity renderer statics on 1.21.4, body for body where 1.21.4 dropped or reshaped them.
 * <ul>
 * <li>BannerRenderer.createBodyLayer: 1.21.3's one layer of flag, pole and bar (1.21.4 splits banner and flag models);</li>
 * <li>HangingSignRenderer.createHangingSignLayer: 1.21.3's one layer with every part (board, plank, both chain sets), whose
 *     visibility HangingSignModel.evaluateVisibleParts set per state (1.21.4 has a layer per attachment);</li>
 * <li>SkullBlockRenderer.createSkullRenderers: 1.21.3's map of the vanilla skull types' models;</li>
 * <li>VaultRenderer.renderItemInside / ItemEntityRenderer's item cluster statics: 1.21.3's drawing of a stack's copies with its
 *     model (ItemRenderer.render(.., model) as 1.21.3's: {@link CbItemRender}).</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
public final class CbBlockEntityRenders {
    private CbBlockEntityRenders() {
    }

    /** BannerRenderer.FLAG */
    public static String field_32816() {
        return "flag";
    }

    /** was BannerRenderer.createBodyLayer() (method_32135) */
    public static net.minecraft.class_5607 method_32135() {
        net.minecraft.class_5609 mesh = new net.minecraft.class_5609();
        net.minecraft.class_5610 root = mesh.method_32111();
        root.method_32117("flag", net.minecraft.class_5606.method_32108().method_32101(0, 0).method_32097(-10.0f, 0.0f, -2.0f, 20.0f, 40.0f, 1.0f),
            net.minecraft.class_5603.method_32090(0.0f, -32.0f, 0.0f));
        root.method_32117("pole", net.minecraft.class_5606.method_32108().method_32101(44, 0).method_32097(-1.0f, -30.0f, -1.0f, 2.0f, 42.0f, 2.0f),
            net.minecraft.class_5603.field_27701);
        root.method_32117("bar", net.minecraft.class_5606.method_32108().method_32101(0, 42).method_32097(-10.0f, -32.0f, -1.0f, 20.0f, 2.0f, 2.0f),
            net.minecraft.class_5603.field_27701);
        return net.minecraft.class_5607.method_32110(mesh, 64, 64);
    }

    /** was HangingSignRenderer.createHangingSignLayer() (method_45795) */
    public static net.minecraft.class_5607 method_45795() {
        net.minecraft.class_5609 mesh = new net.minecraft.class_5609();
        net.minecraft.class_5610 root = mesh.method_32111();
        root.method_32117("board", net.minecraft.class_5606.method_32108().method_32101(0, 12).method_32097(-7.0f, 0.0f, -1.0f, 14.0f, 10.0f, 2.0f),
            net.minecraft.class_5603.field_27701);
        root.method_32117("plank", net.minecraft.class_5606.method_32108().method_32101(0, 0).method_32097(-8.0f, -6.0f, -2.0f, 16.0f, 2.0f, 4.0f),
            net.minecraft.class_5603.field_27701);
        net.minecraft.class_5610 normal = root.method_32117("normalChains", net.minecraft.class_5606.method_32108(), net.minecraft.class_5603.field_27701);
        normal.method_32117("chainL1", net.minecraft.class_5606.method_32108().method_32101(0, 6).method_32097(-1.5f, 0.0f, 0.0f, 3.0f, 6.0f, 0.0f),
            net.minecraft.class_5603.method_32091(-5.0f, -6.0f, 0.0f, 0.0f, -0.7853982f, 0.0f));
        normal.method_32117("chainL2", net.minecraft.class_5606.method_32108().method_32101(6, 6).method_32097(-1.5f, 0.0f, 0.0f, 3.0f, 6.0f, 0.0f),
            net.minecraft.class_5603.method_32091(-5.0f, -6.0f, 0.0f, 0.0f, 0.7853982f, 0.0f));
        normal.method_32117("chainR1", net.minecraft.class_5606.method_32108().method_32101(0, 6).method_32097(-1.5f, 0.0f, 0.0f, 3.0f, 6.0f, 0.0f),
            net.minecraft.class_5603.method_32091(5.0f, -6.0f, 0.0f, 0.0f, -0.7853982f, 0.0f));
        normal.method_32117("chainR2", net.minecraft.class_5606.method_32108().method_32101(6, 6).method_32097(-1.5f, 0.0f, 0.0f, 3.0f, 6.0f, 0.0f),
            net.minecraft.class_5603.method_32091(5.0f, -6.0f, 0.0f, 0.0f, 0.7853982f, 0.0f));
        root.method_32117("vChains", net.minecraft.class_5606.method_32108().method_32101(14, 6).method_32097(-6.0f, -6.0f, 0.0f, 12.0f, 6.0f, 0.0f),
            net.minecraft.class_5603.field_27701);
        return net.minecraft.class_5607.method_32110(mesh, 64, 32);
    }

    /** was SkullBlockRenderer.createSkullRenderers(entityModels) (method_32160): the vanilla skull types' models */
    public static Map<net.minecraft.class_2484.class_2485, net.minecraft.class_5598> method_32160(net.minecraft.class_5599 models) {
        ImmutableMap.Builder<net.minecraft.class_2484.class_2485, net.minecraft.class_5598> builder = ImmutableMap.builder();
        builder.put(net.minecraft.class_2484.class_2486.field_11512, new net.minecraft.class_607(models.method_32072(net.minecraft.class_5602.field_27653)));
        builder.put(net.minecraft.class_2484.class_2486.field_11513, new net.minecraft.class_607(models.method_32072(net.minecraft.class_5602.field_27634)));
        builder.put(net.minecraft.class_2484.class_2486.field_11510, new net.minecraft.class_607(models.method_32072(net.minecraft.class_5602.field_27578)));
        builder.put(net.minecraft.class_2484.class_2486.field_11508, new net.minecraft.class_607(models.method_32072(net.minecraft.class_5602.field_27640)));
        builder.put(net.minecraft.class_2484.class_2486.field_11507, new net.minecraft.class_607(models.method_32072(net.minecraft.class_5602.field_27548)));
        builder.put(net.minecraft.class_2484.class_2486.field_11511, new net.minecraft.class_626(models.method_32072(net.minecraft.class_5602.field_27553)));
        builder.put(net.minecraft.class_2484.class_2486.field_41313, new net.minecraft.class_7946(models.method_32072(net.minecraft.class_5602.field_41382)));
        return builder.build();
    }

    // ---- ItemEntityRenderer / VaultRenderer ----

    /** was ItemEntityRenderer.getSeedForItemStack(stack) (method_56856) */
    public static int method_56856(net.minecraft.class_1799 stack) {
        return stack.method_7960() ? 187 : net.minecraft.class_1792.method_7880(stack.method_7909()) + stack.method_7919();
    }

    /** 1.21.3 ItemEntityRenderer.getRenderedAmount(count) */
    static int renderedAmount(int count) {
        if (count <= 1) {
            return 1;
        } else if (count <= 16) {
            return 2;
        } else if (count <= 32) {
            return 3;
        } else {
            return count <= 48 ? 4 : 5;
        }
    }

    /** was ItemEntityRenderer.renderMultipleFromCount(itemRenderer, pose, buffers, light, stack, random, level) (method_56857) */
    public static void method_56857(net.minecraft.class_918 itemRenderer, net.minecraft.class_4587 pose, net.minecraft.class_4597 buffers, int light,
        net.minecraft.class_1799 stack, net.minecraft.class_5819 random, net.minecraft.class_1937 level) {
        net.minecraft.class_1087 model = CbItemRender.getModel(itemRenderer, stack, level, null, 0);
        method_56858(itemRenderer, pose, buffers, light, stack, model, model.method_4712(), random);
    }

    /** was ItemEntityRenderer.renderMultipleFromCount(itemRenderer, pose, buffers, light, stack, model, gui3d, random) (method_56858) */
    public static void method_56858(net.minecraft.class_918 itemRenderer, net.minecraft.class_4587 pose, net.minecraft.class_4597 buffers, int light,
        net.minecraft.class_1799 stack, net.minecraft.class_1087 model, boolean gui3d, net.minecraft.class_5819 random) {
        int amount = renderedAmount(stack.method_7947());
        org.joml.Vector3f scale = model.method_4709().comp_3374().field_4285;
        float sx = scale.x();
        float sy = scale.y();
        float sz = scale.z();
        if (!gui3d) {
            float x = -0.0f * (amount - 1) * 0.5f * sx;
            float y = -0.0f * (amount - 1) * 0.5f * sy;
            float z = -0.09375f * (amount - 1) * 0.5f * sz;
            pose.method_46416(x, y, z);
        }
        for (int i = 0; i < amount; i++) {
            pose.method_22903();
            if (i > 0) {
                if (gui3d) {
                    float x = (random.method_43057() * 2.0f - 1.0f) * 0.15f;
                    float y = (random.method_43057() * 2.0f - 1.0f) * 0.15f;
                    float z = (random.method_43057() * 2.0f - 1.0f) * 0.15f;
                    pose.method_46416(x, y, z);
                } else {
                    float x = (random.method_43057() * 2.0f - 1.0f) * 0.15f * 0.5f;
                    float y = (random.method_43057() * 2.0f - 1.0f) * 0.15f * 0.5f;
                    pose.method_46416(x, y, 0.0f);
                }
            }
            CbItemRender.render(itemRenderer, stack, net.minecraft.class_811.field_4318, false, pose, buffers, light, net.minecraft.class_4608.field_21444, model);
            pose.method_22909();
            if (!gui3d) {
                pose.method_46416(0.0f * sx, 0.0f * sy, 0.09375f * sz);
            }
        }
    }

    /** was VaultRenderer.renderItemInside(partialTick, level, pose, buffers, light, stack, itemRenderer, spin, previousSpin, random) (method_56851) */
    public static void method_56851(float partialTick, net.minecraft.class_1937 level, net.minecraft.class_4587 pose, net.minecraft.class_4597 buffers,
        int light, net.minecraft.class_1799 stack, net.minecraft.class_918 itemRenderer, float previousSpin, float spin, net.minecraft.class_5819 random) {
        pose.method_22903();
        pose.method_46416(0.5f, 0.4f, 0.5f);
        pose.method_22907(net.minecraft.class_7833.field_40716.rotationDegrees(net.minecraft.class_3532.method_17821(partialTick, previousSpin, spin)));
        method_56857(itemRenderer, pose, buffers, light, stack, random, level);
        pose.method_22909();
    }
}

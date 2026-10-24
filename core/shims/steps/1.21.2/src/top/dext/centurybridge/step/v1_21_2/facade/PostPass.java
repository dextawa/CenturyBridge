package top.dext.centurybridge.step.v1_21_2.facade;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.util.List;
import java.util.function.IntSupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.joml.Matrix4f;

/**
 * 1.21.1's PostPass (net/minecraft/class_283, classRenames): one pass of a 1.21.1 post chain -- an EffectInstance drawn
 * over a full-target quad from an input target (and auxiliary textures) into an output target. 1.21.2 keeps a class
 * of that name with another model (resource handles of a frame graph, a CompiledShaderProgram); a 1.21.1 mod's
 * PostPass is this one, 1.21.1's bodies on the GL calls 1.21.2 keeps (the output target's clear(ON_OSX) is 1.21.1's
 * flagged clear, CbRenderTargetMixin).
 */
@Environment(EnvType.CLIENT)
public class PostPass implements AutoCloseable {

    public final EffectInstance field_1540;
    public final net.minecraft.class_276 field_1536;
    public final net.minecraft.class_276 field_1538;
    public final List<IntSupplier> field_1534 = Lists.newArrayList();
    public final List<String> field_1539 = Lists.newArrayList();
    public final List<Integer> field_1533 = Lists.newArrayList();
    public final List<Integer> field_1537 = Lists.newArrayList();
    public Matrix4f field_1535;
    public final int field_49906;

    public PostPass(net.minecraft.class_5912 resourceProvider, String name, net.minecraft.class_276 inTarget, net.minecraft.class_276 outTarget,
                    boolean useLinearFilter) throws IOException {
        this.field_1540 = new EffectInstance(resourceProvider, name);
        this.field_1536 = inTarget;
        this.field_1538 = outTarget;
        this.field_49906 = useLinearFilter ? 9729 : 9728;
    }

    @Override
    public void close() {
        this.field_1540.close();
    }

    /** getName() */
    public final String method_35777() {
        return this.field_1540.method_35763();
    }

    /** addAuxAsset(name, texture, width, height) */
    public void method_1292(String name, IntSupplier texture, int width, int height) {
        this.field_1539.add(this.field_1539.size(), name);
        this.field_1534.add(this.field_1534.size(), texture);
        this.field_1533.add(this.field_1533.size(), width);
        this.field_1537.add(this.field_1537.size(), height);
    }

    /** setOrthoMatrix(matrix) */
    public void method_1291(Matrix4f matrix) {
        this.field_1535 = matrix;
    }

    /** process(time) */
    public void method_1293(float time) {
        this.field_1536.method_1240();
        float width = this.field_1538.field_1482;
        float height = this.field_1538.field_1481;
        RenderSystem.viewport(0, 0, (int) width, (int) height);
        net.minecraft.class_276 in = this.field_1536;
        this.field_1540.method_1269("DiffuseSampler", in::method_30277);
        for (int i = 0; i < this.field_1534.size(); i++) {
            this.field_1540.method_1269(this.field_1539.get(i), this.field_1534.get(i));
            this.field_1540.method_1275("AuxSize" + i).method_1255((float) this.field_1533.get(i), (float) this.field_1537.get(i));
        }
        this.field_1540.method_1275("ProjMat").method_1250(this.field_1535);
        this.field_1540.method_1275("InSize").method_1255((float) this.field_1536.field_1482, (float) this.field_1536.field_1481);
        this.field_1540.method_1275("OutSize").method_1255(width, height);
        this.field_1540.method_1275("Time").method_1251(time);
        net.minecraft.class_310 minecraft = net.minecraft.class_310.method_1551();
        this.field_1540.method_1275("ScreenSize").method_1255((float) minecraft.method_22683().method_4489(), (float) minecraft.method_22683().method_4506());
        this.field_1540.method_1277();
        top.dext.centurybridge.step.v1_21_2.rt.CbRenderPost.clear(this.field_1538, net.minecraft.class_310.field_1703);
        this.field_1538.method_1235(false);
        RenderSystem.depthFunc(519);
        net.minecraft.class_287 builder = net.minecraft.class_289.method_1348().method_60827(net.minecraft.class_293.class_5596.field_27382, net.minecraft.class_290.field_1592);
        builder.method_22912(0.0F, 0.0F, 500.0F);
        builder.method_22912(width, 0.0F, 500.0F);
        builder.method_22912(width, height, 500.0F);
        builder.method_22912(0.0F, height, 500.0F);
        net.minecraft.class_286.method_43437(builder.method_60800());
        RenderSystem.depthFunc(515);
        this.field_1540.method_1273();
        this.field_1538.method_1240();
        this.field_1536.method_1242();
        for (Object asset : this.field_1534) {
            if (asset instanceof net.minecraft.class_276 target) {
                target.method_1242();
            }
        }
    }

    /** getEffect() */
    public EffectInstance method_1295() {
        return this.field_1540;
    }

    /** getFilterMode() */
    public int method_58234() {
        return this.field_49906;
    }
}

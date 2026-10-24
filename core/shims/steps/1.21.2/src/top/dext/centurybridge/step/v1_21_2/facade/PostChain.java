package top.dext.centurybridge.step.v1_21_2.facade;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.joml.Matrix4f;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderPost;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderTargets;

/**
 * 1.21.1's PostChain (net/minecraft/class_279, classRenames): a post-processing chain read from a 1.21.1 post json
 * (targets, passes with intarget / outtarget / auxtargets / uniforms) and run over the screen target. 1.21.2 keeps a
 * class of that name built from its own post_effect format by the ShaderManager and run inside a frame graph; a 1.21.1
 * mod's PostChain is this one, 1.21.1's bodies on the GL-level API 1.21.2 keeps (RenderTarget, TextureTarget,
 * TextureManager, RenderSystem), with 1.21.1's flagged RenderTarget calls as 1.21.1 made them (CbRenderPost,
 * CbRenderTargets).
 */
@Environment(EnvType.CLIENT)
public class PostChain implements AutoCloseable {

    public static final String field_32770 = "minecraft:main";
    public final net.minecraft.class_276 field_1499;
    public final net.minecraft.class_5912 field_49569;
    public final String field_1494;
    public final List<PostPass> field_1497 = Lists.newArrayList();
    public final Map<String, net.minecraft.class_276> field_1495 = Maps.newHashMap();
    public final List<net.minecraft.class_276> field_1496 = Lists.newArrayList();
    public Matrix4f field_1498;
    public int field_1493;
    public int field_1492;
    public float field_1501;
    public float field_1500;

    public PostChain(net.minecraft.class_1060 textureManager, net.minecraft.class_5912 resourceProvider, net.minecraft.class_276 screenTarget,
                     net.minecraft.class_2960 location) throws IOException, JsonSyntaxException {
        this.field_49569 = resourceProvider;
        this.field_1499 = screenTarget;
        this.field_1501 = 0.0F;
        this.field_1500 = 0.0F;
        this.field_1493 = screenTarget.field_1480;
        this.field_1492 = screenTarget.field_1477;
        this.field_1494 = location.toString();
        this.method_1267();
        this.method_1256(textureManager, location);
    }

    /**
     * A chain with no 1.21.1 json of its own (rt/CbRenderPostNative: 1.21.2's chain GameRenderer runs, as 1.21.1's
     * currentEffect() answered it): 1.21.1's state over the screen target, no passes or targets.
     */
    protected PostChain(net.minecraft.class_5912 resourceProvider, net.minecraft.class_276 screenTarget, String name) {
        this.field_49569 = resourceProvider;
        this.field_1499 = screenTarget;
        this.field_1501 = 0.0F;
        this.field_1500 = 0.0F;
        this.field_1493 = screenTarget.field_1480;
        this.field_1492 = screenTarget.field_1477;
        this.field_1494 = name;
        this.method_1267();
    }

    /** load(textureManager, location) */
    public void method_1256(net.minecraft.class_1060 textureManager, net.minecraft.class_2960 location) throws IOException, JsonSyntaxException {
        net.minecraft.class_3298 resource = this.field_49569.getResourceOrThrow(location);
        try (Reader reader = resource.method_43039()) {
            JsonObject json = net.minecraft.class_3518.method_15255(reader);
            if (net.minecraft.class_3518.method_15264(json, "targets")) {
                JsonArray targets = json.getAsJsonArray("targets");
                int i = 0;
                for (JsonElement e : targets) {
                    try {
                        this.method_1265(e);
                    } catch (Exception ex) {
                        net.minecraft.class_2973 c = net.minecraft.class_2973.method_12856(ex);
                        c.method_12854("targets[" + i + "]");
                        throw c;
                    }
                    i++;
                }
            }
            if (net.minecraft.class_3518.method_15264(json, "passes")) {
                JsonArray passes = json.getAsJsonArray("passes");
                int i = 0;
                for (JsonElement e : passes) {
                    try {
                        this.method_1257(textureManager, e);
                    } catch (Exception ex) {
                        net.minecraft.class_2973 c = net.minecraft.class_2973.method_12856(ex);
                        c.method_12854("passes[" + i + "]");
                        throw c;
                    }
                    i++;
                }
            }
        } catch (Exception ex) {
            net.minecraft.class_2973 c = net.minecraft.class_2973.method_12856(ex);
            c.method_12855(location.method_12832() + " (" + resource.method_14480() + ")");
            throw c;
        }
    }

    /** parseTargetNode(json) */
    public void method_1265(JsonElement json) throws net.minecraft.class_2973 {
        if (net.minecraft.class_3518.method_15286(json)) {
            this.method_1261(json.getAsString(), this.field_1493, this.field_1492);
        } else {
            JsonObject o = net.minecraft.class_3518.method_15295(json, "target");
            String name = net.minecraft.class_3518.method_15265(o, "name");
            int width = net.minecraft.class_3518.method_15282(o, "width", this.field_1493);
            int height = net.minecraft.class_3518.method_15282(o, "height", this.field_1492);
            if (this.field_1495.containsKey(name)) {
                throw new net.minecraft.class_2973(name + " is already defined");
            }
            this.method_1261(name, width, height);
        }
    }

    /** parsePassNode(textureManager, json) */
    public void method_1257(net.minecraft.class_1060 textureManager, JsonElement json) throws IOException {
        JsonObject o = net.minecraft.class_3518.method_15295(json, "pass");
        String name = net.minecraft.class_3518.method_15265(o, "name");
        String inName = net.minecraft.class_3518.method_15265(o, "intarget");
        String outName = net.minecraft.class_3518.method_15265(o, "outtarget");
        net.minecraft.class_276 in = this.method_1266(inName);
        net.minecraft.class_276 out = this.method_1266(outName);
        boolean linear = net.minecraft.class_3518.method_15258(o, "use_linear_filter", false);
        if (in == null) {
            throw new net.minecraft.class_2973("Input target '" + inName + "' does not exist");
        }
        if (out == null) {
            throw new net.minecraft.class_2973("Output target '" + outName + "' does not exist");
        }
        PostPass pass = this.method_1262(name, in, out, linear);
        JsonArray aux = net.minecraft.class_3518.method_15292(o, "auxtargets", null);
        if (aux != null) {
            int i = 0;
            for (JsonElement e : aux) {
                try {
                    JsonObject a = net.minecraft.class_3518.method_15295(e, "auxtarget");
                    String auxName = net.minecraft.class_3518.method_15265(a, "name");
                    String id = net.minecraft.class_3518.method_15265(a, "id");
                    boolean depth;
                    String targetName;
                    if (id.endsWith(":depth")) {
                        depth = true;
                        targetName = id.substring(0, id.lastIndexOf(58));
                    } else {
                        depth = false;
                        targetName = id;
                    }
                    net.minecraft.class_276 target = this.method_1266(targetName);
                    if (target == null) {
                        if (depth) {
                            throw new net.minecraft.class_2973("Render target '" + targetName + "' can't be used as depth buffer");
                        }
                        net.minecraft.class_2960 texture = net.minecraft.class_2960.method_60656("textures/effect/" + targetName + ".png");
                        this.field_49569.method_14486(texture).orElseThrow(() -> method_43210(targetName));
                        RenderSystem.setShaderTexture(0, texture);
                        CbRenderPost.bindForSetup(textureManager, texture);
                        net.minecraft.class_1044 abstractTexture = textureManager.method_4619(texture);
                        int width = net.minecraft.class_3518.method_15260(a, "width");
                        int height = net.minecraft.class_3518.method_15260(a, "height");
                        boolean bilinear = net.minecraft.class_3518.method_15270(a, "bilinear");
                        if (bilinear) {
                            RenderSystem.texParameter(3553, 10241, 9729);
                            RenderSystem.texParameter(3553, 10240, 9729);
                        } else {
                            RenderSystem.texParameter(3553, 10241, 9728);
                            RenderSystem.texParameter(3553, 10240, 9728);
                        }
                        pass.method_1292(auxName, abstractTexture::method_4624, width, height);
                    } else if (depth) {
                        pass.method_1292(auxName, target::method_30278, target.field_1482, target.field_1481);
                    } else {
                        pass.method_1292(auxName, target::method_30277, target.field_1482, target.field_1481);
                    }
                } catch (Exception ex) {
                    net.minecraft.class_2973 c = net.minecraft.class_2973.method_12856(ex);
                    c.method_12854("auxtargets[" + i + "]");
                    throw c;
                }
                i++;
            }
        }
        JsonArray uniforms = net.minecraft.class_3518.method_15292(o, "uniforms", null);
        if (uniforms != null) {
            int i = 0;
            for (JsonElement e : uniforms) {
                try {
                    this.method_1263(e);
                } catch (Exception ex) {
                    net.minecraft.class_2973 c = net.minecraft.class_2973.method_12856(ex);
                    c.method_12854("uniforms[" + i + "]");
                    throw c;
                }
                i++;
            }
        }
    }

    /** parseUniformNode(json): sets a uniform of the last pass */
    public void method_1263(JsonElement json) throws net.minecraft.class_2973 {
        JsonObject o = net.minecraft.class_3518.method_15295(json, "uniform");
        String name = net.minecraft.class_3518.method_15265(o, "name");
        net.minecraft.class_284 uniform = this.field_1497.get(this.field_1497.size() - 1).method_1295().method_1271(name);
        if (uniform == null) {
            throw new net.minecraft.class_2973("Uniform '" + name + "' does not exist");
        }
        float[] values = new float[4];
        int i = 0;
        for (JsonElement e : net.minecraft.class_3518.method_15261(o, "values")) {
            try {
                values[i] = net.minecraft.class_3518.method_15269(e, "value");
            } catch (Exception ex) {
                net.minecraft.class_2973 c = net.minecraft.class_2973.method_12856(ex);
                c.method_12854("values[" + i + "]");
                throw c;
            }
            i++;
        }
        switch (i) {
            case 0:
                break;
            case 1:
                uniform.method_1251(values[0]);
                break;
            case 2:
                uniform.method_1255(values[0], values[1]);
                break;
            case 3:
                uniform.method_1249(values[0], values[1], values[2]);
                break;
            case 4:
                uniform.method_1254(values[0], values[1], values[2], values[3]);
                break;
            default:
                break;
        }
    }

    /** getTempTarget(name) */
    public net.minecraft.class_276 method_1264(String name) {
        return this.field_1495.get(name);
    }

    /** addTempTarget(name, width, height) */
    public void method_1261(String name, int width, int height) {
        net.minecraft.class_276 target = CbRenderTargets.textureTarget(width, height, true, net.minecraft.class_310.field_1703);
        target.method_1236(0.0F, 0.0F, 0.0F, 0.0F);
        this.field_1495.put(name, target);
        if (width == this.field_1493 && height == this.field_1492) {
            this.field_1496.add(target);
        }
    }

    @Override
    public void close() {
        for (net.minecraft.class_276 target : this.field_1495.values()) {
            target.method_1238();
        }
        for (PostPass pass : this.field_1497) {
            pass.close();
        }
        this.field_1497.clear();
    }

    /** addPass(name, in, out, useLinearFilter) */
    public PostPass method_1262(String name, net.minecraft.class_276 in, net.minecraft.class_276 out, boolean useLinearFilter) throws IOException {
        PostPass pass = new PostPass(this.field_49569, name, in, out, useLinearFilter);
        this.field_1497.add(this.field_1497.size(), pass);
        return pass;
    }

    /** updateOrthoMatrix() */
    public void method_1267() {
        this.field_1498 = new Matrix4f().setOrtho(0.0F, (float) this.field_1499.field_1482, 0.0F, (float) this.field_1499.field_1481, 0.1F, 1000.0F);
    }

    /** resize(width, height) */
    public void method_1259(int width, int height) {
        this.field_1493 = this.field_1499.field_1482;
        this.field_1492 = this.field_1499.field_1481;
        this.method_1267();
        for (PostPass pass : this.field_1497) {
            pass.method_1291(this.field_1498);
        }
        for (net.minecraft.class_276 target : this.field_1496) {
            CbRenderPost.resize(target, width, height, net.minecraft.class_310.field_1703);
        }
    }

    /** setFilterMode(mode) */
    public void method_58233(int mode) {
        this.field_1499.method_58226(mode);
        for (net.minecraft.class_276 target : this.field_1495.values()) {
            target.method_58226(mode);
        }
    }

    /** process(partialTick) */
    public void method_1258(float partialTick) {
        this.field_1501 += partialTick;
        while (this.field_1501 > 20.0F) {
            this.field_1501 -= 20.0F;
        }
        int filter = 9728;
        for (PostPass pass : this.field_1497) {
            int mode = pass.method_58234();
            if (filter != mode) {
                this.method_58233(mode);
                filter = mode;
            }
            pass.method_1293(this.field_1501 / 20.0F);
        }
        this.method_58233(9728);
    }

    /** setUniform(name, value): on every pass */
    public void method_57799(String name, float value) {
        for (PostPass pass : this.field_1497) {
            pass.method_1295().method_1275(name).method_1251(value);
        }
    }

    /** getName() */
    public final String method_1260() {
        return this.field_1494;
    }

    /** getRenderTarget(name): "minecraft:main" is the screen target */
    public net.minecraft.class_276 method_1266(String name) {
        if (name == null) {
            return null;
        }
        return name.equals("minecraft:main") ? this.field_1499 : this.field_1495.get(name);
    }

    public static net.minecraft.class_2973 method_43210(String name) {
        return new net.minecraft.class_2973("Render target or texture '" + name + "' does not exist");
    }
}

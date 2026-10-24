package top.dext.centurybridge.step.v1_21_2.facade;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.function.IntSupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.slf4j.Logger;

/**
 * 1.21.1's EffectInstance (net/minecraft/class_280, classRenames), gone in 1.21.2: a post-processing program read from
 * shaders/program/&lt;name&gt;.json (vertex / fragment EffectPrograms, samplers, attributes, uniforms, blend), linked
 * and driven through GlStateManager / RenderSystem, which 1.21.2 keeps. 1.21.1's bodies; the uniforms are 1.21.2's
 * Uniform (1.21.1 handed it this effect as parent only for markDirty, whose flag 1.21.1 never read -- 1.21.2's Uniform
 * has no parent), the GL lookups 1.21.1's Uniform statics made are GlStateManager's same calls.
 */
@Environment(EnvType.CLIENT)
public class EffectInstance implements Effect, AutoCloseable {

    public static final String field_32682 = "shaders/program/";
    public static final Logger field_1514 = com.mojang.logging.LogUtils.getLogger();
    public static final net.minecraft.class_278 field_1520 = new net.minecraft.class_278();
    public static final boolean field_32683 = true;
    public static EffectInstance field_1512;
    public static int field_1505 = -1;
    public final Map<String, IntSupplier> field_1516 = Maps.newHashMap();
    public final List<String> field_1503 = Lists.newArrayList();
    public final List<Integer> field_1506 = Lists.newArrayList();
    public final List<net.minecraft.class_284> field_1515 = Lists.newArrayList();
    public final List<Integer> field_1507 = Lists.newArrayList();
    public final Map<String, net.minecraft.class_284> field_1510 = Maps.newHashMap();
    public final int field_1521;
    public final String field_1509;
    public boolean field_1511;
    public final BlendMode field_1517;
    public final List<Integer> field_1518;
    public final List<String> field_1504;
    public final EffectProgram field_1508;
    public final EffectProgram field_1519;

    public EffectInstance(net.minecraft.class_5912 resourceProvider, String name) throws IOException {
        net.minecraft.class_2960 location = net.minecraft.class_2960.method_60656("shaders/program/" + name + ".json");
        this.field_1509 = name;
        net.minecraft.class_3298 resource = resourceProvider.getResourceOrThrow(location);
        try (Reader reader = resource.method_43039()) {
            JsonObject json = net.minecraft.class_3518.method_15255(reader);
            String vertex = net.minecraft.class_3518.method_15265(json, "vertex");
            String fragment = net.minecraft.class_3518.method_15265(json, "fragment");
            JsonArray samplers = net.minecraft.class_3518.method_15292(json, "samplers", null);
            if (samplers != null) {
                int i = 0;
                for (JsonElement e : samplers) {
                    try {
                        this.method_1276(e);
                    } catch (Exception ex) {
                        net.minecraft.class_2973 c = net.minecraft.class_2973.method_12856(ex);
                        c.method_12854("samplers[" + i + "]");
                        throw c;
                    }
                    i++;
                }
            }
            JsonArray attributes = net.minecraft.class_3518.method_15292(json, "attributes", null);
            if (attributes != null) {
                int i = 0;
                this.field_1518 = Lists.newArrayListWithCapacity(attributes.size());
                this.field_1504 = Lists.newArrayListWithCapacity(attributes.size());
                for (JsonElement e : attributes) {
                    try {
                        this.field_1504.add(net.minecraft.class_3518.method_15287(e, "attribute"));
                    } catch (Exception ex) {
                        net.minecraft.class_2973 c = net.minecraft.class_2973.method_12856(ex);
                        c.method_12854("attributes[" + i + "]");
                        throw c;
                    }
                    i++;
                }
            } else {
                this.field_1518 = null;
                this.field_1504 = null;
            }
            JsonArray uniforms = net.minecraft.class_3518.method_15292(json, "uniforms", null);
            if (uniforms != null) {
                int i = 0;
                for (JsonElement e : uniforms) {
                    try {
                        this.method_1272(e);
                    } catch (Exception ex) {
                        net.minecraft.class_2973 c = net.minecraft.class_2973.method_12856(ex);
                        c.method_12854("uniforms[" + i + "]");
                        throw c;
                    }
                    i++;
                }
            }
            this.field_1517 = method_16035(net.minecraft.class_3518.method_15281(json, "blend", null));
            this.field_1508 = method_16036(resourceProvider, Program.Type.field_1530, vertex);
            this.field_1519 = method_16036(resourceProvider, Program.Type.field_1531, fragment);
            this.field_1521 = ProgramManager.method_1306();
            ProgramManager.method_1307(this);
            this.method_1268();
            if (this.field_1504 != null) {
                for (String attribute : this.field_1504) {
                    int location2 = GlStateManager._glGetAttribLocation(this.field_1521, attribute);
                    this.field_1518.add(location2);
                }
            }
        } catch (Exception ex) {
            net.minecraft.class_2973 c = net.minecraft.class_2973.method_12856(ex);
            c.method_12855(location.method_12832() + " (" + resource.method_14480() + ")");
            throw c;
        }
        this.method_1279();
    }

    /** getOrCreate(provider, type, name) */
    public static EffectProgram method_16036(net.minecraft.class_5912 resourceProvider, Program.Type type, String name) throws IOException {
        Program program = type.method_1289().get(name);
        if (program != null && !(program instanceof EffectProgram)) {
            throw new InvalidClassException("Program is not of type EffectProgram");
        }
        EffectProgram effectProgram;
        if (program == null) {
            net.minecraft.class_2960 location = net.minecraft.class_2960.method_60656("shaders/program/" + name + type.method_1284());
            net.minecraft.class_3298 resource = resourceProvider.getResourceOrThrow(location);
            try (InputStream stream = resource.method_14482()) {
                effectProgram = EffectProgram.method_34415(type, name, stream, resource.method_14480());
            }
        } else {
            effectProgram = (EffectProgram) program;
        }
        return effectProgram;
    }

    /** parseBlendNode(json) */
    public static BlendMode method_16035(JsonObject json) {
        if (json == null) {
            return new BlendMode();
        }
        int func = 32774;
        int srcColor = 1;
        int dstColor = 0;
        int srcAlpha = 1;
        int dstAlpha = 0;
        boolean opaque = true;
        boolean separate = false;
        if (net.minecraft.class_3518.method_15289(json, "func")) {
            func = BlendMode.method_1247(json.get("func").getAsString());
            if (func != 32774) {
                opaque = false;
            }
        }
        if (net.minecraft.class_3518.method_15289(json, "srcrgb")) {
            srcColor = BlendMode.method_1243(json.get("srcrgb").getAsString());
            if (srcColor != 1) {
                opaque = false;
            }
        }
        if (net.minecraft.class_3518.method_15289(json, "dstrgb")) {
            dstColor = BlendMode.method_1243(json.get("dstrgb").getAsString());
            if (dstColor != 0) {
                opaque = false;
            }
        }
        if (net.minecraft.class_3518.method_15289(json, "srcalpha")) {
            srcAlpha = BlendMode.method_1243(json.get("srcalpha").getAsString());
            if (srcAlpha != 1) {
                opaque = false;
            }
            separate = true;
        }
        if (net.minecraft.class_3518.method_15289(json, "dstalpha")) {
            dstAlpha = BlendMode.method_1243(json.get("dstalpha").getAsString());
            if (dstAlpha != 0) {
                opaque = false;
            }
            separate = true;
        }
        if (opaque) {
            return new BlendMode();
        }
        return separate ? new BlendMode(srcColor, dstColor, srcAlpha, dstAlpha, func) : new BlendMode(srcColor, dstColor, func);
    }

    @Override
    public void close() {
        for (net.minecraft.class_284 uniform : this.field_1515) {
            uniform.close();
        }
        ProgramManager.method_1304(this);
    }

    /** clear() */
    public void method_1273() {
        RenderSystem.assertOnRenderThread();
        ProgramManager.method_22094(0);
        field_1505 = -1;
        field_1512 = null;
        for (int i = 0; i < this.field_1506.size(); i++) {
            if (this.field_1516.get(this.field_1503.get(i)) != null) {
                GlStateManager._activeTexture(33984 + i);
                GlStateManager._bindTexture(0);
            }
        }
    }

    /** apply() */
    public void method_1277() {
        this.field_1511 = false;
        field_1512 = this;
        this.field_1517.method_1244();
        if (this.field_1521 != field_1505) {
            ProgramManager.method_22094(this.field_1521);
            field_1505 = this.field_1521;
        }
        for (int i = 0; i < this.field_1506.size(); i++) {
            String name = this.field_1503.get(i);
            IntSupplier supplier = this.field_1516.get(name);
            if (supplier != null) {
                RenderSystem.activeTexture(33984 + i);
                int texture = supplier.getAsInt();
                if (texture != -1) {
                    RenderSystem.bindTexture(texture);
                    net.minecraft.class_284.method_22095(this.field_1506.get(i), i);
                }
            }
        }
        for (net.minecraft.class_284 uniform : this.field_1515) {
            uniform.method_1300();
        }
    }

    /** markDirty() */
    @Override
    public void method_1279() {
        this.field_1511 = true;
    }

    /** getUniform(name) */
    public net.minecraft.class_284 method_1271(String name) {
        RenderSystem.assertOnRenderThread();
        return this.field_1510.get(name);
    }

    /** safeGetUniform(name) */
    public net.minecraft.class_278 method_1275(String name) {
        net.minecraft.class_284 uniform = this.method_1271(name);
        return uniform == null ? field_1520 : uniform;
    }

    /** updateLocations() */
    public void method_1268() {
        RenderSystem.assertOnRenderThread();
        IntList missing = new IntArrayList();
        for (int i = 0; i < this.field_1503.size(); i++) {
            String name = this.field_1503.get(i);
            int location = GlStateManager._glGetUniformLocation(this.field_1521, name);
            if (location == -1) {
                field_1514.warn("Shader {} could not find sampler named {} in the specified shader program.", this.field_1509, name);
                this.field_1516.remove(name);
                missing.add(i);
            } else {
                this.field_1506.add(location);
            }
        }
        for (int i = missing.size() - 1; i >= 0; i--) {
            this.field_1503.remove(missing.getInt(i));
        }
        for (net.minecraft.class_284 uniform : this.field_1515) {
            String name = uniform.method_1298();
            int location = GlStateManager._glGetUniformLocation(this.field_1521, name);
            if (location == -1) {
                field_1514.warn("Shader {} could not find uniform named {} in the specified shader program.", this.field_1509, name);
            } else {
                this.field_1507.add(location);
                uniform.method_1297(location);
                this.field_1510.put(name, uniform);
            }
        }
    }

    /** parseSamplerNode(json) */
    public void method_1276(JsonElement json) {
        JsonObject o = net.minecraft.class_3518.method_15295(json, "sampler");
        String name = net.minecraft.class_3518.method_15265(o, "name");
        if (!net.minecraft.class_3518.method_15289(o, "file")) {
            this.field_1516.put(name, null);
            this.field_1503.add(name);
        } else {
            this.field_1503.add(name);
        }
    }

    /** setSampler(name, texture) */
    public void method_1269(String name, IntSupplier texture) {
        if (this.field_1516.containsKey(name)) {
            this.field_1516.remove(name);
        }
        this.field_1516.put(name, texture);
        this.method_1279();
    }

    /** parseUniformNode(json) */
    public void method_1272(JsonElement json) throws net.minecraft.class_2973 {
        JsonObject o = net.minecraft.class_3518.method_15295(json, "uniform");
        String name = net.minecraft.class_3518.method_15265(o, "name");
        int type = net.minecraft.class_284.method_1299(net.minecraft.class_3518.method_15265(o, "type"));
        int count = net.minecraft.class_3518.method_15260(o, "count");
        float[] values = new float[Math.max(count, 16)];
        JsonArray array = net.minecraft.class_3518.method_15261(o, "values");
        if (array.size() != count && array.size() > 1) {
            throw new net.minecraft.class_2973("Invalid amount of values specified (expected " + count + ", found " + array.size() + ")");
        }
        int i = 0;
        for (JsonElement e : array) {
            try {
                values[i] = net.minecraft.class_3518.method_15269(e, "value");
            } catch (Exception ex) {
                net.minecraft.class_2973 c = net.minecraft.class_2973.method_12856(ex);
                c.method_12854("values[" + i + "]");
                throw c;
            }
            i++;
        }
        if (count > 1 && array.size() == 1) {
            while (i < count) {
                values[i] = values[0];
                i++;
            }
        }
        int extra = count > 1 && count <= 4 && type < 8 ? count - 1 : 0;
        net.minecraft.class_284 uniform = new net.minecraft.class_284(name, type + extra, count);
        if (type <= 3) {
            uniform.method_1248((int) values[0], (int) values[1], (int) values[2], (int) values[3]);
        } else if (type <= 7) {
            uniform.method_1252(values[0], values[1], values[2], values[3]);
        } else {
            uniform.method_1253(values);
        }
        this.field_1515.add(uniform);
    }

    /** getVertexProgram() */
    @Override
    public Program method_1274() {
        return this.field_1508;
    }

    /** getFragmentProgram() */
    @Override
    public Program method_1278() {
        return this.field_1519;
    }

    /** attachToProgram() */
    @Override
    public void method_34418() {
        this.field_1519.method_34414(this);
        this.field_1508.method_34414(this);
    }

    /** getName() */
    public String method_35763() {
        return this.field_1509;
    }

    /** getId() */
    @Override
    public int method_1270() {
        return this.field_1521;
    }
}

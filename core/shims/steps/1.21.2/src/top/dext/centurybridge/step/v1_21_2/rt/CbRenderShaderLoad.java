package top.dext.centurybridge.step.v1_21_2.rt;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_21_2.facade.Program;

/**
 * 1.21.1's ShaderInstance(provider, name, format) on 1.21.2.
 *
 * <p>1.21.1 read shaders/core/&lt;name&gt;.json through the provider (namespace:name: namespace:shaders/core/name.json,
 * as Fabric API's rendering module let 1.21.1 do), compiled its vertex and fragment Program (cached per type by name;
 * #moj_import from shaders/include/, or namespace:path from namespace:shaders/include/), created a GL program, bound
 * the format's attribute names in order, attached both shaders and linked (a link failure was logged, not thrown), then
 * read the uniforms and sampler names. 1.21.2's compiled program (the same class, class_5944) is built the same way by
 * its ShaderManager from a key; here it is built from the provider, as 1.21.1 did: {@link #link} does everything up to
 * the GL program id, which 1.21.2's constructor (class_5944(int), opened by the layer's access widener) takes, and
 * {@link #setup} hands the constructed program its uniforms and samplers (1.21.2's setupUniforms) and its 1.21.1 name,
 * format and Programs (CbRenderShaderMixin).
 *
 * <p>A json in 1.21.2's form (vertex / fragment as namespace:core/... ids, optional defines) loads too: its sources
 * resolve under namespace:shaders/ where 1.21.1's resolved under namespace:shaders/core/, and its defines are injected
 * as 1.21.2's ShaderManager injects them.
 */
@Environment(EnvType.CLIENT)
public final class CbRenderShaderLoad {

    private static final org.slf4j.Logger LOGGER = com.mojang.logging.LogUtils.getLogger();

    /** what {@link #link} built, waiting for the constructor it was built for to return ({@link #setup}) */
    private record Pending(int id, String name, net.minecraft.class_293 format, Program vertex, Program fragment,
                           List<net.minecraft.class_10157.class_10158> samplers, List<net.minecraft.class_10157.class_10159> uniforms) {
    }

    private static final ThreadLocal<Pending> PENDING = new ThreadLocal<>();

    private CbRenderShaderLoad() {
    }

    /** what CbRenderShaderMixin keeps of a program 1.21.1's constructor built */
    public interface Legacy {
        void cb$legacy(String name, net.minecraft.class_293 format, Program vertex, Program fragment);
    }

    /** 1.21.1 new ShaderInstance(provider, name, format) (ctorRedirects) */
    public static net.minecraft.class_5944 create(net.minecraft.class_5912 provider, String name, net.minecraft.class_293 format) throws IOException {
        int id = link(provider, name, format);
        net.minecraft.class_5944 shader = new net.minecraft.class_5944(id);
        setup(shader, provider, name, format);
        return shader;
    }

    /**
     * super(provider, name, format) of a 1.21.1 ShaderInstance subclass (superCtorReshapes, conv:0,1,2): 1.21.1's
     * constructor up to the linked GL program, whose id 1.21.2's constructor takes; {@link #setup} follows it.
     */
    public static int link(net.minecraft.class_5912 provider, String name, net.minecraft.class_293 format) throws IOException {
        com.mojang.blaze3d.systems.RenderSystem.assertOnRenderThread();
        String namespace = "minecraft";
        String path = name;
        int colon = name.indexOf(':');
        if (colon >= 0) {
            namespace = name.substring(0, colon);
            path = name.substring(colon + 1);
        }
        net.minecraft.class_2960 jsonId = net.minecraft.class_2960.method_60655(namespace, "shaders/core/" + path + ".json");
        Program vertex;
        Program fragment;
        List<net.minecraft.class_10157.class_10158> samplers = new ArrayList<>();
        List<net.minecraft.class_10157.class_10159> uniforms = new ArrayList<>();
        try (Reader reader = provider.openAsReader(jsonId)) {
            com.google.gson.JsonObject json = net.minecraft.class_3518.method_15255(reader);
            String vertexName = net.minecraft.class_3518.method_15265(json, "vertex");
            String fragmentName = net.minecraft.class_3518.method_15265(json, "fragment");
            com.google.gson.JsonArray samplerArray = net.minecraft.class_3518.method_15292(json, "samplers", null);
            if (samplerArray != null) {
                int i = 0;
                for (com.google.gson.JsonElement e : samplerArray) {
                    try {
                        com.google.gson.JsonObject o = net.minecraft.class_3518.method_15295(e, "sampler");
                        samplers.add(new net.minecraft.class_10157.class_10158(net.minecraft.class_3518.method_15265(o, "name")));
                    } catch (Exception ex) {
                        net.minecraft.class_2973 chained = net.minecraft.class_2973.method_12856(ex);
                        chained.method_12854("samplers[" + i + "]");
                        throw chained;
                    }
                    i++;
                }
            }
            com.google.gson.JsonArray uniformArray = net.minecraft.class_3518.method_15292(json, "uniforms", null);
            if (uniformArray != null) {
                int i = 0;
                for (com.google.gson.JsonElement e : uniformArray) {
                    try {
                        uniforms.add(uniform(e));
                    } catch (Exception ex) {
                        net.minecraft.class_2973 chained = net.minecraft.class_2973.method_12856(ex);
                        chained.method_12854("uniforms[" + i + "]");
                        throw chained;
                    }
                    i++;
                }
            }
            net.minecraft.class_10149 defines = net.minecraft.class_10149.field_53930;
            if (json.has("defines")) {
                defines = net.minecraft.class_10149.field_53931.parse(com.mojang.serialization.JsonOps.INSTANCE, json.get("defines"))
                    .getOrThrow(net.minecraft.class_2973::new);
            }
            vertex = program(provider, Program.Type.field_1530, vertexName, defines);
            fragment = program(provider, Program.Type.field_1531, fragmentName, defines);
        } catch (Exception e) {
            net.minecraft.class_2973 chained = net.minecraft.class_2973.method_12856(e);
            chained.method_12855(jsonId.method_12832());
            throw chained;
        }
        int id = com.mojang.blaze3d.platform.GlStateManager.glCreateProgram();
        format.method_62886(id);
        com.mojang.blaze3d.platform.GlStateManager.glAttachShader(id, fragment.method_34417());
        com.mojang.blaze3d.platform.GlStateManager.glAttachShader(id, vertex.method_34417());
        com.mojang.blaze3d.platform.GlStateManager.glLinkProgram(id);
        if (com.mojang.blaze3d.platform.GlStateManager.glGetProgrami(id, 35714) == 0) {
            LOGGER.warn("Error encountered when linking program containing VS {} and FS {}. Log output:", vertex.method_1280(), fragment.method_1280());
            LOGGER.warn(com.mojang.blaze3d.platform.GlStateManager.glGetProgramInfoLog(id, 32768));
        }
        PENDING.set(new Pending(id, name, format, vertex, fragment, samplers, uniforms));
        return id;
    }

    /** right after the constructor {@link #link} built the program for (superCtorReshapes post, or {@link #create}) */
    public static void setup(net.minecraft.class_5944 shader, net.minecraft.class_5912 provider, String name, net.minecraft.class_293 format) {
        Pending pending = PENDING.get();
        PENDING.remove();
        if (pending == null || pending.id() != shader.method_1270()) {
            throw new IllegalStateException("1.21.1 shader " + name + ": no program linked for this constructor");
        }
        shader.method_62900(pending.uniforms(), pending.samplers());
        ((Legacy) shader).cb$legacy(pending.name(), pending.format(), pending.vertex(), pending.fragment());
    }

    /** 1.21.1 ShaderInstance.parseUniformNode: values as 1.21.1 filled them (one value fills every slot, none is zeros) */
    private static net.minecraft.class_10157.class_10159 uniform(com.google.gson.JsonElement element) throws IOException {
        com.google.gson.JsonObject o = net.minecraft.class_3518.method_15295(element, "uniform");
        String name = net.minecraft.class_3518.method_15265(o, "name");
        String type = net.minecraft.class_3518.method_15265(o, "type");
        int count = net.minecraft.class_3518.method_15260(o, "count");
        com.google.gson.JsonArray values = net.minecraft.class_3518.method_15292(o, "values", new com.google.gson.JsonArray());
        if (values.size() != count && values.size() > 1) {
            throw new net.minecraft.class_2973("Invalid amount of values specified (expected " + count + ", found " + values.size() + ")");
        }
        List<Float> floats = new ArrayList<>(count);
        int i = 0;
        for (com.google.gson.JsonElement v : values) {
            try {
                floats.add(net.minecraft.class_3518.method_15269(v, "value"));
            } catch (Exception ex) {
                net.minecraft.class_2973 chained = net.minecraft.class_2973.method_12856(ex);
                chained.method_12854("values[" + i + "]");
                throw chained;
            }
            i++;
        }
        float fill = floats.isEmpty() ? 0.0F : floats.get(0);
        while (floats.size() < count) {
            floats.add(fill);
        }
        return new net.minecraft.class_10157.class_10159(name, type, count, floats);
    }

    /** 1.21.1 ShaderInstance.getOrCreate(provider, type, name): the cached Program of that name, else compiled from the provider */
    private static Program program(net.minecraft.class_5912 provider, Program.Type type, String name, net.minecraft.class_10149 defines)
        throws IOException {
        String cacheKey = defines.method_62930() ? name : name + "#" + defines.method_62929();
        Program cached = type.method_1289().get(cacheKey);
        if (cached != null) {
            return cached;
        }
        String namespace = "minecraft";
        String path = name;
        int colon = name.indexOf(':');
        if (colon >= 0) {
            namespace = name.substring(0, colon);
            path = name.substring(colon + 1);
        }
        net.minecraft.class_2960 legacy = net.minecraft.class_2960.method_60655(namespace, "shaders/core/" + path + type.method_1284());
        net.minecraft.class_2960 current = net.minecraft.class_2960.method_60655(namespace, "shaders/" + path + type.method_1284());
        net.minecraft.class_2960 location = provider.method_14486(legacy).isPresent() || provider.method_14486(current).isEmpty() ? legacy : current;
        net.minecraft.class_3298 resource = provider.getResourceOrThrow(location);
        String parentDir = net.minecraft.class_4239.method_34675(location.method_12832());
        try (InputStream stream = resource.method_14482()) {
            Program program = Program.method_1283(type, cacheKey, stream, resource.method_14480(), preprocessor(provider, location.method_12836(), parentDir, defines));
            return program;
        }
    }

    /** 1.21.1 ShaderInstance's GlslPreprocessor (#moj_import), with a 1.21.2 json's defines injected after it */
    private static net.minecraft.class_5913 preprocessor(net.minecraft.class_5912 provider, String parentNamespace, String parentDir,
                                                         net.minecraft.class_10149 defines) {
        return new net.minecraft.class_5913() {
            private final Set<String> imported = new HashSet<>();

            @Override
            public String method_34233(boolean relative, String path) {
                String full;
                if (!relative && path.indexOf(':') >= 0) {
                    int colon = path.indexOf(':');
                    full = path.substring(0, colon) + ":" + net.minecraft.class_4239.method_34676("shaders/include/" + path.substring(colon + 1));
                } else if (relative) {
                    full = parentNamespace + ":" + net.minecraft.class_4239.method_34676(parentDir + path);
                } else {
                    full = "minecraft:" + net.minecraft.class_4239.method_34676("shaders/include/" + path);
                }
                if (!this.imported.add(full)) {
                    return null;
                }
                try (Reader reader = provider.openAsReader(net.minecraft.class_2960.method_60654(full))) {
                    return org.apache.commons.io.IOUtils.toString(reader);
                } catch (IOException e) {
                    LOGGER.error("Could not open GLSL import {}: {}", path, e.getMessage());
                    return "#error " + e.getMessage();
                }
            }

            @Override
            public List<String> method_34229(String source) {
                List<String> parts = super.method_34229(source);
                if (defines.method_62930()) {
                    return parts;
                }
                return List.of(net.minecraft.class_5913.method_62880(String.join("", parts), defines));
            }
        };
    }
}

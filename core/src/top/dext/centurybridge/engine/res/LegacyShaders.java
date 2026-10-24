package top.dext.centurybridge.engine.res;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import top.dext.centurybridge.res.Json;

/**
 * Core shaders across 1.21.2 (a mod's assets/&lt;ns&gt;/shaders/core/*.json and their GLSL).
 *
 * <p>1.21.1 named a core shader's programs by bare name ("vertex": "rendertype_foo": shaders/core/rendertype_foo.vsh;
 * Fabric API's FabricShaderProgram resolved a bare name in the shader's own namespace, a namespaced one as given) and
 * listed its vertex attributes and optional blend state. 1.21.2's ShaderProgramConfig names them by resource location
 * under shaders/ ("minecraft:core/rendertype_foo": the same file), takes samplers, uniforms and defines in the same
 * shape, gets the vertex format from code, and reads no blend state; a config that does not parse leaves the shader
 * uncompiled (the ShaderManager logs it, the render type draws nothing). Each 1.21.1 core shader json is rewritten:
 * vertex / fragment as namespace:core/path (the namespace 1.21.1 Fabric API resolved), attributes dropped (1.21.2 binds
 * the format's own), the chunk-offset uniform under 1.21.2's name (ChunkOffset is ModelOffset, which 1.21.2 sets for
 * every drawn section). A blend block is reported: 1.21.2 sets blending from the render type, not the shader.
 *
 * <p>GLSL under shaders/: an absolute import of a bare include (#moj_import &lt;fog.glsl&gt;, which 1.21.1 read from
 * minecraft's shaders/include) is written namespaced as 1.21.2's shaders write it (the include files are byte-identical
 * in the two releases), and the ChunkOffset uniform takes its 1.21.2 name.
 *
 * <p>Post-effect chains and their program jsons (shaders/post, shaders/program) are left in 1.21.1's format: a 1.21.1 mod
 * loads them through the step layer's 1.21.1 PostChain, which reads that format.
 */
final class LegacyShaders {

    private static final Pattern CORE_JSON = Pattern.compile("assets/([^/]+)/shaders/core/(.+)\\.json");
    private static final Pattern GLSL = Pattern.compile("assets/[^/]+/shaders/(core|include)/.+\\.(vsh|fsh|glsl)");
    private static final Pattern BARE_IMPORT = Pattern.compile("(#moj_import\\s*<)([^:>]+)>");
    private static final Pattern CHUNK_OFFSET = Pattern.compile("\\bChunkOffset\\b");

    private LegacyShaders() {
    }

    static void apply(Map<String, byte[]> entries, ResourceTranslator.Outcome out) {
        for (var e : entries.entrySet()) {
            String n = e.getKey();
            Matcher m = CORE_JSON.matcher(n);
            if (m.matches()) {
                JsonElement j = Json.tryParse(e.getValue());
                if (j instanceof JsonObject o && core(o, m.group(1), n, out)) {
                    e.setValue(Json.write(o));
                    out.rewritten++;
                }
            } else if (GLSL.matcher(n).matches()) {
                String src = new String(e.getValue(), StandardCharsets.UTF_8);
                String up = CHUNK_OFFSET.matcher(BARE_IMPORT.matcher(src).replaceAll("$1minecraft:$2>")).replaceAll("ModelOffset");
                if (!up.equals(src)) {
                    e.setValue(up.getBytes(StandardCharsets.UTF_8));
                    out.rewritten++;
                }
            }
        }
    }

    /** whether a core shader json was in 1.21.1's format and is now 1.21.2's */
    private static boolean core(JsonObject o, String namespace, String file, ResourceTranslator.Outcome out) {
        boolean changed = false;
        for (String program : new String[] {"vertex", "fragment"}) {
            JsonElement v = o.get(program);
            if (v == null || !v.isJsonPrimitive()) {
                continue;
            }
            String name = v.getAsString();
            if (name.contains(":") && name.substring(name.indexOf(':') + 1).startsWith("core/")) {
                continue; // already 1.21.2's form
            }
            int colon = name.indexOf(':');
            String ns = colon < 0 ? namespace : name.substring(0, colon);
            String path = colon < 0 ? name : name.substring(colon + 1);
            o.addProperty(program, ns + ":core/" + path);
            changed = true;
        }
        if (!changed) {
            return false;
        }
        o.remove("attributes"); // 1.21.2 binds the vertex format's own attributes
        if (o.has("blend")) {
            out.lost("core shader blend state not applied: 1.21.2 takes blending from the render type (" + file + ")");
        }
        if (o.get("uniforms") instanceof JsonArray uniforms) {
            for (JsonElement u : uniforms) {
                if (u instanceof JsonObject uo && uo.has("name") && "ChunkOffset".equals(uo.get("name").getAsString())) {
                    uo.addProperty("name", "ModelOffset");
                }
            }
        }
        return true;
    }
}

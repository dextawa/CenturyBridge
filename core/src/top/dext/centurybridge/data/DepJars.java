package top.dext.centurybridge.data;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

/**
 * The separately released mods a bridge layer depends on (core/mods/&lt;mod&gt;: centurybridge-stbir1 for the wall layer),
 * for wiring-check and mixin-check. A layer's wiring may point converted call sites at such a mod's public classes; the
 * gates then judge those targets against the mod's jar, as they judge the layer's own classes against its compiled tree.
 * <p>
 * -Dcb.depJars=&lt;jar&gt;[&lt;path separator&gt;&lt;jar&gt;...] (build.sh: every jar it built into $OUT/deps/) and
 * -Dcb.layerModJson=&lt;the layer's fabric.mod.json&gt;. A jar counts only when its own fabric.mod.json id is one the layer
 * declares in "depends" (Fabric Loader then refuses to start without it); any other jar, or a jar without an id, is a
 * problem the gate reports. Class headers and member signatures only (no code).
 */
final class DepJars {
    private DepJars() {
    }

    /** the dependency jars' classes by internal name; problems found on the way are added to {@code problems} */
    static Map<String, ClassNode> read(List<String> problems) throws IOException {
        Map<String, ClassNode> out = new HashMap<>();
        String jars = System.getProperty("cb.depJars");
        if (jars == null || jars.isEmpty()) {
            return out;
        }
        String fmj = System.getProperty("cb.layerModJson");
        if (fmj == null || !Files.exists(Path.of(fmj))) {
            problems.add("cb.depJars given without -Dcb.layerModJson (the layer's fabric.mod.json): cannot tell what the layer declares");
            return out;
        }
        JsonObject layer = JsonParser.parseString(Files.readString(Path.of(fmj))).getAsJsonObject();
        JsonObject depends = layer.has("depends") ? layer.getAsJsonObject("depends") : new JsonObject();
        for (String j : jars.split(java.io.File.pathSeparator)) {
            if (j.isEmpty()) {
                continue;
            }
            try (var z = new java.util.zip.ZipFile(j)) {
                var e = z.getEntry("fabric.mod.json");
                String id = null;
                if (e != null) {
                    JsonObject m = JsonParser.parseReader(new InputStreamReader(z.getInputStream(e), StandardCharsets.UTF_8)).getAsJsonObject();
                    id = m.has("id") ? m.get("id").getAsString() : null;
                }
                if (id == null) {
                    problems.add("dependency jar " + j + ": no fabric.mod.json id");
                    continue;
                }
                if (!depends.has(id)) {
                    problems.add("dependency jar " + j + ": mod " + id + " is not in the layer's depends (" + fmj + ")");
                    continue;
                }
                for (var en : java.util.Collections.list(z.entries())) {
                    if (!en.getName().endsWith(".class") || en.getName().startsWith("META-INF/")) {
                        continue;
                    }
                    ClassNode cn = new ClassNode();
                    new ClassReader(z.getInputStream(en).readAllBytes()).accept(cn,
                        ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
                    out.putIfAbsent(cn.name, cn);
                }
            }
        }
        return out;
    }
}

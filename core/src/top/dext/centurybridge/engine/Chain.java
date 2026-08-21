package top.dext.centurybridge.engine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * An ordered run of frozen boundary segments. Resolution walks the segments in
 * release order, so every verdict carries attribution: WHICH boundary killed
 * or changed the symbol. Within the intermediary era identity is name
 * equality; the Mojmap frontier segment (1.21.11 -> 26.1) adds rename tables.
 */
public final class Chain {
    /** release order of the intermediary era (frontier + 26.x appended later) */
    public static final List<String> RELEASES = List.of(
        "1.20.1", "1.20.2", "1.20.3", "1.20.4", "1.20.5", "1.20.6",
        "1.21", "1.21.1", "1.21.2", "1.21.3", "1.21.4", "1.21.5", "1.21.6",
        "1.21.7", "1.21.8", "1.21.9", "1.21.10", "1.21.11");

    private static final Pattern METHOD_PAT = Pattern.compile("method_\\d+");
    private static final Pattern FIELD_PAT = Pattern.compile("field_\\d+");
    private static final Pattern CLASS_PAT = Pattern.compile("net/minecraft/class_\\d+");

    public enum Level { OK, L2, L3 }

    /** a verdict plus the boundary that caused it */
    public record Issue(Level level, String boundary) {
        public static final Issue OK = new Issue(Level.OK, null);

        public Issue worst(Issue other) {
            return other.level.ordinal() > this.level.ordinal() ? other : this;
        }
    }

    public final String from;
    public final String to;
    public final List<Segment> segments = new ArrayList<>();
    /** "owner.name(desc)" -> runtime class carrying a same-name static replacement */
    public final java.util.Map<String, String> staticRedirects = new java.util.HashMap<>();
    /** instance calls redirected to a static (receiver becomes arg 0) -- for interface targets Mixin cannot touch */
    public final java.util.Map<String, String> instanceRedirects = new java.util.HashMap<>();
    /** GETSTATIC field reads redirected to a same-name field on a runtime class */
    public final java.util.Map<String, String> fieldRedirects = new java.util.HashMap<>();
    /** dead vanilla classes renamed to facade classes shipped in the runtime bridge */
    public final java.util.Map<String, String> classRenames = new java.util.HashMap<>();
    /** instance fields renamed in place (resolution reaches relocated/renamed fields via hierarchy) */
    public final java.util.Map<String, String> fieldRenames = new java.util.HashMap<>();
    /** methods renamed in place (relocated-to-super methods resolve via hierarchy) */
    public final java.util.Map<String, String> methodRenames = new java.util.HashMap<>();
    /** examined-and-classified damage (quirk tier / structural / etc): "owner" or "owner.name" -> reason */
    public final java.util.Map<String, String> ledger = new java.util.HashMap<>();


    /** A cover/redirect recorded on any ancestor applies to this call site too. */
    public boolean coveredAnywhere(String owner, String nameDesc) {
        for (String o : selfAndSupers(owner)) {
            if (shimCovers.contains(o + "." + nameDesc)) {
                return true;
            }
        }
        return false;
    }

    /** Look a member up in a redirect map, walking the hierarchy. */
    public String redirectAnywhere(java.util.Map<String, String> map,
                                   String owner, String nameDesc) {
        for (String o : selfAndSupers(owner)) {
            String rt = map.get(o + "." + nameDesc);
            if (rt != null) {
                return rt;
            }
        }
        return null;
    }

    public String ledgerReason(String owner, String name) {
        String r = null;
        if (name != null) {
            // the entry may be recorded against any class in the hierarchy, since
            // that is where the member is declared
            for (String o : selfAndSupers(owner)) {
                r = ledger.get(o + "." + name);
                if (r != null) {
                    break;
                }
            }
        }
        if (r == null) {
            r = ledger.get(owner);
        }
        if (r == null) {
            int inner = owner.indexOf('$');
            if (inner > 0) {
                r = ledger.get(owner.substring(0, inner)); // outer-class entries cover inner classes
            }
        }
        return r;
    }
    /** old signatures restored at runtime by shim mixins -- neither issues nor tombstones */
    public final java.util.Set<String> shimCovers = new java.util.HashSet<>();

    /**
     * Owners that were classes when the corpus was compiled and are interfaces
     * on the target (DFU 7 turned DataResult into one). The JVM verifies the
     * call-site kind strictly: a Methodref against an interface throws
     * IncompatibleClassChangeError before any code runs, so these call sites
     * need their opcode and itf flag rewritten, not their target.
     */
    public final java.util.Set<String> interfaceized = new java.util.HashSet<>();

    /**
     * child -> superclass, read from the OLD stub. A mod calls a method through
     * whatever class it holds a reference to, but the declaration -- and hence
     * the ledger entry, cover or redirect -- lives wherever the member is
     * actually declared. Looking up only the call site's owner missed the skin
     * accessors on class_742, onCraft on class_1792 and ChatHud's accessor, all
     * of which were written off as dead while a bridge already existed.
     */
    public final java.util.Map<String, String> superOf = new java.util.HashMap<>();

    /** Walk owner and every ancestor, in order, so callers can resolve via any of them. */
    public java.util.List<String> selfAndSupers(String owner) {
        java.util.List<String> chainUp = new java.util.ArrayList<>();
        String cur = owner;
        for (int hops = 0; cur != null && hops < 12; hops++) {
            chainUp.add(cur);
            cur = superOf.get(cur);
            if (cur != null && !cur.startsWith("net/minecraft/")) {
                break;   // left the game's own hierarchy
            }
        }
        return chainUp;
    }

    /** Load the class hierarchy of a stub jar so member lookups can walk it. */
    public void loadHierarchy(Path stubJar) throws IOException {
        try (java.util.zip.ZipInputStream zin =
                 new java.util.zip.ZipInputStream(Files.newInputStream(stubJar))) {
            java.util.zip.ZipEntry e;
            while ((e = zin.getNextEntry()) != null) {
                if (!e.getName().endsWith(".class") || !e.getName().startsWith("net/minecraft/")) {
                    continue;
                }
                new org.objectweb.asm.ClassReader(zin.readAllBytes()).accept(
                    new org.objectweb.asm.ClassVisitor(org.objectweb.asm.Opcodes.ASM9) {
                        @Override
                        public void visit(int v, int a, String name, String sig,
                                          String sup, String[] itf) {
                            if (sup != null) {
                                superOf.put(name, sup);
                            }
                        }
                    }, org.objectweb.asm.ClassReader.SKIP_CODE
                       | org.objectweb.asm.ClassReader.SKIP_DEBUG
                       | org.objectweb.asm.ClassReader.SKIP_FRAMES);
            }
        }
    }
    /** source-version class -> "client" | "datagen" (absent = common/server) */
    public final java.util.Map<String, String> classSide = new java.util.HashMap<>();

    public String sideOf(String internalName) {
        return classSide.getOrDefault(internalName, "common");
    }

    private Chain(String from, String to) {
        this.from = from;
        this.to = to;
    }

    public static Chain load(Path segmentsDir, String from, String to) throws IOException {
        int a = RELEASES.indexOf(from);
        int b = RELEASES.indexOf(to);
        if (a < 0 || b < 0 || a >= b) {
            throw new IllegalArgumentException("bad chain range " + from + " -> " + to);
        }
        Chain c = new Chain(from, to);
        for (int i = a; i < b; i++) {
            c.segments.add(Segment.load(
                segmentsDir.resolve(RELEASES.get(i) + "__" + RELEASES.get(i + 1) + ".json")));
        }
        Path shims = segmentsDir.resolve("shims-" + to + ".json");
        if (java.nio.file.Files.exists(shims)) {
            var gson = new com.google.gson.Gson();
            var root = gson.fromJson(java.nio.file.Files.readString(shims), com.google.gson.JsonObject.class);
            if (root.has("staticRedirects")) {
                var sr = root.getAsJsonObject("staticRedirects");
                for (String k : sr.keySet()) {
                    c.staticRedirects.put(k, sr.get(k).getAsString());
                }
            }
            if (root.has("instanceRedirects")) {
                var ir = root.getAsJsonObject("instanceRedirects");
                for (String k : ir.keySet()) {
                    c.instanceRedirects.put(k, ir.get(k).getAsString());
                }
            }
            if (root.has("fieldRedirects")) {
                var fr = root.getAsJsonObject("fieldRedirects");
                for (String k : fr.keySet()) {
                    c.fieldRedirects.put(k, fr.get(k).getAsString());
                }
            }
            if (root.has("interfaceized")) {
                for (var el : root.getAsJsonArray("interfaceized")) {
                    c.interfaceized.add(el.getAsString());
                }
            }
            if (root.has("classRenames")) {
                var cr = root.getAsJsonObject("classRenames");
                for (String k : cr.keySet()) {
                    c.classRenames.put(k, cr.get(k).getAsString());
                }
            }
            if (root.has("fieldRenames")) {
                var fr = root.getAsJsonObject("fieldRenames");
                for (String k : fr.keySet()) {
                    c.fieldRenames.put(k, fr.get(k).getAsString());
                }
            }
            if (root.has("methodRenames")) {
                var mr = root.getAsJsonObject("methodRenames");
                for (String k : mr.keySet()) {
                    c.methodRenames.put(k, mr.get(k).getAsString());
                }
            }
            if (root.has("ledger")) {
                var lg = root.getAsJsonObject("ledger");
                for (String k : lg.keySet()) {
                    c.ledger.put(k, lg.get(k).getAsString());
                }
            }
            if (root.has("covers")) {
                for (var e : root.getAsJsonArray("covers")) {
                    c.shimCovers.add(e.getAsString());
                }
            }
        }
        Path sides = segmentsDir.resolve("sides-" + from + ".json");
        if (java.nio.file.Files.exists(sides)) {
            var gson = new com.google.gson.Gson();
            var root = gson.fromJson(java.nio.file.Files.readString(sides), com.google.gson.JsonObject.class);
            for (String side : new String[] {"client", "datagen"}) {
                if (root.has(side)) {
                    for (var e : root.getAsJsonArray(side)) {
                        c.classSide.put(e.getAsString(), side);
                    }
                }
            }
        }
        return c;
    }

    public Issue resolveMember(char kind, String name) {
        Pattern pat = kind == 'm' ? METHOD_PAT : FIELD_PAT;
        if (!pat.matcher(name).matches()) {
            return Issue.OK; // stable / non-intermediary name
        }
        Issue result = Issue.OK;
        for (Segment s : segments) {
            String boundary = s.from + "->" + s.to;
            if ((kind == 'm' ? s.mg : s.fg).contains(name)) {
                return new Issue(Level.L3, boundary); // first death wins
            }
            if (result.level() == Level.OK && (kind == 'm' ? s.mc : s.fc).contains(name)) {
                result = new Issue(Level.L2, boundary);
            }
        }
        return result;
    }

    public Issue resolveClass(String internal) {
        if (!CLASS_PAT.matcher(internal).matches()) {
            return Issue.OK;
        }
        for (Segment s : segments) {
            if (s.cg.contains(internal)) {
                return new Issue(Level.L3, s.from + "->" + s.to);
            }
        }
        return Issue.OK;
    }

    /** member verdict combined with deaths of any class buried in its descriptor */
    public Issue resolveSpec(char kind, String name, String desc) {
        Issue r = resolveMember(kind, name);
        if (desc != null) {
            var m = CLASS_PAT.matcher(desc);
            while (m.find()) {
                r = r.worst(resolveClass(m.group()));
            }
        }
        return r;
    }
}

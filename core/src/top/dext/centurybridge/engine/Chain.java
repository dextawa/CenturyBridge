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
        "1.21.7", "1.21.8", "1.21.9", "1.21.10", "1.21.11",
        // year-based era: ships unobfuscated, intermediary is dead -- the
        // 1.21.11 -> 26.1 segment is a whole-namespace wall (intermediary ->
        // mojang), not just a stub diff
        "26.1", "26.1.1", "26.1.2", "26.2");

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
     * Hooks that gained a trailing RegistryWrapper.WrapperLookup parameter on
     * this target (the 1.20.5 component wall): name -> [old desc, new desc].
     * Damage is two-sided -- an old-shape override is orphaned, an old-shape
     * call site references a descriptor that no longer exists -- so the
     * rewrite both synthesizes forwarding overrides and appends a lookup at
     * call sites. Declared per-target in shims-<to>.json ("lookupHooks"):
     * a chain whose target predates the wall must leave these calls alone.
     */
    public final java.util.Map<String, String[]> lookupHooks = new java.util.HashMap<>();

    /**
     * Generic orphaned-override reconnection ("overrideHooks" in
     * shims-<to>.json): vanilla stopped calling a method mods override,
     * replaced by one with a new name and a new parameter type (26.2:
     * Screen.render(GuiGraphics..) -> extractRenderState(GuiGraphicsExtractor..)).
     * For every mod class declaring the old shape but not the new, the rewrite
     * synthesizes the new shape: it wraps the changed argument through
     * {@code wrap} and forwards to the old override, guarded per object by
     * {@code guard}.cbEnter/cbExit so a super-call chain that lands back in
     * the new shape falls through to INVOKESPECIAL super instead of recursing.
     */
    public static final class OverrideHook {
        public String oldName, oldDesc, newName, newDesc;
        /** "owner.name(desc)" static that converts the new-typed argument to the old type. */
        public String wrapOwner, wrapName, wrapDesc;
        /** internal class name providing cbEnter(Object)Z and cbExit(Object)V */
        public String guard;
        /** hierarchy filters: apply only to classes under / not under this vanilla class */
        public String onlyUnder, notUnder;
        /**
         * For arity changes: for each OLD parameter, the index of the NEW
         * parameter to pass (e.g. mouseScrolled(DDD) -> (DDDD): [0, 1, 3]).
         * Null means same arity, positional.
         */
        public int[] args;
    }

    public final java.util.List<OverrideHook> overrideHooks = new java.util.ArrayList<>();

    public java.util.List<OverrideHook> overrideHooksForOld(String name, String desc) {
        java.util.List<OverrideHook> out = new java.util.ArrayList<>();
        for (OverrideHook h : overrideHooks) {
            if (h.oldName.equals(name) && h.oldDesc.equals(desc)) {
                out.add(h);
            }
        }
        return out;
    }

    public OverrideHook overrideHookForNew(String name, String desc) {
        for (OverrideHook h : overrideHooks) {
            if (h.newName.equals(name) && h.newDesc.equals(desc)) {
                return h;
            }
        }
        return null;
    }

    /**
     * Constructor redirects ("ctorRedirects" in shims-<to>.json):
     * "owner.<init>(desc)V" -> "runtimeClass#factory". Mixin cannot add a
     * constructor, but a NEW site can be rewritten: NEW/DUP/args/INVOKESPECIAL
     * becomes args/INVOKESTATIC factory(args)->owner, which leaves the exact
     * same stack. Only NEW sites qualify -- a subclass super(...) call has no
     * NEW to drop and stays damage.
     */
    public final java.util.Map<String, String> ctorRedirects = new java.util.HashMap<>();

    /** owners that have at least one constructor redirect (fast pre-check) */
    public final java.util.Set<String> ctorRedirectOwners = new java.util.HashSet<>();

    /**
     * Constructors the target actually has, owner -> descriptors, keyed in the
     * analysis (intermediary) namespace. Segment data knows member NAMES, and
     * every constructor is named <init>: without this a dead constructor call
     * site was simply invisible to the accounting (7841 Identifier sites at
     * 26.2 never counted). Empty when no target stub was available.
     */
    public final java.util.Map<String, java.util.Set<String>> targetCtors = new java.util.HashMap<>();

    /**
     * Index the target's constructors. For a real-name (26.x) target the jar is
     * mojang-named: owners and descriptor classes are translated back through
     * the inter->mojang table so they compare against intermediary call sites;
     * classes born after the wall stay mojang-named and match nothing, which
     * is right -- no old mod can name them.
     */
    public void loadTargetCtors(Path stubJar, Path interToMojangTsv) throws IOException {
        java.util.Map<String, String> back = new java.util.HashMap<>();
        if (interToMojangTsv != null && java.nio.file.Files.exists(interToMojangTsv)) {
            for (String ln : java.nio.file.Files.readAllLines(interToMojangTsv)) {
                String[] p = ln.split("\t");
                if (p.length >= 3 && p[0].equals("C")) {
                    back.put(p[2], p[1]);
                }
            }
        }
        var idx = top.dext.centurybridge.data.StubDiff.index(stubJar);
        java.util.regex.Pattern cls = java.util.regex.Pattern.compile("L([^;]+);");
        for (var e : idx.methods.entrySet()) {
            java.util.Set<String> descs = e.getValue().get("<init>");
            if (descs == null) {
                continue;
            }
            String owner = back.getOrDefault(e.getKey(), e.getKey());
            java.util.Set<String> out = targetCtors.computeIfAbsent(owner, k -> new java.util.HashSet<>());
            for (String d : descs) {
                if (back.isEmpty()) {
                    out.add(d);
                } else {
                    out.add(cls.matcher(d).replaceAll(mr -> "L" + java.util.regex.Matcher.quoteReplacement(
                        back.getOrDefault(mr.group(1), mr.group(1))) + ";"));
                }
            }
        }
    }

    /**
     * Redirects that apply to INVOKESPECIAL super-calls only ("superRedirects"):
     * "owner.name(desc)" -> "runtimeClass[#member]", receiver as arg 0. The
     * method still exists on the target with the same descriptor, so a plain
     * instance redirect would be wrong for virtual calls -- but the OLD
     * super-call contract differs (1.20.1 Screen.render drew the widgets and
     * nothing else; since 1.20.2 it draws the background first, over whatever
     * the mod drew). Only a mod's own super.render(..) is rerouted.
     */
    public final java.util.Map<String, String> superRedirects = new java.util.HashMap<>();

    /** fabric.mod.json dependency id migrations declared per target
     *  ("depRenames" in shims-<to>.json), on top of the per-segment ones. */
    public final java.util.Map<String, String> depRenames = new java.util.HashMap<>();

    /** Runtime class serving cbLookup() for the hooks above ("lookupRt"). */
    public String lookupRt;

    /** cbLookup()'s descriptor -- intermediary through 1.21.11, mojang names
     *  past the namespace wall ("lookupDesc" in shims-<to>.json). */
    public String lookupDesc = "()Lnet/minecraft/class_7225$class_7874;";

    /**
     * True when this target is past the 1.20.5 Block.use split
     * (method_9534 6-arg -> method_55766 5-arg): old-shape overrides need a
     * synthesized new-shape hook or vanilla never calls them again.
     */
    public boolean blockUseSplit;

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

    private static String[] splitSig(String sig) {
        int p = sig.indexOf('(');
        return new String[] {sig.substring(0, p), sig.substring(p)};
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
            if (root.has("lookupHooks")) {
                var lh = root.getAsJsonObject("lookupHooks");
                for (String k : lh.keySet()) {
                    var arr = lh.getAsJsonArray(k);
                    c.lookupHooks.put(k, new String[] {
                        arr.get(0).getAsString(), arr.get(1).getAsString()});
                }
            }
            if (root.has("overrideHooks")) {
                for (var el : root.getAsJsonArray("overrideHooks")) {
                    var o = el.getAsJsonObject();
                    OverrideHook h = new OverrideHook();
                    String[] osig = splitSig(o.get("old").getAsString());
                    String[] nsig = splitSig(o.get("new").getAsString());
                    h.oldName = osig[0];
                    h.oldDesc = osig[1];
                    h.newName = nsig[0];
                    h.newDesc = nsig[1];
                    if (o.has("wrap")) {
                        String w = o.get("wrap").getAsString();
                        int dot = w.lastIndexOf('.', w.indexOf('('));
                        h.wrapOwner = w.substring(0, dot);
                        String[] wsig = splitSig(w.substring(dot + 1));
                        h.wrapName = wsig[0];
                        h.wrapDesc = wsig[1];
                    }
                    h.guard = o.has("guard") ? o.get("guard").getAsString() : null;
                    h.onlyUnder = o.has("onlyUnder") ? o.get("onlyUnder").getAsString() : null;
                    if (o.has("args")) {
                        var arr = o.getAsJsonArray("args");
                        h.args = new int[arr.size()];
                        for (int i = 0; i < arr.size(); i++) {
                            h.args[i] = arr.get(i).getAsInt();
                        }
                    }
                    h.notUnder = o.has("notUnder") ? o.get("notUnder").getAsString() : null;
                    c.overrideHooks.add(h);
                }
            }
            if (root.has("depRenames")) {
                var dr = root.getAsJsonObject("depRenames");
                for (String k : dr.keySet()) {
                    c.depRenames.put(k, dr.get(k).getAsString());
                }
            }
            if (root.has("superRedirects")) {
                var sr2 = root.getAsJsonObject("superRedirects");
                for (String k : sr2.keySet()) {
                    c.superRedirects.put(k, sr2.get(k).getAsString());
                }
            }
            if (root.has("ctorRedirects")) {
                var cr = root.getAsJsonObject("ctorRedirects");
                for (String k : cr.keySet()) {
                    c.ctorRedirects.put(k, cr.get(k).getAsString());
                    c.ctorRedirectOwners.add(k.substring(0, k.indexOf(".<init>")));
                }
            }
            if (root.has("lookupDesc")) {
                c.lookupDesc = root.get("lookupDesc").getAsString();
            }
            if (root.has("lookupRt")) {
                c.lookupRt = root.get("lookupRt").getAsString();
            }
            if (root.has("blockUseSplit")) {
                c.blockUseSplit = root.get("blockUseSplit").getAsBoolean();
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

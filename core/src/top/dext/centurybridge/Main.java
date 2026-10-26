package top.dext.centurybridge;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import top.dext.centurybridge.data.SegmentGen;
import top.dext.centurybridge.data.TinyMappings;
import top.dext.centurybridge.engine.Chain;
import top.dext.centurybridge.engine.JarProcessor;
import top.dext.centurybridge.engine.Segment;

/** CenturyBridge core CLI (the same engine later fronts the mod and the agent). */
public final class Main {
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            usage();
            return;
        }
        switch (args[0]) {
            case "gen-segment" -> genSegment(args[1], args[2], Path.of(args[3]), Path.of(args[4]));
            case "gen-chain" -> genChain(args);
            case "convert" -> convert(args);
            case "convert-climb" -> convertClimb(args);
            case "remap-jar" -> top.dext.centurybridge.data.JarRemapper
                .remapToIntermediary(Path.of(args[1]), Path.of(args[2]), Path.of(args[3]));
            case "remap-mojang" -> top.dext.centurybridge.data.JarRemapper
                .remapWithTable(Path.of(args[1]), Path.of(args[2]), Path.of(args[3]));
            case "gen-sides" -> top.dext.centurybridge.data.SideAnnotator.generate(
                Path.of(args[1]), Path.of(args[2]), Path.of(args[3]), Path.of(args[4]), Path.of(args[5]));
            case "res-parity" -> top.dext.centurybridge.data.ResParity.run(
                clientJar(args[1]), args[1], clientJar(args[2]), args[2],
                args.length > 3 ? Integer.parseInt(args[3]) : 3);
            case "apply-aw" -> {
                // apply-aw <in.jar> <out.jar> <aw...>
                java.util.List<Path> aws = new ArrayList<>();
                for (int i = 3; i < args.length; i++) {
                    aws.add(Path.of(args[i]));
                }
                top.dext.centurybridge.data.ApplyAw.run(Path.of(args[1]), Path.of(args[2]), aws);
            }
            // lib-diff <oldLibrariesDir> <newLibrariesDir> <out.tsv> [<artifacts.tsv> <V> <W>]
            case "lib-diff" -> top.dext.centurybridge.data.LibDiff.run(Path.of(args[1]), Path.of(args[2]), Path.of(args[3]),
                args.length > 6 ? Path.of(args[4]) : null, args.length > 6 ? args[5] : null, args.length > 6 ? args[6] : null);
            case "api-probe" -> top.dext.centurybridge.data.ApiProbe.run(args);
            case "anchor-diff" -> top.dext.centurybridge.data.AnchorDiff.run(args);
            // mixin-probe <V code jar> <out.jar>; mixin-judge <code jar> <probe jar> <index.tsv> <out.tsv> [--layer <jar>]... [--keys <tsv>]
            case "mixin-probe" -> top.dext.centurybridge.data.MixinProbe.run(args);
            case "mixin-judge" -> top.dext.centurybridge.engine.MixinJudge.run(args);
            case "mixin-cause" -> top.dext.centurybridge.data.MixinCause.run(args);
            case "gen-mixin-targets" -> top.dext.centurybridge.data.GenMixinTargets.run(args);
            // access-diff <oldStub> <newStub> [<wall.tsv>] [<shims-ver.json>] <out.aw>
            case "access-diff" -> top.dext.centurybridge.data.AccessDiff.run(args);
            case "gen-renames" -> {
                // gen-renames <newest client jar> <out.json> [<jarsDir> <IdRenameTable.java>]
                top.dext.centurybridge.data.IdRenames.run(Path.of(args[1]), Path.of(args[2]));
                if (args.length > 4) {
                    top.dext.centurybridge.data.IdRenames.writeTable(Path.of(args[2]), Path.of(args[3]), Path.of(args[4]));
                }
            }
            case "res-diff" -> top.dext.centurybridge.data.ResDiff.run(
                Path.of(args[1]), Path.of(args[2]), args.length > 3 ? Integer.parseInt(args[3]) : 8);
            case "stub-diff" -> top.dext.centurybridge.data.StubDiff.run(
                Path.of(args[1]), Path.of(args[2]), Path.of(args[3]));
            case "class-fate" -> top.dext.centurybridge.data.ClassFate.run(
                Path.of(args[1]), Path.of(args[2]), Path.of(args[3]));
            case "forge-orders" -> top.dext.centurybridge.data.BridgeForge.run(
                Path.of(args[1]), Path.of(args[2]), Path.of(args[3]),
                args.length > 5 ? Path.of(args[4]) : null,
                Path.of(args[args.length - 1]));
            case "classify" -> top.dext.centurybridge.data.SymbolAudit.classify(
                Path.of(args[1]), Path.of(args[2]), Path.of(args[3]));
            case "stub-dump" -> stubDump(Path.of(args[1]), Path.of(args[2]));
            case "mixin-check" -> top.dext.centurybridge.data.MixinCheck.run(
                Path.of(args[1]), Path.of(args[2]),
                args.length > 3 && !args[3].isEmpty() ? Path.of(args[3]) : null,
                args.length > 4 && !args[4].isEmpty() ? Path.of(args[4]) : null,
                args.length > 6 ? Path.of(args[5]) : null,
                args.length > 6 ? args[6] : null);
            case "server-check" -> top.dext.centurybridge.data.ServerCheck.run(
                Path.of(args[1]), Path.of(args[2]), Path.of(args[3]), Path.of(args[4]), Path.of(args[5]),
                args.length > 6 && args[6].equals("--fix"));
            case "wiring-check" -> top.dext.centurybridge.data.WiringCheck.run(
                Path.of(args[1]), Path.of(args[2]),
                args.length > 3 && !args[3].isEmpty() ? Path.of(args[3]) : null,
                args.length > 4 && !args[4].isEmpty() ? Path.of(args[4]) : null,
                args.length > 6 ? Path.of(args[5]) : null,
                args.length > 6 ? args[6] : null);
            case "mixin-anchors" -> top.dext.centurybridge.engine.MixinRepair.audit(args);
            case "link-check" -> top.dext.centurybridge.data.LinkCheck.run(args);
            case "audit" -> top.dext.centurybridge.data.SymbolAudit.run(
                Path.of(args[1]), Path.of(args[2]), Path.of(args[3]), Path.of(args[4]));
            default -> usage();
        }
    }

    private static void genSegment(String from, String to, Path mappingsDir, Path out) throws Exception {
        TinyMappings a = TinyMappings.load(mappingsDir.resolve("intermediary-" + from + ".tiny"));
        TinyMappings b = TinyMappings.load(mappingsDir.resolve("intermediary-" + to + ".tiny"));
        Segment s = SegmentGen.diff(from, to, a, b);
        s.save(out);
        System.out.printf("segment %s -> %s: classes gone %d, methods gone %d / sig-changed %d, fields gone %d / type-changed %d%n",
            s.from, s.to, s.classesGone.size(), s.methodsGone.size(), s.methodsDescChanged.size(),
            s.fieldsGone.size(), s.fieldsDescChanged.size());
    }

    // gen-chain <from> <to> <mappingsDir> <segmentsDir>
    private static void genChain(String[] args) throws Exception {
        int a = Chain.RELEASES.indexOf(args[1]);
        int b = Chain.RELEASES.indexOf(args[2]);
        Path mappings = Path.of(args[3]);
        Path segDir = Path.of(args[4]);
        Files.createDirectories(segDir);
        for (int i = a; i < b; i++) {
            String from = Chain.RELEASES.get(i);
            String to = Chain.RELEASES.get(i + 1);
            Path out = segDir.resolve(from + "__" + to + ".json");
            if (Files.exists(out)) {
                System.out.println("segment " + from + " -> " + to + ": cached");
                continue;
            }
            genSegment(from, to, mappings, out);
        }
    }

    // convert <segmentsDir> <from> <to> <outDir> <jar...>
    private static void convert(String[] args) throws Exception {
        Chain chain = Chain.load(Path.of(args[1]), args[2], args[3]);
        // hierarchy of the SOURCE version: mods were compiled against it, so a call
        // site's owner and the class actually declaring the member are related there
        Path srcStub = stubOf(args[2]);
        Path tgtStub = stubOf(args[3]);
        // both stubs are read relative to the working directory (data/jars): run from anywhere else and
        // the hierarchy, the target's constructors and the interface-kind fixes are silently absent --
        // a converted jar that links on paper and throws IncompatibleClassChangeError (s2rest's harness)
        for (Path stub : new Path[] {srcStub, tgtStub}) {
            if (!Files.exists(stub) && !Boolean.getBoolean("cb.allowMissingStubs")) {
                throw new IllegalStateException(stub.toAbsolutePath() + " is missing: run from the checkout that holds"
                    + " data/jars (or set -Dcb.allowMissingStubs=true to convert without it, knowingly)");
            }
        }
        if (Files.exists(srcStub)) {
            chain.loadHierarchy(srcStub);
            System.out.println("hierarchy: " + chain.superOf.size() + " classes from " + srcStub.getFileName());
        }
        // the interfaces the source release's Fabric API injects into vanilla classes: a mod compiled against
        // it calls (and overrides) them through the vanilla class (Chain.readInjectedInterfaces)
        java.util.Map<String, List<String>> srcInjected = injectedInterfacesOf(args[2]);
        chain.addSourceInjections(srcInjected);
        System.out.println("injected interfaces: " + srcInjected.size() + " vanilla classes in " + args[2] + "'s Fabric API");
        // and its own class hierarchy: wiring keyed on a Fabric API type serves a call through a subtype of it
        // (Chain.libSuperOf); the same modules, so a missing directory has already been refused above
        chain.loadLibraryHierarchy(Path.of("data/runtime/client-" + args[2] + "/.fabric/processedMods"));
        System.out.println("library hierarchy: " + chain.libSuperOf.size() + " classes in " + args[2] + "'s Fabric API");
        // and the game's own libraries (authlib, DFU, brigadier...): wiring keyed on a library type serves a mod
        // class under a library subclass too (a mod's YggdrasilMinecraftSessionService subclass is a
        // MinecraftSessionService). Absent: nothing is read
        chain.loadLibraryHierarchy(Path.of("data/runtime/client-" + args[2] + "/libraries"));
        System.out.println("library hierarchy: " + chain.libSuperOf.size() + " classes with " + args[2] + "'s game libraries");
        Path outDir = Path.of(args[4]);
        top.dext.centurybridge.data.FullAudit.enabled = true;
        top.dext.centurybridge.data.FullAudit.reset();
        List<String> lines = new ArrayList<>();
        lines.add("# CenturyBridge conversion report (" + chain.from + " -> " + chain.to
            + ", " + chain.segments.size() + " segments)");
        lines.add("");
        List<Path> jars = new ArrayList<>();
        for (int i = 5; i < args.length; i++) {
            Path p = Path.of(args[i]);
            if (Files.isDirectory(p)) {
                try (var s = Files.list(p)) {
                    s.filter(f -> f.toString().endsWith(".jar")).sorted().forEach(jars::add);
                }
            } else {
                jars.add(p);
            }
        }
        // the 1.21.11 -> 26.1 boundary is a namespace wall, not a symbol wall:
        // everything that survives it gets renamed to mojang names. When the
        // chain crosses it, every converted jar takes a whole-jar rename pass.
        boolean crossesWall = Chain.RELEASES.indexOf(args[2]) <= Chain.RELEASES.indexOf("1.21.11")
            && Chain.RELEASES.indexOf(args[3]) > Chain.RELEASES.indexOf("1.21.11");
        // the table spelling names the way the TARGET declares them (core/segments/wall-tables.tsv)
        Path renameTable = crossesWall ? top.dext.centurybridge.engine.WallTable.forTarget(Path.of(args[1]), args[3])
                                       : Path.of("data/mappings/inter-to-mojang-1.21.11.tsv");
        if (crossesWall && !Files.exists(renameTable)) {
            throw new IllegalStateException("chain crosses the namespace wall but " + renameTable + " is missing");
        }
        String lj = System.getProperty("cb.layerJar");
        if (lj == null && System.getProperty("cb.layersDir") != null) {
            lj = Path.of(System.getProperty("cb.layersDir"), "centurybridge-0.3.0+" + args[3] + ".jar").toString();
        }
        // the whole-jar rename every converted jar takes past the wall (facade tokens keep their names
        // where the facades declare them so: read off the step's layer -- the climb's, or the one
        // build.sh built in the work dir whose core/segments this conversion reads)
        top.dext.centurybridge.data.JarRemapper.Table wallTable = null;
        if (crossesWall) {
            Path workDir = Path.of(args[1]).toAbsolutePath().normalize().getParent().getParent();
            Path surfaces = lj != null ? Path.of(lj) : workDir.resolve("build/centurybridge-0.3.0+" + args[3] + ".jar");
            java.util.Map<String, java.util.Set<String>> declared = null;
            if (Files.exists(surfaces)) {
                declared = top.dext.centurybridge.data.JarRemapper.Table.facadeMembers(surfaces);
                System.out.println("facade surfaces for the wall rename: " + surfaces);
            } else if (!top.dext.centurybridge.data.JarRemapper.Table.facadeOwners(chain.classRenames).isEmpty()) {
                System.out.println("WARNING: no layer jar (" + surfaces + "): every facade-only token keeps its intermediary name");
            }
            wallTable = top.dext.centurybridge.data.JarRemapper.loadTable(renameTable).keepFacadeTokens(chain.classRenames, declared);
            // the wall table's source names are 1.21.11's: a bare method id in a mixin selector gets its 1.21.11
            // descriptor where the target class has several methods of the new name (Table.sourceMethods)
            if (Files.exists(codeOf("1.21.11"))) {
                wallTable.loadSource(codeOf("1.21.11"));
            }
        }
        if (lj != null && Files.exists(Path.of(lj))) {
            chain.layerJar = Path.of(lj);
            chain.layerMembers = top.dext.centurybridge.engine.LayerMembers.read(chain.layerJar);
            System.out.println("step layer for redirect bodies and restored members: " + lj);
        }
        Path targetStub = stubOf(args[3]);
        if (Files.exists(targetStub)) {
            chain.loadTargetCtors(targetStub, crossesWall ? renameTable : null);
            System.out.println("target constructors: " + chain.targetCtors.size() + " owners from " + targetStub.getFileName());
        }
        // and the target release's, into the target hierarchy (after loadTargetCtors, which writes it)
        java.util.Map<String, List<String>> tgtInjected = injectedInterfacesOf(args[3]);
        chain.addTargetInjections(tgtInjected, crossesWall ? renameTable : null);
        if (Files.exists(targetStub)) {
            chain.loadTargetRaw(targetStub);
        }
        chain.loadTargetLibraryDecls(Path.of("data/runtime/client-" + args[3] + "/.fabric/processedMods"));
        chain.wallRemapper = crossesWall && wallTable != null ? wallTable.remapper() : null;
        System.out.println("target Fabric API declarations: " + chain.targetLibDecls.size() + " classes");
        System.out.println("injected interfaces: " + tgtInjected.size() + " vanilla classes in " + args[3] + "'s Fabric API");
        // real method bodies for injector anchors (the stub has none). Intermediary
        // era only: past the wall the refmaps are renamed later in the pipeline.
        if (!crossesWall) {
            String tc = System.getProperty("cb.targetCode", System.getenv("CB_TARGET_CODE"));
            Path codeJar = tc != null ? Path.of(tc) : null;
            if (codeJar == null && Files.exists(codeOf(args[3]))) {
                codeJar = codeOf(args[3]);
            }
            if (codeJar == null) {
                Path rj = Path.of("data/runtime/client-" + args[3] + "/.fabric/remappedJars");
                if (Files.isDirectory(rj)) {
                    try (var s = Files.walk(rj)) {
                        codeJar = s.filter(f -> f.getFileName().toString().equals("client-intermediary.jar")).findFirst().orElse(null);
                    }
                }
            }
            if (codeJar != null && Files.exists(codeJar)) {
                chain.mixinRepair = new top.dext.centurybridge.engine.MixinRepair(codeJar);
                chain.mixinRepair.renamed = chain::renamedDesc;
                chain.mixinRepair.layerMembers = chain.layerMembers;
                chain.mixinRepair.classRenames = chain.classRenames; // anchor-facade: what restored bodies call
                chain.mixinRepair.stepTo = args[3]; // the mixed-member signal (MixinRepair.MIXED_DIR)
                chain.mixinRepair.mixinMoves = chain.mixinMoves; // injectors on static methods W moved to another class
                chain.mixinRepair.converter = chain::argConverter; // captured target arguments the step retyped (CASE 14)
                System.out.println("mixin anchors: checked against " + codeJar);
            } else {
                System.out.println("mixin anchors: NOT checked (no code-bearing target jar; set CB_TARGET_CODE)");
            }
            // the source release's bodies: an @At anchor the step's wiring moves is counted where the
            // mod wrote it before it is retargeted (MixinBridge)
            String sc = System.getProperty("cb.sourceCode", System.getenv("CB_SOURCE_CODE"));
            Path srcCode = sc != null ? Path.of(sc) : codeOf(args[2]);
            if (Files.exists(srcCode)) {
                chain.sourceCode = new top.dext.centurybridge.engine.MixinRepair(srcCode);
                // and an ordinal-pinned anchor is moved to its V site's counterpart in W (MixinRepair ordinal-remap)
                if (chain.mixinRepair != null) {
                    chain.mixinRepair.source = chain.sourceCode;
                }
            }
        }
        // past the wall the anchors are checked once the jar has its real names, against
        // the target's own unobfuscated code
        top.dext.centurybridge.engine.MixinRepair wallRepair = null;
        if (crossesWall) {
            Path code = System.getenv("CB_TARGET_CODE") != null ? Path.of(System.getenv("CB_TARGET_CODE"))
                                                                : Path.of("data/jars/client-" + args[3] + ".jar");
            if (Files.exists(code)) {
                wallRepair = new top.dext.centurybridge.engine.MixinRepair(code);
                // the layer is built against W's real names, the names the jar has by now: a selector on a member
                // the layer restores finds it there (LayerMembers), as chain.mixinRepair does before the wall,
                // instead of the injector being disabled as "target gone"
                wallRepair.layerMembers = chain.layerMembers;
                wallRepair.stepTo = args[3];
                chain.injectorsJudgedPastWall = true;
                System.out.println("mixin anchors past the wall: checked against " + code);
            } else {
                System.out.println("mixin anchors past the wall: NOT checked (no " + code + "; set CB_TARGET_CODE)");
            }
            // the wall step itself: MixinBridge carries the mods' mixins through the step's wiring in the
            // analysis namespace, against 26.1's bodies taken back into it (WallInverse) and 1.21.11's own
            Path vCode = codeOf(args[2]);
            if (args[2].equals("1.21.11") && Files.exists(code) && Files.exists(vCode)) {
                chain.bridgeCode = new top.dext.centurybridge.engine.MixinRepair(
                    top.dext.centurybridge.engine.WallInverse.intermediaryView(code, vCode, renameTable));
                chain.sourceCode = new top.dext.centurybridge.engine.MixinRepair(vCode);
            }
            // and past the wall, the injectors are judged in 26.1's names: 1.21.11's bodies renamed through the
            // same wall table give the repair V's sites to match (ordinal-remap, sites-split, descend-site), as
            // chain.sourceCode does on every other step
            if (wallRepair != null && Files.exists(vCode)) {
                Path vWall = Files.createTempFile("cb-wall-vcode-", ".jar");
                vWall.toFile().deleteOnExit();
                top.dext.centurybridge.data.JarRemapper.remapWithTable(renameTable, vCode, vWall, code);
                wallRepair.source = new top.dext.centurybridge.engine.MixinRepair(vWall);
                System.out.println("mixin anchors past the wall: V sites from " + vCode + " renamed through " + renameTable);
            }
        }
        java.util.Map<String, Integer> verdicts = new java.util.TreeMap<>();
        // the step's layer for the code models: a mixin following its target's class rename onto a facade is judged
        // against the facade's own code (Chain.mixinFollow); at the wall in the analysis namespace like W's bodies
        top.dext.centurybridge.engine.MixinRepair layerModel = chain.bridgeCode != null ? chain.bridgeCode : chain.mixinRepair;
        if (chain.layerJar != null && layerModel != null) {
            Path layerView = chain.layerJar;
            if (chain.bridgeCode != null && Files.exists(codeOf(args[2]))) {
                layerView = top.dext.centurybridge.engine.WallInverse.intermediaryView(chain.layerJar, codeOf(args[2]), renameTable);
                // MixinBridge and the triage carry the wall step in the analysis namespace: the layer's restored members
                // in it too (swall7: a selector on a restored member with a route stays there, rather than following
                // V's code by similarity onto another 26.1 method); the repair past the wall keeps the real names
                chain.layerMembers = top.dext.centurybridge.engine.LayerMembers.read(layerView);
            }
            layerModel.layer(layerView);
        }
        int engineErrors = 0;
        int unaccounted = 0;
        int ledgered = 0;
        int brokenClasses = 0;
        int orphans = 0;
        int resourceLosses = 0;
        for (Path jar : jars) {
            try {
                JarProcessor.Report r = JarProcessor.process(jar, outDir, chain);
                if (crossesWall && r.outputJar != null && Files.exists(r.outputJar)) {
                    Path tmp = r.outputJar.resolveSibling(r.outputJar.getFileName() + ".preNs");
                    Files.move(r.outputJar, tmp);
                    // with the target's code jar: calls also take the invoke kind the target declares their owner as
                    top.dext.centurybridge.data.JarRemapper.remapWithTable(wallTable, tmp, r.outputJar,
                        System.getenv("CB_TARGET_CODE") != null ? Path.of(System.getenv("CB_TARGET_CODE"))
                                                                : Path.of("data/jars/client-" + args[3] + ".jar"));
                    Files.delete(tmp);
                    if (wallRepair != null) {
                        JarProcessor.repairPastWall(r.outputJar, wallRepair, r);
                    }
                }
                verdicts.merge(r.verdict, 1, Integer::sum);
                unaccounted += r.issues.size();
                ledgered += r.ledgered;
                brokenClasses += r.brokenHierarchy.size();
                lines.add("## " + (r.modId != null ? r.modId : r.file) + " -- " + r.verdict);
                r.brokenHierarchy.forEach(s -> lines.add("- !! cannot load: " + s));
                r.notes.forEach(n -> lines.add("- " + n));
                r.strippedMixins.forEach(s -> lines.add("- stripped mixin: " + s));
                r.disabledInjectors.forEach(s -> lines.add("- disabled injector: " + s));
                r.orphanedOverrides.forEach(s -> lines.add("- orphaned override (vanilla never calls it): " + s));
                orphans += r.orphanedOverrides.size();
                r.resourceLosses.forEach(s -> lines.add("- resource not carried to " + chain.to + ": " + s));
                resourceLosses += r.resourceLosses.size();
                r.issues.forEach(s -> lines.add("- " + s));
                r.clientIssues.forEach(s -> lines.add("- " + s));
                if (r.datagenSkipped > 0) {
                    lines.add("- (datagen-only refs ignored: " + r.datagenSkipped + ")");
                }
                lines.add("");
            } catch (Exception ex) {
                engineErrors++;
                verdicts.merge("ENGINE_ERROR", 1, Integer::sum);
                lines.add("## " + jar.getFileName() + " -- ENGINE_ERROR");
                lines.add("- " + ex);
                lines.add("");
                System.out.println("ENGINE_ERROR " + jar.getFileName() + ": " + ex);
            }
        }
        System.out.println("verdicts: " + verdicts);
        System.out.println("unaccounted runtime refs: " + unaccounted + " (ledgered: " + ledgered + ")");
        if (orphans > 0) {
            System.out.println("orphaned overrides: " + orphans + " (mod overrides of a vanilla shape the target no longer has)");
        }
        if (resourceLosses > 0) {
            System.out.println("resource losses: " + resourceLosses + " (data/assets the translation could not carry; see report)");
        }
        if (brokenClasses > 0) {
            System.out.println("broken hierarchy: " + brokenClasses + " mod class(es) cannot load on " + chain.to
                + " (" + verdicts.getOrDefault("broken_hierarchy", 0) + " mods)");
        }
        if (engineErrors > 0) {
            System.out.println("!! engine errors: " + engineErrors + " (see report)");
        }
        Path report = outDir.resolve("report.md");
        Files.createDirectories(outDir); // no jar may have been written (all engine errors / bad paths)
        Files.write(report, String.join("\n", lines).getBytes(StandardCharsets.UTF_8));
        System.out.println("report -> " + report);
        Path fullAudit = outDir.resolve("full-audit.tsv");
        top.dext.centurybridge.data.FullAudit.write(fullAudit,
            crossesWall ? wallTable.remapper() : null);
        System.out.println("full-audit (distinct symbols by fate category): "
            + top.dext.centurybridge.data.FullAudit.summary());
        System.out.println("full-audit -> " + fullAudit);
    }

    /**
     * convert-climb <segmentsDir> <from> <to> <outDir> <jar|dir...>: a mod climbs one release at a
     * time. Each step V -> W is an ordinary conversion over that one segment with that step's own
     * bridge wiring (core/segments/steps/V__W.json; step 1.20.1 -> 1.20.2 is the 1.20.2 tree,
     * shims-1.20.2.json) and W's own code for the injector anchors; its output, a W-era mod with the
     * step's bridge references in it, is the next step's input. Every step keeps its report and audit
     * under outDir/steps/V__W/; the final jars land in outDir as name+to.jar.
     */
    private static void convertClimb(String[] args) throws Exception {
        Path segs = Path.of(args[1]);
        String from = args[2];
        String to = args[3];
        Path outDir = Path.of(args[4]);
        int a = Chain.RELEASES.indexOf(from);
        int b = Chain.RELEASES.indexOf(to);
        if (a < 0 || b < 0 || a >= b) {
            throw new IllegalArgumentException("bad climb " + from + " -> " + to);
        }
        List<Path> inputs = new ArrayList<>();
        for (int i = 5; i < args.length; i++) {
            Path p = Path.of(args[i]);
            if (Files.isDirectory(p)) {
                try (var s = Files.list(p)) {
                    s.filter(f -> f.toString().endsWith(".jar")).sorted().forEach(inputs::add);
                }
            } else {
                inputs.add(p);
            }
        }
        // the name each jar keeps through the climb (a step's output carries "+W")
        java.util.Map<String, String> baseOf = new java.util.LinkedHashMap<>();
        for (Path p : inputs) {
            String n = p.getFileName().toString();
            baseOf.put(n, n.substring(0, n.length() - 4));
        }
        for (int i = a; i < b; i++) {
            String v = Chain.RELEASES.get(i);
            String w = Chain.RELEASES.get(i + 1);
            String seg = v + "__" + w;
            Path stepWiring = v.equals("1.20.1") && w.equals("1.20.2")
                ? segs.resolve("shims-1.20.2.json") : segs.resolve("steps").resolve(seg + ".json");
            System.setProperty("cb.wiring", stepWiring.toString());
            Path stepOut = outDir.resolve("steps").resolve(seg);
            System.out.println("== climb " + seg + (top.dext.centurybridge.engine.Wiring.exists(stepWiring) ? " (bridges: " + stepWiring + ")" : " (no bridges for this step yet)"));
            List<String> a2 = new ArrayList<>(List.of("convert", segs.toString(), v, w, stepOut.toString()));
            inputs.forEach(p -> a2.add(p.toString()));
            convert(a2.toArray(new String[0]));
            List<Path> next = new ArrayList<>();
            java.util.Map<String, String> nextBase = new java.util.LinkedHashMap<>();
            for (Path p : inputs) {
                String base = baseOf.get(p.getFileName().toString());
                String produced = p.getFileName().toString();
                produced = produced.substring(0, produced.length() - 4) + "+" + w + ".jar";
                Path o = stepOut.resolve(produced);
                if (!Files.exists(o)) {
                    throw new IllegalStateException("step " + seg + " produced no " + o.getFileName() + " (see " + stepOut.resolve("report.md") + ")");
                }
                Path staged = stepOut.resolve("next").resolve(base + ".jar");
                Files.createDirectories(staged.getParent());
                Files.copy(o, staged, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                next.add(staged);
                nextBase.put(staged.getFileName().toString(), base);
            }
            inputs = next;
            baseOf = nextBase;
            // -Dcb.climb.keep=false: a corpus-sized climb keeps only each step's report (report.md,
            // full-audit.tsv); the converted jars of a step go once the next step has read them --
            // 270 mods are a gigabyte per step, twenty-one steps more than a shared disk has room for
            if (!Boolean.parseBoolean(System.getProperty("cb.climb.keep", "true"))) {
                try (var s = Files.list(stepOut)) {
                    for (Path f : (Iterable<Path>) s::iterator) {
                        if (f.getFileName().toString().endsWith(".jar")) {
                            Files.delete(f);
                        }
                    }
                }
                if (i > a) {
                    Path prevNext = outDir.resolve("steps").resolve(Chain.RELEASES.get(i - 1) + "__" + v).resolve("next");
                    deleteTree(prevNext);
                }
            }
        }
        System.clearProperty("cb.wiring");
        Files.createDirectories(outDir);
        for (Path p : inputs) {
            Files.copy(p, outDir.resolve(baseOf.get(p.getFileName().toString()) + "+" + to + ".jar"),
                java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        }
        System.out.println("climb " + from + " -> " + to + ": " + inputs.size() + " jar(s) -> " + outDir);
    }

    private static boolean postWall(String version) {
        return Chain.RELEASES.indexOf(version) > Chain.RELEASES.indexOf("1.21.11");
    }

    /** removes a directory the climb itself wrote (a step's staged jars), failing loudly on any error */
    private static void deleteTree(Path dir) throws java.io.IOException {
        if (!Files.exists(dir)) {
            return;
        }
        try (var s = Files.walk(dir)) {
            for (Path p : (Iterable<Path>) s.sorted(java.util.Comparator.reverseOrder())::iterator) {
                Files.delete(p);
            }
        }
    }

    /**
     * A release's compile stub under data/jars: the intermediary stub through 1.21.11; past the
     * namespace wall the plain jar itself (mojang names, nothing to remap).
     */
    private static Path stubOf(String version) {
        return Path.of(postWall(version) ? "data/jars/client-" + version + ".jar"
                                          : "data/jars/client-" + version + "-intermediary.jar");
    }

    /**
     * The interfaces a release's Fabric API injects into vanilla classes, from its modules under
     * data/runtime/client-&lt;version&gt;/.fabric/processedMods (Chain.readInjectedInterfaces): missing modules
     * fail the conversion, unless -Dcb.allowMissingStubs=true says to go on without them, knowingly.
     */
    private static java.util.Map<String, List<String>> injectedInterfacesOf(String version) throws java.io.IOException {
        Path mods = Path.of("data/runtime/client-" + version + "/.fabric/processedMods");
        if (!Files.isDirectory(mods) && Boolean.getBoolean("cb.allowMissingStubs")) {
            return java.util.Map.of();
        }
        return Chain.readInjectedInterfaces(mods);
    }

    /** a release's jar with real method bodies, for mixin anchors: past the wall the plain jar carries them */
    private static Path codeOf(String version) {
        return Path.of(postWall(version) ? "data/jars/client-" + version + ".jar"
                                          : "data/jars/client-" + version + "-intermediary-code.jar");
    }

    /** a release's client jar under data/jars: obfuscated before 26.1, plain after */
    private static Path clientJar(String version) {
        Path obf = Path.of("data/jars/client-" + version + "-obf.jar");
        return Files.exists(obf) ? obf : Path.of("data/jars/client-" + version + ".jar");
    }

    // stub-dump <stubJar> <out.txt>: "M owner.name(desc)" per method, the
    // self-recursion gate's input in assemble.py
    private static void stubDump(Path stub, Path out) throws Exception {
        var idx = top.dext.centurybridge.data.StubDiff.index(stub);
        List<String> lines = new ArrayList<>();
        for (var e : idx.methods.entrySet()) {
            for (var m : e.getValue().entrySet()) {
                for (String d : m.getValue()) {
                    lines.add("M " + e.getKey() + "." + m.getKey() + d);
                }
            }
        }
        java.util.Collections.sort(lines);
        Files.write(out, String.join("\n", lines).getBytes(StandardCharsets.UTF_8));
        System.out.println("stub-dump: " + lines.size() + " methods -> " + out);
    }

    private static void usage() {
        System.out.println("""
            centurybridge-core
              gen-segment <from> <to> <mappingsDir> <out.json>
              gen-chain <from> <to> <mappingsDir> <segmentsDir>
              convert <segmentsDir> <from> <to> <outDir> <jar...>
              convert-climb <segmentsDir> <from> <to> <outDir> <jar...>   (one release at a time, each step its own bridges)
              remap-mojang <inter-to-mojang.tsv> <in.jar> <out.jar>
              mixin-check <compiledShimDir> <targetJar> [<shims.json> [<inter-to-mojang.tsv> [<stubDir> <targetVersion>]]]
              wiring-check <compiledShimDir> <shims.json> [<inter-to-mojang.tsv> [<stub> [<stubDir> <targetVersion>]]]
              server-check <compiledShimDir> <mixins.json> <serverJar> <serverLibraries> <fabricApiJar> [--fix]
              forge-orders <open.tsv> <oldStub> <newStub> [<full-audit.tsv>] <orders.json>
              stub-dump <stubJar> <out.txt>
              link-check <outDir> <full-audit.tsv|-> <convertedJar|dir...> -- <classpathJar|dir...>""");
    }
}

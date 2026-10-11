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
            case "access-diff" -> top.dext.centurybridge.data.AccessDiff.run(Path.of(args[1]), Path.of(args[2]),
                args.length > 4 && !args[3].isEmpty() ? Path.of(args[3]) : null, Path.of(args[args.length - 1]));
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
        Path srcStub = Path.of("data/jars/client-" + args[2] + "-intermediary.jar");
        if (Files.exists(srcStub)) {
            chain.loadHierarchy(srcStub);
            System.out.println("hierarchy: " + chain.superOf.size() + " classes from " + srcStub.getFileName());
        }
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
        Path renameTable = Path.of("data/mappings/inter-to-mojang-1.21.11.tsv");
        if (crossesWall && !Files.exists(renameTable)) {
            throw new IllegalStateException("chain crosses the namespace wall but " + renameTable + " is missing");
        }
        Path targetStub = crossesWall ? Path.of("data/jars/client-" + args[3] + ".jar")
                                      : Path.of("data/jars/client-" + args[3] + "-intermediary.jar");
        if (Files.exists(targetStub)) {
            chain.loadTargetCtors(targetStub, crossesWall ? renameTable : null);
            System.out.println("target constructors: " + chain.targetCtors.size() + " owners from " + targetStub.getFileName());
        }
        // real method bodies for injector anchors (the stub has none). Intermediary
        // era only: past the wall the refmaps are renamed later in the pipeline.
        if (!crossesWall) {
            Path codeJar = System.getenv("CB_TARGET_CODE") != null ? Path.of(System.getenv("CB_TARGET_CODE")) : null;
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
                System.out.println("mixin anchors: checked against " + codeJar);
            } else {
                System.out.println("mixin anchors: NOT checked (no code-bearing target jar; set CB_TARGET_CODE)");
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
                System.out.println("mixin anchors past the wall: checked against " + code);
            } else {
                System.out.println("mixin anchors past the wall: NOT checked (no " + code + "; set CB_TARGET_CODE)");
            }
        }
        java.util.Map<String, Integer> verdicts = new java.util.TreeMap<>();
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
                    top.dext.centurybridge.data.JarRemapper.remapWithTable(renameTable, tmp, r.outputJar);
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
            crossesWall ? top.dext.centurybridge.data.JarRemapper.loadTable(renameTable).remapper() : null);
        System.out.println("full-audit (distinct symbols by fate category): "
            + top.dext.centurybridge.data.FullAudit.summary());
        System.out.println("full-audit -> " + fullAudit);
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
              remap-mojang <inter-to-mojang.tsv> <in.jar> <out.jar>
              mixin-check <compiledShimDir> <targetJar> [<shims.json> [<inter-to-mojang.tsv> [<stubDir> <targetVersion>]]]
              wiring-check <compiledShimDir> <shims.json> [<inter-to-mojang.tsv> [<stub> [<stubDir> <targetVersion>]]]
              server-check <compiledShimDir> <mixins.json> <serverJar> <serverLibraries> <fabricApiJar> [--fix]
              forge-orders <open.tsv> <oldStub> <newStub> [<full-audit.tsv>] <orders.json>
              stub-dump <stubJar> <out.txt>
              link-check <outDir> <full-audit.tsv|-> <convertedJar|dir...> -- <classpathJar|dir...>""");
    }
}

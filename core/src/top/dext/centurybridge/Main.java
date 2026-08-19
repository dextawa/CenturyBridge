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
            case "gen-sides" -> top.dext.centurybridge.data.SideAnnotator.generate(
                Path.of(args[1]), Path.of(args[2]), Path.of(args[3]), Path.of(args[4]), Path.of(args[5]));
            case "stub-diff" -> top.dext.centurybridge.data.StubDiff.run(
                Path.of(args[1]), Path.of(args[2]), Path.of(args[3]));
            case "classify" -> top.dext.centurybridge.data.SymbolAudit.classify(
                Path.of(args[1]), Path.of(args[2]), Path.of(args[3]));
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
        java.util.Map<String, Integer> verdicts = new java.util.TreeMap<>();
        int engineErrors = 0;
        int unaccounted = 0;
        int ledgered = 0;
        for (Path jar : jars) {
            try {
                JarProcessor.Report r = JarProcessor.process(jar, outDir, chain);
                verdicts.merge(r.verdict, 1, Integer::sum);
                unaccounted += r.issues.size();
                ledgered += r.ledgered;
                lines.add("## " + (r.modId != null ? r.modId : r.file) + " -- " + r.verdict);
                r.notes.forEach(n -> lines.add("- " + n));
                r.strippedMixins.forEach(s -> lines.add("- stripped mixin: " + s));
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
        if (engineErrors > 0) {
            System.out.println("!! engine errors: " + engineErrors + " (see report)");
        }
        Path report = outDir.resolve("report.md");
        Files.write(report, String.join("\n", lines).getBytes(StandardCharsets.UTF_8));
        System.out.println("report -> " + report);
        Path fullAudit = outDir.resolve("full-audit.tsv");
        top.dext.centurybridge.data.FullAudit.write(fullAudit);
        System.out.println("full-audit (distinct symbols by fate category): "
            + top.dext.centurybridge.data.FullAudit.summary());
        System.out.println("full-audit -> " + fullAudit);
    }

    private static void usage() {
        System.out.println("""
            centurybridge-core
              gen-segment <from> <to> <mappingsDir> <out.json>
              gen-chain <from> <to> <mappingsDir> <segmentsDir>
              convert <segmentsDir> <from> <to> <outDir> <jar...>""");
    }
}

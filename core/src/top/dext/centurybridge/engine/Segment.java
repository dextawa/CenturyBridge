package top.dext.centurybridge.engine;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * One boundary segment of the version chain: which symbols died or changed
 * signature between two adjacent releases, plus (only on the
 * intermediary->Mojmap frontier segment) rename tables.
 *
 * Segments are generated once per boundary and frozen; a new drop adds one
 * segment at the head of the chain.
 */
public final class Segment {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public String from;
    public String to;
    public String namespace = "intermediary";
    public List<String> classesGone = new ArrayList<>();
    public List<String> methodsGone = new ArrayList<>();
    public List<String> methodsDescChanged = new ArrayList<>();
    public List<String> fieldsGone = new ArrayList<>();
    public List<String> fieldsDescChanged = new ArrayList<>();
    /** mod-id renames at this boundary (e.g. fabric -> fabric-api), usually empty */
    public Map<String, String> depRenames = new HashMap<>();

    public transient Set<String> cg;
    public transient Set<String> mg;
    public transient Set<String> mc;
    public transient Set<String> fg;
    public transient Set<String> fc;

    public void index() {
        cg = new HashSet<>(classesGone);
        mg = new HashSet<>(methodsGone);
        mc = new HashSet<>(methodsDescChanged);
        fg = new HashSet<>(fieldsGone);
        fc = new HashSet<>(fieldsDescChanged);
    }

    public static Segment load(Path p) throws IOException {
        try (Reader r = Files.newBufferedReader(p)) {
            Segment s = GSON.fromJson(r, Segment.class);
            s.index();
            return s;
        }
    }

    public void save(Path p) throws IOException {
        Files.createDirectories(p.getParent());
        try (Writer w = Files.newBufferedWriter(p)) {
            GSON.toJson(this, w);
        }
    }
}

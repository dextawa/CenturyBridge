package top.dext.centurybridge.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Classifies every vanilla class of a version by execution context:
 *   datagen  -- named path under net/minecraft/data/ (dev-time only, never
 *               loaded in a live game; damage here is not runtime damage)
 *   client   -- present in the client jar but absent from the server jar
 *   common   -- everything else
 * Static analysis without this annotation systematically overstates damage:
 * recipe/advancement builder churn (datagen) inflates issue counts although
 * the bytecode never executes in play.
 */
public final class SideAnnotator {

    public static void generate(Path clientObfJar, Path serverJar, Path tiny,
                                Path proguard, Path out) throws IOException {
        Set<String> clientObf = classNames(Files.readAllBytes(clientObfJar));
        Set<String> serverObf = classNames(unwrapBundler(Files.readAllBytes(serverJar)));

        TinyMappings.Obf obf = TinyMappings.loadObf(tiny);

        // ProGuard class lines only: "named.path -> obf:"
        Map<String, String> obfToNamed = new HashMap<>();
        for (String line : Files.readAllLines(proguard)) {
            if (line.startsWith(" ") || line.startsWith("#") || !line.endsWith(":")) {
                continue;
            }
            int arrow = line.indexOf(" -> ");
            if (arrow < 0) {
                continue;
            }
            String named = line.substring(0, arrow).replace('.', '/');
            String o = line.substring(arrow + 4, line.length() - 1).replace('.', '/');
            obfToNamed.put(o, named);
        }

        List<String> client = new ArrayList<>();
        List<String> datagen = new ArrayList<>();
        for (Map.Entry<String, String> e : obf.classes.entrySet()) {
            String o = e.getKey();
            String inter = e.getValue();
            String named = obfToNamed.get(o);
            if (named != null && named.startsWith("net/minecraft/data/")) {
                datagen.add(inter);
            } else if (clientObf.contains(o) && !serverObf.contains(o)) {
                client.add(inter);
            }
        }
        client.sort(String::compareTo);
        datagen.sort(String::compareTo);

        Map<String, Object> root = new TreeMap<>();
        root.put("client", client);
        root.put("datagen", datagen);
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        Files.createDirectories(out.getParent());
        Files.writeString(out, gson.toJson(root));
        System.out.printf("sides: %d client-only, %d datagen classes -> %s%n",
            client.size(), datagen.size(), out);
    }

    private static Set<String> classNames(byte[] jar) throws IOException {
        Set<String> names = new HashSet<>();
        try (ZipInputStream zin = new ZipInputStream(new java.io.ByteArrayInputStream(jar))) {
            ZipEntry e;
            while ((e = zin.getNextEntry()) != null) {
                String n = e.getName();
                if (n.endsWith(".class") && !n.contains("META-INF")) {
                    names.add(n.substring(0, n.length() - 6));
                }
            }
        }
        return names;
    }

    /** modern server jars are bundlers: the real jar sits in META-INF/versions/ */
    private static byte[] unwrapBundler(byte[] jar) throws IOException {
        try (ZipInputStream zin = new ZipInputStream(new java.io.ByteArrayInputStream(jar))) {
            ZipEntry e;
            while ((e = zin.getNextEntry()) != null) {
                if (e.getName().startsWith("META-INF/versions/") && e.getName().endsWith(".jar")) {
                    return zin.readAllBytes();
                }
            }
        }
        return jar; // pre-bundler format: classes at top level
    }
}

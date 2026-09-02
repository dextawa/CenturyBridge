import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Applies Fabric API's access wideners to a compile stub jar. The runtime
 * loader widens vanilla classes per module (.accesswidener / .classtweaker
 * inside each nested module jar), so fapi-facing shim code (e.g. a class
 * implementing CreativeModeTab$Output) compiles only against a stub that
 * carries the same widening. Nothing at runtime uses this jar.
 *
 *   java WidenStub <stub.jar> <fabric-api-bundle.jar> <out.jar>
 *
 * Handles: accessible/extendable class (incl. InnerClasses attributes
 * everywhere the class is named), accessible/extendable method,
 * accessible/mutable field; transitive- prefixes likewise.
 */
public final class WidenStub {

    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            System.err.println("usage: WidenStub <stub.jar> <fapi-bundle.jar> <out.jar>");
            System.exit(2);
        }
        Set<String> pubClasses = new HashSet<>();
        Set<String> extClasses = new HashSet<>();
        Set<String> pubMethods = new HashSet<>();   // owner.name+desc
        Set<String> extMethods = new HashSet<>();
        Set<String> pubFields = new HashSet<>();    // owner.name
        Set<String> mutFields = new HashSet<>();

        try (ZipInputStream zin = new ZipInputStream(Files.newInputStream(Path.of(args[1])))) {
            ZipEntry e;
            while ((e = zin.getNextEntry()) != null) {
                byte[] data = zin.readAllBytes();
                if (e.getName().endsWith(".accesswidener") || e.getName().endsWith(".classtweaker")) {
                    parseAw(data, pubClasses, extClasses, pubMethods, extMethods, pubFields, mutFields);
                } else if (e.getName().startsWith("META-INF/jars/") && e.getName().endsWith(".jar")) {
                    try (ZipInputStream nested = new ZipInputStream(new ByteArrayInputStream(data))) {
                        ZipEntry n;
                        while ((n = nested.getNextEntry()) != null) {
                            if (n.getName().endsWith(".accesswidener") || n.getName().endsWith(".classtweaker")) {
                                parseAw(nested.readAllBytes(), pubClasses, extClasses, pubMethods, extMethods, pubFields, mutFields);
                            }
                        }
                    }
                }
            }
        }
        System.out.println("widening: " + pubClasses.size() + " accessible classes, " + extClasses.size()
            + " extendable classes, " + pubMethods.size() + "/" + extMethods.size() + " methods, "
            + pubFields.size() + " fields (" + mutFields.size() + " mutable)");

        int patched = 0;
        Map<String, byte[]> out = new LinkedHashMap<>();
        try (ZipInputStream zin = new ZipInputStream(Files.newInputStream(Path.of(args[0])))) {
            ZipEntry e;
            while ((e = zin.getNextEntry()) != null) {
                if (e.isDirectory()) {
                    continue;
                }
                byte[] data = zin.readAllBytes();
                if (e.getName().endsWith(".class")) {
                    byte[] widened = widen(data, pubClasses, extClasses, pubMethods, extMethods, pubFields, mutFields);
                    if (widened != null) {
                        data = widened;
                        patched++;
                    }
                }
                out.putIfAbsent(e.getName(), data);
            }
        }
        try (ZipOutputStream zout = new ZipOutputStream(Files.newOutputStream(Path.of(args[2])))) {
            for (Map.Entry<String, byte[]> en : out.entrySet()) {
                zout.putNextEntry(new ZipEntry(en.getKey()));
                zout.write(en.getValue());
                zout.closeEntry();
            }
        }
        System.out.println("patched " + patched + " classes -> " + args[2]);
    }

    private static void parseAw(byte[] data, Set<String> pubC, Set<String> extC,
                                Set<String> pubM, Set<String> extM, Set<String> pubF, Set<String> mutF) {
        for (String ln : new String(data, StandardCharsets.UTF_8).split("\n")) {
            String t = ln.trim();
            if (t.isEmpty() || t.startsWith("#") || t.startsWith("accessWidener") || t.startsWith("classTweaker")) {
                continue;
            }
            String[] w = t.split("\\s+");
            if (w.length < 3) {
                continue;
            }
            String kind = w[0].startsWith("transitive-") ? w[0].substring("transitive-".length()) : w[0];
            switch (w[1]) {
                case "class" -> {
                    if (kind.equals("accessible")) {
                        pubC.add(w[2]);
                    } else if (kind.equals("extendable")) {
                        extC.add(w[2]);
                    }
                }
                case "method" -> {
                    if (w.length >= 5) {
                        if (kind.equals("accessible")) {
                            pubM.add(w[2] + "." + w[3] + w[4]);
                        } else if (kind.equals("extendable")) {
                            extM.add(w[2] + "." + w[3] + w[4]);
                        }
                    }
                }
                case "field" -> {
                    if (w.length >= 4) {
                        if (kind.equals("accessible")) {
                            pubF.add(w[2] + "." + w[3]);
                        } else if (kind.equals("mutable")) {
                            mutF.add(w[2] + "." + w[3]);
                        }
                    }
                }
                default -> { }
            }
        }
    }

    private static int widenAccess(int acc, boolean makePublic, boolean makeExtendable) {
        if (makePublic || makeExtendable) {
            acc = (acc & ~(Opcodes.ACC_PRIVATE | Opcodes.ACC_PROTECTED)) | Opcodes.ACC_PUBLIC;
        }
        if (makeExtendable) {
            acc &= ~Opcodes.ACC_FINAL;
        }
        return acc;
    }

    private static byte[] widen(byte[] data, Set<String> pubC, Set<String> extC,
                                Set<String> pubM, Set<String> extM, Set<String> pubF, Set<String> mutF) {
        ClassReader cr = new ClassReader(data);
        String self = cr.getClassName();
        // fast reject: does this class or any inner-class reference need touching?
        // (cheap textual scan keeps the whole-jar pass quick)
        boolean maybe = pubC.isEmpty() && extC.isEmpty();
        String raw = new String(data, StandardCharsets.ISO_8859_1);
        for (String c : pubC) {
            if (raw.contains(c)) {
                maybe = true;
                break;
            }
        }
        if (!maybe) {
            for (String c : extC) {
                if (raw.contains(c)) {
                    maybe = true;
                    break;
                }
            }
        }
        boolean selfMembers = false;
        for (String k : pubM) {
            if (k.startsWith(self + ".")) {
                selfMembers = true;
                break;
            }
        }
        if (!selfMembers) {
            for (String k : extM) {
                if (k.startsWith(self + ".")) {
                    selfMembers = true;
                    break;
                }
            }
        }
        if (!selfMembers) {
            for (String k : pubF) {
                if (k.startsWith(self + ".")) {
                    selfMembers = true;
                    break;
                }
            }
        }
        if (!selfMembers) {
            for (String k : mutF) {
                if (k.startsWith(self + ".")) {
                    selfMembers = true;
                    break;
                }
            }
        }
        if (!maybe && !selfMembers) {
            return null;
        }
        final boolean[] changed = {false};
        ClassWriter cw = new ClassWriter(0);
        cr.accept(new ClassVisitor(Opcodes.ASM9, cw) {
            @Override
            public void visit(int version, int access, String name, String sig, String superName, String[] ifaces) {
                int a = widenAccess(access, pubC.contains(name), extC.contains(name));
                changed[0] |= a != access;
                super.visit(version, a, name, sig, superName, ifaces);
            }

            @Override
            public void visitInnerClass(String name, String outer, String inner, int access) {
                int a = widenAccess(access, pubC.contains(name), extC.contains(name));
                changed[0] |= a != access;
                super.visitInnerClass(name, outer, inner, a);
            }

            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String sig, String[] ex) {
                String key = self + "." + name + desc;
                int a = widenAccess(access, pubM.contains(key), extM.contains(key));
                changed[0] |= a != access;
                return super.visitMethod(a, name, desc, sig, ex);
            }

            @Override
            public FieldVisitor visitField(int access, String name, String desc, String sig, Object value) {
                String key = self + "." + name;
                int a = access;
                if (pubF.contains(key)) {
                    a = widenAccess(a, true, false);
                }
                if (mutF.contains(key)) {
                    a &= ~Opcodes.ACC_FINAL;
                }
                changed[0] |= a != access;
                return super.visitField(a, name, desc, sig, value);
            }
        }, 0);
        return changed[0] ? cw.toByteArray() : null;
    }

    private WidenStub() {
    }
}

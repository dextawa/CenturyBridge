package top.dext.centurybridge.data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * A compile stub with an access widener applied, as the loader applies it at runtime:
 * the shim tree compiles against the access it will have in the game (CB_STUB=...-aw.jar).
 * Lines: accessible / extendable / mutable for class, method, field (v1/v2 syntax,
 * "transitive-" prefixes read as the plain directive).
 */
public final class ApplyAw {

    public static void run(Path in, Path out, java.util.List<Path> aws) throws IOException {
        Set<String> accClass = new HashSet<>();
        Set<String> extClass = new HashSet<>();
        Map<String, Set<String>> accMember = new HashMap<>();
        Map<String, Set<String>> extMethod = new HashMap<>();
        Map<String, Set<String>> mutField = new HashMap<>();
        java.util.List<String> lines = new java.util.ArrayList<>();
        for (Path aw : aws) {
            lines.addAll(Files.readAllLines(aw));
        }
        for (String raw : lines) {
            String ln = raw.contains("#") ? raw.substring(0, raw.indexOf('#')) : raw;
            String[] t = ln.trim().split("\\s+");
            if (t.length < 3 || t[0].equals("accessWidener")) {
                continue;
            }
            String dir = t[0].replace("transitive-", "");
            String kind = t[1];
            if (kind.equals("class")) {
                (dir.equals("extendable") ? extClass : accClass).add(t[2]);
                continue;
            }
            if (t.length < 5) {
                continue;
            }
            String key = t[3] + (kind.equals("field") ? ":" : "") + t[4];
            Map<String, Set<String>> target = switch (dir) {
                case "extendable" -> extMethod;
                case "mutable" -> mutField;
                default -> accMember;
            };
            target.computeIfAbsent(t[2], k -> new HashSet<>()).add(key);
            if (!dir.equals("mutable")) {
                accClass.add(t[2]); // a member made reachable needs its class reachable
            }
        }
        int changed = 0;
        try (ZipFile z = new ZipFile(in.toFile()); ZipOutputStream zo = new ZipOutputStream(Files.newOutputStream(out))) {
            for (var en : java.util.Collections.list(z.entries())) {
                byte[] b = z.getInputStream(en).readAllBytes();
                if (en.getName().endsWith(".class")) {
                    ClassReader cr = new ClassReader(b);
                    String cls = cr.getClassName();
                    // every class is rewritten: an inner class made reachable is also recorded
                    // in its outer class's (and its users') InnerClasses attribute, which javac reads
                    {
                        ClassWriter cw = new ClassWriter(0);
                        cr.accept(new ClassVisitor(Opcodes.ASM9, cw) {
                            @Override
                            public void visit(int v, int acc, String name, String sig, String sup, String[] itf) {
                                if (accClass.contains(name) || extClass.contains(name)) {
                                    acc = (acc & ~(Opcodes.ACC_PRIVATE | Opcodes.ACC_PROTECTED)) | Opcodes.ACC_PUBLIC;
                                }
                                if (extClass.contains(name)) {
                                    acc &= ~Opcodes.ACC_FINAL;
                                }
                                super.visit(v, acc, name, sig, sup, itf);
                            }

                            @Override
                            public void visitInnerClass(String name, String outer, String inner, int acc) {
                                if (accClass.contains(name) || extClass.contains(name)) {
                                    acc = (acc & ~(Opcodes.ACC_PRIVATE | Opcodes.ACC_PROTECTED)) | Opcodes.ACC_PUBLIC;
                                }
                                if (extClass.contains(name)) {
                                    acc &= ~Opcodes.ACC_FINAL;
                                }
                                super.visitInnerClass(name, outer, inner, acc);
                            }

                            @Override
                            public FieldVisitor visitField(int acc, String name, String desc, String sig, Object val) {
                                String k = name + ":" + desc;
                                if (accMember.getOrDefault(cls, Set.of()).contains(k)) {
                                    acc = (acc & ~(Opcodes.ACC_PRIVATE | Opcodes.ACC_PROTECTED)) | Opcodes.ACC_PUBLIC;
                                }
                                if (mutField.getOrDefault(cls, Set.of()).contains(k)) {
                                    acc &= ~Opcodes.ACC_FINAL;
                                }
                                return super.visitField(acc, name, desc, sig, val);
                            }

                            @Override
                            public MethodVisitor visitMethod(int acc, String name, String desc, String sig, String[] ex) {
                                String k = name + desc;
                                if (accMember.getOrDefault(cls, Set.of()).contains(k)) {
                                    acc = (acc & ~(Opcodes.ACC_PRIVATE | Opcodes.ACC_PROTECTED)) | Opcodes.ACC_PUBLIC;
                                }
                                if (extMethod.getOrDefault(cls, Set.of()).contains(k)) {
                                    if ((acc & Opcodes.ACC_PUBLIC) == 0) {
                                        acc = (acc & ~Opcodes.ACC_PRIVATE) | Opcodes.ACC_PROTECTED;
                                    }
                                    acc &= ~Opcodes.ACC_FINAL;
                                }
                                return super.visitMethod(acc, name, desc, sig, ex);
                            }
                        }, 0);
                        b = cw.toByteArray();
                        changed++;
                    }
                }
                zo.putNextEntry(new ZipEntry(en.getName()));
                zo.write(b);
                zo.closeEntry();
            }
        }
        System.out.println("apply-aw: " + changed + " classes widened -> " + out);
    }

    private ApplyAw() {
    }
}

package top.dext.centurybridge.data;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.RecordComponentVisitor;

/**
 * A compile stub with access wideners applied as the loader applies them at runtime
 * (AwRules): the shim tree compiles against the access it will have in the game
 * (CB_STUB=...-aw.jar), no more -- an entry the loader never applies (a class outside the
 * game packages) does not widen the stub either, so javac rejects what the JVM would.
 */
public final class ApplyAw {

    public static void run(Path in, Path out, List<Path> aws) throws IOException {
        AwRules rules = new AwRules();
        for (Path aw : aws) {
            String err = rules.read(AwRules.lines(Files.readString(aw, StandardCharsets.UTF_8)), null);
            if (err != null) {
                throw new IOException(aw + " does not load: " + err);
            }
        }
        for (String c : rules.untweakable()) {
            System.out.println("apply-aw: never applied by the loader (not a game class): " + c);
        }
        int changed = 0;
        try (ZipFile z = new ZipFile(in.toFile()); ZipOutputStream zo = new ZipOutputStream(Files.newOutputStream(out))) {
            for (var en : java.util.Collections.list(z.entries())) {
                byte[] b = z.getInputStream(en).readAllBytes();
                if (en.getName().endsWith(".class")) {
                    // every class is rewritten: an inner class made reachable is also recorded
                    // in its outer class's (and its users') InnerClasses attribute, which javac reads
                    ClassReader cr = new ClassReader(b);
                    ClassWriter cw = new ClassWriter(0);
                    cr.accept(new Widen(cw, rules), 0);
                    b = cw.toByteArray();
                    changed++;
                }
                zo.putNextEntry(new ZipEntry(en.getName()));
                zo.write(b);
                zo.closeEntry();
            }
        }
        System.out.println("apply-aw: " + changed + " classes rewritten -> " + out);
    }

    /** AccessWidenerClassVisitor's effect on access flags */
    private static final class Widen extends ClassVisitor {
        private final AwRules rules;
        private String cls;
        private int classAccess;
        private final StringBuilder canonical = new StringBuilder("(");
        private boolean record;

        Widen(ClassVisitor cv, AwRules rules) {
            super(Opcodes.ASM9, cv);
            this.rules = rules;
        }

        @Override
        public void visit(int v, int acc, String name, String sig, String sup, String[] itf) {
            cls = name;
            classAccess = acc;
            record = (acc & Opcodes.ACC_RECORD) != 0;
            super.visit(v, rules.classAccess(name, acc), name, sig, sup, itf);
        }

        @Override
        public void visitPermittedSubclass(String sub) {
            if (!rules.unsealed(cls)) {
                super.visitPermittedSubclass(sub);
            }
        }

        @Override
        public void visitInnerClass(String name, String outer, String inner, int acc) {
            super.visitInnerClass(name, outer, inner, rules.classAccess(name, acc));
        }

        @Override
        public RecordComponentVisitor visitRecordComponent(String name, String desc, String sig) {
            canonical.append(desc);
            return super.visitRecordComponent(name, desc, sig);
        }

        @Override
        public FieldVisitor visitField(int acc, String name, String desc, String sig, Object val) {
            return super.visitField(rules.fieldAccess(cls, name, desc, acc, classAccess), name, desc, sig, val);
        }

        @Override
        public MethodVisitor visitMethod(int acc, String name, String desc, String sig, String[] ex) {
            // record components precede methods in the class file, so the canonical
            // descriptor is complete here (the loader defers the methods to be sure)
            if (record && name.equals("<init>") && desc.equals(canonical + ")V")) {
                acc = rules.canonicalConstructorAccess(cls, acc);
            }
            return super.visitMethod(rules.methodAccess(cls, name, desc, acc, classAccess), name, desc, sig, ex);
        }
    }

    private ApplyAw() {
    }
}

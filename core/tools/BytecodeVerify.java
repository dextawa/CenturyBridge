import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicInterpreter;
import org.objectweb.asm.tree.analysis.BasicValue;

/**
 * Dataflow-verify every class in every converted jar: the analyzer walks each
 * method's bytecode and fails on stack underflow/overflow against the declared
 * max_stack -- exactly the class of damage a rewrite that pushes an extra
 * value without recomputing maxs would cause, and exactly what "the converter
 * reported ok" does not prove.
 *
 *   java BytecodeVerify <dirOfJars>
 */
public final class BytecodeVerify {

    public static void main(String[] args) throws IOException {
        int jars = 0, classes = 0, bad = 0;
        try (var stream = Files.list(Path.of(args[0]))) {
            for (Path jar : (Iterable<Path>) stream.filter(p -> p.toString().endsWith(".jar"))::iterator) {
                jars++;
                bad += verifyJar(jar);
                classes += lastCount;
            }
        }
        System.out.println("verified " + classes + " classes in " + jars + " jars, "
            + bad + " methods FAILED dataflow verification");
        if (bad > 0) {
            System.exit(1);
        }
    }

    static int lastCount;

    static int verifyJar(Path jar) throws IOException {
        int bad = 0;
        lastCount = 0;
        try (ZipInputStream zin = new ZipInputStream(Files.newInputStream(jar))) {
            ZipEntry e;
            while ((e = zin.getNextEntry()) != null) {
                if (!e.getName().endsWith(".class")) {
                    continue;
                }
                byte[] bytes = zin.readAllBytes();
                lastCount++;
                ClassNode cn = new ClassNode();
                try {
                    new ClassReader(bytes).accept(cn, ClassReader.SKIP_DEBUG);
                } catch (Exception ex) {
                    System.out.println("UNREADABLE " + jar.getFileName() + " " + e.getName() + ": " + ex);
                    bad++;
                    continue;
                }
                for (MethodNode mn : cn.methods) {
                    if (mn.instructions.size() == 0) {
                        continue;
                    }
                    try {
                        Analyzer<BasicValue> az = new Analyzer<>(new BasicInterpreter());
                        az.analyze(cn.name, mn);
                        // the analyzer tracks real depth; compare to declared max
                        int max = 0;
                        for (var f : az.getFrames()) {
                            if (f != null) {
                                int d = 0;
                                for (int i = 0; i < f.getStackSize(); i++) {
                                    d += f.getStack(i).getSize();
                                }
                                max = Math.max(max, d);
                            }
                        }
                        if (max > mn.maxStack) {
                            System.out.println("MAXSTACK " + jar.getFileName() + " "
                                + cn.name + "." + mn.name + mn.desc
                                + " needs " + max + " declared " + mn.maxStack);
                            bad++;
                        }
                    } catch (AnalyzerException ex) {
                        System.out.println("DATAFLOW " + jar.getFileName() + " "
                            + cn.name + "." + mn.name + ": " + ex.getMessage());
                        bad++;
                    }
                }
            }
        }
        return bad;
    }

    private BytecodeVerify() {
    }
}

package cb.slib;

import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.ModInitializer;
import org.joml.Matrix3x2f;
import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Vector2d;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.joml.Vector4i;
import org.joml.Vector4ic;
import oshi.hardware.PhysicalMemory;

/**
 * A 1.21.3-era mod (compiled against JOML 1.10.5, OSHI 6.4.10, fastutil 8.5.12, log4j-core 2.22.1) for slib's library bridges
 * of the 1.21.3 -> 1.21.4 step. Each check prints "CB-SLIB ok|FAIL ..."; the last line is "CB-SLIB DONE passed/checks".
 */
public class SlibLibs214Test implements ModInitializer {
    static final List<String> FAILS = new ArrayList<>();
    static int checks;

    static void check(boolean ok, String what) {
        checks++;
        System.out.println("CB-SLIB " + (ok ? "ok   " : "FAIL ") + what);
        if (!ok) FAILS.add(what);
    }

    static void run(String what, Runnable r) {
        try {
            r.run();
        } catch (Throwable e) {
            check(false, what + " threw " + e);
            e.printStackTrace(System.out);
        }
    }

    /** a mod subclass of Vector4i that answers dot in its own way */
    static class MyVec extends Vector4i {
        MyVec(int x, int y, int z, int w) {
            super(x, y, z, w);
        }

        @Override
        public int dot(Vector4ic v) {
            return super.dot(v) + 1000;
        }
    }

    /** a mod's own read-only vector (JOML 1.10.5's Vector3fc had no mulPositionTranslation) */
    static Vector3fc ownVector() {
        return new Vector3f(1, 2, 3) {
        };
    }

    /** a mod subclass of Matrix4f overriding set3x3(Matrix4f) */
    static class MyMatrix extends Matrix4f {
        int calls;

        @Override
        public Matrix4f set3x3(Matrix4f mat) {
            calls++;
            return super.set3x3(mat);
        }
    }

    @Override
    public void onInitialize() {
        run("JOML widened parameters", () -> {
            Vector3f out = new Matrix3x2f().translate(5, 6).transform(new Vector3f(1, 2, 1), new Vector3f());
            check(out.x == 6 && out.y == 8 && out.z == 1, "Matrix3x2f.transform(Vector3f, Vector3f)");
            Matrix4d d = new Matrix4d().mul(new Matrix4f().scaling(2));
            check(d.m00() == 2 && d.m11() == 2, "Matrix4d.mul(Matrix4f)");
            Matrix4f m = new Matrix4f().set3x3(new Matrix4f().scaling(3));
            check(m.m00() == 3 && m.m33() == 1, "Matrix4f.set3x3(Matrix4f)");
            check(new Vector2d(6, 8).div(new Vector2d(2, 4)).equals(new Vector2d(3, 2)), "Vector2d.div(Vector2d)");
            check(new Vector3d(6, 8, 10).div(new Vector3d(2, 4, 5)).equals(new Vector3d(3, 2, 2)), "Vector3d.div(Vector3d)");
        });
        run("JOML int answers", () -> {
            Vector4i a = new Vector4i(1, 2, 3, 4);
            check(a.dot(new Vector4i(2, 2, 2, 2)) == 20, "Vector4i.dot answers int");
            check(a.distanceSquared(1, 2, 3, 6) == 4, "Vector4i.distanceSquared(int x4)");
            Vector4i big = new Vector4i(50000, 0, 0, 0);
            check(big.dot(big) == 50000 * 50000, "an overflowing dot wraps as 1.10.5's int arithmetic did");
            Vector4ic own = new MyVec(1, 0, 0, 0);
            check(own.dot(new Vector4i(1, 0, 0, 0)) == 1001, "a subclass's int dot override answers through the interface");
        });
        run("JOML rotateAbout", () -> {
            Vector4f v = new Vector4f(1, 0, 0, 1).rotateAbout((float) Math.PI / 2, 0, 0, 1);
            check(Math.abs(v.x) < 1e-6 && Math.abs(v.y - 1) < 1e-6, "Vector4f.rotateAbout rotates about the axis");
        });
        run("JOML overrides and new interface methods", () -> {
            MyMatrix my = new MyMatrix();
            try {
                Matrix4f.class.getMethod("set3x3", org.joml.Matrix4fc.class).invoke(my, new Matrix4f().scaling(2));
                check(my.calls == 1 && my.m00() == 2, "JOML 1.10.8's set3x3(Matrix4fc) reaches the subclass's override");
            } catch (ReflectiveOperationException e) {
                check(false, "set3x3(Matrix4fc) " + e);
            }
            Vector3fc own = ownVector();
            try {
                Object r = Vector3fc.class.getMethod("mulPositionTranslation", org.joml.Matrix4fc.class, Vector3f.class)
                    .invoke(own, new Matrix4f().translation(1, 1, 1), new Vector3f());
                check(new Vector3f(2, 3, 4).equals(r), "a 1.10.5 Vector3fc answers 1.10.8's mulPositionTranslation");
            } catch (ReflectiveOperationException e) {
                check(false, "mulPositionTranslation " + e);
            }
        });
        run("fastutil", () -> {
            ObjectSet<String> s = ObjectSortedSets.emptySet();
            check(s.isEmpty(), "ObjectSortedSets.emptySet()");
        });
        run("OSHI", () -> {
            PhysicalMemory mem = new PhysicalMemory("BANK 0", 1024L * 1024 * 1024, 3_200_000_000L, "Acme", "DDR4");
            check("Acme".equals(mem.getManufacturer()) && mem.getCapacity() == 1024L * 1024 * 1024, "PhysicalMemory keeps its values");
            check(!mem.toString().contains("Part number") && mem.toString().startsWith("Bank label: BANK 0"), "PhysicalMemory prints what 6.4.10 printed");
            try {
                Object part = mem.getClass().getMethod("getPartNumber").invoke(mem);
                check("unknown".equals(part), "a 6.4.10 memory's part number is unknown");
            } catch (ReflectiveOperationException e) {
                check(false, "getPartNumber " + e);
            }
            check(oshi.util.FileUtil.readAllBytes("/proc/self/status").length > 0, "FileUtil.readAllBytes(file)");
        });
        run("log4j-core", () -> {
            check(new org.apache.logging.log4j.core.jackson.JsonConstants() != null, "new JsonConstants()");
        });
        System.out.println("CB-SLIB DONE " + (checks - FAILS.size()) + "/" + checks);
    }
}

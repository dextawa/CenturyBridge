package cb.recordtest;

import net.fabricmc.api.ClientModInitializer;
import com.google.common.collect.ImmutableMap;
import net.minecraft.class_1535;
import net.minecraft.class_290;
import net.minecraft.class_293;
import net.minecraft.class_296;
import net.minecraft.class_783;

/** 1.20.6 source: subclasses of the three classes that are records on the target. */
public final class RecordTestMod implements ClientModInitializer {
    private static int ok;
    private static int fail;

    private static final class Painting extends class_1535 {
        Painting() {
            super(17, 33);
        }

        @Override
        public int method_6945() {
            return 112;
        }

        @Override
        public int method_6943() {
            return 128;
        }

        @Override
        public String toString() {
            return "painting-custom";
        }

        @Override
        public boolean equals(Object other) {
            return other == this;
        }

        @Override
        public int hashCode() {
            return 17;
        }

        int superWidth() {
            return super.method_6945();
        }

        int superHeight() {
            return super.method_6943();
        }
    }

    private static final class Face extends class_783 {
        Face() {
            super(null, 7, "cb", null);
        }

        @Override
        public String toString() {
            return "face-custom";
        }

        @Override
        public boolean equals(Object other) {
            return other == this;
        }

        @Override
        public int hashCode() {
            return 19;
        }
    }

    private static final class Element extends class_296 {
        private int clears;

        Element() {
            super(3, class_290.field_1587.method_1386(), class_290.field_1587.method_1382(), 3);
        }

        @Override
        public void method_22653(int elementIndex) {
            clears++;
        }

        int clears() {
            return clears;
        }

        @Override
        public boolean equals(Object other) {
            return other == this;
        }

        @Override
        public int hashCode() {
            return 73;
        }

        int superHash() {
            return super.hashCode();
        }
    }

    private static void check(boolean condition, String name, String detail) {
        if (condition) {
            ok++;
        } else {
            fail++;
        }
        System.out.println("CB-RECORDTEST " + (condition ? "ok" : "FAIL") + " " + name + " " + detail);
    }

    @Override
    public void onInitializeClient() {
        try {
            Painting painting = new Painting();
            check(painting.method_6945() == 112 && painting.method_6943() == 128,
                "painting-overrides", painting.method_6945() + "/" + painting.method_6943());
            check(painting.superWidth() == 16 && painting.superHeight() == 32,
                "painting-super-getters", painting.superWidth() + "/" + painting.superHeight());
            check("painting-custom".equals(painting.toString()) && painting.hashCode() == 17 && painting.equals(painting),
                "painting-object-methods", "toString/equals/hashCode");
        } catch (Throwable t) {
            check(false, "painting-load", t.toString());
            t.printStackTrace();
        }
        try {
            Face face = new Face();
            check(face.field_4226 == 7 && "cb".equals(face.field_4224),
                "face-native-record", face.field_4226 + "/" + face.field_4224);
            check("face-custom".equals(face.toString()) && face.hashCode() == 19 && face.equals(face),
                "face-object-methods", "toString/equals/hashCode");
        } catch (Throwable t) {
            check(false, "face-load", t.toString());
            t.printStackTrace();
        }
        try {
            Element element = new Element();
            check(element.method_1385() == 3 && element.method_34451() == 3,
                "element-shape", element.method_1385() + "/" + element.method_34451());
            check(element.method_1386() == class_290.field_1587.method_1386()
                    && element.method_1382() == class_290.field_1587.method_1382(), "element-type-usage", "same");
            check(element.hashCode() == 73 && element.equals(element) && !element.equals(null),
                "element-overrides", "equals/hashCode");
            element.superHash();
            check(true, "element-super-hash", "returned");
            class_293 format = new class_293(ImmutableMap.of("cb", element));
            check(format.method_1362() == 12, "element-native-format", "vertex bytes=" + format.method_1362());
            format.method_22651();
            check(element.clears() == 1, "element-format-clear", "override calls=" + element.clears());
        } catch (Throwable t) {
            check(false, "element-load", t.toString());
            t.printStackTrace();
        }
        System.out.println("CB-RECORDTEST summary ok=" + ok + " fail=" + fail);
    }
}

package cb.s9r;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.class_1058;
import net.minecraft.class_1059;
import net.minecraft.class_1299;
import net.minecraft.class_2398;
import net.minecraft.class_2400;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_437;
import net.minecraft.class_4604;
import net.minecraft.class_5604;
import net.minecraft.class_5606;
import net.minecraft.class_572;
import net.minecraft.class_630;
import net.minecraft.class_638;
import net.minecraft.class_687;
import net.minecraft.class_702;
import net.minecraft.class_703;
import net.minecraft.class_707;
import net.minecraft.class_719;
import net.minecraft.class_761;
import net.minecraft.class_897;
import net.minecraft.class_922;
import net.minecraft.class_970;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * 1.21.8 client members this mod reaches through its own access widener (cb_s9r.accesswidener) that 1.21.9 moved,
 * retyped or dropped (damage-aw rows of the 1.21.8 -> 1.21.9 step): ParticleEngine's sprite sets / atlas / register
 * methods and MutableSpriteSet, package-private particle constructors, HumanoidArmorLayer inner / outer models,
 * CubeDefinition / Polygon / Vertex vectors, LevelRenderer.cullingFrustum, KeyboardHandler / MouseHandler private
 * handlers. Run on the render thread by the native driver (reflection); each entry is {pass, detail}.
 */
public final class AwCheck {
    private AwCheck() {
    }

    static void put(Map<String, Object[]> out, String name, boolean pass, String detail) {
        out.put(name, new Object[]{pass, detail});
    }

    /** a 1.21.8 particle provider that answers no particle and counts the calls */
    static final class CountingProvider implements class_707<class_2400> {
        int calls;

        @Override
        public class_703 method_3090(class_2400 type, class_638 level, double x, double y, double z, double dx, double dy, double dz) {
            calls++;
            return null;
        }
    }

    /** ParticleEngine sprite sets, atlas, particle constructors, register(type, provider / registration) */
    public static Map<String, Object[]> particles() {
        Map<String, Object[]> out = new LinkedHashMap<>();
        class_310 mc = class_310.method_1551();
        class_702 pe = mc.field_1713;
        Map<class_2960, class_702.class_4090> sets = pe.field_18300;
        class_702.class_4090 smoke = sets.get(class_2960.method_60656("smoke"));
        List<class_1058> sprites = smoke == null ? null : smoke.field_18303;
        put(out, "I.spriteSets", smoke != null && sprites != null && !sprites.isEmpty() && smoke.method_18138(0, 1) == sprites.get(0),
            "spriteSets has " + sets.size() + " sets; smoke's MutableSpriteSet.sprites " + (sprites == null ? null : sprites.size())
                + ", get(0, 1) is its first sprite");
        class_1059 atlas = pe.field_18301;
        put(out, "I.atlas", atlas != null && atlas.method_24106().equals(class_1059.field_17898)
                && smoke != null && atlas.method_4608(smoke.method_18138(0, 1).method_45851().method_45816()) == smoke.method_18138(0, 1),
            "ParticleEngine.textureAtlas is " + (atlas == null ? null : atlas.method_24106()) + " and holds smoke's sprite");

        class_638 level = mc.field_1687;
        class_719 rain = new class_719(level, 1.5, 80.0, -2.5, 0.0, 0.0, 0.0);
        class_687 bubble = new class_687(level, -4.0, 70.0, 6.0, 0.0, 0.0, 0.0);
        put(out, "I.ctors", rain.field_3874 == 1.5 && bubble.field_3874 == -4.0,
            "package-private new DripParticle-family (class_719) / class_687 (level, x, y, z, ..) construct at x = " + rain.field_3874 + ", " + bubble.field_3874);

        CountingProvider p = new CountingProvider();
        pe.method_3043(class_2398.field_11251, p);
        pe.method_3056(class_2398.field_11251, 0.0, 80.0, 0.0, 0.0, 0.0, 0.0);
        put(out, "I.register", p.calls == 1, "after register(SMOKE, provider), createParticle(SMOKE, ..) asked the 1.21.8 provider " + p.calls + "x");

        CountingProvider q = new CountingProvider();
        Object[] handed = {null};
        pe.method_18834(class_2398.field_17430, (class_702.class_4091<class_2400>) s -> {
            handed[0] = s;
            return q;
        });
        pe.method_3056(class_2398.field_17430, 0.0, 80.0, 0.0, 0.0, 0.0, 0.0);
        put(out, "I.registration", q.calls == 1 && handed[0] != null && handed[0] == sets.get(class_2960.method_60656("campfire_cosy_smoke")),
            "register(CAMPFIRE_COSY_SMOKE, registration): create got the set kept in spriteSets (" + (handed[0] != null) + "), its provider asked " + q.calls + "x");
        return out;
    }

    /** HumanoidArmorLayer inner / outer models of the armor stand renderer, read and written */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Map<String, Object[]> armor() {
        Map<String, Object[]> out = new LinkedHashMap<>();
        class_310 mc = class_310.method_1551();
        class_897 r = mc.method_1561().field_4696.get(class_1299.field_6131);
        class_970 layer = null;
        for (Object l : ((class_922) r).field_4738) {
            if (l instanceof class_970 a) {
                layer = a;
            }
        }
        if (layer == null) {
            put(out, "J.layer", false, "armor stand renderer has no HumanoidArmorLayer");
            return out;
        }
        class_572 inner = layer.field_4830, outer = layer.field_4831;
        Map<String, java.util.Set<Vector3f>>[] baked = ArmorCheck.bake().get("armor_stand");
        Map<String, java.util.Set<Vector3f>> in = ArmorCheck.parts(inner.method_63512()), ou = ArmorCheck.parts(outer.method_63512());
        put(out, "J.read", inner != outer && in.get("body").equals(baked[0].get("body")) && in.get("right_leg").equals(baked[0].get("right_leg"))
                && ou.get("head").equals(baked[1].get("head")),
            "innerModel body/legs = INNER_ARMOR's, outerModel head = OUTER_ARMOR's (vertex counts " + in.get("body").size() + " / " + ou.get("head").size() + ")");
        layer.field_4830 = outer;
        boolean swapped = layer.field_4830 == outer && layer.field_4831 == outer;
        layer.field_4830 = inner;
        put(out, "J.write", swapped && layer.field_4830 == inner && layer.field_4831 == outer,
            "innerModel = outer reads back (" + swapped + "), outerModel unchanged, restore reads back inner");
        return out;
    }

    /** CubeDefinition origin / dimensions, ModelPart.Polygon normal, ModelPart.Vertex pos and its (pos, u, v) constructor */
    public static Map<String, Object[]> vectors() {
        Map<String, Object[]> out = new LinkedHashMap<>();
        class_5604 def = class_5606.method_32108().method_32101(0, 0).method_32097(1, 2, 3, 4, 5, 6).method_32107().get(0);
        Vector3f o = def.field_27709, d = def.field_27710;
        put(out, "K.cubeDef", o.equals(new Vector3f(1, 2, 3)) && d.equals(new Vector3f(4, 5, 6)), "CubeDefinition origin " + o + " dimensions " + d);
        class_630.class_628 cube = def.method_32093(64, 64);
        boolean inside = true, agree = true, unit = true;
        int n = 0;
        for (class_630.class_593 poly : cube.field_3649) {
            Vector3f nf = poly.comp_3185;
            agree &= nf.equals(poly.comp_3185());
            unit &= Math.abs(nf.length() - 1.0F) < 1e-5F && (Math.abs(nf.x) + Math.abs(nf.y) + Math.abs(nf.z)) == 1.0F;
            for (class_630.class_618 v : poly.comp_3184()) {
                Vector3f p = v.comp_3186;
                agree &= p.equals(v.comp_3186());
                inside &= p.x >= 1 && p.x <= 5 && p.y >= 2 && p.y <= 7 && p.z >= 3 && p.z <= 9;
                n++;
            }
        }
        put(out, "K.polygons", n == 24 && inside && agree && unit,
            n + " vertices inside the cube (" + inside + "), field and accessor agree (" + agree + "), normals axis unit vectors (" + unit + ")");
        class_630.class_618 v = new class_630.class_618(new Vector3f(7, 8, 9), 0.25F, 0.5F);
        put(out, "K.vertex", v.comp_3186().equals(new Vector3f(7, 8, 9)) && v.comp_3187() == 0.25F && v.comp_3188() == 0.5F,
            "new Vertex((7, 8, 9), 0.25, 0.5): pos " + v.comp_3186() + " u " + v.comp_3187() + " v " + v.comp_3188());
        return out;
    }

    /** LevelRenderer.cullingFrustum read (the native driver compares it with what 1.21.9's prepareCullFrustum returned) */
    public static Object readFrustum() {
        return class_310.method_1551().field_1769.field_27740;
    }

    /** LevelRenderer.cullingFrustum write: a new frustum the next frame must cull with */
    public static Object writeFrustum() {
        class_761 lr = class_310.method_1551().field_1769;
        class_4604 f = new class_4604(new Matrix4f(), new Matrix4f());
        f.method_23088(0.0, 0.0, 0.0);
        lr.field_27740 = f;
        return f;
    }

    /** a 1.21.8 screen recording what the input handlers hand it */
    public static final class InputScreen extends class_437 {
        public final StringBuilder typed = new StringBuilder();
        public int clicks, lastButton = -1, releases;

        public InputScreen() {
            super(class_2561.method_43470("cb-s9r input"));
        }

        @Override
        public boolean method_25400(char c, int mods) {
            typed.append(c);
            return true;
        }

        @Override
        public boolean method_25402(double x, double y, int button) {
            clicks++;
            lastButton = button;
            return true;
        }

        @Override
        public boolean method_25406(double x, double y, int button) {
            releases++;
            return true;
        }
    }

    public static Object newInputScreen() {
        return new InputScreen();
    }

    /** KeyboardHandler.charTyped / handleDebugKeys and MouseHandler.onPress, called with the screen up */
    public static Map<String, Object[]> input(Object screenObj) {
        Map<String, Object[]> out = new LinkedHashMap<>();
        InputScreen screen = (InputScreen) screenObj;
        class_310 mc = class_310.method_1551();
        long window = mc.method_22683().method_4490();
        mc.field_1774.method_1457(window, 'q', 0);
        mc.field_1774.method_1457(window, 0x1F600, 0);
        put(out, "L.charTyped", screen.typed.toString().equals("q😀"),
            "KeyboardHandler.charTyped(window, 'q' then U+1F600, 0) reached the 1.21.8 screen's charTyped as \"" + screen.typed + "\"");
        boolean before = mc.field_1690.field_1827;
        boolean handled = mc.field_1774.method_1468(72);
        boolean flipped = mc.field_1690.field_1827 != before;
        mc.field_1774.method_1468(72);
        put(out, "L.debugKeys", handled && flipped && mc.field_1690.field_1827 == before,
            "handleDebugKeys(H) answered " + handled + " and flipped advanced tooltips (" + flipped + "), a second call restored them");
        mc.field_1729.method_1601(window, 1, 1, 0);
        mc.field_1729.method_1601(window, 1, 0, 0);
        put(out, "L.onPress", screen.clicks == 1 && screen.lastButton == 1 && screen.releases == 1,
            "MouseHandler.onPress(window, right, press / release, 0): screen mouseClicked " + screen.clicks + "x button " + screen.lastButton
                + ", mouseReleased " + screen.releases + "x");
        return out;
    }
}

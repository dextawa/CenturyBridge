package cb.s9;

import com.mojang.brigadier.StringReader;
import com.mojang.serialization.Dynamic;
import net.minecraft.class_2152;
import net.minecraft.class_2303;
import net.minecraft.class_2487;
import net.minecraft.class_2509;
import net.minecraft.class_2780;
import net.minecraft.class_2784;
import net.minecraft.server.MinecraftServer;

/** 1.21.8 WorldBorder.Settings / DelegateBorderChangeListener / WrappedMinMaxBounds use by 1.21.8 code (s9d) */
final class BorderSelectorTest {

    private BorderSelectorTest() {
    }

    static void run(MinecraftServer server) {
        S9TestMod.run("WorldBorder.Settings: accessors, DEFAULT_SETTINGS, level.dat tags, createSettings", () -> {
            class_2784.class_5200 d = class_2784.field_24122;
            S9TestMod.check(d.method_27361() == 5.0 && d.method_27360() == 0.2 && d.method_27364() == 5.9999968E7,
                "DEFAULT_SETTINGS: safe zone 5, damage 0.2, size 5.9999968E7, got " + d.method_27361() + " " + d.method_27360() + " " + d.method_27364());
            class_2784 border = new class_2784();
            border.method_11978(12.5, -40.0);
            border.method_11969(300.0);
            border.method_11967(7);
            class_2784.class_5200 s = border.method_27355();
            S9TestMod.check(s.method_27356() == 12.5 && s.method_27359() == -40.0 && s.method_27364() == 300.0 && s.method_27362() == 7,
                "createSettings() carries centre / size / warning blocks, got " + s.method_27356() + " " + s.method_27359() + " " + s.method_27364() + " " + s.method_27362());
            class_2487 tag = new class_2487();
            s.method_27357(tag);
            S9TestMod.check(tag.method_10545("BorderCenterX") && tag.method_10545("BorderWarningTime") && tag.method_10545("BorderSizeLerpTarget"),
                "write(tag) writes 1.21.8's level.dat keys, got " + tag);
            class_2784.class_5200 back = class_2784.class_5200.method_27358(new Dynamic<>(class_2509.field_11560, tag), d);
            S9TestMod.check(back.method_27356() == 12.5 && back.method_27359() == -40.0 && back.method_27364() == 300.0
                && back.method_27362() == 7 && back.method_27361() == d.method_27361(),
                "read(dynamic, defaults) reads them back, got " + back.method_27356() + " " + back.method_27364() + " " + back.method_27362());
            java.util.List<class_2784.class_10763> sides = border.method_67544(12.5, -40.0 + 100.0);
            S9TestMod.check(sides.size() == 4 && sides.get(0).comp_3672() == 50.0,
                "closestBorder(x, z) sorts the four sides by distance (nearest 50), got " + sides);
        });
        S9TestMod.run("DelegateBorderChangeListener: repeats a border's changes on its delegate", () -> {
            class_2784 source = new class_2784();
            class_2784 delegate = new class_2784();
            class_2780 listener = new class_2780.class_3976(delegate);
            source.method_11983(listener);
            source.method_11978(64.0, 32.0);
            source.method_11969(500.0);
            source.method_11975(9);
            S9TestMod.check(delegate.method_11964() == 64.0 && delegate.method_11980() == 32.0 && delegate.method_11965() == 500.0
                && delegate.method_11956() == 9, "the delegate follows centre / size / warning time, got "
                + delegate.method_11964() + " " + delegate.method_11980() + " " + delegate.method_11965() + " " + delegate.method_11956());
            source.method_35318(listener);
        });
        S9TestMod.run("WrappedMinMaxBounds and the selector's rotation bounds", () -> {
            class_2152 r;
            try {
                r = class_2152.method_9172(new StringReader("10..20"), true, f -> f);
            } catch (com.mojang.brigadier.exceptions.CommandSyntaxException e) {
                throw new IllegalStateException(e);
            }
            S9TestMod.check(r.comp_1840() == 10f && r.comp_1841() == 20f && r.method_35409(15f) && !r.method_35409(25f),
                "fromReader parses 10..20 and matches inside it, got " + r);
            S9TestMod.check(class_2152.method_35401(3f).method_35407().getAsFloat() == 3f, "exactly(3) serializes to 3");
            class_2303 parser = new class_2303(new StringReader("@e[x_rotation=10..20]"), true);
            parser.method_9898(class_2152.method_35402(-30f, 45f));
            class_2152 got = parser.method_9883();
            S9TestMod.check(got != null && got.comp_1840() == -30f && got.comp_1841() == 45f,
                "setRotX / getRotX keep the bounds, got " + got);
            parser.method_9855(class_2152.method_35406(5f));
            class_2152 y = parser.method_9853();
            S9TestMod.check(y != null && y.comp_1840() == 5f && y.comp_1841() == null, "setRotY(atLeast 5) / getRotY, got " + y);
        });
    }
}

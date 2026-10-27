package top.dext.centurybridge.step.v1_21_9.rt;

import com.mojang.serialization.DynamicLike;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import net.minecraft.class_2350;
import net.minecraft.class_2487;
import net.minecraft.class_2784;
import net.minecraft.class_3532;
import top.dext.centurybridge.step.v1_21_9.facade.DistancePerDirection;

/**
 * WorldBorder members 1.21.8 code reaches that 1.21.9 dropped or moved (javap -c both releases). 1.21.9 turned
 * WorldBorder.Settings into a record (its accessors renamed, wired as methodRenames), moved DEFAULT_SETTINGS onto it,
 * saves it through a codec instead of the level.dat tags, and moved the closest-border list onto the client's border
 * render state (class_12078; the DistancePerDirection record is restored as a facade). Bodies here are 1.21.8's.
 */
public final class CbWorldBorders {
    private CbWorldBorders() {
    }

    /** WorldBorder.DEFAULT_SETTINGS: 1.21.9's WorldBorder.Settings.DEFAULT, built from the same constants */
    public static final class_2784.class_5200 field_24122 = class_2784.class_5200.field_62538;

    /** WorldBorder.Settings.read(dynamic, defaults): the level.dat keys 1.21.8 read, the centre clamped */
    public static class_2784.class_5200 method_27358(DynamicLike<?> dynamic, class_2784.class_5200 defaults) {
        double centerX = class_3532.method_15350(dynamic.get("BorderCenterX").asDouble(defaults.comp_4774()), -2.9999984E7, 2.9999984E7);
        double centerZ = class_3532.method_15350(dynamic.get("BorderCenterZ").asDouble(defaults.comp_4775()), -2.9999984E7, 2.9999984E7);
        double size = dynamic.get("BorderSize").asDouble(defaults.comp_4780());
        long lerpTime = dynamic.get("BorderSizeLerpTime").asLong(defaults.comp_4781());
        double lerpTarget = dynamic.get("BorderSizeLerpTarget").asDouble(defaults.comp_4782());
        double safeZone = dynamic.get("BorderSafeZone").asDouble(defaults.comp_4777());
        double damagePerBlock = dynamic.get("BorderDamagePerBlock").asDouble(defaults.comp_4776());
        int warningBlocks = dynamic.get("BorderWarningBlocks").asInt(defaults.comp_4778());
        int warningTime = dynamic.get("BorderWarningTime").asInt(defaults.comp_4779());
        return new class_2784.class_5200(centerX, centerZ, damagePerBlock, safeZone, warningBlocks, warningTime, size, lerpTime, lerpTarget);
    }

    /** WorldBorder.Settings.write(tag): the level.dat keys 1.21.8 wrote (the warning values as doubles, as 1.21.8 did) */
    public static void method_27357(class_2784.class_5200 settings, class_2487 tag) {
        tag.method_10549("BorderCenterX", settings.comp_4774());
        tag.method_10549("BorderCenterZ", settings.comp_4775());
        tag.method_10549("BorderSize", settings.comp_4780());
        tag.method_10544("BorderSizeLerpTime", settings.comp_4781());
        tag.method_10549("BorderSafeZone", settings.comp_4777());
        tag.method_10549("BorderDamagePerBlock", settings.comp_4776());
        tag.method_10549("BorderSizeLerpTarget", settings.comp_4782());
        tag.method_10549("BorderWarningBlocks", settings.comp_4778());
        tag.method_10549("BorderWarningTime", settings.comp_4779());
    }

    /** WorldBorder.createSettings(): 1.21.9's Settings(border) constructor reads the same nine values */
    public static class_2784.class_5200 method_27355(class_2784 border) {
        return new class_2784.class_5200(border);
    }

    /** WorldBorder.closestBorder(x, z): the four sides by distance, as 1.21.8 computed them from the border */
    public static List<DistancePerDirection> method_67544(class_2784 border, double x, double z) {
        DistancePerDirection[] sides = {
                new DistancePerDirection(class_2350.field_11043, z - border.method_11958()),
                new DistancePerDirection(class_2350.field_11035, border.method_11977() - z),
                new DistancePerDirection(class_2350.field_11039, x - border.method_11976()),
                new DistancePerDirection(class_2350.field_11034, border.method_11963() - x),
        };
        return Arrays.stream(sides).sorted(Comparator.comparingDouble(DistancePerDirection::comp_3672)).toList();
    }
}

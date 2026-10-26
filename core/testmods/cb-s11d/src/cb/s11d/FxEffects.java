package cb.s11d;

import net.minecraft.class_243;
import net.minecraft.class_5294;

/**
 * A 1.21.10 mod's own DimensionSpecialEffects (END sky, constant ambient light, force-bright lightmap, foggy everywhere,
 * a fixed fog color, a sunrise at every time), counting each call so the fixture sees which 1.21.11 drawing points
 * consulted it.
 */
public class FxEffects extends class_5294 {
    public static volatile int fogColor, foggyAt, sunrise, sunriseColor, skyType, constantAmbient, forceBright;

    public FxEffects() {
        super(class_5294.class_5401.field_25641, true, true);
    }

    @Override
    public class_243 method_28112(class_243 fog, float brightness) {
        fogColor++;
        return new class_243(0.2, 0.4, 0.6);
    }

    @Override
    public boolean method_28110(int x, int z) {
        foggyAt++;
        return true;
    }

    @Override
    public boolean method_62183(float timeOfDay) {
        sunrise++;
        return true;
    }

    @Override
    public int method_62184(float timeOfDay) {
        sunriseColor++;
        return 0x80FF0000;
    }

    @Override
    public class_5294.class_5401 method_29992() {
        skyType++;
        return super.method_29992();
    }

    @Override
    public boolean method_29993() {
        constantAmbient++;
        return super.method_29993();
    }

    @Override
    public boolean method_73235() {
        forceBright++;
        return super.method_73235();
    }
}

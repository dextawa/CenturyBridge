package top.dext.centurybridge.step.v1_21_9.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2396;
import net.minecraft.class_2398;
import net.minecraft.class_2400;
import net.minecraft.class_310;
import net.minecraft.class_3940;
import net.minecraft.class_4002;
import net.minecraft.class_638;
import net.minecraft.class_663;
import net.minecraft.class_7923;

/**
 * DripParticle's 1.21.8 static factories (createWaterHangParticle ...). 1.21.9 turned each into a provider class
 * of its own (DripParticle$*Provider, holding the sprite set) with the same body plus the sprite pick (javap: same
 * particle class, fluid, next particle type, colour, lifetime and gravity). Each V factory is the provider 1.21.8
 * registered it for (ParticleEngine.registerProviders) and 1.21.9 registers for the same type
 * (ParticleResources.registerProviders), built over that type's loaded sprite set and called with the client
 * level's random. 1.21.8's factory left the sprite to its caller (ParticleEngine's sprite provider wrapper then
 * picked one from the caller's set, and still does: CbParticles); 1.21.9's picks one of the vanilla type's sprites
 * first, which such a caller replaces.
 */
@Environment(EnvType.CLIENT)
public final class CbDripParticles {
    private CbDripParticles() {
    }

    /** the sprite set ParticleResources loaded for a vanilla particle type */
    private static class_4002 sprites(class_2396<?> type) {
        return (class_4002) class_310.method_1551().field_62565.field_62627.get(class_7923.field_41180.method_10221(type));
    }

    public static class_3940 method_49316(class_2400 type, class_638 level, double x, double y, double z, double dx, double dy, double dz) {
        return (class_3940) new class_663.class_11929(sprites(class_2398.field_11232)).method_74273(type, level, x, y, z, dx, dy, dz, CbParticles.random(level));
    }

    public static class_3940 method_49317(class_2400 type, class_638 level, double x, double y, double z, double dx, double dy, double dz) {
        return (class_3940) new class_663.class_11928(sprites(class_2398.field_18306)).method_74272(type, level, x, y, z, dx, dy, dz, CbParticles.random(level));
    }

    public static class_3940 method_49318(class_2400 type, class_638 level, double x, double y, double z, double dx, double dy, double dz) {
        return (class_3940) new class_663.class_11921(sprites(class_2398.field_11223)).method_74265(type, level, x, y, z, dx, dy, dz, CbParticles.random(level));
    }

    public static class_3940 method_49319(class_2400 type, class_638 level, double x, double y, double z, double dx, double dy, double dz) {
        return (class_3940) new class_663.class_11920(sprites(class_2398.field_18304)).method_74264(type, level, x, y, z, dx, dy, dz, CbParticles.random(level));
    }

    public static class_3940 method_49320(class_2400 type, class_638 level, double x, double y, double z, double dx, double dy, double dz) {
        return (class_3940) new class_663.class_11922(sprites(class_2398.field_18305)).method_74266(type, level, x, y, z, dx, dy, dz, CbParticles.random(level));
    }

    public static class_3940 method_49324(class_2400 type, class_638 level, double x, double y, double z, double dx, double dy, double dz) {
        return (class_3940) new class_663.class_11918(sprites(class_2398.field_20534)).method_74262(type, level, x, y, z, dx, dy, dz, CbParticles.random(level));
    }

    public static class_3940 method_49325(class_2400 type, class_638 level, double x, double y, double z, double dx, double dy, double dz) {
        return (class_3940) new class_663.class_11917(sprites(class_2398.field_20535)).method_74261(type, level, x, y, z, dx, dy, dz, CbParticles.random(level));
    }

    public static class_3940 method_49326(class_2400 type, class_638 level, double x, double y, double z, double dx, double dy, double dz) {
        return (class_3940) new class_663.class_11919(sprites(class_2398.field_20536)).method_74263(type, level, x, y, z, dx, dy, dz, CbParticles.random(level));
    }

    public static class_3940 method_49327(class_2400 type, class_638 level, double x, double y, double z, double dx, double dy, double dz) {
        return (class_3940) new class_663.class_11916(sprites(class_2398.field_28078)).method_74260(type, level, x, y, z, dx, dy, dz, CbParticles.random(level));
    }

    public static class_3940 method_49328(class_2400 type, class_638 level, double x, double y, double z, double dx, double dy, double dz) {
        return (class_3940) new class_663.class_11915(sprites(class_2398.field_28079)).method_74259(type, level, x, y, z, dx, dy, dz, CbParticles.random(level));
    }

    public static class_3940 method_49329(class_2400 type, class_638 level, double x, double y, double z, double dx, double dy, double dz) {
        return (class_3940) new class_663.class_11914(sprites(class_2398.field_28076)).method_74258(type, level, x, y, z, dx, dy, dz, CbParticles.random(level));
    }

    public static class_3940 method_49330(class_2400 type, class_638 level, double x, double y, double z, double dx, double dy, double dz) {
        return (class_3940) new class_663.class_11913(sprites(class_2398.field_28077)).method_74257(type, level, x, y, z, dx, dy, dz, CbParticles.random(level));
    }

    public static class_3940 method_49331(class_2400 type, class_638 level, double x, double y, double z, double dx, double dy, double dz) {
        return (class_3940) new class_663.class_11923(sprites(class_2398.field_20537)).method_74267(type, level, x, y, z, dx, dy, dz, CbParticles.random(level));
    }

    public static class_3940 method_49332(class_2400 type, class_638 level, double x, double y, double z, double dx, double dy, double dz) {
        return (class_3940) new class_663.class_11927(sprites(class_2398.field_28802)).method_74271(type, level, x, y, z, dx, dy, dz, CbParticles.random(level));
    }

    public static class_3940 method_49333(class_2400 type, class_638 level, double x, double y, double z, double dx, double dy, double dz) {
        return (class_3940) new class_663.class_11925(sprites(class_2398.field_22446)).method_74269(type, level, x, y, z, dx, dy, dz, CbParticles.random(level));
    }

    public static class_3940 method_49334(class_2400 type, class_638 level, double x, double y, double z, double dx, double dy, double dz) {
        return (class_3940) new class_663.class_11924(sprites(class_2398.field_22447)).method_74268(type, level, x, y, z, dx, dy, dz, CbParticles.random(level));
    }

    public static class_3940 method_49335(class_2400 type, class_638 level, double x, double y, double z, double dx, double dy, double dz) {
        return (class_3940) new class_663.class_11926(sprites(class_2398.field_22448)).method_74270(type, level, x, y, z, dx, dy, dz, CbParticles.random(level));
    }
}

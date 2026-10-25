package top.dext.centurybridge.step.v1_21_9.rt;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1058;
import net.minecraft.class_1059;
import net.minecraft.class_1921;
import net.minecraft.class_310;
import net.minecraft.class_3940;
import net.minecraft.class_3999;
import net.minecraft.class_4002;
import net.minecraft.class_4184;
import net.minecraft.class_4588;
import net.minecraft.class_4668;
import net.minecraft.class_5819;
import net.minecraft.class_638;
import net.minecraft.class_703;
import net.minecraft.class_10799;
import net.minecraft.class_11944;
import net.minecraft.class_2960;
import org.joml.Quaternionf;

/**
 * 1.21.8's particle API on 1.21.9's particle groups.
 *
 * 1.21.8: a particle answered getRenderType() -- a sheet (terrain, opaque, translucent: a RenderType over an atlas),
 * CUSTOM (renderCustom into the buffers) or NO_RENDER -- and render(VertexConsumer, camera, partialTick) drew it into
 * that sheet's buffer; TextureSheetParticle held the sprite; Particle held colour, alpha and roll.
 * 1.21.9: a particle answers getGroup(); SingleQuadParticle (which absorbed TextureSheetParticle, its sprite, colour,
 * alpha and roll) answers getLayer() and extracts quads into a QuadParticleRenderState; ParticleEngine keeps one
 * ParticleGroup per group.
 *
 * Bridged:
 *  - 1.21.8's sheet types are ParticleRenderType records of their own (TERRAIN_SHEET, PARTICLE_SHEET_OPAQUE,
 *    PARTICLE_SHEET_TRANSLUCENT, CUSTOM; NO_RENDER is 1.21.9's), each with the RenderType 1.21.8 drew it with; a sheet
 *    type stands for the 1.21.9 layer of the same atlas and pipeline.
 *  - a 1.21.8 SingleQuadParticle answering a sheet type and drawing the 1.21.8 way only through vanilla's quad code is a
 *    1.21.9 single-quad particle of that layer (cbGroup / cbLayer, synthesized by adapterHooks);
 *  - every other 1.21.8 particle -- one that draws itself (its own render / renderRotatedQuad / renderCustom), extends
 *    Particle directly, or answers CUSTOM or a mod's own type -- goes to a legacy group keyed by its 1.21.8 type
 *    (CbParticleGroup), which draws it 1.21.8's way into that type's buffer: render(vc, camera, partialTick), or
 *    renderCustom for CUSTOM. CbParticleEngineMixin creates and extracts those groups; 1.21.9's own groups are untouched.
 */
@Environment(EnvType.CLIENT)
public final class CbParticles {

    private CbParticles() {
    }

    // ------------------------------------------------------------------ RandomSource (createParticle)

    /** a provider's random: 1.21.8's providers drew from the level's */
    public static class_5819 random(class_638 level) {
        return level.method_8409();
    }

    /** a mod's own createParticle call (no frame): the client level's random, as 1.21.8's providers used */
    public static class_5819 frameRandom() {
        class_638 level = class_310.method_1551().field_1687;
        return level != null ? level.method_8409() : class_5819.method_43053();
    }

    // ------------------------------------------------------------------ providers (createParticle gained the RandomSource)

    /** ParticleProvider.Sprite.createParticle(..) for 1.21.8 code: 1.21.9's method_74291 with the level's random */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static class_3940 createParticle(net.minecraft.class_707.class_8187 provider, net.minecraft.class_2394 options, class_638 level,
            double x, double y, double z, double dx, double dy, double dz) {
        return provider.method_74291(options, level, x, y, z, dx, dy, dz, random(level));
    }

    /** a 1.21.8 lambda / method reference of ParticleProvider (lambdaAdapters) */
    public interface LegacyProvider {
        class_703 method_3090(net.minecraft.class_2394 options, class_638 level, double x, double y, double z, double dx, double dy, double dz);
    }

    /** 1.21.9's provider over it: the random is dropped (1.21.8's providers drew from the level) */
    @SuppressWarnings("rawtypes")
    public static net.minecraft.class_707 adaptProvider(LegacyProvider fn) {
        return (options, level, x, y, z, dx, dy, dz, random) -> fn.method_3090(options, level, x, y, z, dx, dy, dz);
    }

    /** a 1.21.8 lambda / method reference of ParticleProvider.Sprite (lambdaAdapters) */
    public interface LegacySprite {
        class_3940 createParticle(net.minecraft.class_2394 options, class_638 level, double x, double y, double z, double dx, double dy, double dz);
    }

    @SuppressWarnings("rawtypes")
    public static net.minecraft.class_707.class_8187 adaptSprite(LegacySprite fn) {
        return (options, level, x, y, z, dx, dy, dz, random) -> fn.createParticle(options, level, x, y, z, dx, dy, dz);
    }

    // ------------------------------------------------------------------ constructors that take the sprite now

    /** a particle's own random as 1.21.8 drew from it before 1.21.9 took the sprite at construction (a fresh one, as Particle.random) */
    private static class_5819 particleRandom() {
        return class_5819.method_43047();
    }

    /** new FallingLeavesParticle(.., sprites, ..): 1.21.8's body picked sprites.get(random.nextInt(12), 12) */
    public static class_1058 leafSprite(class_4002 sprites) {
        return sprites.method_18138(particleRandom().method_43048(12), 12);
    }

    /** new BreakingItemParticle(.., itemState): 1.21.8's body took the state's particle icon, else the blocks atlas's missing sprite (1.21.9's providers the same) */
    public static class_1058 itemSprite(net.minecraft.class_10444 state) {
        class_1058 sprite = state.method_65603(particleRandom());
        return sprite != null ? sprite
            : class_310.method_1551().method_72703().method_73025(net.minecraft.class_10725.field_56382).method_73023();
    }

    /** new ItemPickupParticle(dispatcher, level, item, target): 1.21.9 takes the item's render state and motion (ClientPacketListener's way) */
    public static net.minecraft.class_693 createPickup(net.minecraft.class_898 dispatcher, class_638 level, net.minecraft.class_1297 item,
            net.minecraft.class_1297 target) {
        return new net.minecraft.class_693(level, pickupState(dispatcher, item), target, motion(item));
    }

    public static net.minecraft.class_10017 pickupState(net.minecraft.class_898 dispatcher, net.minecraft.class_1297 item) {
        return dispatcher.method_72977(item, 1.0F);
    }

    public static net.minecraft.class_243 motion(net.minecraft.class_1297 item) {
        return item.method_18798();
    }

    /** a 1.21.8 item particle provider overriding calculateState (1.21.9 computes the sprite in method_74256) */
    public interface LegacyItemState {
        net.minecraft.class_10444 method_65524(net.minecraft.class_1799 stack, class_638 level);
    }

    /** BreakingItemParticle.ItemParticleProvider.calculateState(stack, level) for 1.21.8 code: the provider's own, else 1.21.8's body */
    @SuppressWarnings("rawtypes")
    public static net.minecraft.class_10444 method_65524(net.minecraft.class_647.class_10417 provider, net.minecraft.class_1799 stack, class_638 level) {
        if (provider instanceof LegacyItemState own) {
            return own.method_65524(stack, level);
        }
        return CbParticleBaseSupers.method_65524(provider, stack, level);
    }

    /** SpriteSet.first() that a 1.21.8 sprite set lacks: its sprite for age 0 (1.21.9's sets answer their first sprite, which is that one) */
    public static class_1058 first(class_4002 sprites) {
        return sprites.method_18138(0, 1);
    }

    // ------------------------------------------------------------------ 1.21.8's ParticleRenderTypes

    public static final class_3999 TERRAIN_SHEET = new class_3999("TERRAIN_SHEET");
    public static final class_3999 PARTICLE_SHEET_OPAQUE = new class_3999("PARTICLE_SHEET_OPAQUE");
    public static final class_3999 PARTICLE_SHEET_TRANSLUCENT = new class_3999("PARTICLE_SHEET_TRANSLUCENT");
    public static final class_3999 CUSTOM = new class_3999("CUSTOM");

    private static final Map<class_3999, class_1921> RENDER_TYPES = new ConcurrentHashMap<>();

    public static class_3999 field_17827() {
        return TERRAIN_SHEET;
    }

    public static class_3999 field_17828() {
        return PARTICLE_SHEET_OPAQUE;
    }

    public static class_3999 field_17829() {
        return PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class_3999 field_17831() {
        return CUSTOM;
    }

    /** new ParticleRenderType(name, renderType): the record 1.21.9 keeps (its name), the RenderType kept beside it */
    public static class_3999 renderType(String name, class_1921 renderType) {
        class_3999 type = new class_3999(name);
        if (renderType != null) {
            RENDER_TYPES.put(type, renderType);
        }
        return type;
    }

    /** ParticleRenderType.renderType(): what 1.21.8's type drew with (null for CUSTOM / NO_RENDER, as on 1.21.8) */
    public static class_1921 comp_3341(class_3999 type) {
        class_1921 rt = RENDER_TYPES.get(type);
        if (rt != null) {
            return rt;
        }
        if (type.equals(TERRAIN_SHEET)) {
            return sheet(TERRAIN_SHEET, true, class_1059.field_5275);
        }
        if (type.equals(PARTICLE_SHEET_OPAQUE)) {
            return sheet(PARTICLE_SHEET_OPAQUE, false, class_1059.field_17898);
        }
        if (type.equals(PARTICLE_SHEET_TRANSLUCENT)) {
            return sheet(PARTICLE_SHEET_TRANSLUCENT, true, class_1059.field_17898);
        }
        return null;
    }

    /**
     * 1.21.8's RenderType.translucentParticle / opaqueParticle(atlas): the particle pipeline over the atlas with the
     * lightmap (1.21.9 keeps both pipelines; its particles target is gone, so the main target)
     */
    private static class_1921 sheet(class_3999 type, boolean translucent, class_2960 atlas) {
        return RENDER_TYPES.computeIfAbsent(type, t -> class_1921.method_24049(translucent ? "translucent_particle" : "opaque_particle",
            1536, false, false, translucent ? class_10799.field_56871 : class_10799.field_56870,
            class_1921.class_4688.method_23598()
                .method_34577(new class_4668.class_4683(atlas, false))
                .method_23608(class_4668.field_21383)
                .method_23617(false)));
    }

    /** the 1.21.9 layer of a 1.21.8 sheet type (same atlas and pipeline), or null for a type without one */
    public static class_3940.class_11941 layer(class_3999 type) {
        if (type.equals(TERRAIN_SHEET)) {
            return class_3940.class_11941.field_62639;
        }
        if (type.equals(PARTICLE_SHEET_OPAQUE)) {
            return class_3940.class_11941.field_62640;
        }
        if (type.equals(PARTICLE_SHEET_TRANSLUCENT)) {
            return class_3940.class_11941.field_62641;
        }
        return null;
    }

    /** the 1.21.8 sheet type of a 1.21.9 layer */
    public static class_3999 sheetOf(class_3940.class_11941 layer) {
        if (layer == class_3940.class_11941.field_62639) {
            return TERRAIN_SHEET;
        }
        if (layer == class_3940.class_11941.field_62640) {
            return PARTICLE_SHEET_OPAQUE;
        }
        if (layer == class_3940.class_11941.field_62641) {
            return PARTICLE_SHEET_TRANSLUCENT;
        }
        return CUSTOM;
    }

    /** whether a group key is one of 1.21.9's own (single quads, item pickup, elder guardians, no render) */
    public static boolean nativeGroup(class_3999 type) {
        return type == class_3999.field_62622 || type == class_3999.field_62623 || type == class_3999.field_62624
            || type == class_3999.field_17832;
    }

    // ------------------------------------------------------------------ getRenderType / getGroup / getLayer

    /** a 1.21.8 particle answering getRenderType() itself */
    public interface LegacyRenderType {
        class_3999 method_18122();
    }

    /** markers: a 1.21.8 particle drawing itself (its own render, renderRotatedQuad or renderCustom) */
    public interface LegacyRender {
        void method_3074(class_4588 consumer, class_4184 camera, float partialTick);
    }

    public interface LegacyQuad {
        void method_60373(class_4588 consumer, class_4184 camera, Quaternionf rotation, float partialTick);
    }

    public interface LegacyRotatedQuad {
        void method_60374(class_4588 consumer, Quaternionf rotation, float x, float y, float z, float partialTick);
    }

    public interface LegacyCustom {
        void method_65198(net.minecraft.class_4587 pose, net.minecraft.class_4597 buffers, class_4184 camera, float partialTick);
    }

    /** whether a particle draws itself the 1.21.8 way */
    static boolean drawsItself(Object p) {
        return p instanceof LegacyRender || p instanceof LegacyQuad || p instanceof LegacyRotatedQuad || p instanceof LegacyCustom;
    }

    /** Particle.getRenderType() for 1.21.8 code: the particle's own answer, else what 1.21.9's particle stands for */
    public static class_3999 method_18122(class_703 p) {
        if (p instanceof LegacyRenderType legacy) {
            return legacy.method_18122();
        }
        return renderTypeOf(p);
    }

    /**
     * a 1.21.9 particle's 1.21.8 type: a single-quad particle's layer's sheet, no-render as is, anything else CUSTOM.
     * Also a 1.21.8 override's super.getRenderType() (CbParticleSupers): the guarded getLayer synthesized in the mod
     * class falls through to the vanilla superclass's on re-entry
     */
    public static class_3999 renderTypeOf(class_703 p) {
        if (p instanceof class_3940 quad) {
            return sheetOf(quad.method_74255());
        }
        class_3999 group = p.method_74274();
        return group == class_3999.field_17832 ? group : CUSTOM;
    }

    /** the synthesized getGroup(): a sheet type drawn by vanilla's quad code is 1.21.9's single quads, else the legacy group of its type */
    public static class_3999 cbGroup(LegacyRenderType self) {
        class_3999 type = self.method_18122();
        if (type == class_3999.field_17832) {
            return type;
        }
        if (self instanceof class_3940 && layer(type) != null && !drawsItself(self)) {
            return class_3999.field_62622;
        }
        return type;
    }

    /** the synthesized getLayer(): the layer of the particle's sheet (translucent particles for a type without one) */
    public static class_3940.class_11941 cbLayer(LegacyRenderType self) {
        class_3940.class_11941 l = layer(self.method_18122());
        return l != null ? l : class_3940.class_11941.field_62641;
    }

    /** a 1.21.8 Particle subclass that never answered getRenderType itself cannot exist (abstract on 1.21.8); a draws-itself class without one: CUSTOM */
    public static class_3999 cbGroupDrawing(Object self) {
        return self instanceof LegacyRenderType legacy ? cbGroup(legacy) : CUSTOM;
    }

    // ------------------------------------------------------------------ colour, alpha, roll (Particle on 1.21.8, SingleQuadParticle on 1.21.9)

    /** {r, g, b, a, roll, oRoll} of a particle that is not a SingleQuadParticle (1.21.8's defaults: white, opaque, no roll) */
    private static final Map<class_703, float[]> EXTRA = new WeakHashMap<>();

    private static synchronized float[] extra(class_703 p) {
        return EXTRA.computeIfAbsent(p, k -> new float[] {1.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F});
    }

    public static float field_3861(class_703 p) {
        return p instanceof class_3940 q ? q.field_62633 : extra(p)[0];
    }

    public static void field_3861(class_703 p, float v) {
        if (p instanceof class_3940 q) {
            q.field_62633 = v;
        } else {
            extra(p)[0] = v;
        }
    }

    public static float field_3842(class_703 p) {
        return p instanceof class_3940 q ? q.field_62634 : extra(p)[1];
    }

    public static void field_3842(class_703 p, float v) {
        if (p instanceof class_3940 q) {
            q.field_62634 = v;
        } else {
            extra(p)[1] = v;
        }
    }

    public static float field_3859(class_703 p) {
        return p instanceof class_3940 q ? q.field_62635 : extra(p)[2];
    }

    public static void field_3859(class_703 p, float v) {
        if (p instanceof class_3940 q) {
            q.field_62635 = v;
        } else {
            extra(p)[2] = v;
        }
    }

    public static float field_3841(class_703 p) {
        return p instanceof class_3940 q ? q.field_62636 : extra(p)[3];
    }

    public static void field_3841(class_703 p, float v) {
        if (p instanceof class_3940 q) {
            q.field_62636 = v;
        } else {
            extra(p)[3] = v;
        }
    }

    public static float field_3839(class_703 p) {
        return p instanceof class_3940 q ? q.field_62637 : extra(p)[4];
    }

    public static void field_3839(class_703 p, float v) {
        if (p instanceof class_3940 q) {
            q.field_62637 = v;
        } else {
            extra(p)[4] = v;
        }
    }

    public static float field_3857(class_703 p) {
        return p instanceof class_3940 q ? q.field_62638 : extra(p)[5];
    }

    public static void field_3857(class_703 p, float v) {
        if (p instanceof class_3940 q) {
            q.field_62638 = v;
        } else {
            extra(p)[5] = v;
        }
    }

    /** a 1.21.8 particle that is no SingleQuadParticle overriding setColor / setAlpha (on a single-quad one the override hooks reach them) */
    public interface LegacyColor {
        void method_3084(float r, float g, float b);
    }

    public interface LegacyAlpha {
        void method_3083(float a);
    }

    /** Particle.setColor(r, g, b) for 1.21.8 code: virtual, as 1.21.8's was */
    public static void method_3084(class_703 p, float r, float g, float b) {
        if (p instanceof class_3940 q) {
            q.method_74305(r, g, b);
        } else if (p instanceof LegacyColor c) {
            c.method_3084(r, g, b);
        } else {
            baseColor(p, r, g, b);
        }
    }

    /** Particle.setColor's own body on a particle that is no SingleQuadParticle (1.21.8's: store the colour) */
    static void baseColor(class_703 p, float r, float g, float b) {
        float[] e = extra(p);
        e[0] = r;
        e[1] = g;
        e[2] = b;
    }

    /** Particle.setAlpha(a) */
    public static void method_3083(class_703 p, float a) {
        if (p instanceof class_3940 q) {
            q.method_74308(a);
        } else if (p instanceof LegacyAlpha c) {
            c.method_3083(a);
        } else {
            baseAlpha(p, a);
        }
    }

    static void baseAlpha(class_703 p, float a) {
        extra(p)[3] = a;
    }

    // ------------------------------------------------------------------ sprites (TextureSheetParticle on 1.21.8)

    /** new TextureSheetParticle(..) / SingleQuadParticle(..): no sprite yet, as 1.21.8's started (pickSprite / setSprite set it) */
    public static class_1058 noSprite() {
        return null;
    }

    /** a 1.21.8 single-quad particle overriding pickSprite (1.21.9 has no such member: vanilla's providers pick themselves) */
    public interface LegacyPickSprite {
        void method_18140(class_4002 sprites);
    }

    /** TextureSheetParticle.pickSprite(sprites) for 1.21.8 code: the particle's own override, else 1.21.8's body */
    public static void method_18140(class_3940 p, class_4002 sprites) {
        if (p instanceof LegacyPickSprite own) {
            own.method_18140(sprites);
        } else {
            CbParticleBaseSupers.method_18140(p, sprites);
        }
    }

    // ------------------------------------------------------------------ drawing into a VertexConsumer

    /** a QuadParticleRenderState that draws each quad at once into one consumer (1.21.8's render target) */
    public static final class Immediate extends class_11944 {
        final class_4588 consumer;

        Immediate(class_4588 consumer) {
            this.consumer = consumer;
        }

        @Override
        public void method_74323(class_3940.class_11941 layer, float x, float y, float z, float qx, float qy, float qz, float qw,
                                 float size, float u0, float u1, float v0, float v1, int color, int light) {
            this.method_74321(this.consumer, x, y, z, qx, qy, qz, qw, size, u0, u1, v0, v1, color, light);
        }
    }

    /** a 1.21.8 call's VertexConsumer as the state 1.21.9's quad code writes to */
    public static class_11944 immediate(class_4588 consumer) {
        return new Immediate(consumer);
    }

    private static final class_4588 NOWHERE = new class_4588() {
        @Override
        public class_4588 method_22912(float x, float y, float z) {
            return this;
        }

        @Override
        public class_4588 method_1336(int r, int g, int b, int a) {
            return this;
        }

        @Override
        public class_4588 method_22913(float u, float v) {
            return this;
        }

        @Override
        public class_4588 method_60796(int u, int v) {
            return this;
        }

        @Override
        public class_4588 method_22921(int u, int v) {
            return this;
        }

        @Override
        public class_4588 method_22914(float x, float y, float z) {
            return this;
        }
    };

    /**
     * the consumer a 1.21.8 override draws into when 1.21.9 hands it a state: an Immediate's (a draws-itself particle is
     * drawn by its legacy group through one); any other state reaching such a class gets nothing drawn
     */
    public static class_4588 consumer(class_11944 state) {
        return state instanceof Immediate i ? i.consumer : NOWHERE;
    }

    /** SingleQuadParticle.renderRotatedQuad(consumer, rotation, x, y, z, partialTick): 1.21.9's quad into the consumer */
    public static void method_60374(class_3940 p, class_4588 consumer, Quaternionf rotation, float x, float y, float z, float partialTick) {
        p.method_60375(immediate(consumer), rotation, x, y, z, partialTick);
    }

    /** Particle.render(consumer, camera, partialTick) called by 1.21.8 code */
    public static void method_3074(class_703 p, class_4588 consumer, class_4184 camera, float partialTick) {
        if (p instanceof class_3940 quad) {
            quad.method_3074(immediate(consumer), camera, partialTick);
        } else if (p instanceof LegacyRender legacy) {
            legacy.method_3074(consumer, camera, partialTick);
        }
    }

    /** the synthesized 1.21.9 extract of a draws-itself particle that is not a SingleQuadParticle: drawn by its legacy group */
    public static void cbExtract(LegacyRender self, class_11944 state, class_4184 camera, float partialTick) {
        self.method_3074(consumer(state), camera, partialTick);
    }

    /** Particle.renderCustom(pose, buffers, camera, partialTick) called by 1.21.8 code: a custom particle's own, else nothing (1.21.8's default) */
    public static void method_65198(class_703 p, net.minecraft.class_4587 pose, net.minecraft.class_4597 buffers, class_4184 camera,
                                    float partialTick) {
        if (p instanceof LegacyCustom custom) {
            custom.method_65198(pose, buffers, camera, partialTick);
        }
    }
}

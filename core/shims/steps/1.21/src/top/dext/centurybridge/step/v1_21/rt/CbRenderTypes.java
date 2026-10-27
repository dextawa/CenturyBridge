package top.dext.centurybridge.step.v1_21.rt;

import java.util.Optional;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_21.facade.BufferBuilderRenderedBuffer;

/**
 * 1.20.6's RenderType pieces 1.21 dropped. A subclass of RenderType only to read RenderStateShard's protected
 * shards and constants, as 1.20.6 render types were defined; never instantiated.
 * <ul>
 * <li>armorGlint() and glintDirect(): 1.20.6's "armor_glint" and "glint_direct" render types, made again with
 *     1.20.6's states. Their shader programs (rendertype_armor_glint, rendertype_glint_direct) were byte-for-byte
 *     1.20.6's rendertype_glint, apart from a JSON blend block 1.21 dropped from every glint program; 1.21 no longer
 *     loads them and its getters for them answer null, so they answer 1.21's glint program instead
 *     (CbRenderGameRendererMixin) -- the shards below are 1.20.6's, over those getters.</li>
 * <li>RenderType.end(builder, sorting): 1.20.6 drew a builder's batch with the type's state (sorted first when the
 *     type sorts on upload); 1.21 draws a MeshData (draw). A 1.20.6 subclass's own end is reached where 1.20.6
 *     called it (CbRenderBufferSourceMixin, and 1.20.6-style calls).</li>
 * <li>asOptional(): Optional.of(type), as 1.20.6 kept it (value-equal each call).</li>
 * <li>ItemRenderer.getArmorFoilBuffer(source, type, noEntity, withGlint) / getCompassFoilBufferDirect: 1.20.6's
 *     bodies over these types.</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
public abstract class CbRenderTypes extends net.minecraft.class_1921 {

    private CbRenderTypes() {
        super("centurybridge:unused", net.minecraft.class_290.field_1592, net.minecraft.class_293.class_5596.field_27382, 0, false, false,
            () -> {
            }, () -> {
            });
    }

    // ------------------------------------------------------------------ 1.20.6's shader shards (RenderStateShard's, protected there)

    /** RENDERTYPE_ARMOR_GLINT_SHADER */
    public static final net.minecraft.class_4668.class_5942 field_29419 = new net.minecraft.class_4668.class_5942(
        net.minecraft.class_757::method_34520);

    /** RENDERTYPE_GLINT_DIRECT_SHADER */
    public static final net.minecraft.class_4668.class_5942 field_29423 = new net.minecraft.class_4668.class_5942(
        net.minecraft.class_757::method_34526);

    /** POSITION_COLOR_TEX_SHADER */
    public static final net.minecraft.class_4668.class_5942 field_29439 = new net.minecraft.class_4668.class_5942(
        CbRenderShaders::method_34541);

    // ------------------------------------------------------------------ armorGlint() / glintDirect()

    /**
     * RenderType.create(name, format, mode, bufferSize, state): package-private, opened by the layer's access
     * widener and named in bytecode (a reflective name string would not follow it as the layer climbs)
     */
    private static net.minecraft.class_1921 cbCreate(String name, net.minecraft.class_1921.class_4688 state) {
        return net.minecraft.class_1921.method_24048(name, net.minecraft.class_290.field_1585,
            net.minecraft.class_293.class_5596.field_27382, 1536, state);
    }

    private static net.minecraft.class_1921 armorGlint;

    private static net.minecraft.class_1921 glintDirect;

    /** armorGlint() of 1.20.6 */
    public static synchronized net.minecraft.class_1921 method_27948() {
        if (armorGlint == null) {
            armorGlint = cbCreate("armor_glint", net.minecraft.class_1921.class_4688.method_23598()
                .method_34578(field_29419)
                .method_34577(new net.minecraft.class_4668.class_4683(net.minecraft.class_918.field_43086, true, false))
                .method_23616(field_21350)
                .method_23603(field_21345)
                .method_23604(field_21347)
                .method_23615(field_21368)
                .method_23614(field_21381)
                .method_23607(field_22241)
                .method_23617(false));
        }
        return armorGlint;
    }

    /** glintDirect() of 1.20.6 */
    public static synchronized net.minecraft.class_1921 method_29706() {
        if (glintDirect == null) {
            glintDirect = cbCreate("glint_direct", net.minecraft.class_1921.class_4688.method_23598()
                .method_34578(field_29423)
                .method_34577(new net.minecraft.class_4668.class_4683(net.minecraft.class_918.field_43087, true, false))
                .method_23616(field_21350)
                .method_23603(field_21345)
                .method_23604(field_21347)
                .method_23615(field_21368)
                .method_23614(field_21381)
                .method_23617(false));
        }
        return glintDirect;
    }

    // ------------------------------------------------------------------ RenderType.end / asOptional

    /** RenderType.end(builder, sorting), as a 1.20.6 render type class declares it */
    public interface CbLegacyEnd {
        void method_23012(net.minecraft.class_287 builder, net.minecraft.class_8251 sorting);
    }

    /** RenderType.asOptional(), as a 1.20.6 render type class declares it */
    public interface CbLegacyAsOptional {
        Optional<net.minecraft.class_1921> method_24296();
    }

    /** type.end(builder, sorting), called 1.20.6's way: a 1.20.6 subclass's own, else 1.20.6's */
    public static void cbEnd(net.minecraft.class_1921 type, net.minecraft.class_287 builder, net.minecraft.class_8251 sorting) {
        if (type instanceof CbLegacyEnd own) {
            own.method_23012(builder, sorting);
            return;
        }
        cbEndBody(type, builder, sorting);
    }

    /**
     * RenderType.end(builder, sorting) of 1.20.6 (a subclass's super.end): nothing unless the builder is building;
     * quad sorting when the type sorts on upload; the batch drawn with the type's state set up around it.
     */
    public static void cbEndBody(net.minecraft.class_1921 type, net.minecraft.class_287 builder, net.minecraft.class_8251 sorting) {
        CbRenderBufferBuilderV b = (CbRenderBufferBuilderV) (Object) builder;
        if (!b.method_22893()) {
            return;
        }
        if (type.method_60894()) {
            b.method_49904(sorting);
        }
        BufferBuilderRenderedBuffer rendered = b.method_1326();
        type.method_23516();
        CbRenderBuffers.cbDrawWithShader(rendered);
        type.method_23518();
    }

    /** type.asOptional(), called 1.20.6's way */
    public static Optional<net.minecraft.class_1921> cbAsOptional(net.minecraft.class_1921 type) {
        if (type instanceof CbLegacyAsOptional own) {
            return own.method_24296();
        }
        return cbAsOptionalBody(type);
    }

    /** asOptional() of 1.20.6 (a subclass's super.asOptional) */
    public static Optional<net.minecraft.class_1921> cbAsOptionalBody(net.minecraft.class_1921 type) {
        return Optional.of(type);
    }

    // ------------------------------------------------------------------ ItemRenderer's foil buffers

    /** ItemRenderer.getArmorFoilBuffer(source, type, noEntity, withGlint) of 1.20.6 */
    public static net.minecraft.class_4588 method_27952(net.minecraft.class_4597 source, net.minecraft.class_1921 type, boolean noEntity,
                                                       boolean withGlint) {
        if (!withGlint) {
            return source.getBuffer(type);
        }
        return net.minecraft.class_4720.method_24037(source.getBuffer(noEntity ? method_27948() : net.minecraft.class_1921.method_27949()),
            source.getBuffer(type));
    }

    /** ItemRenderer.getCompassFoilBufferDirect(source, type, pose) of 1.20.6 */
    public static net.minecraft.class_4588 method_30115(net.minecraft.class_4597 source, net.minecraft.class_1921 type,
                                                       net.minecraft.class_4587.class_4665 pose) {
        return net.minecraft.class_4720.method_24037(new net.minecraft.class_4583(source.getBuffer(method_29706()), pose, 0.0078125F),
            source.getBuffer(type));
    }

    /** the vertex sorting 1.20.6's BufferSource passed to end(..) */
    public static net.minecraft.class_8251 cbSorting() {
        return RenderSystem.getVertexSorting();
    }
}

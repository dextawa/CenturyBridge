package top.dext.centurybridge.step.v1_21.mixin;

import java.util.List;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_21.facade.BufferBuilderDrawState;
import top.dext.centurybridge.step.v1_21.facade.BufferBuilderRenderedBuffer;
import top.dext.centurybridge.step.v1_21.facade.BufferBuilderSortState;
import top.dext.centurybridge.step.v1_21.rt.CbClientModels;
import top.dext.centurybridge.step.v1_21.rt.CbPrioritySplit;
import top.dext.centurybridge.step.v1_21.rt.CbRenderBufferBuilderV;
import top.dext.centurybridge.step.v1_21.rt.CbVanillaReads;

/**
 * 1.20.6's BufferBuilder on 1.21's (CbRenderBufferBuilderV). 1.21's builder is one batch: made building over a
 * ByteBufferBuilder with its mode and format, built once. 1.20.6's lived on: begin(mode, format) started a batch
 * (refused while one was building), end() finished it into a RenderedBuffer (sorted first when setQuadSorting
 * was called) and left the builder idle for the next begin. Here begin() sets a builder up the way 1.21's
 * constructor does -- mode, format, vertex size, the elements each vertex must fill, their offsets, the two fast
 * paths -- so one builder takes batch after batch over its ByteBufferBuilder; end() is 1.21's build() (which ends
 * the last vertex as 1.20.6's endVertex had, duplicating line vertices the same way), sorted with 1.21's
 * MeshData.sortQuads when 1.20.6 asked for sorting, and returned as 1.20.6's RenderedBuffer.
 *
 * <ul>
 * <li>getSortState / restoreSortState: the quads' centroids, taken where 1.20.6 took them (setQuadSorting, from
 *     the unbuilt batch); after a restore, end() answers the index-only batch of the quads re-sorted
 *     (MeshData.SortState.buildSortedIndexBuffer).</li>
 * <li>the element cursor (currentElement / nextElement / putByte / putShort / putFloat): the current element is
 *     the vertex's first unfilled one in format order -- 1.20.6's cursor walked the elements in that order -- and
 *     a write at the first element begins the next vertex, as 1.20.6's cursor started each vertex there.</li>
 * <li>defaultColor: every vertex then takes that color, and an explicit color is refused, as 1.20.6's builder
 *     filled COLOR itself when its cursor reached it and threw on color(..).</li>
 * <li>clear / discard / release: the ByteBufferBuilder's (clear warns of unused batches, as 1.20.6's did); release
 *     refuses with unused batches or while building, as 1.20.6's did. discard keeps a batch still being built
 *     (1.21's ByteBufferBuilder.discard); 1.20.6 dropped its bytes but kept counting its vertices.</li>
 * </ul>
 * A mesh about to be built first ends the vertices 1.21 code left open on 1.20.6 consumers (CbRenderVertex).
 * The injectors into 1.21's own methods (the default color under 1.21's calls, the open vertices) are in
 * CbRenderBufferBuilderMixinHooks (default priority).
 */
@Mixin(value = net.minecraft.class_287.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRenderBufferBuilderMixin implements CbRenderBufferBuilderV, CbPrioritySplit.BufferDefaultColor {

    @Shadow
    @Final
    private net.minecraft.class_9799 field_52071;

    /** vertexPointer */
    @Shadow
    private long field_52072;

    /** vertices */
    @Shadow
    private int field_1554;

    /** format */
    @Shadow
    @Final
    @Mutable
    private net.minecraft.class_293 field_1565;

    /** mode */
    @Shadow
    @Final
    @Mutable
    private net.minecraft.class_293.class_5596 field_52073;

    /** fastFormat */
    @Shadow
    @Final
    @Mutable
    private boolean field_21594;

    /** fullFormat */
    @Shadow
    @Final
    @Mutable
    private boolean field_21595;

    /** vertexSize */
    @Shadow
    @Final
    @Mutable
    private int field_52074;

    /** initialElementsToFill */
    @Shadow
    @Final
    @Mutable
    private int field_52075;

    /** offsetsByElement */
    @Shadow
    @Final
    @Mutable
    private int[] field_52076;

    /** elementsToFill */
    @Shadow
    private int field_52077;

    /** building */
    @Shadow
    private boolean field_1556;

    /** beginVertex() */
    @Shadow
    private long method_60805() {
        throw new AssertionError();
    }

    /** build() */
    @Shadow
    public abstract net.minecraft.class_9801 method_60794();

    /** addVertex(x, y, z) */
    @Shadow
    public abstract net.minecraft.class_4588 method_22912(float x, float y, float z);

    /** setColor(r, g, b, a) */
    @Shadow
    public abstract net.minecraft.class_4588 method_1336(int r, int g, int b, int a);

    /** setUv1(u, v) */
    @Shadow
    public abstract net.minecraft.class_4588 method_60796(int u, int v);

    /** setUv2(u, v) */
    @Shadow
    public abstract net.minecraft.class_4588 method_22921(int u, int v);

    /** setLight(packed) */
    @Shadow
    public abstract net.minecraft.class_4588 method_60803(int packed);

    /** addVertex(x, y, z, color, u, v, overlay, light, nx, ny, nz) */
    @Shadow
    public abstract void method_23919(float x, float y, float z, int color, float u, float v, int overlay, int light, float nx,
                                      float ny, float nz);

    @Unique
    private net.minecraft.class_8251 cb$sorting;

    @Unique
    private org.joml.Vector3f[] cb$sortingPoints;

    @Unique
    private boolean cb$indexOnly;

    @Unique
    private boolean cb$closed;

    /** inside method_43578's (storeRenderedBuffer's) call of 1.21's build() (CbRenderBufferBuilderRouteMixin's end route stands aside) */
    @Unique
    private boolean cb$storing;

    /** the cursor is on the position of a vertex it began (1.21 counts the position filled as the vertex begins) */
    @Unique
    private boolean cb$posPending;

    @Unique
    private boolean cb$defaultColorSet;

    @Unique
    private int cb$defaultR;

    @Unique
    private int cb$defaultG;

    @Unique
    private int cb$defaultB;

    @Unique
    private int cb$defaultA;

    @Unique
    private boolean cb$fillingDefault;

    // ------------------------------------------------------------------ the batch

    /** begin(mode, format) */
    @Override
    @CbVanillaReads(value = "CbRenderBufferBuilderRouteMixin: every 1.21 builder, made for one batch where 1.20.6 began one, is begun "
        + "again through this member at its constructor's RETURN when a converted mod mixes it (the same setup; a cancelled "
        + "begin leaves 1.21's caller its building builder)",
        carries = {"Inject@HEAD", "Inject@RETURN", "Inject@TAIL"})
    public void method_1328(net.minecraft.class_293.class_5596 mode, net.minecraft.class_293 format) {
        if (this.field_1556) {
            throw new IllegalStateException("Already building!");
        }
        this.method_54559();
        this.field_52073 = mode;
        this.method_23918(format);
        this.field_52072 = -1L;
        this.field_1554 = 0;
        this.field_52077 = 0;
        this.cb$posPending = false;
        this.field_1556 = true;
    }

    /** end() */
    @Override
    @CbVanillaReads(value = "CbRenderBufferBuilderRouteMixin: 1.21's build() / buildOrThrow() at the sites 1.20.6 called end() answer "
        + "this member's mesh when a converted mod mixes it (not the section compile: endOrDiscardIfEmpty there)",
        carries = {"Inject@HEAD", "Inject@RETURN", "Inject@TAIL", "ModifyReturnValue@RETURN"})
    public BufferBuilderRenderedBuffer method_1326() {
        this.method_43577();
        BufferBuilderRenderedBuffer rendered = this.method_43578();
        this.method_43579();
        return rendered;
    }

    /** endOrDiscardIfEmpty() */
    @Override
    @CbVanillaReads(value = "CbRenderSectionCompileRouteMixin: 1.21's section compile (SectionCompiler.compile's build()) answers "
        + "this member's mesh when a converted mod mixes it",
        carries = {"Inject@HEAD", "Inject@RETURN", "Inject@TAIL", "ModifyReturnValue@RETURN"})
    public BufferBuilderRenderedBuffer method_43575() {
        this.method_43577();
        if (this.method_43574()) {
            this.method_43579();
            return null;
        }
        BufferBuilderRenderedBuffer rendered = this.method_43578();
        this.method_43579();
        return rendered;
    }

    @Override
    public boolean cb$v1_21$storing() {
        return this.cb$storing;
    }

    /** isCurrentBatchEmpty() */
    @Override
    public boolean method_43574() {
        return this.field_1554 == 0;
    }

    /** building() */
    @Override
    public boolean method_22893() {
        return this.field_1556;
    }

    /** clear(): the ByteBufferBuilder warns of unused batches and drops them, as 1.20.6's builder did */
    @Override
    public void method_1343() {
        this.field_52071.method_60809();
    }

    /** discard() */
    @Override
    public void method_23477() {
        this.field_52071.method_60811();
    }

    /** release() */
    @Override
    public void method_54558() {
        if (((CbRenderByteBufferBuilderAccess) (Object) this.field_52071).cb$resultCount() > 0) {
            throw new IllegalStateException("BufferBuilder closed with unused batches");
        }
        if (this.field_1556) {
            throw new IllegalStateException("Cannot close BufferBuilder while it is building");
        }
        if (this.cb$closed) {
            return;
        }
        this.cb$closed = true;
        this.field_52071.close();
    }

    /** setQuadSorting(sorting): quads only; the centroids of the batch's quads as they stand now */
    @Override
    public void method_49904(net.minecraft.class_8251 sorting) {
        if (this.field_52073 != net.minecraft.class_293.class_5596.field_27382) {
            return;
        }
        this.cb$sorting = sorting;
        if (this.cb$sortingPoints == null) {
            this.cb$sortingPoints = this.cb$quadCentroids();
        }
    }

    /** getSortState() */
    @Override
    public BufferBuilderSortState method_1334() {
        return new BufferBuilderSortState(this.field_52073, this.field_1554, this.cb$sortingPoints, this.cb$sorting);
    }

    /** restoreSortState(state): the next end() answers the quads re-sorted, indices only */
    @Override
    public void method_1324(BufferBuilderSortState state) {
        this.method_54559();
        this.field_52073 = state.field_27358;
        this.field_1554 = state.field_27359;
        this.cb$sortingPoints = state.field_27360;
        this.cb$sorting = state.field_43359;
        this.cb$indexOnly = true;
    }

    /** 1.20.6's ensureBuilding, its body as 1.20.6's (1.20.6 called it only from end and endOrDiscardIfEmpty, both routed) */
    @CbVanillaReads("CbRenderBufferBuilderRouteMixin: 1.21's build() runs the restored end (CbRenderSectionCompileRouteMixin: endOrDiscardIfEmpty at the section compile), which calls this, when a converted mod mixes it")
    private void method_43577() {
        if (!this.field_1556) {
            throw new IllegalStateException("Not building!");
        }
    }

    /** 1.20.6's ensureNotClosed, its body as 1.20.6's on the layer's closed flag (1.20.6 called it from begin, routed, and restoreSortState) */
    @CbVanillaReads("CbRenderBufferBuilderRouteMixin: 1.21's BufferBuilder constructor runs the restored begin, which calls this, when a converted mod mixes it")
    private void method_54559() {
        if (this.cb$closed) {
            throw new IllegalStateException("This BufferBuilder has been closed");
        }
    }

    /** 1.20.6's storeRenderedBuffer on 1.21's build */
    @CbVanillaReads(value = "CbRenderBufferBuilderRouteMixin: 1.21's build() runs the restored end (CbRenderSectionCompileRouteMixin: endOrDiscardIfEmpty at the section compile), which calls this, when a converted mod mixes it",
        carries = {"Inject@HEAD", "Inject@RETURN", "Inject@TAIL", "ModifyReturnValue@RETURN"})
    private BufferBuilderRenderedBuffer method_43578() {
        net.minecraft.class_293 format = this.field_1565;
        net.minecraft.class_293.class_5596 mode = this.field_52073;
        net.minecraft.class_287 self = (net.minecraft.class_287) (Object) this;
        if (this.cb$indexOnly) {
            int vertices = this.field_1554;
            int indexCount = mode.method_31973(vertices);
            net.minecraft.class_293.class_5595 type = net.minecraft.class_293.class_5595.method_31972(vertices);
            if (this.cb$sortingPoints == null) {
                return BufferBuilderRenderedBuffer.cbOf(self, new BufferBuilderDrawState(format, vertices, indexCount, mode, type, true, true),
                    null, null);
            }
            if (this.cb$sorting == null) {
                throw new IllegalStateException("Sorting state uninitialized");
            }
            net.minecraft.class_9799.class_9800 indices = new net.minecraft.class_9801.class_9802(this.cb$sortingPoints, type)
                .method_60824(this.field_52071, this.cb$sorting);
            return BufferBuilderRenderedBuffer.cbOf(self, new BufferBuilderDrawState(format, vertices, indexCount, mode, type, true, false),
                null, indices);
        }
        boolean sorted = this.cb$sortingPoints != null;
        if (sorted && this.cb$sorting == null) {
            throw new IllegalStateException("Sorting state uninitialized");
        }
        net.minecraft.class_9801 mesh;
        this.cb$storing = true;
        try {
            mesh = this.method_60794();
        } finally {
            this.cb$storing = false;
        }
        if (mesh == null) {
            return BufferBuilderRenderedBuffer.cbOf(self, new BufferBuilderDrawState(format, 0, mode.method_31973(0), mode,
                net.minecraft.class_293.class_5595.method_31972(0), false, !sorted), null, null);
        }
        if (sorted) {
            mesh.method_60819(this.field_52071, this.cb$sorting);
        }
        net.minecraft.class_9801.class_4574 drawn = mesh.method_60822();
        return BufferBuilderRenderedBuffer.cbOf(self, new BufferBuilderDrawState(format, drawn.comp_750(), drawn.comp_751(), mode,
            drawn.comp_753(), false, !sorted), mesh, null);
    }

    /** 1.20.6's reset: idle, no vertices, no sorting */
    @CbVanillaReads(value = "CbRenderBufferBuilderRouteMixin: 1.21's build() runs the restored end (CbRenderSectionCompileRouteMixin: endOrDiscardIfEmpty at the section compile), which calls this, when a converted mod mixes it",
        carries = {"Inject@HEAD", "Inject@RETURN", "Inject@TAIL"})
    private void method_43579() {
        this.field_1556 = false;
        this.field_1554 = 0;
        this.field_52072 = -1L;
        this.field_52077 = 0;
        this.cb$posPending = false;
        this.cb$sortingPoints = null;
        this.cb$sorting = null;
        this.cb$indexOnly = false;
    }

    /** 1.20.6's switchFormat(format): the format and what 1.21's constructor derives from it */
    @CbVanillaReads(value = "CbRenderBufferBuilderRouteMixin: 1.21's BufferBuilder constructor runs the restored begin, which calls this, when a converted mod mixes it",
        carries = {"Inject@HEAD", "Inject@RETURN", "Inject@TAIL"})
    private void method_23918(net.minecraft.class_293 format) {
        this.field_1565 = format;
        this.field_52074 = format.method_1362();
        this.field_52075 = format.method_60839() & ~net.minecraft.class_296.field_52107.method_60843();
        this.field_52076 = format.method_60838();
        boolean entity = format == net.minecraft.class_290.field_1580;
        boolean block = format == net.minecraft.class_290.field_1590;
        this.field_21594 = entity || block;
        this.field_21595 = entity;
    }

    /** 1.20.6's makeQuadSortingPoints: each quad's centre, the midpoint of its vertices 0 and 2 */
    @Unique
    private org.joml.Vector3f[] cb$quadCentroids() {
        CbRenderByteBufferBuilderAccess bytes = (CbRenderByteBufferBuilderAccess) (Object) this.field_52071;
        long base = bytes.cb$pointer() + bytes.cb$nextResultOffset();
        long stride = this.field_52074;
        int quads = this.field_1554 / this.field_52073.field_27385;
        org.joml.Vector3f[] points = new org.joml.Vector3f[quads];
        for (int i = 0; i < quads; i++) {
            long q0 = base + i * stride * this.field_52073.field_27385;
            long q2 = q0 + stride * 2;
            points[i] = new org.joml.Vector3f(
                (MemoryUtil.memGetFloat(q0) + MemoryUtil.memGetFloat(q2)) / 2.0F,
                (MemoryUtil.memGetFloat(q0 + 4) + MemoryUtil.memGetFloat(q2 + 4)) / 2.0F,
                (MemoryUtil.memGetFloat(q0 + 8) + MemoryUtil.memGetFloat(q2 + 8)) / 2.0F);
        }
        return points;
    }

    // ------------------------------------------------------------------ the element cursor

    /** the element 1.20.6's cursor would stand on */
    @Unique
    private net.minecraft.class_296 cb$cursorElement() {
        List<net.minecraft.class_296> elements = this.field_1565.method_1357();
        if (this.cb$posPending || this.field_52072 == -1L || this.field_52077 == 0) {
            return elements.get(0);
        }
        for (net.minecraft.class_296 e : elements) {
            if ((this.field_52077 & e.method_60843()) != 0) {
                return e;
            }
        }
        return elements.get(0);
    }

    /** where a write at the cursor goes; a write at the first element begins the next vertex */
    @Unique
    private long cb$cursorPointer() {
        net.minecraft.class_296 e = this.cb$cursorElement();
        if (!this.cb$posPending && (this.field_52072 == -1L || this.field_52077 == 0)) {
            this.method_60805();
            this.cb$posPending = true;
        }
        return this.field_52072 + this.field_52076[e.comp_2842()];
    }

    /** currentElement() */
    @Override
    public net.minecraft.class_296 method_22900() {
        if (!this.field_1556) {
            throw new IllegalStateException("BufferBuilder not started");
        }
        return this.cb$cursorElement();
    }

    /** nextElement(): the element at the cursor is filled; 1.20.6 then filled COLOR itself under a default color */
    @Override
    public void method_1325() {
        if (this.cb$posPending) {
            this.cb$posPending = false;
        } else {
            this.field_52077 &= ~this.cb$cursorElement().method_60843();
        }
        if (this.cb$defaultColorSet && this.field_52077 != 0
                && this.cb$cursorElement().comp_2845() == net.minecraft.class_296.class_298.field_1632) {
            this.cb$fillDefaultColor();
        }
    }

    /** putByte(index, value) */
    @Override
    public void method_22896(int index, byte value) {
        MemoryUtil.memPutByte(this.cb$cursorPointer() + index, value);
    }

    /** putShort(index, value) */
    @Override
    public void method_22898(int index, short value) {
        MemoryUtil.memPutShort(this.cb$cursorPointer() + index, value);
    }

    /** putFloat(index, value) */
    @Override
    public void method_22897(int index, float value) {
        MemoryUtil.memPutFloat(this.cb$cursorPointer() + index, value);
    }

    /** uvShort(u, v, index) of 1.20.6: the UV element of that index, two shorts, if the cursor is on it */
    @Override
    public net.minecraft.class_4588 method_22899(short u, short v, int index) {
        net.minecraft.class_296 e = this.method_22900();
        if (e.comp_2845() != net.minecraft.class_296.class_298.field_1636 || e.comp_2843() != index) {
            return (net.minecraft.class_4588) (Object) this;
        }
        if (e.comp_2844() != net.minecraft.class_296.class_297.field_1625 || e.comp_2846() != 2) {
            throw new IllegalStateException();
        }
        this.method_22898(0, u);
        this.method_22898(2, v);
        this.method_1325();
        return (net.minecraft.class_4588) (Object) this;
    }

    // ------------------------------------------------------------------ 1.20.6's VertexConsumer calls

    /** vertex(x, y, z) */
    @Override
    public net.minecraft.class_4588 method_22912(double x, double y, double z) {
        return this.cbLegacyVertex(x, y, z);
    }

    /** vertex(x, y, z): the position, as the floats 1.20.6's builder stored */
    @Override
    public net.minecraft.class_4588 cbLegacyVertex(double x, double y, double z) {
        return this.method_22912((float) x, (float) y, (float) z);
    }

    /** vertex(matrix, x, y, z) / 1.21's addVertex(matrix, x, y, z): reaches a 1.20.6 subclass's own */
    public net.minecraft.class_4588 method_22918(org.joml.Matrix4f matrix, float x, float y, float z) {
        return this.cbLegacyVertexMatrix(matrix, x, y, z);
    }

    /** vertex(matrix, x, y, z) of 1.20.6 (1.21's is the same transform): the transformed position, then vertex(..) */
    @Override
    public net.minecraft.class_4588 cbLegacyVertexMatrix(org.joml.Matrix4f matrix, float x, float y, float z) {
        org.joml.Vector3f p = matrix.transformPosition(x, y, z, new org.joml.Vector3f());
        return this.cbLegacyVertex(p.x(), p.y(), p.z());
    }

    /** vertex(pose, x, y, z) / 1.21's addVertex(pose, x, y, z): reaches a 1.20.6 subclass's own */
    public net.minecraft.class_4588 method_56824(net.minecraft.class_4587.class_4665 pose, float x, float y, float z) {
        return this.cbLegacyVertexPose(pose, x, y, z);
    }

    /** vertex(pose, x, y, z) of 1.20.6: vertex(pose.pose(), x, y, z) */
    @Override
    public net.minecraft.class_4588 cbLegacyVertexPose(net.minecraft.class_4587.class_4665 pose, float x, float y, float z) {
        return this.cbLegacyVertexMatrix(pose.method_23761(), x, y, z);
    }

    /** overlayCoords(u, v): 1.21's setUv1 */
    @Override
    public net.minecraft.class_4588 method_22917(int u, int v) {
        return this.method_60796(u, v);
    }

    /** uv2(packed): 1.21's setLight */
    @Override
    public net.minecraft.class_4588 method_22916(int packed) {
        return this.method_60803(packed);
    }

    /** normal(pose, x, y, z): 1.21's setNormal(pose, ..), the same transform */
    @Override
    public net.minecraft.class_4588 method_23763(net.minecraft.class_4587.class_4665 pose, float x, float y, float z) {
        return ((net.minecraft.class_4588) (Object) this).method_60831(pose, x, y, z);
    }

    /** vertex(x, y, z, r, g, b, a, u, v, overlay, light, nx, ny, nz): 1.21's whole vertex, the channels as the bytes 1.20.6 wrote */
    @Override
    public void method_23919(float x, float y, float z, float r, float g, float b, float a, float u, float v, int overlay, int light,
                             float nx, float ny, float nz) {
        if (this.cb$defaultColorSet) {
            throw new IllegalStateException();
        }
        this.method_23919(x, y, z, CbClientModels.cbColor(r, g, b, a), u, v, overlay, light, nx, ny, nz);
    }

    /** endVertex() */
    @Override
    public void method_1344() {
        this.cbLegacyEndVertex();
    }

    /** endVertex() of 1.20.6: the vertex must be whole (1.21 ends it when the next begins, or at build) */
    @Override
    public void cbLegacyEndVertex() {
        if (this.cb$posPending || (this.field_52072 != -1L && this.field_52077 != 0)) {
            throw new IllegalStateException("Not filled all elements of the vertex");
        }
    }

    /** defaultColor(r, g, b, a) */
    @Override
    public void method_22901(int r, int g, int b, int a) {
        this.cb$defaultR = r;
        this.cb$defaultG = g;
        this.cb$defaultB = b;
        this.cb$defaultA = a;
        this.cb$defaultColorSet = true;
    }

    /** unsetDefaultColor() */
    @Override
    public void method_35666() {
        this.cb$defaultColorSet = false;
    }

    @Override
    public net.minecraft.class_9799 cb$byteBuffer() {
        return this.field_52071;
    }

    @Override
    public void cb$idle() {
        this.field_1556 = false;
        this.field_52072 = -1L;
    }

    @Override
    public net.minecraft.class_293.class_5596 cb$mode() {
        return this.field_52073;
    }

    @Override
    public net.minecraft.class_293 cb$format() {
        return this.field_1565;
    }

    @Unique
    private void cb$fillDefaultColor() {
        this.cb$fillingDefault = true;
        try {
            this.method_1336(this.cb$defaultR, this.cb$defaultG, this.cb$defaultB, this.cb$defaultA);
        } finally {
            this.cb$fillingDefault = false;
        }
    }

    // ------------------------------------------------------------------ for 1.21's calls under a 1.20.6 default color (CbRenderBufferBuilderMixinHooks)

    @Override
    public boolean cb$v1_21$defaultColorSet() {
        return this.cb$defaultColorSet;
    }

    @Override
    public boolean cb$v1_21$fillingDefault() {
        return this.cb$fillingDefault;
    }

    @Override
    public void cb$v1_21$fillDefaultColor() {
        this.cb$fillDefaultColor();
    }
}

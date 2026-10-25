package top.dext.centurybridge.step.v1_21_6.rt;

/**
 * 1.21.5's GuiGraphics.pose() -- a PoseStack -- on 1.21.6, whose GUI transform is a Matrix3x2fStack. 1.21.5 drew
 * the GUI through an orthographic projection that drops z, so what a 4x4 pose did to the GUI is its XY part: x' =
 * m00 x + m10 y + m30, y' = m01 x + m11 y + m31. This PoseStack keeps the 4x4 stack a 1.21.5 caller builds (z,
 * rotations about any axis, composed as 1.21.5 composed them) and writes the XY part of its top into GuiGraphics'
 * Matrix3x2fStack after every operation; pushPose / popPose push and pop that stack too, so the nesting is 1.21.5's.
 * Where vanilla moved the 2D stack since (it renders the mod's widget inside its own translations), the top is lifted
 * from it before the next operation.
 *
 * <p>Not carried: a caller writing into last().pose()'s Matrix4f directly reaches the 2D stack only with its next
 * operation on this PoseStack (pushPose, translate, ...), since 1.21.6 reads the 2D stack when it records a draw.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public final class CbGuiPose extends net.minecraft.class_4587 {

    private static final java.util.Map<net.minecraft.class_332, CbGuiPose> POSES = new java.util.WeakHashMap<>();

    private final org.joml.Matrix3x2fStack gui;
    /** the 2D top as this PoseStack last wrote or lifted it */
    private final org.joml.Matrix3x2f written = new org.joml.Matrix3x2f();

    private CbGuiPose(org.joml.Matrix3x2fStack gui) {
        this.gui = gui;
        lift();
    }

    /** GuiGraphics.pose() of 1.21.5 */
    public static net.minecraft.class_4587 pose(net.minecraft.class_332 g) {
        CbGuiPose p = POSES.computeIfAbsent(g, k -> new CbGuiPose(k.method_51448()));
        p.follow();
        return p;
    }

    private void follow() {
        if (!gui.equals(written, 0.0F)) {
            lift();
        }
    }

    /** this top := the 2D top, as a 4x4 transform */
    private void lift() {
        org.joml.Matrix4f m = super.method_23760().method_23761();
        m.set(gui.m00(), gui.m01(), 0.0F, 0.0F,
            gui.m10(), gui.m11(), 0.0F, 0.0F,
            0.0F, 0.0F, 1.0F, 0.0F,
            gui.m20(), gui.m21(), 0.0F, 1.0F);
        written.set(gui);
    }

    /** the 2D top := this top's XY part */
    private void write() {
        org.joml.Matrix4f m = super.method_23760().method_23761();
        gui.set(m.m00(), m.m01(), m.m10(), m.m11(), m.m30(), m.m31());
        written.set(gui);
    }

    @Override
    public void method_22904(double x, double y, double z) {
        follow();
        super.method_22904(x, y, z);
        write();
    }

    @Override
    public void method_46416(float x, float y, float z) {
        follow();
        super.method_46416(x, y, z);
        write();
    }

    @Override
    public void method_61958(net.minecraft.class_243 v) {
        follow();
        super.method_61958(v);
        write();
    }

    @Override
    public void method_22905(float x, float y, float z) {
        follow();
        super.method_22905(x, y, z);
        write();
    }

    @Override
    public void method_22907(org.joml.Quaternionfc q) {
        follow();
        super.method_22907(q);
        write();
    }

    @Override
    public void method_49278(org.joml.Quaternionfc q, float x, float y, float z) {
        follow();
        super.method_49278(q, x, y, z);
        write();
    }

    @Override
    public void method_22903() {
        follow();
        super.method_22903();
        gui.pushMatrix();
        write();
    }

    @Override
    public void method_22909() {
        super.method_22909();
        gui.popMatrix();
        written.set(gui);
        // the 2D top popped back to what it was at the matching push, which is this top's XY part
    }

    @Override
    public net.minecraft.class_4587.class_4665 method_23760() {
        follow();
        return super.method_23760();
    }

    @Override
    public void method_34426() {
        follow();
        super.method_34426();
        write();
    }

    @Override
    public void method_34425(org.joml.Matrix4fc m) {
        follow();
        super.method_34425(m);
        write();
    }
}

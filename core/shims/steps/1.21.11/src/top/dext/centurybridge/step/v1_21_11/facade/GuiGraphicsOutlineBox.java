package top.dext.centurybridge.step.v1_21_11.facade;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_332;

/**
 * 1.21.10 GuiGraphics.OutlineBox (class_332$class_11731, a private record reached through a mod's access widener):
 * x, y, width, height, color. 1.21.10's GuiGraphics queued these and drew each with draw(graphics) in renderDeferred;
 * 1.21.11 dropped the record and GuiGraphics.submitOutline(x, y, width, height, color) (method_73198) draws the same
 * four fills at once (javap of both: the same fill(..) arguments as the record's draw). draw(graphics) is that call.
 */
@Environment(EnvType.CLIENT)
public final class GuiGraphicsOutlineBox {
    public final int comp_4600;
    public final int comp_4601;
    public final int comp_4602;
    public final int comp_4603;
    public final int comp_4604;

    public GuiGraphicsOutlineBox(int x, int y, int width, int height, int color) {
        this.comp_4600 = x;
        this.comp_4601 = y;
        this.comp_4602 = width;
        this.comp_4603 = height;
        this.comp_4604 = color;
    }

    /** draw(graphics) */
    public void method_73200(class_332 graphics) {
        graphics.method_73198(this.comp_4600, this.comp_4601, this.comp_4602, this.comp_4603, this.comp_4604);
    }

    public int comp_4600() {
        return this.comp_4600;
    }

    public int comp_4601() {
        return this.comp_4601;
    }

    public int comp_4602() {
        return this.comp_4602;
    }

    public int comp_4603() {
        return this.comp_4603;
    }

    public int comp_4604() {
        return this.comp_4604;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof GuiGraphicsOutlineBox b && b.comp_4600 == this.comp_4600 && b.comp_4601 == this.comp_4601
            && b.comp_4602 == this.comp_4602 && b.comp_4603 == this.comp_4603 && b.comp_4604 == this.comp_4604;
    }

    @Override
    public int hashCode() {
        return (((this.comp_4600 * 31 + this.comp_4601) * 31 + this.comp_4602) * 31 + this.comp_4603) * 31 + this.comp_4604;
    }

    @Override
    public String toString() {
        return "OutlineBox[x=" + this.comp_4600 + ", y=" + this.comp_4601 + ", width=" + this.comp_4602 + ", height="
            + this.comp_4603 + ", color=" + this.comp_4604 + "]";
    }
}

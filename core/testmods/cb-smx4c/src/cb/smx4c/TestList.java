package cb.smx4c;

import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_4280;

/** A 1.21.3 ObjectSelectionList: getMaxScroll fixed at 40, scroll bar at width - 10, getScrollAmount counted. */
public class TestList extends class_4280<TestList.Row> {
    public TestList(class_310 mc, int width, int height, int y, int itemHeight) {
        super(mc, width, height, y, itemHeight);
        for (int i = 0; i < 30; i++) {
            this.method_25321(new Row(mc.field_1772, "row " + i));
        }
    }

    @Override
    public int method_25331() {
        return 40;
    }

    @Override
    protected int method_25329() {
        return this.field_22758 - 10;
    }

    @Override
    public double method_25341() {
        Counters.SCROLL_AMOUNT_OVERRIDE.incrementAndGet();
        return super.method_25341();
    }

    /** the scroll members, as a 1.21.3 key handler called them */
    public String probe() {
        int max = this.method_25331();
        this.method_25307(1000);
        double amount = this.method_25341();
        this.method_60322();
        boolean visible = this.method_57717();
        int bar = this.method_25329();
        return max + " " + amount + " " + visible + " " + bar + " " + this.field_22758;
    }

    public double amount() {
        return this.method_25341();
    }

    public void scrollTo(double a) {
        this.method_25307(a);
    }

    public static class Row extends class_4280.class_4281<Row> {
        final class_327 font;
        final String text;

        Row(class_327 font, String text) {
            this.font = font;
            this.text = text;
        }

        @Override
        public void method_25343(class_332 g, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float pt) {
            g.method_25303(this.font, this.text, left + 2, top + 2, 0xFFFFFF);
        }

        @Override
        public class_2561 method_37006() {
            return class_2561.method_43470(this.text);
        }
    }
}

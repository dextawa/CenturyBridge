package top.dext.centurybridge.facade.v1_20_2;

/**
 * 1.20.1 PositionImpl (class_2376), gone in 1.20.2: a Position holding three coordinates, ported
 * from the 1.20.1 class as it was. Still a real Position (class_2374), so it goes wherever 1.20.2
 * takes one. The 1.20.1 fields were protected; public here, as an access widener never reaches a
 * CenturyBridge class.
 */
public class class_2376 implements net.minecraft.class_2374 {

    public final double field_11119;
    public final double field_11118;
    public final double field_11117;

    public class_2376(double x, double y, double z) {
        this.field_11119 = x;
        this.field_11118 = y;
        this.field_11117 = z;
    }

    @Override
    public double method_10216() {
        return this.field_11119;
    }

    @Override
    public double method_10214() {
        return this.field_11118;
    }

    @Override
    public double method_10215() {
        return this.field_11117;
    }
}

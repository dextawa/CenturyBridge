package top.dext.centurybridge.step.v1_20_5.rt;

/**
 * 1.20.4's Hopper suck area on 1.20.5. 1.20.4 described where a hopper picks items up as a
 * VoxelShape -- INSIDE (2..14, 11..16, 2..14) united with ABOVE (0..16, 16..32, 0..16), offered
 * as the default getSuckShape(), overridable -- and HopperBlockEntity queried each box of it
 * (getItemsAtAndAbove) or tested an entity against the whole shape (entityInside). 1.20.5 keeps a
 * single box, SUCK_AABB = (0..16, 11..32, 0..16), behind getSuckAabb(), and added the abstract
 * isGridAligned(): a grid-aligned hopper takes no items from the world while a full block sits on
 * top of it, a test 1.20.4's suckInItems did not make.
 *
 * <ul>
 * <li>The constants are 1.20.4's shapes, built as 1.20.4's Hopper.&lt;clinit&gt; built them.</li>
 * <li>A mod hopper class overriding getSuckShape() is handed to 1.20.5 with getSuckAabb() as the
 *     bounds of its shape (override hook, bridge mode); 1.20.5's own two users of the area,
 *     HopperBlockEntity.getItemsAtAndAbove and entityInside, take such a hopper's shape itself,
 *     box by box as 1.20.4 did (CbBlockHopperMixin).</li>
 * <li>A mod's call of getSuckShape(): the receiver's own shape when its class overrides it,
 *     1.20.4's default for a hopper still on the default area, and the box itself for one that
 *     answers another box (a 1.20.5 class overriding getSuckAabb).</li>
 * <li>A mod class implementing Hopper itself (it declares getLevelX/Y/Z) gets isGridAligned()
 *     answering false: no full-block test, as in 1.20.4.</li>
 * </ul>
 */
public final class CbBlockHopper {

    /** Hopper.INSIDE */
    private static final net.minecraft.class_265 INSIDE = net.minecraft.class_2248.method_9541(2.0, 11.0, 2.0, 14.0, 16.0, 14.0);
    /** Hopper.ABOVE */
    private static final net.minecraft.class_265 ABOVE = net.minecraft.class_2248.method_9541(0.0, 16.0, 0.0, 16.0, 32.0, 16.0);
    /** Hopper.SUCK = INSIDE | ABOVE */
    private static final net.minecraft.class_265 SUCK = net.minecraft.class_259.method_1084(INSIDE, ABOVE);

    /** the 1.20.4 getSuckShape() a mod hopper class overrides (adapter of the override hook) */
    public interface Legacy {
        net.minecraft.class_265 method_11262();
    }

    /** super's getSuckAabb(), reached from such a class's super.getSuckShape() */
    public interface Super {
        net.minecraft.class_238 cb$super$method_11262();
    }

    /** Hopper.INSIDE */
    public static net.minecraft.class_265 field_12025() {
        return INSIDE;
    }

    /** Hopper.ABOVE */
    public static net.minecraft.class_265 field_12027() {
        return ABOVE;
    }

    /** Hopper.SUCK */
    public static net.minecraft.class_265 field_12026() {
        return SUCK;
    }

    /** 1.20.4's shape for a 1.20.5 suck box: 1.20.4's default for the default box, else the box */
    private static net.minecraft.class_265 shapeOf(net.minecraft.class_238 box) {
        return box == net.minecraft.class_2615.field_12026 ? SUCK : net.minecraft.class_259.method_1078(box);
    }

    /** Hopper.getSuckShape() called on any hopper */
    public static net.minecraft.class_265 method_11262(net.minecraft.class_2615 self) {
        return self instanceof Legacy legacy ? legacy.method_11262() : shapeOf(self.method_11262());
    }

    /**
     * getSuckAabb() of a mod hopper class overriding getSuckShape(): the bounds of its shape -- one
     * box is all 1.20.5's type can say; vanilla's own users take the whole shape (see the class
     * comment). An empty shape, which 1.20.4 queried no box of, has no bounds: a box of no volume.
     */
    public static net.minecraft.class_238 getSuckAabb(Legacy self) {
        net.minecraft.class_265 shape = self.method_11262();
        return shape.method_1110() ? new net.minecraft.class_238(0.0, 0.0, 0.0, 0.0, 0.0, 0.0) : shape.method_1107();
    }

    /** a mod hopper's super.getSuckShape(): super's area as 1.20.4's shape */
    public static net.minecraft.class_265 superSuckShape(Super self) {
        return shapeOf(self.cb$super$method_11262());
    }

    /** isGridAligned() of a mod class implementing Hopper itself */
    public static boolean isGridAligned(net.minecraft.class_2615 self) {
        return false;
    }

    /** HopperBlockEntity.getItemsAtAndAbove(level, hopper) as 1.20.4 ran it, for a hopper with a 1.20.4 shape */
    public static java.util.List<net.minecraft.class_1542> itemsAtAndAbove(net.minecraft.class_1937 level,
            net.minecraft.class_2615 hopper, net.minecraft.class_265 shape) {
        java.util.List<net.minecraft.class_1542> out = new java.util.ArrayList<>();
        for (net.minecraft.class_238 box : shape.method_1090()) {
            out.addAll(level.method_8390(net.minecraft.class_1542.class,
                box.method_989(hopper.method_11266() - 0.5, hopper.method_11264() - 0.5, hopper.method_11265() - 0.5),
                net.minecraft.class_1301.field_6154));
        }
        return out;
    }

    private CbBlockHopper() {
    }
}

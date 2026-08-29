package top.dext.centurybridge.facade.v1_20_2;

/**
 * Facade for 1.20.1 net/minecraft/class_2345 (BlockSourceImpl), deleted in 1.20.2 when
 * BlockPointer (class_2342) became a final record. Standalone value object carrying the old
 * accessor surface. It is NOT a class_2342, so a site that passes it into a dispenser API
 * stays unresolved (and is ledgered) -- the mod-side reads work.
 */
public class class_2345 {
    private final net.minecraft.class_3218 world;
    private final net.minecraft.class_2338 pos;

    public class_2345(net.minecraft.class_3218 world, net.minecraft.class_2338 pos) {
        this.world = world;
        this.pos = pos;
    }
    public net.minecraft.class_3218 method_10207() { return world; }
    public double method_10216() { return (double) pos.method_10263() + 0.5; }
    public double method_10214() { return (double) pos.method_10264() + 0.5; }
    public double method_10215() { return (double) pos.method_10260() + 0.5; }
    public net.minecraft.class_2338 method_10122() { return pos; }
    public net.minecraft.class_2680 method_10120() { return world.method_8320(pos); }
    @SuppressWarnings("unchecked")
    public <T extends net.minecraft.class_2586> T method_10121() { return (T) world.method_8321(pos); }
}

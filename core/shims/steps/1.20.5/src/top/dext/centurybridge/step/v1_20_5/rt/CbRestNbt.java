package top.dext.centurybridge.step.v1_20_5.rt;

/**
 * 1.20.4 NbtUtils members 1.20.5 changed, with 1.20.4's bodies on 1.20.5's CompoundTag.
 */
public final class CbRestNbt {

    /** 1.20.4 NbtUtils.readBlockPos(tag): the position under X, Y, Z (1.20.5 reads an int array under a key) */
    public static net.minecraft.class_2338 method_10691(net.minecraft.class_2487 tag) {
        return new net.minecraft.class_2338(tag.method_10550("X"), tag.method_10550("Y"), tag.method_10550("Z"));
    }

    /** 1.20.4 NbtUtils.writeBlockPos(pos): a compound with X, Y, Z (1.20.5 writes an int array) */
    public static net.minecraft.class_2487 method_10692(net.minecraft.class_2338 pos) {
        net.minecraft.class_2487 tag = new net.minecraft.class_2487();
        tag.method_10569("X", pos.method_10263());
        tag.method_10569("Y", pos.method_10264());
        tag.method_10569("Z", pos.method_10260());
        return tag;
    }

    private CbRestNbt() {
    }
}

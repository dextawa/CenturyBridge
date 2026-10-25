package top.dext.centurybridge.step.v1_21_6.facade;

/**
 * 1.21.5's TerracottaBlock (class_10738), which 1.21.6 removed (its Blocks.TERRACOTTA is a plain Block; the ambient
 * desert sounds moved to SandBlock and the dry vegetation blocks, each with its own rules). Members as 1.21.5's
 * (javap): a simple codec, and animateTick playing 1.21.5's AmbientDesertBlockSoundsPlayer.playAmbientBlockSounds,
 * which 1.21.6 no longer has (restored in CbDesertSounds).
 */
public class TerracottaBlock extends net.minecraft.class_2248 {

    public static final com.mojang.serialization.MapCodec<TerracottaBlock> field_56466 = method_54094(TerracottaBlock::new);

    public TerracottaBlock(net.minecraft.class_4970.class_2251 properties) {
        super(properties);
    }

    @Override
    public com.mojang.serialization.MapCodec<TerracottaBlock> method_53969() {
        return field_56466;
    }

    /** animateTick */
    @Override
    public void method_9496(net.minecraft.class_2680 state, net.minecraft.class_1937 level, net.minecraft.class_2338 pos,
            net.minecraft.class_5819 random) {
        top.dext.centurybridge.step.v1_21_6.rt.CbDesertSounds.method_67408(state, level, pos, random);
    }
}

package top.dext.centurybridge.facade.v1_20_4;

/**
 * Facade for 1.20.1 net/minecraft/class_2647 (AbstractTreeGrower), deleted at 1.20.3 when
 * sapling growth became data-driven (class_8813 TreeGrower). Mods subclass it and override
 * getConfiguredFeature; reached via classRenames so every reference moves here.
 *
 * generate() and the flower scan are the 1.20.1 bodies ported verbatim (fluid legacy block
 * put in place first with flag 4, sendBlockUpdated on success, original state restored on
 * failure). Reachability: vanilla only grows a sapling through SaplingBlock, whose 1.20.1
 * constructor (taking this grower) does not exist on 1.20.4 -- until that constructor is
 * bridged, a legacy sapling's grower is never called and the block construction fails
 * honestly at its call site. Member names are intermediary tokens.
 */
public abstract class class_2647 {

    public class_2647() {
    }

    /** getConfiguredFeature(random, hasFlowers) */
    public abstract net.minecraft.class_5321<net.minecraft.class_2975<?, ?>> method_11430(
            net.minecraft.class_5819 random, boolean hasFlowers);

    /** generate(level, chunkGenerator, pos, state, random) */
    public boolean method_11431(net.minecraft.class_3218 level, net.minecraft.class_2794 generator,
            net.minecraft.class_2338 pos, net.minecraft.class_2680 state, net.minecraft.class_5819 random) {
        net.minecraft.class_5321<net.minecraft.class_2975<?, ?>> key = method_11430(random, method_24282(level, pos));
        if (key == null) {
            return false;
        }
        net.minecraft.class_6880<net.minecraft.class_2975<?, ?>> holder = level.method_30349()
            .method_30530(net.minecraft.class_7924.field_41239).method_40264(key).orElse(null);
        if (holder == null) {
            return false;
        }
        net.minecraft.class_2975<?, ?> feature = holder.comp_349();
        net.minecraft.class_2680 fluid = level.method_8316(pos).method_15759();
        level.method_8652(pos, fluid, 4);
        if (feature.method_12862(level, generator, random, pos)) {
            if (level.method_8320(pos) == fluid) {
                level.method_8413(pos, state, fluid, 2);
            }
            return true;
        }
        level.method_8652(pos, state, 4);
        return false;
    }

    /** hasFlowers(level, pos): any FLOWERS-tagged block in the 5x3x5 box around the sapling */
    public boolean method_24282(net.minecraft.class_1936 level, net.minecraft.class_2338 pos) {
        for (net.minecraft.class_2338 p : net.minecraft.class_2338.method_10097(
                pos.method_10074().method_10076(2).method_10088(2),
                pos.method_10084().method_10077(2).method_10089(2))) {
            if (level.method_8320(p).method_26164(net.minecraft.class_3481.field_20339)) {
                return true;
            }
        }
        return false;
    }
}

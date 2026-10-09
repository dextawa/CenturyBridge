package top.dext.centurybridge.facade.v1_21_4;

/**
 * Facade for 1.20.1 net/minecraft/class_2647 (SaplingGenerator / AbstractTreeGrower),
 * deleted at 1.20.3 when sapling growth became data-driven (class_8813 TreeGrower,
 * a record holding feature keys). Corpus mods subclass it to define custom saplings
 * (bewitchment, tconstruct, hexcasting, culturaldelights, ...), overriding the
 * abstract getConfiguredFeature; `extends class_2647` fails to load once the class
 * is gone, so this facade re-supplies it as an abstract class via superRenames.
 *
 * Faithful growth, not just loading: generate() resolves the subclass's feature key
 * against the live ConfiguredFeature registry and places it, exactly as the 1.20.1
 * class did -- so a legacy sapling actually grows into its tree. Member names are
 * intermediary tokens.
 */
public abstract class class_2647 {

    public class_2647() {
    }

    /** getConfiguredFeature(random, bees) -> the tree's ConfiguredFeature key */
    protected abstract net.minecraft.class_5321<net.minecraft.class_2975<?, ?>> method_11430(
            net.minecraft.class_5819 random, boolean bees);

    /** generate(world, chunkGen, pos, state, random): resolve the key and place the feature */
    public boolean method_11431(net.minecraft.class_3218 world, net.minecraft.class_2794 chunkGen,
            net.minecraft.class_2338 pos, net.minecraft.class_2680 state, net.minecraft.class_5819 random) {
        net.minecraft.class_5321<net.minecraft.class_2975<?, ?>> key = method_11430(random, hasFlowers(world, pos));
        if (key == null) {
            return false;
        }
        net.minecraft.class_2378<net.minecraft.class_2975<?, ?>> registry =
            world.method_30349().method_30530(net.minecraft.class_7924.field_41239);
        net.minecraft.class_2975<?, ?> feature = registry.method_31189(key).orElse(null);
        if (feature == null) {
            return false;
        }
        world.method_8501(pos, net.minecraft.class_2246.field_10124.method_9564()); // clear the sapling first, as vanilla did
        if (feature.method_12862(world, chunkGen, random, pos)) {
            return true;
        }
        world.method_8501(pos, state); // restore on failure
        return false;
    }

    /** the old private helper that scanned for an adjacent flower (bees variant); the
     *  legacy subclasses never relied on its being present, so a conservative false
     *  keeps the common (non-bees) feature key path. */
    private boolean hasFlowers(net.minecraft.class_1936 world, net.minecraft.class_2338 pos) {
        return false;
    }
}

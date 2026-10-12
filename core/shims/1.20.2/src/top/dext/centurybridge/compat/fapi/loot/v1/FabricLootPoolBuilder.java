package top.dext.centurybridge.compat.fapi.loot.v1;

/**
 * Fabric API loot tables v1's FabricLootPoolBuilder: a LootPool.Builder answering itself from every
 * call, with the v1 additions done through loot API v2's builder methods (which 1.20.2's Fabric API
 * still injects into LootPool.Builder), as in 1.20.1. The vanilla-named overrides are the bridges
 * 1.20.1's covariant overrides had: a vanilla caller of a builder method reaches the v1 one.
 */
public class FabricLootPoolBuilder extends net.minecraft.class_55.class_56 {

    private FabricLootPoolBuilder() {
    }

    private FabricLootPoolBuilder(net.minecraft.class_55 pool) {
        copyFrom(pool, true);
    }

    private net.fabricmc.fabric.api.loot.v2.FabricLootPoolBuilder asV2() {
        return (net.fabricmc.fabric.api.loot.v2.FabricLootPoolBuilder) (Object) this;
    }

    public FabricLootPoolBuilder rolls(net.minecraft.class_5658 rolls) {
        super.method_352(rolls);
        return this;
    }

    public FabricLootPoolBuilder with(net.minecraft.class_79.class_80<?> entry) {
        super.method_351(entry);
        return this;
    }

    public FabricLootPoolBuilder conditionally(net.minecraft.class_5341.class_210 condition) {
        super.method_356(condition);
        return this;
    }

    public FabricLootPoolBuilder apply(net.minecraft.class_117.class_118 function) {
        super.method_353(function);
        return this;
    }

    public FabricLootPoolBuilder withEntry(net.minecraft.class_79 entry) {
        asV2().with(entry);
        return this;
    }

    public FabricLootPoolBuilder withCondition(net.minecraft.class_5341 condition) {
        asV2().conditionally(condition);
        return this;
    }

    public FabricLootPoolBuilder withFunction(net.minecraft.class_117 function) {
        asV2().apply(function);
        return this;
    }

    /** copies the entries, conditions and functions of the pool */
    public FabricLootPoolBuilder copyFrom(net.minecraft.class_55 pool) {
        return copyFrom(pool, false);
    }

    /** copies the entries, conditions and functions of the pool, and its rolls if asked */
    public FabricLootPoolBuilder copyFrom(net.minecraft.class_55 pool, boolean copyRolls) {
        FabricLootPool extended = (FabricLootPool) pool;
        asV2().with(extended.getEntries());
        asV2().conditionally(extended.getConditions());
        asV2().apply(extended.getFunctions());
        if (copyRolls) {
            rolls(extended.getRolls());
        }
        return this;
    }

    public static FabricLootPoolBuilder builder() {
        return new FabricLootPoolBuilder();
    }

    public static FabricLootPoolBuilder of(net.minecraft.class_55 pool) {
        return new FabricLootPoolBuilder(pool);
    }

    @Override
    public net.minecraft.class_55.class_56 method_352(net.minecraft.class_5658 rolls) {
        return rolls(rolls);
    }

    @Override
    public net.minecraft.class_55.class_56 method_351(net.minecraft.class_79.class_80<?> entry) {
        return with(entry);
    }

    @Override
    public net.minecraft.class_55.class_56 method_356(net.minecraft.class_5341.class_210 condition) {
        return conditionally(condition);
    }

    @Override
    public net.minecraft.class_55.class_56 method_353(net.minecraft.class_117.class_118 function) {
        return apply(function);
    }
}

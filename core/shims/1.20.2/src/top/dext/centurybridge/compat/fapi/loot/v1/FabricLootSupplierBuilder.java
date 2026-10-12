package top.dext.centurybridge.compat.fapi.loot.v1;

import java.util.Collection;

/**
 * Fabric API loot tables v1's FabricLootSupplierBuilder: a LootTable.Builder answering itself from
 * every call, with the v1 additions done through loot API v2's table builder methods (which 1.20.2's
 * Fabric API still injects into LootTable.Builder), as in 1.20.1. Extendable as 1.20.1's was; the
 * vanilla-named overrides are the bridges 1.20.1's covariant overrides had, so a vanilla caller of a
 * builder method reaches the v1 one (and a subclass's override of it).
 */
public class FabricLootSupplierBuilder extends net.minecraft.class_52.class_53 {

    protected FabricLootSupplierBuilder() {
    }

    private FabricLootSupplierBuilder(net.minecraft.class_52 table) {
        copyFrom(table, true);
    }

    private net.fabricmc.fabric.api.loot.v2.FabricLootTableBuilder asV2() {
        return (net.fabricmc.fabric.api.loot.v2.FabricLootTableBuilder) (Object) this;
    }

    public FabricLootSupplierBuilder pool(net.minecraft.class_55.class_56 pool) {
        super.method_336(pool);
        return this;
    }

    public FabricLootSupplierBuilder type(net.minecraft.class_176 type) {
        super.method_334(type);
        return this;
    }

    public FabricLootSupplierBuilder apply(net.minecraft.class_117.class_118 function) {
        super.method_335(function);
        return this;
    }

    public FabricLootSupplierBuilder withPool(net.minecraft.class_55 pool) {
        asV2().pool(pool);
        return this;
    }

    public FabricLootSupplierBuilder withFunction(net.minecraft.class_117 function) {
        asV2().apply(function);
        return this;
    }

    public FabricLootSupplierBuilder withPools(Collection<net.minecraft.class_55> pools) {
        asV2().pools(pools);
        return this;
    }

    public FabricLootSupplierBuilder withFunctions(Collection<net.minecraft.class_117> functions) {
        asV2().apply(functions);
        return this;
    }

    /** copies the pools and functions of the table */
    public FabricLootSupplierBuilder copyFrom(net.minecraft.class_52 table) {
        return copyFrom(table, false);
    }

    /** copies the pools and functions of the table, and its context type if asked */
    public FabricLootSupplierBuilder copyFrom(net.minecraft.class_52 table, boolean copyType) {
        FabricLootSupplier extended = (FabricLootSupplier) table;
        asV2().pools(extended.getPools());
        asV2().apply(extended.getFunctions());
        if (copyType) {
            type(extended.getType());
        }
        return this;
    }

    public static FabricLootSupplierBuilder builder() {
        return new FabricLootSupplierBuilder();
    }

    public static FabricLootSupplierBuilder of(net.minecraft.class_52 table) {
        return new FabricLootSupplierBuilder(table);
    }

    @Override
    public net.minecraft.class_52.class_53 method_336(net.minecraft.class_55.class_56 pool) {
        return pool(pool);
    }

    @Override
    public net.minecraft.class_52.class_53 method_334(net.minecraft.class_176 type) {
        return type(type);
    }

    @Override
    public net.minecraft.class_52.class_53 method_335(net.minecraft.class_117.class_118 function) {
        return apply(function);
    }
}

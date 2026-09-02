package top.dext.centurybridge.compat.fapi;

import java.util.function.Predicate;

import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

/**
 * fabric-object-builder-api-v1's VillagerProfessionBuilder (2 corpus mods). 26.2's
 * VillagerProfession is a record (name, held/acquirable job-site predicates,
 * requested items, secondary POIs, work sound, trade sets by level); build()
 * constructs it exactly as the 1.20.1 builder did and the mod registers it. Trade
 * sets are data-driven in 26.x, so the built profession starts with none.
 */
public final class CbVillagerProfessionBuilder {

    private Identifier id;
    private Predicate<Holder<PoiType>> workstation = holder -> false;
    private Predicate<Holder<PoiType>> jobSite;
    private final ImmutableSet.Builder<Item> harvestableItems = ImmutableSet.builder();
    private final ImmutableSet.Builder<Block> secondaryJobSites = ImmutableSet.builder();
    private SoundEvent workSound;

    private CbVillagerProfessionBuilder() {
    }

    public static CbVillagerProfessionBuilder create() {
        return new CbVillagerProfessionBuilder();
    }

    public CbVillagerProfessionBuilder id(Identifier id) {
        this.id = id;
        return this;
    }

    public CbVillagerProfessionBuilder workstation(ResourceKey<PoiType> key) {
        return workstation(holder -> holder.is(key));
    }

    public CbVillagerProfessionBuilder workstation(Predicate<Holder<PoiType>> predicate) {
        this.workstation = predicate;
        return this;
    }

    public CbVillagerProfessionBuilder jobSite(Predicate<Holder<PoiType>> predicate) {
        this.jobSite = predicate;
        return this;
    }

    public CbVillagerProfessionBuilder harvestableItems(Item... items) {
        this.harvestableItems.add(items);
        return this;
    }

    public CbVillagerProfessionBuilder harvestableItems(Iterable<Item> items) {
        this.harvestableItems.addAll(items);
        return this;
    }

    public CbVillagerProfessionBuilder secondaryJobSites(Block... blocks) {
        this.secondaryJobSites.add(blocks);
        return this;
    }

    public CbVillagerProfessionBuilder secondaryJobSites(Iterable<Block> blocks) {
        this.secondaryJobSites.addAll(blocks);
        return this;
    }

    public CbVillagerProfessionBuilder workSound(SoundEvent sound) {
        this.workSound = sound;
        return this;
    }

    public VillagerProfession build() {
        if (id == null) {
            throw new IllegalStateException("VillagerProfessionBuilder: no id set");
        }
        Component name = Component.translatable("entity." + id.getNamespace() + ".villager." + id.getPath());
        return new VillagerProfession(name, workstation, jobSite == null ? workstation : jobSite,
            harvestableItems.build(), secondaryJobSites.build(), workSound, Int2ObjectMaps.emptyMap());
    }
}

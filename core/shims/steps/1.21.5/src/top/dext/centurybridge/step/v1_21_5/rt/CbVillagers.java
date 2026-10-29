package top.dext.centurybridge.step.v1_21_5.rt;

import com.google.common.collect.ImmutableSet;
import java.util.function.Predicate;
import net.minecraft.class_1792;
import net.minecraft.class_2248;
import net.minecraft.class_2960;
import net.minecraft.class_3414;
import net.minecraft.class_3850;
import net.minecraft.class_3852;
import net.minecraft.class_3854;
import net.minecraft.class_4158;
import net.minecraft.class_5321;
import net.minecraft.class_6880;
import net.minecraft.class_7923;

/**
 * 1.21.4's VillagerData / VillagerProfession members that 1.21.5 reshaped (javap -c -p both). 1.21.5 made VillagerData
 * a record of registry holders (Holder&lt;VillagerType&gt;, Holder&lt;VillagerProfession&gt;, level) and the VillagerProfession
 * constants ResourceKeys of the profession registry (the professions themselves are registered under them by
 * VillagerProfession.bootstrap, as 1.21.4 registered its constants) -- VillagerType's likewise -- and a profession's
 * name a Component.
 * <ul>
 * <li>VillagerData: new(type, profession, level), setType / setProfession take the value: 1.21.5's with the value's
 * holder in its built-in registry (BuiltInRegistries.VILLAGER_TYPE / VILLAGER_PROFESSION wrapAsHolder: the reference
 * holder of a registered value, a direct holder otherwise, as 1.21.4 kept the value itself); getType / getProfession
 * / getLevel answer the holders' values and the level.</li>
 * <li>The VillagerProfession constants (ARMORER ... WEAPONSMITH, NONE / NITWIT) and the VillagerType constants (DESERT
 * ... TAIGA): the values registered under 1.21.5's keys of the same names; VillagerType.byBiome: the type registered
 * under the key 1.21.5 answers for the biome.</li>
 * <li>VillagerProfession.name(): 1.21.4's name string, the profession's registry path (VillagerProfession.register
 * named and registered each profession by the same string; 1.21.4's Villager.getTypeName read the path); a profession
 * no registry knows answers its 1.21.5 name's text.</li>
 * <li>new VillagerProfession(name, ...): 1.21.5's with the name 1.21.4's Villager.getTypeName showed for it,
 * Component.translatable("entity.minecraft.villager." + name) (1.21.4: villager type description id + "." + registry
 * path, which for a profession registered under its name is the same key).</li>
 * </ul>
 */
public final class CbVillagers {

    private CbVillagers() {
    }

    private static class_3852 profession(class_5321<class_3852> key) {
        return class_7923.field_41195.method_31140(key);
    }

    /** new VillagerData(VillagerType, VillagerProfession, int) */
    public static class_3850 newVillagerData(class_3854 type, class_3852 profession, int level) {
        return new class_3850(class_7923.field_41194.method_47983(type), class_7923.field_41195.method_47983(profession), level);
    }

    /** VillagerData.getType() */
    public static class_3854 method_16919(class_3850 data) {
        return data.comp_3520().comp_349();
    }

    /** VillagerData.getProfession() */
    public static class_3852 method_16924(class_3850 data) {
        return data.comp_3521().comp_349();
    }

    /** VillagerData.getLevel() */
    public static int method_16925(class_3850 data) {
        return data.comp_3522();
    }

    /** VillagerData.setType(VillagerType) */
    public static class_3850 method_16922(class_3850 data, class_3854 type) {
        return data.method_16922(class_7923.field_41194.method_47983(type));
    }

    /** VillagerData.setProfession(VillagerProfession) */
    public static class_3850 method_16921(class_3850 data, class_3852 profession) {
        return data.method_16921(class_7923.field_41195.method_47983(profession));
    }

    /** VillagerProfession.name() */
    public static String comp_818(class_3852 profession) {
        class_2960 id = class_7923.field_41195.method_10221(profession);
        return id != null ? id.method_12832() : profession.comp_818().getString();
    }

    /** new VillagerProfession(String, Predicate, Predicate, ImmutableSet, ImmutableSet, SoundEvent) */
    public static class_3852 newVillagerProfession(String name, Predicate<class_6880<class_4158>> heldJobSite,
            Predicate<class_6880<class_4158>> acquirableJobSite, ImmutableSet<class_1792> requestedItems, ImmutableSet<class_2248> secondaryPoi,
            class_3414 workSound) {
        return new class_3852(net.minecraft.class_2561.method_43471("entity.minecraft.villager." + name), heldJobSite, acquirableJobSite,
            requestedItems, secondaryPoi, workSound);
    }

    /** VillagerProfession.NONE */
    public static class_3852 field_17051() {
        return profession(class_3852.field_17051);
    }

    /** VillagerProfession.ARMORER */
    public static class_3852 field_17052() {
        return profession(class_3852.field_17052);
    }

    /** VillagerProfession.BUTCHER */
    public static class_3852 field_17053() {
        return profession(class_3852.field_17053);
    }

    /** VillagerProfession.CARTOGRAPHER */
    public static class_3852 field_17054() {
        return profession(class_3852.field_17054);
    }

    /** VillagerProfession.CLERIC */
    public static class_3852 field_17055() {
        return profession(class_3852.field_17055);
    }

    /** VillagerProfession.FARMER */
    public static class_3852 field_17056() {
        return profession(class_3852.field_17056);
    }

    /** VillagerProfession.FISHERMAN */
    public static class_3852 field_17057() {
        return profession(class_3852.field_17057);
    }

    /** VillagerProfession.FLETCHER */
    public static class_3852 field_17058() {
        return profession(class_3852.field_17058);
    }

    /** VillagerProfession.LEATHERWORKER */
    public static class_3852 field_17059() {
        return profession(class_3852.field_17059);
    }

    /** VillagerProfession.LIBRARIAN */
    public static class_3852 field_17060() {
        return profession(class_3852.field_17060);
    }

    /** VillagerProfession.MASON */
    public static class_3852 field_17061() {
        return profession(class_3852.field_17061);
    }

    /** VillagerProfession.NITWIT */
    public static class_3852 field_17062() {
        return profession(class_3852.field_17062);
    }

    /** VillagerProfession.SHEPHERD */
    public static class_3852 field_17063() {
        return profession(class_3852.field_17063);
    }

    /** VillagerProfession.TOOLSMITH */
    public static class_3852 field_17064() {
        return profession(class_3852.field_17064);
    }

    /** VillagerProfession.WEAPONSMITH */
    public static class_3852 field_17065() {
        return profession(class_3852.field_17065);
    }

    /** VillagerType.DESERT */
    public static class_3854 field_17071() {
        return class_7923.field_41194.method_31140(class_3854.field_17071);
    }

    /** VillagerType.JUNGLE */
    public static class_3854 field_17072() {
        return class_7923.field_41194.method_31140(class_3854.field_17072);
    }

    /** VillagerType.PLAINS */
    public static class_3854 field_17073() {
        return class_7923.field_41194.method_31140(class_3854.field_17073);
    }

    /** VillagerType.SAVANNA */
    public static class_3854 field_17074() {
        return class_7923.field_41194.method_31140(class_3854.field_17074);
    }

    /** VillagerType.SNOW */
    public static class_3854 field_17075() {
        return class_7923.field_41194.method_31140(class_3854.field_17075);
    }

    /** VillagerType.SWAMP */
    public static class_3854 field_17076() {
        return class_7923.field_41194.method_31140(class_3854.field_17076);
    }

    /** VillagerType.TAIGA */
    public static class_3854 field_17077() {
        return class_7923.field_41194.method_31140(class_3854.field_17077);
    }

    /** VillagerType.byBiome(Holder&lt;Biome&gt;): the type 1.21.5 keys the biome to */
    public static class_3854 method_16930(class_6880<net.minecraft.class_1959> biome) {
        return class_7923.field_41194.method_31140(class_3854.method_16930(biome));
    }

    /**
     * a 1.21.4 profession as the holder 1.21.5's members take (VillagerGoalPackages.getXPackage(profession, speed)): its
     * holder in BuiltInRegistries.VILLAGER_PROFESSION, as VillagerData does above
     */
    public static class_6880<class_3852> professionHolder(class_3852 profession) {
        return class_7923.field_41195.method_47983(profession);
    }

    /** a 1.21.4 villager type as the holder 1.21.5's Villager constructor takes */
    public static class_6880<class_3854> typeHolder(class_3854 type) {
        return class_7923.field_41194.method_47983(type);
    }

    /**
     * new Villager(EntityType, Level, VillagerType). 1.21.5's (EntityType, Level, Holder) constructor is 1.21.5's
     * (EntityType, Level) one -- which ends in setVillagerData(PLAINS, profession NONE) -- with the type holder in place of
     * PLAINS (javap -c 1.21.5 class_1646: both reach super(type, level), the same fields and navigation, then
     * setVillagerData(getVillagerData().withType(holder).withProfession(NONE))). Built through the members every later
     * release keeps (26.2 declares only Villager(EntityType, Level); getVillagerData / setVillagerData /
     * VillagerData.withType(Holder) stay): the two-argument constructor, then the data with the type swapped. The
     * profession stays NONE, so setVillagerData keeps the (empty) offers exactly as the one call did.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static net.minecraft.class_1646 newVillager(net.minecraft.class_1299 entityType, net.minecraft.class_1937 level, class_3854 type) {
        net.minecraft.class_1646 v = new net.minecraft.class_1646(entityType, level);
        v.method_7195(v.method_7231().method_16922(typeHolder(type)));
        return v;
    }

    /**
     * the gossips a 1.21.4 Villager.setGossips(Tag) / ZombieVillager.setGossips(Tag) read from the tag: 1.21.4 decoded the
     * tag's GossipEntry list (Target / Type / Value, the same codec in 1.21.5: javap class_4136$class_4138) partially,
     * logging what did not read, and put each entry into the villager's gossips; 1.21.5 takes a GossipContainer and
     * puts its entries the same way (Villager.setGossips: GossipContainer.putAll, method_67652; ZombieVillager keeps it
     * for the cured villager as 1.21.4 kept the tag). The container is 1.21.5's GossipContainer.CODEC's read of the tag
     * (that list, decoded partially), empty when nothing reads.
     */
    public static net.minecraft.class_4136 gossips(net.minecraft.class_2520 tag) {
        return net.minecraft.class_4136.field_56649.parse(net.minecraft.class_2509.field_11560, tag)
            .resultOrPartial(msg -> LOGGER.warn("Failed to read gossips: {}", msg)).orElseGet(net.minecraft.class_4136::new);
    }

    private static final org.slf4j.Logger LOGGER = com.mojang.logging.LogUtils.getLogger();

    /** VillagerDataHolder.getVariant() (1.21.4's default: getVillagerData().getType()) */
    public static class_3854 method_47882(net.minecraft.class_3851 holder) {
        return holder.method_7231().comp_3520().comp_349();
    }

    /** VillagerDataHolder.setVariant(VillagerType) (1.21.4's default: setVillagerData(getVillagerData().setType(type))) */
    public static void method_47883(net.minecraft.class_3851 holder, class_3854 type) {
        holder.method_7195(holder.method_7231().method_16922(typeHolder(type)));
    }
}

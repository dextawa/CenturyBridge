package top.dext.centurybridge.step.v1_20_5.rt;

/**
 * 1.20.4's BeehiveBlockEntity occupant API on 1.20.5. 1.20.4 kept a stored bee as BeeData(entity
 * tag, ticks in hive, minimum occupation ticks -- 2400 with nectar, else 600) built from a tag
 * (storeBee(tag, ticks, hasNectar), its constructor first stripping the tags a hive does not keep),
 * added a bee through addOccupant(entity, hasNectar) = addOccupantWithPresetTicks(entity, hasNectar,
 * 0), and wrote the list as writeBees() ([{EntityData without UUID, TicksInHive, MinOccupationTicks}],
 * saved under "Bees"). 1.20.5 keeps an Occupant(entity data, ticks in hive, minimum ticks) record,
 * adds a bee with addOccupant(entity) -- nectar read from the bee's own saved HasNectar -- and saves
 * the list through Occupant.LIST_CODEC under "bees".
 *
 * <p>A mod's calls of the old shapes run 1.20.4's bodies on 1.20.5's API, each step dispatched as
 * 1.20.4 dispatched it: to the mod's own override where its class has one (the Legacy* adapters),
 * else to the 1.20.5 implementation. 1.20.5 calling a mod hive class that overrides an old shape
 * reaches the override with what 1.20.4's caller passed (a bee's own nectar); a mod overriding
 * writeBees() has its list saved and copied to the hive item, as 1.20.4 did (CbBlockBeehiveMixin).
 */
public final class CbBlockBeehive {

    /** 1.20.4's addOccupant(entity, hasNectar) a mod hive class overrides */
    public interface LegacyAddOccupant {
        void method_21848(net.minecraft.class_1297 entity, boolean hasNectar);
    }

    /** 1.20.4's addOccupantWithPresetTicks(entity, hasNectar, ticksInHive) */
    public interface LegacyAddWithTicks {
        void method_21849(net.minecraft.class_1297 entity, boolean hasNectar, int ticksInHive);
    }

    /** 1.20.4's storeBee(entityData, ticksInHive, hasNectar) */
    public interface LegacyStoreBee {
        void method_35292(net.minecraft.class_2487 entityData, int ticksInHive, boolean hasNectar);
    }

    /** 1.20.4's writeBees() */
    public interface LegacyWriteBees {
        net.minecraft.class_2499 method_21859();
    }

    /** super's storeBee(occupant), reached from a mod hive's super.storeBee(..) */
    public interface SuperStoreBee {
        void cb$super$method_35292(net.minecraft.class_4482.class_9309 occupant);
    }

    /** the hive's private state 1.20.4's bodies touch (CbBlockBeehiveAccessor) */
    public interface Hive {
        java.util.List<net.minecraft.class_4482.class_9309> cb$getBees();

        net.minecraft.class_2338 cb$savedFlowerPos();

        void cb$setSavedFlowerPos(net.minecraft.class_2338 pos);
    }

    /** the nectar 1.20.4's callers passed for an entity: a bee's own */
    public static boolean nectarOf(net.minecraft.class_1297 entity) {
        return entity instanceof net.minecraft.class_4466 bee && bee.method_21784();
    }

    /**
     * 1.20.4's BeeData from a tag: the tags a hive does not keep stripped from the tag itself, as 1.20.4's
     * BeeData constructor did -- 1.20.5's list of them (BeehiveBlockEntity.IGNORED_BEE_TAGS), since the
     * tag is a 1.20.5 entity's (1.20.5 renamed HivePos and Leash to hive_pos and leash, added Sleeping*)
     */
    public static net.minecraft.class_4482.class_9309 occupant(net.minecraft.class_2487 entityData, int ticksInHive, boolean hasNectar) {
        for (String t : top.dext.centurybridge.step.v1_20_5.mixin.CbBlockBeehiveAccessor.cb$ignoredBeeTags()) {
            entityData.method_10551(t);
        }
        return new net.minecraft.class_4482.class_9309(net.minecraft.class_9279.method_57456(entityData), ticksInHive,
            hasNectar ? 2400 : 600);
    }

    /** addOccupant(entity, hasNectar) called on any hive */
    public static void method_21848(net.minecraft.class_4482 self, net.minecraft.class_1297 entity, boolean hasNectar) {
        if (self instanceof LegacyAddOccupant legacy) {
            legacy.method_21848(entity, hasNectar);
        } else {
            superAddOccupant(self, entity, hasNectar);
        }
    }

    /** 1.20.4's addOccupant body (a mod's super call): addOccupantWithPresetTicks(entity, hasNectar, 0) */
    public static void superAddOccupant(net.minecraft.class_4482 self, net.minecraft.class_1297 entity, boolean hasNectar) {
        method_21849(self, entity, hasNectar, 0);
    }

    /** addOccupantWithPresetTicks(entity, hasNectar, ticks) called on any hive */
    public static void method_21849(net.minecraft.class_4482 self, net.minecraft.class_1297 entity, boolean hasNectar, int ticksInHive) {
        if (self instanceof LegacyAddWithTicks legacy) {
            legacy.method_21849(entity, hasNectar, ticksInHive);
        } else {
            superAddWithTicks(self, entity, hasNectar, ticksInHive);
        }
    }

    /** 1.20.4's addOccupantWithPresetTicks body on 1.20.5's API (also a mod's super call) */
    public static void superAddWithTicks(net.minecraft.class_4482 self, net.minecraft.class_1297 entity, boolean hasNectar, int ticksInHive) {
        if (self.method_23903() >= 3) { // 1.20.4: stored.size() >= 3
            return;
        }
        entity.method_5848();
        entity.method_5772();
        net.minecraft.class_2487 tag = new net.minecraft.class_2487();
        entity.method_5662(tag);
        method_35292(self, tag, ticksInHive, hasNectar);
        net.minecraft.class_1937 level = self.method_10997();
        if (level != null) {
            Hive hive = (Hive) self;
            if (entity instanceof net.minecraft.class_4466 bee && bee.method_21779()
                    && (hive.cb$savedFlowerPos() == null || level.field_9229.method_43056())) {
                hive.cb$setSavedFlowerPos(bee.method_21778());
            }
            net.minecraft.class_2338 pos = self.method_11016();
            level.method_43128(null, pos.method_10263(), pos.method_10264(), pos.method_10260(),
                net.minecraft.class_3417.field_20609, net.minecraft.class_3419.field_15245, 1.0F, 1.0F);
            level.method_43276(net.minecraft.class_5712.field_28733, pos,
                net.minecraft.class_5712.class_7397.method_43286(entity, self.method_11010()));
        }
        entity.method_31472();
        // 1.20.4 called BlockEntity's setChanged (super.setChanged()), not the hive's own fire check:
        // BlockEntity.setChanged(level, pos, state) as 1.20.5 has it
        net.minecraft.class_1937 l = self.method_10997();
        if (l != null) {
            l.method_8524(self.method_11016());
            if (!self.method_11010().method_26215()) {
                l.method_8455(self.method_11016(), self.method_11010().method_26204());
            }
        }
    }

    /** storeBee(entityData, ticks, hasNectar) called on any hive */
    public static void method_35292(net.minecraft.class_4482 self, net.minecraft.class_2487 entityData, int ticksInHive, boolean hasNectar) {
        if (self instanceof LegacyStoreBee legacy) {
            legacy.method_35292(entityData, ticksInHive, hasNectar);
        } else {
            self.method_35292(occupant(entityData, ticksInHive, hasNectar));
        }
    }

    /** a mod hive's super.storeBee(entityData, ticks, hasNectar): super's storeBee of 1.20.4's BeeData */
    public static void superStoreBee(SuperStoreBee self, net.minecraft.class_2487 entityData, int ticksInHive, boolean hasNectar) {
        self.cb$super$method_35292(occupant(entityData, ticksInHive, hasNectar));
    }

    /** storeBee(occupant) of a mod hive class overriding 1.20.4's storeBee (override hook bridge) */
    public static void storeBee(LegacyStoreBee self, net.minecraft.class_4482.class_9309 occupant) {
        self.method_35292(occupant.comp_2431().method_57461(), occupant.comp_2432(), occupant.comp_2433() == 2400);
    }

    /** addOccupant(entity) of a mod hive class overriding 1.20.4's addOccupant(entity, hasNectar) */
    public static void addOccupant(LegacyAddOccupant self, net.minecraft.class_1297 entity) {
        self.method_21848(entity, nectarOf(entity));
    }

    /** addOccupant(entity) of a mod hive class overriding only addOccupantWithPresetTicks: 1.20.4's addOccupant reached it with 0 ticks */
    public static void addOccupantWithTicks(LegacyAddWithTicks self, net.minecraft.class_1297 entity) {
        self.method_21849(entity, nectarOf(entity), 0);
    }

    /** writeBees() called on any hive */
    public static net.minecraft.class_2499 method_21859(net.minecraft.class_4482 self) {
        if (self instanceof LegacyWriteBees legacy) {
            return legacy.method_21859();
        }
        net.minecraft.class_2499 list = new net.minecraft.class_2499();
        for (net.minecraft.class_4482.class_9309 o : ((Hive) self).cb$getBees()) {
            net.minecraft.class_2487 data = o.comp_2431().method_57461();
            data.method_10551("UUID");
            net.minecraft.class_2487 bee = new net.minecraft.class_2487();
            bee.method_10566("EntityData", data);
            bee.method_10569("TicksInHive", o.comp_2432());
            bee.method_10569("MinOccupationTicks", o.comp_2433());
            list.add(bee);
        }
        return list;
    }

    /** a 1.20.4 bee list (writeBees' format) as 1.20.5's occupants */
    public static java.util.List<net.minecraft.class_4482.class_9309> occupants(net.minecraft.class_2499 bees) {
        java.util.List<net.minecraft.class_4482.class_9309> out = new java.util.ArrayList<>();
        for (int i = 0; i < bees.size(); i++) {
            net.minecraft.class_2487 bee = bees.method_10602(i);
            out.add(new net.minecraft.class_4482.class_9309(net.minecraft.class_9279.method_57456(bee.method_10562("EntityData")),
                bee.method_10550("TicksInHive"), bee.method_10550("MinOccupationTicks")));
        }
        return out;
    }

    private CbBlockBeehive() {
    }
}

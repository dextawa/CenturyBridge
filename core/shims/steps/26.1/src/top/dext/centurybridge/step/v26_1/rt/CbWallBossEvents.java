package top.dext.centurybridge.step.v26_1.rt;

import java.util.Map;
import java.util.UUID;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.bossevents.CustomBossEvent;
import net.minecraft.server.bossevents.CustomBossEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.dext.centurybridge.step.v26_1.mixin.CbWallBossEventsAccess;

/**
 * 1.21.11 custom boss event members on 26.1's. 1.21.11's events were saved through the world data at every
 * world save and had no dirty callback; 26.1's are saved data, each event telling its owner it changed.
 * 1.21.11's tag format is 26.1's codec's ({id: {Name, Visible, Value, Max, Color, Overlay, ...}}).
 */
public final class CbWallBossEvents {

    private static final Logger LOGGER = LoggerFactory.getLogger("centurybridge");

    private CbWallBossEvents() {
    }

    /** an event with no owner to tell: 1.21.11's events had no dirty callback */
    private static final Runnable NO_OWNER = () -> {
    };

    /** the UUID 1.21.11's event drew for itself (BossEvent's Mth.createInsecureUUID) */
    public static UUID newEventId() {
        return Mth.createInsecureUUID(RandomSource.create());
    }

    /** a dirty callback for a 1.21.11-constructed event: none, as 1.21.11's had none */
    public static Runnable noOwner() {
        return NO_OWNER;
    }

    /** 1.21.11 CustomBossEvents.create(id, name) */
    public static CustomBossEvent create(CustomBossEvents events, Identifier id, Component name) {
        return events.create(CbWallFrames.bossEventRandom(), id, name);
    }

    /** 1.21.11 CustomBossEvent.getTextId(): 26.1's customId() (a 1.21.11 override is renamed to it) */
    public static Identifier getTextId(CustomBossEvent event) {
        return event.customId();
    }

    /** 1.21.11 CustomBossEvent.load(id, packed): an event of its own (no owner, as 1.21.11's) */
    public static CustomBossEvent load(Identifier id, CustomBossEvent.Packed packed) {
        return CustomBossEvent.load(newEventId(), id, packed, NO_OWNER);
    }

    /** 1.21.11 CustomBossEvents.save(registries): every event packed under its id */
    public static CompoundTag save(CustomBossEvents events, HolderLookup.Provider registries) {
        Map<Identifier, CustomBossEvent.Packed> packed = Util.mapValues(((CbWallBossEventsAccess) events).cb$events(), CustomBossEvent::pack);
        Tag tag = CbWallBossEventsAccess.cb$eventsCodec().encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), packed).getOrThrow();
        return tag instanceof CompoundTag compound ? compound : new CompoundTag();
    }

    /** 1.21.11 CustomBossEvents.load(tag, registries): each saved event added under its id (an unreadable tag logged and skipped) */
    public static void load(CustomBossEvents events, CompoundTag tag, HolderLookup.Provider registries) {
        Map<Identifier, CustomBossEvent.Packed> packed = CbWallBossEventsAccess.cb$eventsCodec()
            .parse(registries.createSerializationContext(NbtOps.INSTANCE), tag)
            .resultOrPartial(e -> LOGGER.error("Failed to parse boss bar events: {}", e))
            .orElse(Map.of());
        Map<Identifier, CustomBossEvent> map = ((CbWallBossEventsAccess) events).cb$events();
        packed.forEach((id, p) -> map.put(id, CustomBossEvent.load(UUID.randomUUID(), id, p, events::setDirty)));
        events.setDirty();
    }
}

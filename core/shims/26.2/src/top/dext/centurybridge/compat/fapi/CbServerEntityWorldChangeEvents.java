package top.dext.centurybridge.compat.fapi;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityLevelChangeEvents;
import net.fabricmc.fabric.api.event.Event;

/** fabric-entity-events-v1's ServerEntityWorldChangeEvents (3 corpus mods): renamed ServerEntityLevelChangeEvents, SAMs afterChangeWorld -> afterChangeLevel (derived in the two Cb SAM interfaces, so the real events take the old listeners). */
@SuppressWarnings({"unchecked", "rawtypes"})
public final class CbServerEntityWorldChangeEvents {

    public static final Event<CbAfterEntityChangeWorld> AFTER_ENTITY_CHANGE_WORLD = (Event) ServerEntityLevelChangeEvents.AFTER_ENTITY_CHANGE_LEVEL;

    public static final Event<CbAfterPlayerChangeWorld> AFTER_PLAYER_CHANGE_WORLD = (Event) ServerEntityLevelChangeEvents.AFTER_PLAYER_CHANGE_LEVEL;

    private CbServerEntityWorldChangeEvents() {
    }
}

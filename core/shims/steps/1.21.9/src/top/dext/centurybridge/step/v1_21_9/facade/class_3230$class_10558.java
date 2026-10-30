package top.dext.centurybridge.step.v1_21_9.facade;

/**
 * Facade for 1.21.8's net/minecraft/class_3230$class_10558 (TicketType.TicketUse), an enum 1.21.9 removed (javap: no
 * class_3230$ class in the 1.21.9 jar) when TicketType's (timeout, persist, use) became (timeout, flags). The three
 * 1.21.8 constants under their 1.21.8 names and order (name(), valueOf, values() as before); field_NNNN are the
 * intermediary names mods refer to them by. rt/CbTicketTypes turns them into 1.21.9's flags 2 (loading) / 4
 * (simulation) and back.
 */
public enum class_3230$class_10558 {
    LOADING,
    SIMULATION,
    LOADING_AND_SIMULATION;

    public static final class_3230$class_10558 field_55601 = LOADING;
    public static final class_3230$class_10558 field_55602 = SIMULATION;
    public static final class_3230$class_10558 field_55603 = LOADING_AND_SIMULATION;
}

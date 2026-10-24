package top.dext.centurybridge.step.v1_20_5.facade;

/**
 * CommandBuildContext.MissingTagAccessPolicy (gone in 1.20.5 with configurable contexts): what a
 * configurable context's lookups do with a tag the registry does not have. Constants under 1.20.4's
 * intermediary names in 1.20.4's order.
 */
public enum CommandBuildContextPolicy {
    /** CREATE_NEW: the lookup creates the (empty) tag */
    field_37824,
    /** FAIL: the lookup has no such tag */
    field_37826
}

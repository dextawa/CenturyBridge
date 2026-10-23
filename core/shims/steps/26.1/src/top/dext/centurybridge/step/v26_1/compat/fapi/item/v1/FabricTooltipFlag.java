package top.dext.centurybridge.step.v26_1.compat.fapi.item.v1;

/**
 * 1.21.11's net.fabricmc.fabric.api.item.v1.FabricTooltipFlag, which 26.1's Fabric API removed: the same
 * default (false: show the usual tooltip). 1.21.11 injected it into TooltipFlag; so does this layer
 * (mixin/CbFapiTooltipFlagMixin), so every flag -- vanilla's answering false, a mod's own answering what it
 * overrides -- is one.
 */
public interface FabricTooltipFlag {
    default boolean shouldDisplayAllInformation() {
        return false;
    }
}

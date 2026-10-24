package top.dext.centurybridge.step.v1_21_2.facade.fapi;

/**
 * Fabric API 0.116's FabricTooltipType (net/fabricmc/fabric/api/item/v1/FabricTooltipType, classRenames), gone in 0.106
 * with nothing in its place. 0.116 injected it into TooltipFlag; its one member answered false unless an
 * implementation said otherwise. A call on any tooltip flag goes through CbItemFapi.shouldDisplayAllInformation.
 */
public interface FabricTooltipType {

    /** shouldDisplayAllInformation(): false */
    default boolean shouldDisplayAllInformation() {
        return false;
    }
}

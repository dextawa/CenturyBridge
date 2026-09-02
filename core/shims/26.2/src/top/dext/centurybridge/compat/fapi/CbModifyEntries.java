package top.dext.centurybridge.compat.fapi;

/**
 * Stand-in for fabric-item-group-api-v1's ItemGroupEvents$ModifyEntries
 * (1.20.x). 26.x renamed the SAM to CreativeModeTabEvents$ModifyOutput
 * .modifyOutput, so old lambdas need this old-shape interface; see
 * CbItemGroupEvents for the forwarding.
 */
@FunctionalInterface
public interface CbModifyEntries {
    void modifyEntries(CbItemGroupEntries entries);
}

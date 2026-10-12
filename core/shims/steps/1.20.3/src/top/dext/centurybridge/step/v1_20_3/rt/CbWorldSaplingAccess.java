package top.dext.centurybridge.step.v1_20_3.rt;

import top.dext.centurybridge.step.v1_20_3.facade.AbstractTreeGrower;

/** what CbWorldSaplingMixin adds to SaplingBlock: the 1.20.2 grower it was built with */
public interface CbWorldSaplingAccess {
    void cb$setLegacyGrower(AbstractTreeGrower grower);
}

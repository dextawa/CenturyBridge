package top.dext.centurybridge.step.v1_20_5.facade;

/**
 * 1.20.4 ChunkHolder.ChunkLoadingFailure (net/minecraft/class_3193$class_3724), which 1.20.5 dropped:
 * a chunk future's result there is a ChunkResult, whose failure carries a message instead. 1.20.4 had
 * exactly one failure, {@link #field_16433 UNLOADED}, whose toString was "UNLOADED"; a mod could
 * implement the interface for failures of its own. rt/CbRestChunks maps every 1.20.5 failure onto
 * UNLOADED and a mod's own failure onto a 1.20.5 failure carrying its text.
 */
public interface class_3193$class_3724 {

    /** ChunkLoadingFailure.UNLOADED */
    class_3193$class_3724 field_16433 = new class_3193$class_3724() {
        @Override
        public String toString() {
            return "UNLOADED";
        }
    };
}

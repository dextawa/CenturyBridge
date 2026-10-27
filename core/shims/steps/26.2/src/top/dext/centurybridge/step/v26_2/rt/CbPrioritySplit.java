package top.dext.centurybridge.step.v26_2.rt;

/**
 * What a restoring layer mixin (at {@link CbVanillaReads#PRIORITY}) shares with the injectors into 26.2's own methods
 * that were split off it into a default-priority {@code <name>Hooks} mixin: the state both use, reached through the
 * restorer (its {@code @Unique} state is the restorer's own). Method names carry the layer ({@code cb$v26_2$}): a game
 * class carries the mixins of every climbed layer.
 */
public final class CbPrioritySplit {
    private CbPrioritySplit() {
    }

    /** CbS26ConnectionEncryptedMixin: 26.1.2's encrypted flag, set where 26.1.2's setEncryptionKey set it */
    public interface ConnectionEncrypted {
        void cb$v26_2$markEncrypted();
    }
}

package top.dext.centurybridge.step.v1_21_2.facade.fapi;

import java.util.function.BiPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.minecraft.class_3222;

/**
 * AttachmentSyncPredicate of 1.21.1's Fabric API (0.116; absent from 1.21.2's 0.106.1), classRenamed: which
 * players receive a synced attachment of a target (rt/CbRestAttachmentSync). 0.116's three predicates.
 */
@FunctionalInterface
public interface AttachmentSyncPredicate extends BiPredicate<AttachmentTarget, class_3222> {

    /** every player that sees the target */
    static AttachmentSyncPredicate all() {
        return (target, player) -> true;
    }

    /** only the target itself (a player's own attachment) */
    static AttachmentSyncPredicate targetOnly() {
        return (target, player) -> target == player;
    }

    /** every player that sees the target except the target itself */
    static AttachmentSyncPredicate allButTarget() {
        return (target, player) -> target != player;
    }
}

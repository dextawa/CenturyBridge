package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_21.rt.CbPrioritySplit;
import top.dext.centurybridge.step.v1_21.rt.CbVanillaReads;

/**
 * Mob (class_1308) leash save state. 1.20.6 kept a leash on Mob: leashHolder (field_6202) and delayedLeashInfo
 * (field_6192, the holder saved by uuid or the fence knot saved by position), and tickLeash (method_5995) called the private
 * restoreLeashFromSave (method_5940) while delayedLeashInfo was set. 1.21 keeps both in Mob's LeashData (field_52221,
 * class_9817$class_9818: leashHolder field_52217, delayedLeashInfo field_52218), and Leashable.tickLeash (static
 * method_60965) calls Leashable's private static restoreLeashFromSave (method_60958(E, LeashData)), whose instructions are
 * 1.20.6's with field_6192 read off the LeashData, setLeashedTo Leashable's and the last write setLeashData(null) (javap -c -p).
 *
 * LeashData stays the truth. This mixin restores the two fields as mirrors of it (both null when Mob has no LeashData)
 * with the values last synced (cb$v1_21$synced*), and restoreLeashFromSave with 1.20.6's body, which reads and writes the
 * mirror field_6192 as 1.20.6 read and wrote the field. CbEntityLeashSaveRestoredMixinHooks and CbEntityLeashSaveRouteMixin
 * copy LeashData into the mirrors wherever 1.21 writes it (setLeashData, readAdditionalSaveData, LeashData.setLeashHolder);
 * a mirror written by a converted mod (its @Shadow / @Accessor) goes into LeashData at the next getLeashData, through which
 * 1.21 reads the leash (cb$v1_21$reconcileLeash). The layer writes LeashData only when a mirror was written: unmixed, 1.21's
 * state is untouched. At CbVanillaReads.PRIORITY so a mod's 1.20.6 injectors anchor inside the restored body.
 */
@Mixin(value = net.minecraft.class_1308.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbEntityLeashSaveRestoredMixin implements CbPrioritySplit.LeashSave {

    @Shadow
    private net.minecraft.class_9817.class_9818 field_52221;

    /** 1.20.6 Mob.leashHolder: mirror of LeashData.leashHolder */
    private net.minecraft.class_1297 field_6202;
    /** 1.20.6 Mob.delayedLeashInfo: mirror of LeashData.delayedLeashInfo */
    private com.mojang.datafixers.util.Either<java.util.UUID, net.minecraft.class_2338> field_6192;

    /** what the mirrors held when last synced with LeashData: a mirror that differs was written by a mod */
    @Unique
    private net.minecraft.class_1297 cb$v1_21$syncedHolder;
    @Unique
    private com.mojang.datafixers.util.Either<java.util.UUID, net.minecraft.class_2338> cb$v1_21$syncedInfo;

    @Override
    public void cb$v1_21$mirrorLeash() {
        net.minecraft.class_9817.class_9818 data = this.field_52221;
        this.field_6202 = this.cb$v1_21$syncedHolder = data == null ? null : data.field_52217;
        this.field_6192 = this.cb$v1_21$syncedInfo = data == null ? null : data.field_52218;
    }

    @Override
    public void cb$v1_21$reconcileLeash() {
        net.minecraft.class_9817.class_9818 data = this.field_52221;
        net.minecraft.class_1297 dataHolder = data == null ? null : data.field_52217;
        com.mojang.datafixers.util.Either<java.util.UUID, net.minecraft.class_2338> dataInfo = data == null ? null : data.field_52218;
        boolean holderWritten = this.field_6202 != this.cb$v1_21$syncedHolder;
        boolean infoWritten = this.field_6192 != this.cb$v1_21$syncedInfo;
        if (holderWritten || infoWritten) {
            net.minecraft.class_1297 holder = holderWritten ? this.field_6202 : dataHolder;
            com.mojang.datafixers.util.Either<java.util.UUID, net.minecraft.class_2338> info = infoWritten ? this.field_6192 : dataInfo;
            net.minecraft.class_1308 self = (net.minecraft.class_1308) (Object) this;
            if (holder == null && info == null) {
                // 1.21 drops a leash by dropping its LeashData (Leashable.dropLeash, restoreLeashFromSave)
                if (data != null) {
                    self.method_60960(null);
                }
            } else if (data == null) {
                net.minecraft.class_9817.class_9818 made = holder != null ? new net.minecraft.class_9817.class_9818(holder)
                    : new net.minecraft.class_9817.class_9818(info);
                made.field_52218 = info;
                self.method_60960(made);
            } else {
                data.field_52217 = holder;
                data.field_52218 = info;
            }
            this.cb$v1_21$mirrorLeash();
        } else if (dataHolder != this.cb$v1_21$syncedHolder || dataInfo != this.cb$v1_21$syncedInfo) {
            // LeashData's public fields written directly (a native 1.21 mod): the mirrors follow
            this.cb$v1_21$mirrorLeash();
        }
    }

    @Override
    public void cb$v1_21$restoreLeashFromSave() {
        this.method_5940();
    }

    /** 1.20.6 Mob.restoreLeashFromSave: the holder by uuid or the knot by position, leashed; dropped after 100 ticks */
    @CbVanillaReads("CbEntityLeashSaveRouteMixin: Leashable.tickLeash (method_60965) runs this method in place of 1.21's "
        + "restoreLeashFromSave (method_60958) for a Mob when a converted mod mixes it; body 1.20.6's")
    private void method_5940() {
        if (this.field_6192 != null) {
            net.minecraft.class_1308 self = (net.minecraft.class_1308) (Object) this;
            if (self.method_37908() instanceof net.minecraft.class_3218 level) {
                java.util.Optional<java.util.UUID> uuid = this.field_6192.left();
                java.util.Optional<net.minecraft.class_2338> pos = this.field_6192.right();
                if (uuid.isPresent()) {
                    net.minecraft.class_1297 holder = level.method_14190(uuid.get());
                    if (holder != null) {
                        self.method_60964(holder, true);
                        return;
                    }
                } else if (pos.isPresent()) {
                    self.method_60964(net.minecraft.class_1532.method_6932(self.method_37908(), pos.get()), true);
                    return;
                }
                if (self.field_6012 > 100) {
                    self.method_5706(net.minecraft.class_1802.field_8719);
                    this.field_6192 = null;
                }
            }
        }
    }
}

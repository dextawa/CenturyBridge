package top.dext.centurybridge.shims.v1_20_2;

import java.util.List;
import net.minecraft.class_310;
import net.minecraft.class_4325;
import net.minecraft.class_7580;
import net.minecraft.class_8656;
import top.dext.centurybridge.rt.v1_20_2.CbNetRealmsAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * RealmsMainScreen's purchase popup state, 1.20.1's shouldShowPopup() and setCreatedTrial(b).
 *
 * 1.20.1 overlaid the popup (trial / buy buttons) when the account had fetched an empty server list,
 * or when the player opened it with the popup button. 1.20.2 shows the purchase offer as a separate
 * screen, AddRealmPopupScreen, drawn over the main screen whose "buy a Realm" button opened it.
 * shouldShowPopup answers 1.20.1's question from those: the server list has arrived and is empty
 * (1.20.1's hasFetchedServers && servers.isEmpty(); 1.20.2 asks the data subscription only once
 * the availability check -- 1.20.1's compatibility and consent checks -- has passed), or the popup
 * showing is the one this screen opened (1.20.1's popupOpenedByUser).
 *
 * setCreatedTrial: 1.20.1 hid the trial offer once the flag was set (the popup's trial button, the
 * button's trial badge) and ignored the data fetcher's trial-availability updates while it was set.
 * 1.20.2 offers the trial through the same two places, both read from trialsAvailable: the badge on
 * the buy button (render) and the trial button of the popup it opens; they read it here as
 * trialsAvailable && !createdTrial, and the fetcher's update is dropped while the flag is set.
 */
@Mixin(value = class_4325.class, priority = top.dext.centurybridge.rt.v1_20_2.CbVanillaReads.PRIORITY)
public abstract class CbNetRealmsPopupBridge implements CbNetRealmsAccess.Popup {

    @Shadow
    private class_7580 field_39681;

    /** 1.20.1 hasFetchedServers: the server list callback has run */
    @Unique
    private boolean cb$fetchedServers;

    /** 1.20.1 createdTrial */
    @Unique
    private volatile boolean cb$createdTrial;

    /** was shouldShowPopup() */
    public boolean method_20870() {
        if (!this.cb$fetchedServers) {
            return false;
        }
        Object current = class_310.method_1551().field_1755;
        if (current instanceof class_8656 popup && ((CbNetRealmsPopupScreenAccessor) popup).cb$backgroundScreen() == (Object) this) {
            return true;
        }
        return this.field_39681.method_52664();
    }

    /** was setCreatedTrial(createdTrial) */
    public void method_35682(boolean createdTrial) {
        this.cb$createdTrial = createdTrial;
    }

    @Override
    public void cb$markServersFetched() {
        this.cb$fetchedServers = true;
    }

    @Override
    public boolean cb$createdTrial() {
        return this.cb$createdTrial;
    }
}

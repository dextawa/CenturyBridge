package top.dext.centurybridge.step.v1_20_5.facade;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.20.4's Realms32BitWarningStatus (classRenames), gone in 1.20.5 with 32-bit support: once per session, on
 * a 32-bit JVM, unless options say to skip it, it shows the Realms 32-bit warning to a player who owns an
 * unexpired Realm -- the subscription check running off-thread, the warning shown on a later call once the
 * answer is in (and never after the check failed). 1.20.4's class on 1.20.5's API; Minecraft.is64Bit() and
 * the skip option are the layer's (rt/CbClientRealms).
 */
@Environment(EnvType.CLIENT)
public class Realms32BitWarningStatus {

    public static final org.slf4j.Logger field_39322 = com.mojang.logging.LogUtils.getLogger();

    public final net.minecraft.class_310 field_39323;
    public java.util.concurrent.CompletableFuture<Boolean> field_39324;
    public boolean field_39325;

    public Realms32BitWarningStatus(net.minecraft.class_310 minecraft) {
        this.field_39323 = minecraft;
    }

    /** showRealms32BitWarningIfNeeded(screen) */
    public void method_44029(net.minecraft.class_437 screen) {
        if (!((top.dext.centurybridge.step.v1_20_5.rt.CbClientRealms.Minecraft64) this.field_39323).cb$is64Bit()
                && !((top.dext.centurybridge.step.v1_20_5.rt.CbClientRealms.Options32Bit) this.field_39323.field_1690).cb$skipRealms32bitWarning()
                && !this.field_39325 && this.method_44027()) {
            this.field_39323.method_1507(new Realms32bitWarningScreen(screen));
            this.field_39325 = true;
        }
    }

    /** checkForRealmsSubscription() */
    public Boolean method_44027() {
        if (this.field_39324 == null) {
            this.field_39324 = java.util.concurrent.CompletableFuture.supplyAsync(this::method_44030, net.minecraft.class_156.method_18349());
        }
        try {
            return this.field_39324.getNow(false);
        } catch (java.util.concurrent.CompletionException e) {
            field_39322.warn("Failed to retrieve realms subscriptions", e);
            this.field_39325 = true;
            return false;
        }
    }

    /** hasRealmsSubscription() */
    public boolean method_44030() {
        try {
            return net.minecraft.class_4341.method_20989(this.field_39323).method_21015().field_22622.stream()
                .anyMatch(this::method_44028);
        } catch (net.minecraft.class_4355 e) {
            return false;
        }
    }

    /** isBlockingRealmsSubscription(server): one the player owns that has not expired */
    public boolean method_44028(net.minecraft.class_4877 server) {
        return !server.field_22608 && this.field_39323.method_52701(server.field_22605);
    }
}

package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * Minecraft's 1.20.1 members 1.20.2 removed, on 1.20.2's client:
 * <ul>
 * <li>clearResourcePacksOnError(error, message): 1.20.2 added the loading context; the 1.20.1 call is the
 * one 1.20.2 itself makes outside the initial load (the level renderer's), with no context.</li>
 * <li>shouldShowBanNotice(): 1.20.1's body, whether there are multiplayer ban details.</li>
 * <li>getProfileProperties(): 1.20.1 filled the signed-in profile's properties from the session service
 * once and kept them; 1.20.2 keeps that profile, fetched at startup, as getGameProfile().</li>
 * <li>isConnectedToRealms() / setConnectedToRealms(b): 1.20.1 kept a flag that vanilla set when it
 * connected to a Realm and cleared on the title and Realms screens; 1.20.2 marks the current server
 * entry as a Realm instead. The flag is the answer once something sets it, as in 1.20.1, and the current
 * entry's mark otherwise; the screens that cleared it in 1.20.1 clear it again (CbGuiRealmsFlagResetBridge).</li>
 * <li>frameTimer / getFrameTimer(): 1.20.1's ring buffer of the last 240 frame times (FrameTimer, the
 * facade standing in for it). 1.20.1's runTick pushed each frame's duration into it; 1.20.2's runTick hands
 * the same value, at the same point, to the debug overlay's logFrameDuration, which is where it is pushed
 * here.</li>
 * </ul>
 */
@Mixin(value = net.minecraft.class_310.class, priority = top.dext.centurybridge.rt.v1_20_2.CbVanillaReads.PRIORITY)
public abstract class CbGuiMinecraftBridge implements top.dext.centurybridge.rt.v1_20_2.CbRealmsFlag, top.dext.centurybridge.rt.v1_20_2.CbGuiRestoredAccess.FrameTimer {

    /** frameTimer */
    public final top.dext.centurybridge.facade.v1_20_2.class_3517 field_1688 = new top.dext.centurybridge.facade.v1_20_2.class_3517();

    @Unique
    private Boolean cb$connectedToRealms;

    /** 1.20.1 getFrameTimer() */
    public top.dext.centurybridge.facade.v1_20_2.class_3517 method_1570() {
        return this.field_1688;
    }

    @Override
    public top.dext.centurybridge.facade.v1_20_2.class_3517 cb$frameTimer() {
        return this.field_1688;
    }

    /** 1.20.1 clearResourcePacksOnError(error, message) */
    public void method_31186(Throwable error, net.minecraft.class_2561 message) {
        ((net.minecraft.class_310) (Object) this).method_31186(error, message, null);
    }

    /** 1.20.1 shouldShowBanNotice() */
    public boolean method_44649() {
        return ((net.minecraft.class_310) (Object) this).method_44647() != null;
    }

    /** 1.20.1 getProfileProperties() */
    public com.mojang.authlib.properties.PropertyMap method_1539() {
        return ((net.minecraft.class_310) (Object) this).method_53462().getProperties();
    }

    /** 1.20.1 isConnectedToRealms() */
    public boolean method_1589() {
        if (this.cb$connectedToRealms != null) {
            return this.cb$connectedToRealms;
        }
        net.minecraft.class_642 server = ((net.minecraft.class_310) (Object) this).method_1558();
        return server != null && server.method_52811();
    }

    /** 1.20.1 setConnectedToRealms(b) */
    public void method_1537(boolean connectedToRealms) {
        this.cb$connectedToRealms = connectedToRealms;
    }

    /** the reset 1.20.1's title and Realms screens did (CbGuiRealmsFlagResetBridge) */
    @Override
    public void cb$forgetRealmsFlag() {
        this.cb$connectedToRealms = null;
    }
}

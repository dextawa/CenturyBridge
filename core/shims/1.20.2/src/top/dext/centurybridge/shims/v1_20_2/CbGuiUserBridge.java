package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.rt.v1_20_2.CbGuiOverrides;
import top.dext.centurybridge.rt.v1_20_2.CbGuiSkins;

/**
 * User's 1.20.1 getUuid() (the UUID string it was built with) and getGameProfile(), dropped in 1.20.2,
 * which keeps the parsed UUID. A User built through the 1.20.1 constructor keeps its string
 * (CbGuiSkins.cbUuid hands it to this constructor); one built by 1.20.2 answers its UUID undashed
 * (authlib's UndashedUuid form). And as in 1.20.1, where getProfileId() parsed getUuid(), a subclass that
 * overrides getUuid() gets its own UUID back from getProfileId().
 */
@Mixin(value = net.minecraft.class_320.class, priority = top.dext.centurybridge.rt.v1_20_2.CbVanillaReads.PRIORITY)
public abstract class CbGuiUserBridge implements top.dext.centurybridge.rt.v1_20_2.CbGuiRestoredAccess.User {

    @Shadow
    public abstract String method_1676();

    @Shadow
    @org.spongepowered.asm.mixin.Final
    private java.util.UUID field_1985;

    @Unique
    private String cb$uuidString;

    /** 1.20.1 getUuid() */
    public String method_1673() {
        if (this.cb$uuidString != null) {
            return this.cb$uuidString;
        }
        return this.field_1985 == null ? null : com.mojang.util.UndashedUuid.toString(this.field_1985);
    }

    /** 1.20.1 getGameProfile(): the profile id and the user name */
    @Override
    public void cb$setUuidString(String uuid) {
        this.cb$uuidString = uuid;
    }

    public com.mojang.authlib.GameProfile method_1677() {
        return new com.mojang.authlib.GameProfile(((net.minecraft.class_320) (Object) this).method_44717(), this.method_1676());
    }
}

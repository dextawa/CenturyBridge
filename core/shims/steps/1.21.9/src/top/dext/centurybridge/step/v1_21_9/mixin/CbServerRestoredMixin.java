package top.dext.centurybridge.step.v1_21_9.mixin;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import net.minecraft.class_1928;
import net.minecraft.class_3176;
import net.minecraft.class_3218;
import net.minecraft.class_7497;
import net.minecraft.class_7500;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_9.rt.CbServerProperties;
import top.dext.centurybridge.step.v1_21_9.rt.CbVanillaReads;

/**
 * MinecraftServer members 1.21.8 code calls that 1.21.9 dropped from MinecraftServer (javap, 1.21.8 bodies):
 * getSpawnProtectionRadius (16; 1.21.9's DedicatedServer still declares its own, the property's value, and overrides
 * this), getSpawnRadius(level) (the spawnRadius rule, 10 without a level), the Services accessors
 * getProfileRepository / getSessionService / getProfileKeySignatureValidator (1.21.9 hands out the Services record:
 * the same components), setPvpAllowed (1.21.9's pvp is the pvp game rule MinecraftServer.isPvpAllowed reads) and
 * setFlightAllowed. 1.21.9 calls none of them.
 * <p>setFlightAllowed: 1.21.8's isFlightAllowed answered the server's flag this sets. 1.21.9's DedicatedServer answers
 * its allow-flight property (the write lands there, in memory as 1.21.8's did: CbServerProperties); every other
 * server answers true, so once 1.21.8 code set the flag, that answer is the flag's (the head below, only while a 1.21.8
 * write was made).
 */
@Mixin(value = MinecraftServer.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbServerRestoredMixin {

    @Unique
    private Boolean cb$flightAllowed;

    @Shadow
    public abstract class_1928 method_3767();

    @Shadow
    public abstract class_7497 method_73550();

    /** MinecraftServer.getSpawnProtectionRadius() */
    public int method_3841() {
        return 16;
    }

    /** MinecraftServer.getSpawnRadius(level) */
    public int method_3829(class_3218 level) {
        return level != null ? level.method_64395().method_8356(class_1928.field_19403) : 10;
    }

    /** MinecraftServer.getProfileRepository() */
    public GameProfileRepository method_3719() {
        return this.method_73550().comp_839();
    }

    /** MinecraftServer.getSessionService() */
    public MinecraftSessionService method_3844() {
        return this.method_73550().comp_837();
    }

    /** MinecraftServer.getProfileKeySignatureValidator() */
    public class_7500 method_44301() {
        return this.method_73550().method_51467();
    }

    /** MinecraftServer.setPvpAllowed(allowed): 1.21.9's pvp game rule */
    public void method_3815(boolean allowed) {
        this.method_3767().method_20746(class_1928.field_62434).method_20758(allowed, (MinecraftServer) (Object) this);
    }

    /** MinecraftServer.setFlightAllowed(allowed) */
    public void method_3745(boolean allowed) {
        if ((Object) this instanceof class_3176 dedicated) {
            CbServerProperties.field_16807(dedicated.method_16705(), allowed);
        } else {
            this.cb$flightAllowed = allowed;
        }
    }

    @Inject(method = "method_3718()Z", at = @At("HEAD"), cancellable = true)
    private void cb$flight(CallbackInfoReturnable<Boolean> cir) {
        if (this.cb$flightAllowed != null) {
            cir.setReturnValue(this.cb$flightAllowed);
        }
    }
}

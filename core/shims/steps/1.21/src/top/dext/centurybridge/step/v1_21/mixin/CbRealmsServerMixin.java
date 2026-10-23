package top.dext.centurybridge.step.v1_21.mixin;

import java.util.List;
import java.util.UUID;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.ProfileResult;
import net.minecraft.class_310;
import net.minecraft.class_4877;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_21.facade.class_4880;
import top.dext.centurybridge.step.v1_21.facade.class_4881;
import top.dext.centurybridge.step.v1_21.rt.CbRealms;

/**
 * RealmsServer's 1.20.6 serverPing (a fresh RealmsServerPing per server, as 1.20.6's constructor made one) and
 * updateServerPing(playerList), which 1.21 removed. 1.20.6's own Realms code read and called neither (only a mod
 * did), so nothing in 1.21 needs to keep them current either.
 */
@Mixin(class_4877.class)
public abstract class CbRealmsServerMixin implements CbRealms.PingHolder {

    @Shadow
    @Final
    private static Logger field_22617;

    @Unique
    private class_4880 cb$serverPing = new class_4880();

    @Override
    public class_4880 cb$serverPing() {
        return this.cb$serverPing;
    }

    @Override
    public void cb$setServerPing(class_4880 ping) {
        this.cb$serverPing = ping;
    }

    /**
     * 1.20.6 updateServerPing(playerList): the number of players other than the local one
     * whose lookup did not fail, and the newline-joined names the session service resolved, a failed lookup logged ("Could not get name for {}").
     */
    public void method_25059(class_4881 playerList) {
        List<String> names = Lists.newArrayList();
        int count = 0;
        MinecraftSessionService sessions = class_310.method_1551().method_1495();
        for (UUID id : playerList.field_22627) {
            if (class_310.method_1551().method_52701(id)) {
                continue;
            }
            try {
                ProfileResult profile = sessions.fetchProfile(id, false);
                if (profile != null) {
                    names.add(profile.profile().getName());
                }
                count++;
            } catch (Exception e) {
                field_22617.error("Could not get name for {}", id, e);
            }
        }
        this.cb$serverPing.field_22624 = String.valueOf(count);
        this.cb$serverPing.field_22625 = Joiner.on('\n').join(names);
    }
}

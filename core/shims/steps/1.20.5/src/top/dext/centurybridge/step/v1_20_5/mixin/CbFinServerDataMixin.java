package top.dext.centurybridge.step.v1_20_5.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_20_5.rt.CbFinClient;

/** ServerData: 1.20.4's enforcesSecureChat, its getter and setter (rt/CbFinClient) */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_642.class)
public abstract class CbFinServerDataMixin implements CbFinClient.ServerDataSlot {

    @Unique
    private boolean cbS4$enforcesSecureChat;

    @Override
    public boolean cbS4$enforcesSecureChat() {
        return this.cbS4$enforcesSecureChat;
    }

    @Override
    public void cbS4$enforcesSecureChat(boolean enforces) {
        this.cbS4$enforcesSecureChat = enforces;
    }

    /** was enforcesSecureChat() */
    public boolean method_45056() {
        return this.cbS4$enforcesSecureChat;
    }

    /** was setEnforcesSecureChat(enforces) */
    public void method_45055(boolean enforces) {
        this.cbS4$enforcesSecureChat = enforces;
    }
}

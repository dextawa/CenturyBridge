package top.dext.centurybridge.step.v26_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v26_2.rt.CbPrioritySplitClient;
import top.dext.centurybridge.step.v26_2.rt.CbVanillaReads;

/**
 * 26.1.2's Minecraft.getVersionType(), which 26.2 dropped with its versionType field (javap of both). 26.1.2 copied
 * GameConfig.game.versionType into the field right after launchedVersion in its constructor; 26.2's GameConfig.GameData
 * still carries versionType (the launcher's --versionType), but 26.2's Minecraft no longer reads it. The value is taken
 * at the same place (CbS26VersionTypeMixinHooks, default priority). 26.1.2's vanilla never called the getter.
 */
@Environment(EnvType.CLIENT)
@Mixin(value = Minecraft.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbS26VersionTypeMixin implements CbPrioritySplitClient.VersionType {
    @Unique
    private String cbS26$versionType;

    /** the constructor read GameConfig.game.versionType (CbS26VersionTypeMixinHooks) */
    @Override
    public void cb$v26_2$versionType(String versionType) {
        this.cbS26$versionType = versionType;
    }

    /** 26.1.2 Minecraft.getVersionType() */
    public String getVersionType() {
        return this.cbS26$versionType;
    }
}

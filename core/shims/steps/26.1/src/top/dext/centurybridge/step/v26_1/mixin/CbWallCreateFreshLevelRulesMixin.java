package top.dext.centurybridge.step.v26_1.mixin;

import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.worldselection.WorldOpenFlows;
import net.minecraft.server.WorldStem;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import top.dext.centurybridge.step.v26_1.rt.CbWallLevelSettings;

/**
 * 1.21.11's WorldOpenFlows.createFreshLevel(name, settings, ..) created the world with the settings' game rules; 26.1
 * hands Minecraft.doWorldLoad no initial rules (Optional.empty()). The stem it loads holds the world data built from
 * those settings (PrimaryLevelData(settings, ..); getLevelSettings() answers their copy, which carries the rules,
 * CbWallLevelSettingsMixin). When they carry rules a 1.21.11 caller built them with (rt/CbWallLevelSettings), those
 * are the world's initial rules; otherwise 26.1's argument is passed on.
 */
@Environment(EnvType.CLIENT)
@Mixin(WorldOpenFlows.class)
public abstract class CbWallCreateFreshLevelRulesMixin {

    @ModifyArg(method = "createFreshLevel", index = 3, at = @At(value = "INVOKE",
        target = "Lnet/minecraft/client/Minecraft;doWorldLoad(Lnet/minecraft/world/level/storage/LevelStorageSource$LevelStorageAccess;Lnet/minecraft/server/packs/repository/PackRepository;Lnet/minecraft/server/WorldStem;Ljava/util/Optional;Z)V"))
    private Optional<GameRules> cb$initialRules(LevelStorageSource.LevelStorageAccess access, PackRepository packs, WorldStem stem,
                                                Optional<GameRules> rules, boolean newWorld) {
        if (!rules.isEmpty()) {
            return rules;
        }
        return CbWallLevelSettings.builtRules(stem.worldDataAndGenSettings().data().getLevelSettings());
    }
}

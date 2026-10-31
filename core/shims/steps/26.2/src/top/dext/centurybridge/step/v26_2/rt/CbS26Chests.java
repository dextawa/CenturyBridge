package top.dext.centurybridge.step.v26_2.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiblockChestResources;
import net.minecraft.client.renderer.special.ChestSpecialRenderer;
import net.minecraft.resources.Identifier;

/**
 * 26.1.2 ChestSpecialRenderer.COPPER_UNAFFECTED / COPPER_EXPOSED / COPPER_WEATHERED / COPPER_OXIDIZED (gone at 26.2).
 * javap of both <clinit>: 26.1.2 built each from createDefaultTextures("copper" / "copper_exposed" / "copper_weathered" /
 * "copper_oxidized"); 26.2 builds the same four, in that order, into COPPER = new WeatheringCopperCollection.ByState(
 * unaffected, exposed, weathered, oxidized). Each constant is the matching component of COPPER (the same object).
 */
@Environment(EnvType.CLIENT)
public final class CbS26Chests {
    private CbS26Chests() {}

    public static MultiblockChestResources<Identifier> copperUnaffected() {
        return ChestSpecialRenderer.COPPER.unaffected();
    }

    public static MultiblockChestResources<Identifier> copperExposed() {
        return ChestSpecialRenderer.COPPER.exposed();
    }

    public static MultiblockChestResources<Identifier> copperWeathered() {
        return ChestSpecialRenderer.COPPER.weathered();
    }

    public static MultiblockChestResources<Identifier> copperOxidized() {
        return ChestSpecialRenderer.COPPER.oxidized();
    }
}

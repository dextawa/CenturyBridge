package top.dext.centurybridge.step.v1_20_3.compat.fapi.object.builder.v1.block.type;

import net.fabricmc.fabric.api.object.builder.v1.block.type.WoodTypeBuilder;

/**
 * Fabric API's WoodTypeRegistry as 1.20.2's Fabric API had it (gone from 1.20.3's, which keeps
 * WoodTypeBuilder): register(id, setType) registered new WoodType(id, setType) -- the wooden sounds --
 * and register(id, setType, ..) the full constructor, through WoodType.register. Built here with
 * 1.20.3's builder (whose defaults are those sounds) and registered through it; 1.20.3's WoodType has
 * the same components.
 */
public final class WoodTypeRegistry {

    private WoodTypeRegistry() {
    }

    /** register(id, setType): 1.20.2's new WoodType(id.toString(), setType) */
    public static net.minecraft.class_4719 register(net.minecraft.class_2960 id, net.minecraft.class_8177 setType) {
        return new WoodTypeBuilder().register(id, setType);
    }

    /** register(id, setType, soundGroup, hangingSignSoundGroup, fenceGateCloseSound, fenceGateOpenSound) */
    public static net.minecraft.class_4719 register(net.minecraft.class_2960 id, net.minecraft.class_8177 setType,
            net.minecraft.class_2498 soundGroup, net.minecraft.class_2498 hangingSignSoundGroup,
            net.minecraft.class_3414 fenceGateCloseSound, net.minecraft.class_3414 fenceGateOpenSound) {
        return new WoodTypeBuilder()
            .soundGroup(soundGroup)
            .hangingSignSoundGroup(hangingSignSoundGroup)
            .fenceGateCloseSound(fenceGateCloseSound)
            .fenceGateOpenSound(fenceGateOpenSound)
            .register(id, setType);
    }
}

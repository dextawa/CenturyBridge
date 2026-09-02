package top.dext.centurybridge.compat.fapi;

import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;

/** fabric-object-builder-api-v1's WoodTypeRegistry (1 corpus mod): the 26.2 record constructor plus WoodType.register. */
public final class CbWoodTypeRegistry {

    public static WoodType register(Identifier id, BlockSetType setType, SoundType soundType, SoundType hangingSignSoundType,
            SoundEvent fenceGateClose, SoundEvent fenceGateOpen) {
        return WoodType.register(new WoodType(id.toString(), setType, soundType, hangingSignSoundType, fenceGateClose, fenceGateOpen));
    }

    private CbWoodTypeRegistry() {
    }
}

package top.dext.centurybridge.compat.fapi;

import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.BlockSetType;

/** fabric-object-builder-api-v1's BlockSetTypeRegistry (1 corpus mod): the 26.2 record constructor plus BlockSetType.register; the wind-charge / arrow / pressure-plate options take the 1.20.1 (wooden) defaults. */
public final class CbBlockSetTypeRegistry {

    public static BlockSetType register(Identifier id, boolean canOpenByHand, SoundType soundType, SoundEvent doorClose, SoundEvent doorOpen,
            SoundEvent trapdoorClose, SoundEvent trapdoorOpen, SoundEvent pressurePlateClickOff, SoundEvent pressurePlateClickOn,
            SoundEvent buttonClickOff, SoundEvent buttonClickOn) {
        return BlockSetType.register(new BlockSetType(id.toString(), canOpenByHand, true, true,
            BlockSetType.PressurePlateSensitivity.EVERYTHING, soundType, doorClose, doorOpen, trapdoorClose, trapdoorOpen,
            pressurePlateClickOff, pressurePlateClickOn, buttonClickOff, buttonClickOn));
    }

    private CbBlockSetTypeRegistry() {
    }
}

package top.dext.centurybridge.step.v1_20_3.compat.fapi.object.builder.v1.block.type;

import net.fabricmc.fabric.api.object.builder.v1.block.type.BlockSetTypeBuilder;

/**
 * Fabric API's BlockSetTypeRegistry as 1.20.2's Fabric API had it (gone from 1.20.3's, which keeps
 * BlockSetTypeBuilder). 1.20.2's registerWood(id) registered new BlockSetType(id) -- the wooden
 * defaults -- and register(id, ..) the full constructor, through BlockSetType.register. The same sets
 * are built here with 1.20.3's builder and registered through it. 1.20.3's set also carries the
 * wind-charge, button-arrow and pressure-plate settings that 1.20.2 kept on the buttons and plates:
 * a hand-openable 1.20.2 set gets the wooden pairing (all on, plates any entity triggers), the others
 * the stone one -- as the step's BlockSetType constructor reshape (s2world, CbWorldBlocks) decides; a
 * 1.20.2 button or plate built with its own setting gets a set differing in that one (CbWorldBlocks).
 */
public final class BlockSetTypeRegistry {

    private BlockSetTypeRegistry() {
    }

    /** registerWood(id): 1.20.2's new BlockSetType(id.toString()), the wooden defaults (1.20.3's builder defaults) */
    public static net.minecraft.class_8177 registerWood(net.minecraft.class_2960 id) {
        return new BlockSetTypeBuilder().register(id);
    }

    /** register(id, openableByHand, soundGroup, doorClose, doorOpen, trapdoorClose, trapdoorOpen, plateOff, plateOn, buttonOff, buttonOn) */
    public static net.minecraft.class_8177 register(net.minecraft.class_2960 id, boolean openableByHand,
            net.minecraft.class_2498 soundGroup, net.minecraft.class_3414 doorCloseSound, net.minecraft.class_3414 doorOpenSound,
            net.minecraft.class_3414 trapdoorCloseSound, net.minecraft.class_3414 trapdoorOpenSound,
            net.minecraft.class_3414 pressurePlateClickOffSound, net.minecraft.class_3414 pressurePlateClickOnSound,
            net.minecraft.class_3414 buttonClickOffSound, net.minecraft.class_3414 buttonClickOnSound) {
        return new BlockSetTypeBuilder()
            .openableByHand(openableByHand)
            .openableByWindCharge(openableByHand)
            .buttonActivatedByArrows(openableByHand)
            .pressurePlateActivationRule(top.dext.centurybridge.step.v1_20_3.rt.CbWorldBlocks.plateSensitivityFor(openableByHand))
            .soundGroup(soundGroup)
            .doorCloseSound(doorCloseSound)
            .doorOpenSound(doorOpenSound)
            .trapdoorCloseSound(trapdoorCloseSound)
            .trapdoorOpenSound(trapdoorOpenSound)
            .pressurePlateClickOffSound(pressurePlateClickOffSound)
            .pressurePlateClickOnSound(pressurePlateClickOnSound)
            .buttonClickOffSound(buttonClickOffSound)
            .buttonClickOnSound(buttonClickOnSound)
            .register(id);
    }
}

package top.dext.centurybridge.step.v26_2.mixin;

import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.entity.animal.parrot.Parrot;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v26_2.facade.FlyingAnimal;

/** 26.1.2's Bee and Parrot implemented FlyingAnimal (facade/FlyingAnimal); their own isFlying() answers it */
@Mixin({Bee.class, Parrot.class})
public abstract class CbS26FlyingAnimalMixin implements FlyingAnimal {
}

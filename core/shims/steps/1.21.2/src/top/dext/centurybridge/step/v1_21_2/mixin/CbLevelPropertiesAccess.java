package top.dext.centurybridge.step.v1_21_2.mixin;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import net.minecraft.class_10162;
import net.minecraft.class_1299;
import net.minecraft.class_2248;
import net.minecraft.class_2498;
import net.minecraft.class_2680;
import net.minecraft.class_2766;
import net.minecraft.class_3619;
import net.minecraft.class_3620;
import net.minecraft.class_4970;
import net.minecraft.class_5321;
import net.minecraft.class_52;
import net.minecraft.class_7699;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import top.dext.centurybridge.step.v1_21_2.rt.CbLevelProperties;

/**
 * BlockBehaviour.Properties' settings, for 1.21.1 Fabric API's FabricBlockSettings copy constructor (facade
 * FabricBlockSettings): every setting 1.21.1's copied, each read and written as the field it is in 1.21.2.
 */
@Mixin(class_4970.class_2251.class)
public interface CbLevelPropertiesAccess extends CbLevelProperties {
    @Override @Accessor("field_10662") Function<class_2680, class_3620> cb$mapColor();
    @Override @Accessor("field_10662") void cb$mapColor(Function<class_2680, class_3620> v);
    @Override @Accessor("field_10664") boolean cb$hasCollision();
    @Override @Accessor("field_10664") void cb$hasCollision(boolean v);
    @Override @Accessor("field_10665") class_2498 cb$soundType();
    @Override @Accessor("field_10665") void cb$soundType(class_2498 v);
    @Override @Accessor("field_10663") ToIntFunction<class_2680> cb$lightEmission();
    @Override @Accessor("field_10663") void cb$lightEmission(ToIntFunction<class_2680> v);
    @Override @Accessor("field_10660") float cb$explosionResistance();
    @Override @Accessor("field_10660") void cb$explosionResistance(float v);
    @Override @Accessor("field_10669") float cb$destroyTime();
    @Override @Accessor("field_10669") void cb$destroyTime(float v);
    @Override @Accessor("field_25185") boolean cb$requiresCorrectToolForDrops();
    @Override @Accessor("field_25185") void cb$requiresCorrectToolForDrops(boolean v);
    @Override @Accessor("field_10661") boolean cb$isRandomlyTicking();
    @Override @Accessor("field_10661") void cb$isRandomlyTicking(boolean v);
    @Override @Accessor("field_10667") float cb$friction();
    @Override @Accessor("field_10667") void cb$friction(float v);
    @Override @Accessor("field_23179") float cb$speedFactor();
    @Override @Accessor("field_23179") void cb$speedFactor(float v);
    @Override @Accessor("field_21209") float cb$jumpFactor();
    @Override @Accessor("field_21209") void cb$jumpFactor(float v);
    @Override @Accessor("field_54007") class_10162<class_2248, Optional<class_5321<class_52>>> cb$drops();
    @Override @Accessor("field_54007") void cb$drops(class_10162<class_2248, Optional<class_5321<class_52>>> v);
    @Override @Accessor("field_20721") boolean cb$canOcclude();
    @Override @Accessor("field_20721") void cb$canOcclude(boolean v);
    @Override @Accessor("field_23180") boolean cb$isAir();
    @Override @Accessor("field_23180") void cb$isAir(boolean v);
    @Override @Accessor("field_43394") boolean cb$ignitedByLava();
    @Override @Accessor("field_43394") void cb$ignitedByLava(boolean v);
    @Override @Accessor("field_44481") boolean cb$liquid();
    @Override @Accessor("field_44481") void cb$liquid(boolean v);
    @Override @Accessor("field_44627") boolean cb$forceSolidOff();
    @Override @Accessor("field_44627") void cb$forceSolidOff(boolean v);
    @Override @Accessor("field_44628") boolean cb$forceSolidOn();
    @Override @Accessor("field_44628") void cb$forceSolidOn(boolean v);
    @Override @Accessor("field_43395") class_3619 cb$pushReaction();
    @Override @Accessor("field_43395") void cb$pushReaction(class_3619 v);
    @Override @Accessor("field_40341") boolean cb$spawnTerrainParticles();
    @Override @Accessor("field_40341") void cb$spawnTerrainParticles(boolean v);
    @Override @Accessor("field_44629") class_2766 cb$instrument();
    @Override @Accessor("field_44629") void cb$instrument(class_2766 v);
    @Override @Accessor("field_44630") boolean cb$replaceable();
    @Override @Accessor("field_44630") void cb$replaceable(boolean v);
    @Override @Accessor("field_23181") class_4970.class_4972<class_1299<?>> cb$isValidSpawn();
    @Override @Accessor("field_23181") void cb$isValidSpawn(class_4970.class_4972<class_1299<?>> v);
    @Override @Accessor("field_23182") class_4970.class_4973 cb$isRedstoneConductor();
    @Override @Accessor("field_23182") void cb$isRedstoneConductor(class_4970.class_4973 v);
    @Override @Accessor("field_23183") class_4970.class_4973 cb$isSuffocating();
    @Override @Accessor("field_23183") void cb$isSuffocating(class_4970.class_4973 v);
    @Override @Accessor("field_23184") class_4970.class_4973 cb$isViewBlocking();
    @Override @Accessor("field_23184") void cb$isViewBlocking(class_4970.class_4973 v);
    @Override @Accessor("field_23185") class_4970.class_4973 cb$hasPostProcess();
    @Override @Accessor("field_23185") void cb$hasPostProcess(class_4970.class_4973 v);
    @Override @Accessor("field_23186") class_4970.class_4973 cb$emissiveRendering();
    @Override @Accessor("field_23186") void cb$emissiveRendering(class_4970.class_4973 v);
    @Override @Accessor("field_10670") boolean cb$dynamicShape();
    @Override @Accessor("field_10670") void cb$dynamicShape(boolean v);
    @Override @Accessor("field_40342") class_7699 cb$requiredFeatures();
    @Override @Accessor("field_40342") void cb$requiredFeatures(class_7699 v);
    @Override @Accessor("field_42818") class_4970.class_8176 cb$offsetFunction();
    @Override @Accessor("field_42818") void cb$offsetFunction(class_4970.class_8176 v);
}

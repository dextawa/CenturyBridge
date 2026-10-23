package top.dext.centurybridge.step.v1_21_2.rt;

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

/**
 * BlockBehaviour.Properties' settings as fields (CbLevelPropertiesAccess implements it on every Properties): what
 * 1.21.1 Fabric API's FabricBlockSettings copy constructor copied (facade FabricBlockSettings).
 */
public interface CbLevelProperties {
    Function<class_2680, class_3620> cb$mapColor();
    void cb$mapColor(Function<class_2680, class_3620> v);
    boolean cb$hasCollision();
    void cb$hasCollision(boolean v);
    class_2498 cb$soundType();
    void cb$soundType(class_2498 v);
    ToIntFunction<class_2680> cb$lightEmission();
    void cb$lightEmission(ToIntFunction<class_2680> v);
    float cb$explosionResistance();
    void cb$explosionResistance(float v);
    float cb$destroyTime();
    void cb$destroyTime(float v);
    boolean cb$requiresCorrectToolForDrops();
    void cb$requiresCorrectToolForDrops(boolean v);
    boolean cb$isRandomlyTicking();
    void cb$isRandomlyTicking(boolean v);
    float cb$friction();
    void cb$friction(float v);
    float cb$speedFactor();
    void cb$speedFactor(float v);
    float cb$jumpFactor();
    void cb$jumpFactor(float v);
    class_10162<class_2248, Optional<class_5321<class_52>>> cb$drops();
    void cb$drops(class_10162<class_2248, Optional<class_5321<class_52>>> v);
    boolean cb$canOcclude();
    void cb$canOcclude(boolean v);
    boolean cb$isAir();
    void cb$isAir(boolean v);
    boolean cb$ignitedByLava();
    void cb$ignitedByLava(boolean v);
    boolean cb$liquid();
    void cb$liquid(boolean v);
    boolean cb$forceSolidOff();
    void cb$forceSolidOff(boolean v);
    boolean cb$forceSolidOn();
    void cb$forceSolidOn(boolean v);
    class_3619 cb$pushReaction();
    void cb$pushReaction(class_3619 v);
    boolean cb$spawnTerrainParticles();
    void cb$spawnTerrainParticles(boolean v);
    class_2766 cb$instrument();
    void cb$instrument(class_2766 v);
    boolean cb$replaceable();
    void cb$replaceable(boolean v);
    class_4970.class_4972<class_1299<?>> cb$isValidSpawn();
    void cb$isValidSpawn(class_4970.class_4972<class_1299<?>> v);
    class_4970.class_4973 cb$isRedstoneConductor();
    void cb$isRedstoneConductor(class_4970.class_4973 v);
    class_4970.class_4973 cb$isSuffocating();
    void cb$isSuffocating(class_4970.class_4973 v);
    class_4970.class_4973 cb$isViewBlocking();
    void cb$isViewBlocking(class_4970.class_4973 v);
    class_4970.class_4973 cb$hasPostProcess();
    void cb$hasPostProcess(class_4970.class_4973 v);
    class_4970.class_4973 cb$emissiveRendering();
    void cb$emissiveRendering(class_4970.class_4973 v);
    boolean cb$dynamicShape();
    void cb$dynamicShape(boolean v);
    class_7699 cb$requiredFeatures();
    void cb$requiredFeatures(class_7699 v);
    class_4970.class_8176 cb$offsetFunction();
    void cb$offsetFunction(class_4970.class_8176 v);
}

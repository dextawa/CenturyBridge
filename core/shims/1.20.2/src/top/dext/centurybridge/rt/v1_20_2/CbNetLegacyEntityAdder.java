package top.dext.centurybridge.rt.v1_20_2;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1297;
import net.minecraft.class_742;

/**
 * The two 1.20.1 ways entities entered a ClientWorld -- addPlayer(id, player) and
 * addEntity(id, entity) -- which 1.20.2 folded into addEntity(entity). A mod subclass overriding
 * either is given this interface; the one it does not override is implemented by the 1.20.1 body
 * CbNetClientLevelBridge puts back on ClientWorld. See CbNetClient.cbAddEntity.
 */
@Environment(EnvType.CLIENT)
public interface CbNetLegacyEntityAdder {

    void method_18107(int id, class_742 player);

    void method_2942(int id, class_1297 entity);
}

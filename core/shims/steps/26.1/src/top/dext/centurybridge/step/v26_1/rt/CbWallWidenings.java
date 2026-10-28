package top.dext.centurybridge.step.v26_1.rt;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

/**
 * Parameters 26.1 widened where 1.21.11's value passes as it is (callReshapes "conv": a reshaped call's operand
 * takes the new parameter's type).
 */
public final class CbWallWidenings {
    private CbWallWidenings() {
    }

    /** a player where 26.1 takes a living entity (MaceItem.calculateImpactPosition) */
    public static LivingEntity living(ServerPlayer player) {
        return player;
    }

    /** a stack where 26.1 takes an item instance (BundleContents.getWeight) */
    public static ItemInstance instance(ItemStack stack) {
        return stack;
    }

    /** a frog where 26.1 takes a mob (FrogAi.canAttack) */
    public static Mob mob(Frog frog) {
        return frog;
    }

    /** a matrix where 26.1 takes the read-only view (vertex helpers of the leash, shadow, player-glyph and lightning renderers) */
    public static Matrix4fc matrix(Matrix4f matrix) {
        return matrix;
    }
}

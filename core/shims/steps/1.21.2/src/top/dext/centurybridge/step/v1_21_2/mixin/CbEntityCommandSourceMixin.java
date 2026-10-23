package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1297;
import net.minecraft.class_1928;
import net.minecraft.class_2165;
import net.minecraft.class_2168;
import net.minecraft.class_2561;
import net.minecraft.class_3218;
import net.minecraft.class_3222;
import org.spongepowered.asm.mixin.Mixin;

/**
 * 1.21.1's Entity was a CommandSource; 1.21.2's is not (a ServerPlayer hands out a separate commandSource()). The
 * interface and the members 1.21.1's Entity declared for it come back on Entity, with 1.21.1's bodies, so a mod's
 * calls link, its overrides override again, and an entity passes as a CommandSource where a 1.21.1 mod passes one:
 * <ul>
 * <li>sendSystemMessage(component): nothing (Entity), ServerPlayer's sendSystemMessage(component, false);</li>
 * <li>acceptsSuccess: the level's sendCommandFeedback rule (a client level's rules were the defaults: true);
 *     acceptsFailure, shouldInformAdmins: true;</li>
 * <li>getPermissionLevel: 0 (Player overrides it, in 1.21.1 as in 1.21.2); hasPermissions(level): getPermissionLevel()
 *     &gt;= level;</li>
 * <li>createCommandSourceStack(): the entity as source, its position and rotation, its level when a ServerLevel, its
 *     permission level, name and display name, the server.</li>
 * </ul>
 * No 1.21.2 entity class declares any of these shapes except Player / LocalPlayer / ServerPlayer's
 * getPermissionLevel, protected as here.
 */
@Mixin(class_1297.class)
public abstract class CbEntityCommandSourceMixin implements class_2165 {

    @Override
    public void method_43496(class_2561 message) {
        if ((Object) this instanceof class_3222 player) {
            player.method_43502(message, false);
        }
    }

    @Override
    public boolean method_9200() {
        if (((class_1297) (Object) this).method_37908() instanceof class_3218 level) {
            return level.method_64395().method_8355(class_1928.field_19400);
        }
        return true;
    }

    @Override
    public boolean method_9202() {
        return true;
    }

    @Override
    public boolean method_9201() {
        return true;
    }

    protected int method_5691() {
        return 0;
    }

    public boolean method_5687(int level) {
        return this.method_5691() >= level;
    }

    public class_2168 method_5671() {
        class_1297 self = (class_1297) (Object) this;
        return new class_2168(this, self.method_19538(), self.method_5802(),
            self.method_37908() instanceof class_3218 level ? level : null, this.method_5691(),
            self.method_5477().getString(), self.method_5476(), self.method_37908().method_8503(), self);
    }
}

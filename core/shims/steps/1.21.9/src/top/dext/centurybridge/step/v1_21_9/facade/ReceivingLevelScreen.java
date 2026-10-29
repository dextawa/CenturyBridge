package top.dext.centurybridge.step.v1_21_9.facade;

import com.mojang.blaze3d.textures.GpuTextureView;
import java.util.function.BooleanSupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1058;
import net.minecraft.class_1060;
import net.minecraft.class_10799;
import net.minecraft.class_11231;
import net.minecraft.class_156;
import net.minecraft.class_2246;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_333;
import net.minecraft.class_3928;
import net.minecraft.class_437;
import net.minecraft.class_840;

/**
 * 1.21.8's ReceivingLevelScreen (class_434), which 1.21.9 folded into LevelLoadingScreen (its Reason enum moved there:
 * LevelLoadingScreen.Reason, same constants). 1.21.8's bodies (javap -c): "multiplayer.downloadingTerrain" above the
 * centre over the reason's background (the nether portal sprite, the end portal effect, or the panorama), closed by
 * tick once levelReceived answers true or 30 s passed, the narrator saying "narrator.ready_to_play" on close; never
 * closed by Escape, no pause, no narration of the title.
 */
@Environment(EnvType.CLIENT)
public class ReceivingLevelScreen extends class_437 {
    // public: a facade's members stand in for the class's, and access wideners never reach a CenturyBridge class
    public static final class_2561 field_26544 = class_2561.method_43471("multiplayer.downloadingTerrain");
    public static final long field_36365 = 30000L;
    public final long field_36368;
    public final BooleanSupplier field_46575;
    public final class_3928.class_9678 field_51485;
    public class_1058 field_51486;

    public ReceivingLevelScreen(BooleanSupplier levelReceived, class_3928.class_9678 reason) {
        super(class_333.field_18967);
        this.field_46575 = levelReceived;
        this.field_51485 = reason;
        this.field_36368 = class_156.method_658();
    }

    @Override
    public boolean method_25422() {
        return false;
    }

    @Override
    public boolean method_48262() {
        return false;
    }

    @Override
    public void method_25394(class_332 graphics, int mouseX, int mouseY, float partialTick) {
        super.method_25394(graphics, mouseX, mouseY, partialTick);
        graphics.method_27534(class_310.method_1551().field_1772, field_26544, ((class_437) this).field_22789 / 2, ((class_437) this).field_22790 / 2 - 50, -1);
    }

    @Override
    public void method_25420(class_332 graphics, int mouseX, int mouseY, float partialTick) {
        if (this.field_51485 == class_3928.class_9678.field_51489) {
            this.method_57728(graphics, partialTick);
            this.method_57734(graphics);
            this.method_57735(graphics);
        } else if (this.field_51485 == class_3928.class_9678.field_51487) {
            graphics.method_52709(class_10799.field_56887, this.method_59838(), 0, 0, graphics.method_51421(), graphics.method_51443());
        } else if (this.field_51485 == class_3928.class_9678.field_51488) {
            class_1060 textures = class_310.method_1551().method_1531();
            GpuTextureView sky = textures.method_4619(class_840.field_4406).method_71659();
            GpuTextureView portal = textures.method_4619(class_840.field_4407).method_71659();
            class_11231 effect = class_11231.method_70901(sky, portal);
            graphics.method_51739(class_10799.field_56828, effect, 0, 0, ((class_437) this).field_22789, ((class_437) this).field_22790);
        }
    }

    public class_1058 method_59838() {
        if (this.field_51486 != null) {
            return this.field_51486;
        }
        this.field_51486 = class_310.method_1551().method_1541().method_3351().method_3339(class_2246.field_10316.method_9564());
        return this.field_51486;
    }

    @Override
    public void method_25393() {
        if (this.field_46575.getAsBoolean() || class_156.method_658() > this.field_36368 + field_36365) {
            this.method_25419();
        }
    }

    @Override
    public void method_25419() {
        class_310.method_1551().method_44713().method_37015(class_2561.method_43471("narrator.ready_to_play"));
        super.method_25419();
    }

    @Override
    public boolean method_25421() {
        return false;
    }
}

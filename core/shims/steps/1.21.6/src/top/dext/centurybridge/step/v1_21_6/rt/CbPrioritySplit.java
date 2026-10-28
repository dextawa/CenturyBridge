package top.dext.centurybridge.step.v1_21_6.rt;

import java.util.List;
import net.minecraft.class_243;
import net.minecraft.class_2540;
import net.minecraft.class_2960;
import net.minecraft.class_5411;

/**
 * What a restoring layer mixin (at {@link CbVanillaReads#PRIORITY}) shares with the injectors into 1.21.6's own
 * methods that were split off it into a default-priority {@code <name>Hooks} mixin: the state and restored members both
 * use, reached through the restorer (its own state; its restored members called virtually, so a mod's mixin on them,
 * or a subclass's override, still answers).
 */
public final class CbPrioritySplit {
    private CbPrioritySplit() {
    }

    /** CbRecipeBookSettingsRestoredMixin: the restored read / write(FriendlyByteBuf) the wrapped STREAM_CODEC routes through */
    public interface RecipeBookSettingsNet {
        /** 1.21.5's write(FriendlyByteBuf) of this settings */
        void cb$v1_21_6$netWrite(class_2540 buf);

        /** 1.21.5's static read(FriendlyByteBuf) (answers new settings; the receiver is not read) */
        class_5411 cb$v1_21_6$netRead(class_2540 buf);
    }

    /** CbSectionDispatcherRestoredMixin: the camera position setCameraPosition last set, kept for the restored getter */
    public interface SectionCameraPosition {
        void cb$v1_21_6$recordCameraPosition(class_243 position);
    }

    /** CbSectionDistanceRestoredMixin: 1.21.5's getDistToPlayerSqr, called virtually */
    public interface SectionDistance {
        double cb$v1_21_6$distToPlayerSqr();
    }

    /** CbCubeMapFacesMixin: 1.21.5's CubeMap.images, set from the constructor's argument */
    public interface CubeMapFaces {
        void cb$v1_21_6$setFaces(List<class_2960> faces);
    }

    /** CbRealmsConfigureWorldRestoredMixin: 1.21.5's layout columns, set from the screen width at init */
    public interface RealmsConfigureWorldLayout {
        void cb$v1_21_6$layout(int width);
    }
}

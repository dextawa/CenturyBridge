package top.dext.centurybridge.step.v26_1.rt;

import com.google.common.collect.MapMaker;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsScreen;
import top.dext.centurybridge.step.v26_1.facade.RealmsLabel;

/**
 * 1.21.11's RealmsScreen label members on 26.1, which dropped them with RealmsLabel (facade/RealmsLabel):
 * addLabel(label) kept the label in the screen's list and added it as a renderable; createLabelNarration()
 * joined the labels' texts in lines (javap class_4905). The list is kept per screen here.
 */
@Environment(EnvType.CLIENT)
public final class CbWallRealms {
    private CbWallRealms() {
    }

    /** RealmsScreen.COLOR_MEDIUM_GRAY, 1.21.11's value (javap -constants class_4905); javac inlines it, a direct read lands here */
    public static final int field_33062 = -9671572;

    private static final Map<RealmsScreen, List<RealmsLabel>> LABELS = new MapMaker().weakKeys().makeMap();

    private static List<RealmsLabel> labels(RealmsScreen screen) {
        return LABELS.computeIfAbsent(screen, s -> new ArrayList<>());
    }

    /** 1.21.11's addLabel(label), declared by a mod's screen subclass (adapterHooks) */
    public interface LegacyAddLabel {
        RealmsLabel addLabel(RealmsLabel label);
    }

    /** 1.21.11's createLabelNarration(), declared by a mod's screen subclass (adapterHooks) */
    public interface LegacyLabelNarration {
        Component createLabelNarration();
    }

    /** a mod's call of addLabel(label): a 1.21.11 override's answer, else 1.21.11's body */
    public static RealmsLabel addLabel(RealmsScreen screen, RealmsLabel label) {
        if (screen instanceof LegacyAddLabel legacy) {
            return legacy.addLabel(label);
        }
        return superAddLabel(screen, label);
    }

    /** 1.21.11's addLabel body: the screen's list, then addRenderableOnly (the layer's access widener opens it) */
    public static RealmsLabel superAddLabel(RealmsScreen screen, RealmsLabel label) {
        labels(screen).add(label);
        return screen.addRenderableOnly(label);
    }

    /** a mod's call of createLabelNarration(): a 1.21.11 override's answer, else 1.21.11's body */
    public static Component createLabelNarration(RealmsScreen screen) {
        if (screen instanceof LegacyLabelNarration legacy) {
            return legacy.createLabelNarration();
        }
        return superCreateLabelNarration(screen);
    }

    /** 1.21.11's createLabelNarration body: CommonComponents.joinLines of the labels' texts */
    public static Component superCreateLabelNarration(RealmsScreen screen) {
        List<RealmsLabel> labels = LABELS.get(screen);
        List<Component> texts = new ArrayList<>();
        if (labels != null) {
            for (RealmsLabel label : labels) {
                texts.add(label.method_25487());
            }
        }
        return CommonComponents.joinLines(texts);
    }
}

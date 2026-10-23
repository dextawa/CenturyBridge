package top.dext.centurybridge.step.v1_21_2.facade;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * RealmsResetNormalWorldScreen (class_4409) of 1.21.1: the screen collecting a seed, level type, structure
 * flag and experiments for regenerating a Realms world, handing them to its callback (null on close).
 * 1.21.2 removed regenerating a Realms world from a seed; the screen is 1.21.1's, built with 1.21.2's same
 * widgets and layouts. What a callback does with the answer against the Realms service is not the screen's
 * (see CbRestRealms).
 */
@Environment(EnvType.CLIENT)
public class RealmsResetNormalWorldScreen extends net.minecraft.class_4905 {

    private static final net.minecraft.class_2561 SEED_LABEL = net.minecraft.class_2561.method_43471("mco.reset.world.seed");
    public static final net.minecraft.class_2561 field_46114 = net.minecraft.class_2561.method_43471("mco.reset.world.generate");

    private final net.minecraft.class_8132 layout = new net.minecraft.class_8132(this);
    private final Consumer<WorldGenerationInfo> callback;
    private net.minecraft.class_342 seedEdit;
    private net.minecraft.class_5672 levelType = net.minecraft.class_5672.field_27944;
    private boolean generateStructures = true;
    private final Set<String> experiments = new HashSet<>();
    private final net.minecraft.class_2561 buttonTitle;

    public RealmsResetNormalWorldScreen(Consumer<WorldGenerationInfo> callback, net.minecraft.class_2561 buttonTitle) {
        super(field_46114);
        this.callback = callback;
        this.buttonTitle = buttonTitle;
    }

    @Override
    public void method_25426() {
        this.seedEdit = new net.minecraft.class_342(this.field_22793, 210, 20, SEED_LABEL);
        this.seedEdit.method_1880(32);
        this.layout.method_57726(this.field_22785, this.field_22793);
        net.minecraft.class_8667 content = this.layout.method_48999(net.minecraft.class_8667.method_52741()).method_52735(10);
        content.method_52736(net.minecraft.class_8767.method_53549(this.field_22793, this.seedEdit, SEED_LABEL));
        content.method_52736(net.minecraft.class_5676.<net.minecraft.class_5672>method_32606(net.minecraft.class_5672::method_32506)
            .method_32624(net.minecraft.class_5672.values()).method_32619(this.levelType)
            .method_32617(0, 0, 210, 20, net.minecraft.class_2561.method_43471("selectWorld.mapType"), (button, value) -> this.levelType = value));
        content.method_52736(net.minecraft.class_5676.method_32613(this.generateStructures)
            .method_32617(0, 0, 210, 20, net.minecraft.class_2561.method_43471("selectWorld.mapFeatures"), (button, value) -> this.generateStructures = value));
        this.createExperimentsButton(content);
        net.minecraft.class_8667 footer = this.layout.method_48996(net.minecraft.class_8667.method_52742().method_52735(10));
        footer.method_52736(net.minecraft.class_4185.method_46430(this.buttonTitle, button -> this.callback.accept(this.createWorldGenerationInfo())).method_46431());
        footer.method_52736(net.minecraft.class_4185.method_46430(net.minecraft.class_5244.field_24339, button -> this.method_25419()).method_46431());
        this.layout.method_48206(widget -> this.method_37063(widget));
        this.method_48640();
    }

    @Override
    public void method_56131() {
        this.method_48265(this.seedEdit);
    }

    private void createExperimentsButton(net.minecraft.class_8667 content) {
        net.minecraft.class_3283 packs = net.minecraft.class_3286.method_52443();
        packs.method_14445();
        content.method_52736(net.minecraft.class_4185.method_46430(net.minecraft.class_2561.method_43471("selectWorld.experiments"),
            button -> this.field_22787.method_1507(new net.minecraft.class_8134(this, packs, this::acceptExperiments))).method_46432(210).method_46431());
    }

    private void acceptExperiments(net.minecraft.class_3283 packs) {
        this.experiments.clear();
        for (net.minecraft.class_3288 pack : packs.method_14444()) {
            if (pack.method_29483() == net.minecraft.class_5352.field_40048) {
                this.experiments.add(pack.method_14463());
            }
        }
        this.field_22787.method_1507(this);
    }

    private WorldGenerationInfo createWorldGenerationInfo() {
        return new WorldGenerationInfo(this.seedEdit.method_1882(), this.levelType, this.generateStructures, this.experiments);
    }

    @Override
    public void method_48640() {
        this.layout.method_48222();
    }

    @Override
    public void method_25419() {
        this.callback.accept(null);
    }
}

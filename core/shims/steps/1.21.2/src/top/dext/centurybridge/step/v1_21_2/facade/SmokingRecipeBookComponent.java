package top.dext.centurybridge.step.v1_21_2.facade;

import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/** SmokingRecipeBookComponent of 1.21.1 (gone in 1.21.2; classRenamed): its filter name, the fuels 1.21.1's furnaces burnt. 1.21.1's body. */
@Environment(EnvType.CLIENT)
public class SmokingRecipeBookComponent extends AbstractFurnaceRecipeBookComponent {

    private static final net.minecraft.class_2561 FILTER_NAME = net.minecraft.class_2561.method_43471("gui.recipebook.toggleRecipes.smokable");

    public SmokingRecipeBookComponent() {
    }

    @Override
    public net.minecraft.class_2561 method_17064() {
        return FILTER_NAME;
    }

    @Override
    public Set<net.minecraft.class_1792> method_17065() {
        return cb$fuels();
    }
}

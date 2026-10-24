package top.dext.centurybridge.step.v1_21_2.facade.fapi;

import net.minecraft.class_2960;

/**
 * Fabric API 0.116's FabricRecipeExporter (classRenames; dev-time datagen), gone in 0.106. 0.116 injected it into
 * RecipeOutput: the vanilla recipe builders saved under getRecipeIdentifier(id) of the output they were given
 * (CbItemRecipeExporterMixin does so on 1.21.2 for an output implementing this). Its default keeps the id.
 */
public interface FabricRecipeExporter {

    /** getRecipeIdentifier(id): the id unchanged */
    default class_2960 getRecipeIdentifier(class_2960 id) {
        return id;
    }
}

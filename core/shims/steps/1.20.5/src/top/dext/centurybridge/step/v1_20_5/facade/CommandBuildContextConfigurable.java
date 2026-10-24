package top.dext.centurybridge.step.v1_20_5.facade;

import net.minecraft.class_2378;
import net.minecraft.class_5321;
import net.minecraft.class_7157;
import net.minecraft.class_7225;

/**
 * CommandBuildContext.Configurable (gone in 1.20.5): a context whose missing-tag policy can be set
 * (CbFinLookups.method_46723 makes one on 1.20.5's registries). Declares 1.20.4's holderLookup, which
 * every implementation answers.
 */
public interface CommandBuildContextConfigurable extends class_7157 {

    /** missingTagAccessPolicy(policy) */
    void method_46724(CommandBuildContextPolicy policy);

    /** holderLookup(key) */
    <T> class_7225<T> method_41699(class_5321<? extends class_2378<T>> key);
}

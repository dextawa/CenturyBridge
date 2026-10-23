package top.dext.centurybridge.step.v1_21_4.facade;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.class_1197;
import net.minecraft.class_1208;

/**
 * 1.21.3's EntityPaintingFieldsRenameFix (net/minecraft/class_7407, classRenames), gone in 1.21.4: a
 * NamedEntityFix on minecraft:painting renaming Motive to variant and Facing to facing. 1.21.3's class body
 * over 1.21.4's NamedEntityFix (same constructor and fix(Typed) contract).
 */
public class EntityPaintingFieldsRenameFix extends class_1197 {

    public EntityPaintingFieldsRenameFix(Schema schema) {
        super(schema, false, "EntityPaintingFieldsRenameFix", class_1208.field_5729, "minecraft:painting");
    }

    /** fixTag(tag) */
    public Dynamic<?> method_43386(Dynamic<?> tag) {
        return tag.renameField("Motive", "variant").renameField("Facing", "facing");
    }

    @Override
    public Typed<?> method_5105(Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), this::method_43386);
    }
}

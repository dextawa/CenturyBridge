package top.dext.centurybridge.step.v26_2.rt;

import com.mojang.math.Transformation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;

/**
 * What 26.1.2's sign renderers drew as a sign's body, carried on 26.2's SignRenderState (mixin/CbS26SignStateMixin)
 * from extraction to submission for the signs 26.2 draws no body for (rt/CbS26SignsClient.legacyBody): the model,
 * its sprite, the body transformation and the sprite getter of the renderer that extracted it. Unset (model null)
 * for 26.2's own signs.
 */
@Environment(EnvType.CLIENT)
public interface CbS26LegacySign {
    void cbS26$setLegacyBody(Model.Simple model, SpriteId sprite, Transformation body, SpriteGetter sprites);

    Model.Simple cbS26$legacyModel();

    SpriteId cbS26$legacySprite();

    Transformation cbS26$legacyBody();

    SpriteGetter cbS26$legacySprites();
}

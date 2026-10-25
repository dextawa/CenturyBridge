package top.dext.centurybridge.step.v26_2.mixin;

import com.mojang.math.Transformation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.blockentity.state.SignRenderState;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v26_2.rt.CbS26LegacySign;

/** 26.1.2's sign body on 26.2's sign render state (rt/CbS26LegacySign) */
@Environment(EnvType.CLIENT)
@Mixin(SignRenderState.class)
public abstract class CbS26SignStateMixin implements CbS26LegacySign {
    @Unique
    private Model.Simple cbS26$model;
    @Unique
    private SpriteId cbS26$sprite;
    @Unique
    private Transformation cbS26$body;
    @Unique
    private SpriteGetter cbS26$sprites;

    @Override
    public void cbS26$setLegacyBody(Model.Simple model, SpriteId sprite, Transformation body, SpriteGetter sprites) {
        this.cbS26$model = model;
        this.cbS26$sprite = sprite;
        this.cbS26$body = body;
        this.cbS26$sprites = sprites;
    }

    @Override
    public Model.Simple cbS26$legacyModel() {
        return this.cbS26$model;
    }

    @Override
    public SpriteId cbS26$legacySprite() {
        return this.cbS26$sprite;
    }

    @Override
    public Transformation cbS26$legacyBody() {
        return this.cbS26$body;
    }

    @Override
    public SpriteGetter cbS26$legacySprites() {
        return this.cbS26$sprites;
    }
}

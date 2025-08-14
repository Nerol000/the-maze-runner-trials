package net.nerol.mazerunner.model.griever;

import net.minecraft.util.Identifier;
import net.nerol.mazerunner.TheMazeRunner;
import net.nerol.mazerunner.entity.GrieverEntity;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class GrieverModel extends GeoModel<GrieverEntity> {
    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return Identifier.of(TheMazeRunner.MOD_ID, "geckolib/models/griever/griever.geo.json");
    }


    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return Identifier.of(TheMazeRunner.MOD_ID, "textures/entity/griever/griever.png");
    }

    @Override
    public Identifier getAnimationResource(GrieverEntity entity) {
        return Identifier.of(TheMazeRunner.MOD_ID, "geckolib/animations/griever.animation.json");
    }
}

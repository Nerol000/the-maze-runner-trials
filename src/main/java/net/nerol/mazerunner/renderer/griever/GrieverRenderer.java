package net.nerol.mazerunner.renderer.griever;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.nerol.mazerunner.entity.GrieverEntity;
import net.nerol.mazerunner.model.griever.GrieverModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class GrieverRenderer extends GeoEntityRenderer<GrieverEntity, GrieverRenderState> {
    public GrieverRenderer(EntityRendererFactory.Context context) {
        super(context, new GrieverModel());
        this.shadowRadius = 1.0f;

        this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }
}
package net.nerol.mazerunner;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.nerol.mazerunner.entity.ModEntities;
import net.nerol.mazerunner.renderer.griever.GrieverRenderer;

public class TheMazeRunnerClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntities.GRIEVER, GrieverRenderer::new);
    }
}

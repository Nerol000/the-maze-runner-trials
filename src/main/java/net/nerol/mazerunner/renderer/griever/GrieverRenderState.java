package net.nerol.mazerunner.renderer.griever;

import net.minecraft.client.render.entity.state.EntityRenderState;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.renderer.base.GeoRenderState;

import java.util.HashMap;
import java.util.Map;

public class GrieverRenderState extends EntityRenderState implements GeoRenderState {
    private final Map<DataTicket<?>, Object> dataMap = new HashMap<>();

    @Override
    public Map<DataTicket<?>, Object> getDataMap() {
        return dataMap;
    }

    @Override
    public <D> void addGeckolibData(DataTicket<D> ticket, D data) {
        dataMap.put(ticket, data);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <D> D getGeckolibData(DataTicket<D> ticket) {
        return (D) dataMap.get(ticket);
    }

    @Override
    public boolean hasGeckolibData(DataTicket<?> ticket) {
        return dataMap.containsKey(ticket);
    }
}
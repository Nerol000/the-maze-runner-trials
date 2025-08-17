package net.nerol.mazerunner.item;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.nerol.mazerunner.TheMazeRunner;
import net.nerol.mazerunner.entity.ModEntities;
import net.nerol.mazerunner.item.custom.FlareCureItem;
import net.nerol.mazerunner.item.custom.FlareInjectorItem;

import java.util.function.Function;

public class ModItems {
    public static final Item FLARE_CURE = registerItem("flare_cure", settings -> new FlareCureItem(settings.maxCount(1)));
    public static final Item FLARE_INJECTOR = registerItem("flare_injector", settings -> new FlareInjectorItem(settings.maxCount(1)));

    public static final Item GRIEVER_SPAWN_EGG = registerItem("griever_spawn_egg", settings ->
            new SpawnEggItem(ModEntities.GRIEVER, settings));

    private static Item registerItem(String name, Function<Item.Settings, Item> function) {
        return Registry.register(Registries.ITEM, Identifier.of(TheMazeRunner.MOD_ID, name),
                function.apply(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(TheMazeRunner.MOD_ID, name)))));
    }

    public static void registerModItems() {
        TheMazeRunner.LOGGER.info("Registering mod items for " + TheMazeRunner.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(entries -> {
            entries.add(FLARE_CURE);
            entries.add(FLARE_INJECTOR);
        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(entries -> {
            entries.add(GRIEVER_SPAWN_EGG);
        });
    }
}

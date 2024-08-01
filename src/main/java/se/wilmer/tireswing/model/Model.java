package se.wilmer.tireswing.model;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ItemDisplay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Model {
    private final List<ModelData> modelDataList;
    private final List<ModelEntity> modelEntities;

    /**
     * Constructs a new Model instance with the given model data.
     *
     * @param modelDataList The list of model data.
     */
    public Model(List<ModelData> modelDataList) {
        this.modelDataList = modelDataList;
        modelEntities = new ArrayList<>();
    }

    /**
     * Spawns the model entities at the specified location and world.
     *
     * @param location The location where to spawn the model entities.
     * @param world The world where to spawn the model entities.
     */
    public void spawn(Location location, World world) {
        modelDataList.forEach(modelData -> {
            ItemDisplay itemDisplay = world.spawn(location, ItemDisplay.class, display -> {
                display.setTransformation(modelData.transformation());
                display.setItemStack(modelData.itemStack());
                display.setPersistent(false);
            });

            ModelEntity modelEntity = new ModelEntity(modelData, itemDisplay);
            modelEntities.add(modelEntity);
        });
    }

    /**
     * Removes all spawned model entities.
     */
    public void clear() {
        modelEntities.clear();
    }

    /**
     * Returns an unmodifiable list of model entities.
     *
     * @return An unmodifiable list of model entities.
     */
    public List<ModelEntity> getModelEntities() {
        return Collections.unmodifiableList(modelEntities);
    }

    /**
     * Returns an unmodifiable list of model datas.
     *
     * @return An unmodifiable list of model datas.
     */
    public List<ModelData> getModelDataList() {
        return Collections.unmodifiableList(modelDataList);
    }
}


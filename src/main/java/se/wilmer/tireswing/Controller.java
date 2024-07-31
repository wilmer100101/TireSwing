package se.wilmer.tireswing;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import se.wilmer.tireswing.animation.Rotation;
import se.wilmer.tireswing.animation.Swing;
import se.wilmer.tireswing.entities.FulcrumEntity;
import se.wilmer.tireswing.entities.InteractionEntity;
import se.wilmer.tireswing.model.ModelEntity;
import se.wilmer.tireswing.model.Model;

import java.util.Optional;


public final class Controller {
    /**
     * The TireSwing plugin instance.
     */
    private final TireSwing plugin;

    /**
     * The location of the tire swing.
     */
    private final Location location;

    /**
     * The world where the tire swing is located.
     */
    private final World world;

    /**
     * The model used for the rotating part of the tire swing.
     */
    private final Model rotationalModel;

    /**
     * The model used for the rope part of the tire swing.
     */
    private final Model ropeModel;

    /**
     * The model used for the still part of the tire swing.
     */
    private final Model stillModel;

    /**
     * The radius of the tire swing's swing.
     */
    private double radius;

    /**
     * The rotation of the tire swing.
     */
    private Rotation rotation;

    /**
     * The interaction associated with the tire swing.
     */
    private Interaction interaction;

    /**
     * The item display for the tire swing.
     */
    private ItemDisplay itemDisplay;

    /**
     * The block display for the tire swing.
     */
    private BlockDisplay blockDisplay;

    /**
     * Indicates whether the tire swing has a passenger.
     */
    private boolean hasPassenger = false;

    /**
     * Indicates whether the tire swing is currently swinging.
     */
    private boolean swinging = false;

    /**
     * Constructs a new Controller instance.
     *
     * @param plugin The TireSwing plugin instance.
     * @param stillModel The model used for the still part of the tire swing.
     * @param rotationalModel The model used for the rotating part of the tire swing.
     * @param ropeModel The model used for the rope part of the tire swing.
     * @param location The location of the tire swing.
     * @param world The world where the tire swing is located.
     */
    public Controller(TireSwing plugin, Model stillModel, Model rotationalModel, Model ropeModel, Location location, World world) {
        this.plugin = plugin;
        this.stillModel = stillModel;
        this.ropeModel = ropeModel;
        this.rotationalModel = rotationalModel;
        this.location = location;
        this.world = world;
    }

    /**
     * Spawns the tire swing at the specified location in the world.
     * <p>
     * This method spawns the three models (still, rope, and rotational) at the given location.
     * It also sets up the rotation for the tire swing and initializes the item display.
     * <p>
     * The first entity from the rotational model is the main item display,
     * and all other rotational item displays is added to it, so they also rotates.
     */
    public void spawn() {
        stillModel.spawn(location, world);
        ropeModel.spawn(location, world);
        rotationalModel.spawn(location, world);

        Optional<ModelEntity> modelEntity = rotationalModel.getModelEntities().stream().findFirst();
        modelEntity.ifPresent(entity -> itemDisplay = entity.itemDisplay());

        rotationalModel.getModelEntities().stream()
                .skip(1)
                .forEach(entity -> itemDisplay.addPassenger(entity.itemDisplay()));

        rotation = new Rotation(rotationalModel, ropeModel, itemDisplay, location, radius);
        rotation.resetRotation();
    }

    /**
     * Creates an interaction for the tire swing at the specified location.
     * <p>
     * This method spawns an interaction entity at the given location and sets its height and width.
     * It retrieves the information from the {@link InteractionEntity}
     *
     * @param entity The interaction entity to create the interaction from.
     */
    public void createInteraction(InteractionEntity entity) {
        interaction = world.spawn(entity.location(), Interaction.class, interaction -> {
            interaction.setInteractionHeight(entity.height());
            interaction.setInteractionWidth(entity.width());
        });
    }

    /**
     * Creates a fulcrum for the tire swing at the specified location.
     * <p>
     * This method spawns a block display at the given location with the specified blockdata.
     * The block display is the log that the swing is hanging on.
     * <p>
     * It also sets the radius of the tire swing.
     * It retrieves the information from the {@link FulcrumEntity}
     *
     * @param entity The fulcrum entity to create the fulcrum from.
     */
    public void createFulcrum(FulcrumEntity entity) {
        blockDisplay = world.spawn(entity.location(), BlockDisplay.class, blockDisplay -> {
            blockDisplay.setBlock(entity.blockData());
            blockDisplay.setTransformation(entity.transformation());
        });
        radius = entity.radius();
    }


    /**
     * Starts the swinging animation for the tire swing with a player as passenger.
     * <p>
     * This method adds the player as a passenger to the item display, sets the swinging flag to true,
     * and starts a timer task to update the swing animation.
     * <p>
     * If the player is jumping of the swing, the swing is slowing itself down.
     *
     * @param player The player to swing.
     */
    public void swing(Player player) {
        Swing swing = new Swing();
        itemDisplay.addPassenger(player);
        swinging = true;
        plugin.getServer().getScheduler().runTaskTimer(plugin, task -> {
            if (!validate()) {
                return;
            }
            swing.update();
            if (swing.isStill()) {
                rotation.resetRotation();
                swinging = false;
                task.cancel();
                return;
            }
            if (!hasPassenger) {
                swing.slowdown();
            }
            rotation.rotate(swing.getAngle());
        }, 0L, 0L);
    }

    /**
     * Validates all the entities that should be animated.
     *
     * @return if all the entities is valid or not.
     */
    public boolean validate() {
        for (ModelEntity modelEntity : rotationalModel.getModelEntities()) {
            if (!modelEntity.itemDisplay().isValid()) {
                clear();
                return false;
            }
        }
        return true;
    }

    /**
     * Clearing the swing.
     */
    public void clear() {
        itemDisplay.eject();
        itemDisplay.remove();
        blockDisplay.remove();
        interaction.remove();

        stillModel.clear();
        ropeModel.clear();
        rotationalModel.clear();
    }

    /**
     * Sets whether the tire swing has a passenger.
     *
     * @param hasPassenger if the tire swing has a passenger.
     */
    public void setHasPassenger(boolean hasPassenger) {
        this.hasPassenger = hasPassenger;
    }

    /**
     * Returns if the tire swing has a passenger.
     *
     * @return if the tire swing has a passenger.
     */
    public boolean hasPassenger() {
        return hasPassenger;
    }

    /**
     * Returns if the tire swing is currently swinging.
     *
     * @return if the tire swing is currently swinging.
     */
    public boolean isSwinging() {
        return swinging;
    }

    /**
     * Returns the interaction associated with the tire swing.
     *
     * @return The interaction associated with the tire swing.
     */
    public Interaction getInteraction() {
        return interaction;
    }

    /**
     * Returns the item display for the tire swing.
     *
     * @return The item display for the tire swing.
     */
    public ItemDisplay getItemDisplay() {
        return itemDisplay;
    }

}
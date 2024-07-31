package se.wilmer.tireswing.configuration;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.Transformation;
import org.joml.*;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.gson.GsonConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;
import se.wilmer.tireswing.TireSwing;
import se.wilmer.tireswing.entities.FulcrumEntity;
import se.wilmer.tireswing.entities.InteractionEntity;
import se.wilmer.tireswing.model.Model;
import se.wilmer.tireswing.model.ModelData;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class Configuration {
    /**
     * The default texture.
     */
    private static final String DEFAULT_TEXTURE = "ewogICJ0aW1lc3RhbXAiIDogMTU4OTI3ODY5NTExMSwKICAicHJvZmlsZUlkIiA6ICI5MThhMDI5NTU5ZGQ0Y2U2YjE2ZjdhNWQ1M2VmYjQxMiIsCiAgInByb2ZpbGVOYW1lIiA6ICJCZWV2ZWxvcGVyIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzgxOWJhN2RkNzM3M2ZiNzFjNzYzYWMzY2UwZmU5NzZhMGFjZDE2ZDRmN2JjNTZkNmI5YzE5OGU0YmMzNzk5ODEiCiAgICB9CiAgfQp9";

    /**
     * The default vector.
     */
    private static final Vector3f DEFAULT_VECTOR = new Vector3f();

    /**
     * The default quaternion.
     */
    private static final Quaternionf DEFAULT_QUATERNIONF = new Quaternionf();

    /**
     * The TireSwing plugin instance.
     */
    private final TireSwing plugin;

    /**
     * The world where the tire swing is located.
     */
    private World world;

    /**
     * The location of the tire swing.
     */
    private Location location;

    /**
     * The model used for the rope part of the tire swing.
     */
    private Model ropeModel;

    /**
     * The model used for the rotating part of the tire swing.
     */
    private Model rotationalModel;

    /**
     * The model used for the still part of the tire swing.
     */
    private Model stillModel;

    /**
     * The information about the interaction entity for the tire swing.
     */
    private InteractionEntity interactionEntity;

    /**
     * The information about fulcrum for the tire swing.
     */
    private FulcrumEntity fulcrumEntity;


    /**
     * Creates a new Configuration instance.
     *
     * @param plugin The TireSwing plugin instance.
     */
    public Configuration(TireSwing plugin) {
        this.plugin = plugin;
    }

    /**
     * Initializes the configuration.
     * <p>
     * This method loads the configuration from JSON files,
     * creates the models, interaction entity information and the fulcrum information used.
     *
     * @return if the configuration was initialized successfully.
     */
    public boolean initialize() {
        Path path = plugin.getDataFolder().toPath();
        ConfigurationNode ModelNode;
        ConfigurationNode configNode;
        try {
            ModelNode = createNode(path.resolve("model.json"), "model.json");
            configNode = createNode(path.resolve("config.json"), "config.json");
        } catch (ConfigurateException e) {
            plugin.getComponentLogger().error("Failed to load configuration node", e);
            return false;
        }

        if (!loadPosition(configNode)) {
            return false;
        }
        if (!loadInteraction(configNode)) {
            return false;
        }
        if (!loadModels(ModelNode)) {
            return false;
        }

        return loadFulcrum(configNode);
    }


    /**
     * Creates a configuration node from the specified path.
     * <p>
     * If the file does not exist, it will be copied from the plugin's resources.
     * The configuration loader is configured to handle Quaternionfc and Vector3fc types.
     *
     * @param path         The path to the configuration file.
     * @param resourcePath The path to the resource file.
     * @return The loaded configuration node.
     * @throws ConfigurateException If an error occurs while loading the configuration.
     */
    private ConfigurationNode createNode(Path path, String resourcePath) throws ConfigurateException {
        if (Files.notExists(path)) {
            plugin.saveResource(resourcePath, false);
        }

        GsonConfigurationLoader loader = GsonConfigurationLoader.builder()
                .path(path)
                .defaultOptions(options -> options.serializers(builder -> builder
                        .register(Quaternionfc.class, new QuaternionfcSerializer())
                        .register(Vector3fc.class, new Vector3fcSerializer())
                ))
                .build();

        return loader.load();
    }


    /**
     * Loads the position of the tire swing from the configuration node.
     * <p>
     * This method reads the world name and location from the "world" and "location" nodes
     * and validates their existence. It then retrieves the corresponding world from the server
     * and sets the internal world and location fields.
     *
     * @param node The configuration node containing the position information.
     * @return if the position was loaded successfully.
     */
    private boolean loadPosition(ConfigurationNode node) {
        String worldName = node.node("world").getString();
        if (worldName == null) {
            plugin.getComponentLogger().error("Missing configuration world in config.json");
            return false;
        }

        ConfigurationNode locationNode = node.node("location");
        if (locationNode == null) {
            plugin.getComponentLogger().error("Missing ConfigurationSection: location in config.json");
            return false;
        }

        World world = plugin.getServer().getWorld(worldName);
        if (world == null) {
            plugin.getComponentLogger().error("World '{}' not found!", worldName);
            return false;
        }
        this.world = world;
        this.location = getLocation(locationNode, world);
        return true;
    }


    /**
     * Loads the interaction entity configuration from the given node.
     * <p>
     * This method extracts interaction properties (height, width, and location) from the configuration
     * and creates an InteractionEntity object, that then is used to create the interaction entity.
     *
     * @param node The configuration node containing interaction information.
     * @return if the interaction was loaded successfully.
     */
    private boolean loadInteraction(ConfigurationNode node) {
        ConfigurationNode interactionNode = node.node("interaction");
        if (interactionNode == null) {
            plugin.getComponentLogger().error("Missing ConfigurationSection: interaction in config.json");
            return false;
        }

        ConfigurationNode locationNode = interactionNode.node("location");
        if (locationNode == null) {
            plugin.getComponentLogger().error("Missing ConfigurationSection: interaction.location in config.json");
            return false;
        }

        this.interactionEntity = new InteractionEntity(
                interactionNode.node("height").getFloat(),
                interactionNode.node("width").getFloat(),
                getLocation(locationNode, world)
        );
        return true;
    }


    /**
     * Loads the models from the configuration node.
     * <p>
     * This method attempts to load the rotational, rope, and still models from the configuration.
     * If any errors occur during deserialization, an error message is logged and false is returned.
     *
     * @param node The configuration node containing model information.
     * @return if all models were loaded successfully.
     */
    private boolean loadModels(ConfigurationNode node) {
        try {
            rotationalModel = getModel(node.node("rotational"));
            ropeModel = getModel(node.node("rope"));
            stillModel = getModel(node.node("still"));
        } catch (SerializationException e) {
            plugin.getComponentLogger().error("Could not load models in model.json", e);
            return false;
        }
        return true;
    }


    /**
     * Loads the fulcrum configuration from the given node.
     * <p>
     * This method extracts fulcrum properties (material, rotation, location, and radius) from the configuration
     * and creates a FulcrumEntity object, used to create the log that the swing is hanging on set the swing radius.
     *
     * @param node The configuration node containing fulcrum information.
     * @return if the fulcrum was loaded successfully.
     */
    private boolean loadFulcrum(ConfigurationNode node) {
        ConfigurationNode fulcrumNode = node.node("fulcrum");
        if (fulcrumNode == null) {
            plugin.getComponentLogger().error("Missing ConfigurationSection: fulcrum in config.json");
            return false;
        }

        String materialName = fulcrumNode.node("material").getString();
        if (materialName == null) {
            plugin.getComponentLogger().error("Missing configuration material: fulcrum.material in config.json");
            return false;
        }
        Material material = Material.matchMaterial(materialName);
        if (material == null) {
            plugin.getComponentLogger().error("Could not find material {}", materialName);
            return false;
        }

        BlockData blockData = material.createBlockData();

        Quaternionf rightRotation;
        Quaternionf leftRotation;
        try {
            rightRotation = fulcrumNode.node("right_rotation").get(Quaternionf.class, DEFAULT_QUATERNIONF);
            leftRotation = fulcrumNode.node("left_rotation").get(Quaternionf.class, DEFAULT_QUATERNIONF);
        } catch (SerializationException e) {
            plugin.getComponentLogger().error("Could not serialize rotation: fulcrum.rotation in config.json", e);
            return false;
        }
        Transformation transformation = new Transformation(
                new Vector3f(0, 0, 0),
                leftRotation,
                new Vector3f(1, 1, 1),
                rightRotation
        );

        ConfigurationNode locationNode = fulcrumNode.node("location");
        if (locationNode == null) {
            plugin.getComponentLogger().error("Missing ConfigurationSection: fulcrum.location in config.json");
            return false;
        }
        double radius = fulcrumNode.node("radius").getDouble();
        this.fulcrumEntity = new FulcrumEntity(
                getLocation(locationNode, world),
                blockData,
                transformation,
                radius
        );
        return true;
    }

    /**
     * Creates a Model instance from the given configuration node.
     * <p>
     * This method parses the configuration node to extract model data, including texture,
     * translation, rotation, and scale for each model component. It then creates a list of
     * ModelData instances and constructs a Model object using this list.
     *
     * @param configurationNode The configuration node containing model data.
     * @return The created Model instance.
     * @throws SerializationException If an error occurs during model data parsing.
     */
    private Model getModel(ConfigurationNode configurationNode) throws SerializationException {
        List<ModelData> modelDataList = new ArrayList<>();
        for (ConfigurationNode node : configurationNode.childrenList()) {
            String texture = node.node("texture").getString(DEFAULT_TEXTURE);
            ItemStack itemStack = getCustomTextureHead(texture);

            Vector3f translation = node.node("translation").get(Vector3f.class, DEFAULT_VECTOR);
            Quaternionf leftRotation = node.node("left_rotation").get(Quaternionf.class, DEFAULT_QUATERNIONF);
            Vector3f scale = node.node("scale").get(Vector3f.class, DEFAULT_VECTOR);
            Quaternionf rightRotation = node.node("right_rotation").get(Quaternionf.class, DEFAULT_QUATERNIONF);

            Transformation transformation = new Transformation(translation, leftRotation, scale, rightRotation);
            modelDataList.add(new ModelData(itemStack, transformation));
        }

        return new Model(modelDataList);
    }


    /**
     * Creates a Location object from the given configuration node and world.
     * <p>
     * This method extracts the x, y, and z coordinates from the configuration node and creates a Location
     * instance in the specified world.
     *
     * @param node  The configuration node containing location information.
     * @param world The world where the location is located.
     * @return The created Location object.
     */
    private Location getLocation(ConfigurationNode node, World world) {
        return new Location(
                world,
                node.node("x").getDouble(),
                node.node("y").getDouble(),
                node.node("z").getDouble()
        );
    }

    /**
     * Creates a custom player head item stack with the specified texture.
     * <p>
     * This method generates a new player head item with a random UUID and sets the given texture
     * as the player's skin.
     *
     * @param texture The base64 encoded texture data.
     * @return The created player head item stack.
     */
    private ItemStack getCustomTextureHead(String texture) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        item.editMeta(SkullMeta.class, meta -> {
            PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
            profile.setProperty(new ProfileProperty("textures", texture));
            meta.setPlayerProfile(profile);
        });
        return item;
    }

    /**
     * Returns the world where the tire swing is located.
     *
     * @return The world where the tire swing is located.
     */
    public World getWorld() {
        return world;
    }

    /**
     * Returns the location of the tire swing.
     *
     * @return The location of the tire swing.
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Returns the still model of the tire swing.
     *
     * @return The still model of the tire swing.
     */
    public Model getStillModel() {
        return stillModel;
    }

    /**
     * Returns the rope model of the tire swing.
     *
     * @return The rope model of the tire swing.
     */
    public Model getRopeModel() {
        return ropeModel;
    }

    /**
     * Returns the rotational model of the tire swing.
     *
     * @return The rotational model of the tire swing.
     */
    public Model getRotationalModel() {
        return rotationalModel;
    }

    /**
     * Returns the fulcrum entity of the tire swing.
     *
     * @return The fulcrum entity of the tire swing.
     */
    public FulcrumEntity getFulcrumEntity() {
        return fulcrumEntity;
    }

    /**
     * Returns the interaction entity of the tire swing.
     *
     * @return The interaction entity of the tire swing.
     */
    public InteractionEntity getInteractionEntity() {
        return interactionEntity;
    }

}

package se.wilmer.tireswing.animation;

import io.papermc.paper.entity.TeleportFlag;
import org.bukkit.Location;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Transformation;
import org.joml.Matrix4f;
import se.wilmer.tireswing.model.ModelEntity;
import se.wilmer.tireswing.model.Model;

public final class Rotation {
    private final Model tireModel;
    private final Model ropeModel;
    private final ItemDisplay itemDisplay;
    private final Location center;
    private final double radius;

    /**
     * Creates a new Rotation instance.
     *
     * @param tireModel The tire model used.
     * @param ropeModel The rope model used.
     * @param itemDisplay The main itemDisplay.
     * @param center The location of the center.
     * @param radius The radius of the rotation.
     */
    public Rotation(Model tireModel, Model ropeModel, ItemDisplay itemDisplay, Location center, double radius) {
        this.tireModel = tireModel;
        this.ropeModel = ropeModel;
        this.itemDisplay = itemDisplay;
        this.center = center;
        this.radius = radius;
    }

    /**
     * Resets the rotation
     */
    public void resetRotation() {
        rotate(Math.toRadians(0));
    }

    /**
     * Rotates the animation by the giving radian.
     *
     * @param radian The specified radius for the animation
     */
    public void rotate(double radian) {
        Matrix4f matrix4f = new Matrix4f();
        rotateRopeModule(radian, matrix4f);
        rotateTireModule(radian, matrix4f);
        teleportTireModule(radian);
    }

    /**
     * Rotates the rope model around its axis by the specified radian.
     * <p>
     * This method calculates the new transformation matrix for each model entity in the rope model,
     * and sets the interpolation duration and delay for the item display, and applies the new matrix.
     *
     * @param radian The angle in radians to rotate the rope.
     * @param matrix4f A {@link Matrix4f} instance.
     */
    private void rotateRopeModule(double radian, Matrix4f matrix4f) {
        for (ModelEntity modelEntity : ropeModel.getModelEntities()) {
            ItemDisplay itemDisplay = modelEntity.itemDisplay();
            Matrix4f matrix = createTransformationMatrix(matrix4f, modelEntity, radian);
            itemDisplay.setTransformationMatrix(matrix);
            itemDisplay.setInterpolationDelay(0);
            itemDisplay.setInterpolationDuration(1);
        }
    }

    /**
     * Creates a transformation matrix for a model entity.
     * <p>
     * This method calculates the transformation matrix for a given model entity by applying
     * translation, scaling, left rotation, and a local X rotation.
     *
     * @param matrix4f A {@link Matrix4f} instance.
     * @param modelEntity The model entity to create the transformation matrix for.
     * @param radian The angle in radians to rotate the model entity.
     * @return The calculated transformation matrix.
     */
    private Matrix4f createTransformationMatrix(Matrix4f matrix4f, ModelEntity modelEntity, double radian) {
        Transformation transformation = modelEntity.modelData().transformation();
        matrix4f.identity()
                .translate(transformation.getTranslation())
                .scale(transformation.getScale())
                .rotate(transformation.getLeftRotation())
                .rotateLocalX((float) radian);
        return matrix4f;
    }

    /**
     * Rotates the tire model around its axis by the specified radian.
     * <p>
     * This method calculates the new transformation matrix for each model entity in the tire model,
     * and sets the interpolation duration and delay for the item display, and applies the new matrix.
     *
     * @param radian The angle in radians to rotate the tire.
     * @param matrix4f A {@link Matrix4f} instance.
     */
    private void rotateTireModule(double radian, Matrix4f matrix4f) {
        tireModel.getModelEntities().forEach(modelEntity -> {
            ItemDisplay itemDisplay = modelEntity.itemDisplay();
            Transformation transformation = modelEntity.modelData().transformation();
            matrix4f.identity()
                    .translate(transformation.getTranslation())
                    .scale(transformation.getScale())
                    .rotate(transformation.getLeftRotation())
                    .rotateLocalX((float) radian);

            itemDisplay.setTransformationMatrix(matrix4f);
            itemDisplay.setInterpolationDelay(0);
            itemDisplay.setInterpolationDuration(1);
        });
    }

    /**
     * Teleports the tire model to a new location based on the given radian.
     * <p>
     * This method use {@link #calculateTireLocation} to calculate
     * the new location of the tire based on the radian and teleports
     * the item display to that location, with the retaining passengers flag.
     *
     * @param radian The angle in radians to calculate the new location.
     */
    private void teleportTireModule(double radian) {
        itemDisplay.setTeleportDuration(0);
        Location location = calculateTireLocation(-(radian + Math.toRadians(90)));
        itemDisplay.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN, TeleportFlag.EntityState.RETAIN_PASSENGERS);
    }

    /**
     * Calculates the location of the tire based on the given radian.
     * <p>
     * This method calculates the X and Z coordinates of the tire's location based on the radian and radius,
     * and creates a new Location object with the calculated coordinates.
     *
     * @param radian The angle in radians to calculate the location.
     * @return The calculated location of the tire.
     */
    private Location calculateTireLocation(double radian) {
        double z = Math.cos(radian) * radius;
        double y = Math.sin(radian) * radius;

        return center.clone().add(0, y, z);
    }
}
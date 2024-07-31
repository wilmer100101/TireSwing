package se.wilmer.tireswing.animation;

public final class Swing {
    /**
     * The total time that the swing is accelerating.
     */
    private static final int ACCELERATION_TIME = 30;
    /**
     * The default damping.
     */
    private static final double NORMAL_DAMPING = 0.5;
    /**
     * Damping to slow down the swing.
     */
    private static final double DECELERATION_DAMPING = 1.2;
    /**
     * The amplitude to increase the speed of the swing.
     */
    private static final double ACCELERATION_AMPLITUDE = 2.0;
    /**
     * The threshold that determines that the swing is still.
     */
    private static final double STILL_THRESHOLD = 0.5;
    /**
     * The threshold that determines that the angle of the swing is small enough to count as still.
     */
    private static final double ANGLE_THRESHOLD = 0.01;
    /**
     * The instance of the pendulum.
     */
    private final Pendulum pendulum;
    /**
     * Determine if the swing is slowing down.
     */
    private boolean slowing;
    /**
     * The time since start.
     */
    private double time;

    /**
     * Creates a new instance of the swing
     */
    public Swing() {
        time = 0;
        slowing = false;
        pendulum = new Pendulum();
    }

    /**
     * Updates the swing.
     * <p>
     * This method increments the internal time counter and updates the pendulum's state based on the current time.
     * If the swing is slowing down, only the pendulum's state is updated. Otherwise, the pendulum's
     * amplitude is adjusted based on the acceleration time and the update method is called.
     */
    public void update() {
        time++;

        if (slowing) {
            pendulum.update();
            return;
        }
        if (time >= ACCELERATION_TIME) {
            pendulum.setAmplitude(0);
            pendulum.setDamping(NORMAL_DAMPING);
        } else {
            pendulum.setAmplitude(ACCELERATION_AMPLITUDE);
        }

        pendulum.update();
    }

    /**
     * Slows down the swing
     * <p>
     * Sets the pendulum's amplitude to 0 and increases damping to decelerate the swing.
     * Marks the swing as slowing down.
     */
    public void slowdown() {
        pendulum.setAmplitude(0);
        pendulum.setDamping(DECELERATION_DAMPING);
        slowing = true;
    }

    /**
     * Determines if the swing is still.
     * <p>
     * A swing is considered still when its angular velocity and angle are below certain thresholds,
     * and the swing has passed the acceleration phase.
     *
     * @return if the swing is still.
     */
    public boolean isStill() {
        return Math.abs(pendulum.getAngularVelocity()) < STILL_THRESHOLD &&
                Math.abs(pendulum.getAngle()) < ANGLE_THRESHOLD &&
                time >= ACCELERATION_TIME;
    }

    /**
     * Returns the current angle of the swing
     * @return the angle of the swing
     */
    public double getAngle() {
        return pendulum.getAngle();
    }
}
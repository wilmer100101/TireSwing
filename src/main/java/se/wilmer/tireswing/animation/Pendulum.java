package se.wilmer.tireswing.animation;

public final class Pendulum {
    /**
     * Gravitational constant (m/s^2).
     */
    private static final double GRAVITY = 9.81;

    /**
     * Length of the pendulum (meters).
     */
    private static final double LENGTH = 1.0;

    /**
     * Mass of the bob (kilograms).
     */
    private static final double MASS = 1.0;

    /**
     * Driving force frequency (radians/second).
     */
    private static final double DRIVE_FREQUENCY = 3.20;

    /**
     * Time step for simulation (seconds).
     */
    private static final double TIME_STAMP = 0.05;

    /**
     * Current angle of the pendulum (radians).
     */
    private double angle;

    /**
     * Current angular velocity of the pendulum (radians/second).
     */
    private double angularVelocity;

    /**
     * Amplitude of the driving force.
     */
    private double amplitude;

    /**
     * Damping factor (controls how quickly the pendulum loses energy).
     */
    private double damping;

    /**
     * Current simulation time (seconds).
     */
    private double time;

    public Pendulum() {
        this.angle = Math.toRadians(0);
        this.angularVelocity = 0;
        this.amplitude = 0;
        this.damping = 0;
        this.time = 0;
    }

    /**
     * Updates the state of the pendulum.
     * This method should be called repeatedly to simulate the pendulum's motion.
     */
    public void update() {
        time += TIME_STAMP;

        double angularAcceleration = calculateAngularAcceleration();

        updateAngle();
        updateVelocity(angularAcceleration);
    }

    /**
     * Calculates the angular acceleration of the pendulum based on the
     * current angle, angular velocity, and driving force.
     *
     * @return Angular acceleration of the pendulum (radians/second^2).
     */
    private double calculateAngularAcceleration() {
        return -(GRAVITY / LENGTH) * Math.sin(angle)
                - (damping / (MASS * Math.pow(LENGTH, 2))) * angularVelocity
                + (amplitude / (MASS * Math.pow(LENGTH, 2))) * Math.cos(DRIVE_FREQUENCY * time);
    }

    /**
     * Updates the angle of the pendulum based on the current angular velocity
     * and time step.
     */
    private void updateAngle() {
        angle += angularVelocity * TIME_STAMP;
        angle = Math.IEEEremainder(angle, Math.toRadians(270));
    }

    /**
     * Updates the angular velocity of the pendulum based on the calculated
     * angular acceleration and time step.
     *
     * @param angularAcceleration Angular acceleration of the pendulum (radians/second^2).
     */
    private void updateVelocity(double angularAcceleration) {
        angularVelocity += angularAcceleration * TIME_STAMP;
    }

    /**
     /**
     * Gets the current angle of the pendulum in radians.
     *
     * @return Current angle of the pendulum.
     */
    public double getAngle() {
        return angle;
    }

    /**
     * Gets the current angular velocity of the pendulum.
     *
     * @return Current angular velocity of the pendulum.
     */
    public double getAngularVelocity() {
        return angularVelocity;
    }

    /**
     * Sets the damping factor of the pendulum.
     *
     * @param damping Damping factor (0 for no damping, higher values for stronger damping).
     */
    public void setDamping(double damping) {
        this.damping = damping;
    }

    /**
     * Sets the amplitude of the driving force.
     *
     * @param amplitude Amplitude of the driving force.
     */
    public void setAmplitude(double amplitude) {
        this.amplitude = amplitude;
    }
}
package se.wilmer.tireswing;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import se.wilmer.tireswing.configuration.Configuration;

public final class TireSwing extends JavaPlugin {
    private Controller controller;

    @Override
    public void onEnable() {
        PluginManager pluginManager = getServer().getPluginManager();

        Configuration configuration = new Configuration(this);
        if (!configuration.initialize()) {
            pluginManager.disablePlugin(this);
            return;
        }

        controller = new Controller(
                this,
                configuration.getStillModel(),
                configuration.getRotationalModel(),
                configuration.getRopeModel(),
                configuration.getLocation(),
                configuration.getWorld()
        );
        controller.createInteraction(configuration.getInteractionEntity());
        controller.createFulcrum(configuration.getFulcrumEntity());
        controller.spawn();

        Passenger passenger = new Passenger(controller);
        pluginManager.registerEvents(passenger, this);
    }

    @Override
    public void onDisable() {
        if (controller != null) {
            controller.clear();
        }
        getServer().getScheduler().cancelTasks(this);
    }
}
package se.wilmer.tireswing;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class Passenger implements Listener {
    private final Controller controller;
    private Player passenger;

    public Passenger(Controller controller) {
        this.controller = controller;
    }

    @EventHandler
    public void onEntityDismount(EntityDismountEvent event) {
        if (!controller.hasPassenger()) {
            return;
        }
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (player.getUniqueId().equals(passenger.getUniqueId())) {
            controller.setHasPassenger(false);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (!controller.hasPassenger()) {
            return;
        }
        Player player = event.getPlayer();
        if (player.getUniqueId().equals(passenger.getUniqueId())) {
            controller.getItemDisplay().removePassenger(player);
            controller.setHasPassenger(false);
        }
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        if (!controller.hasPassenger() && event.getRightClicked().getUniqueId().equals(controller.getInteraction().getUniqueId()) && !controller.isSwinging()) {
            Player player = event.getPlayer();
            passenger = player;
            controller.swing(player);
            controller.setHasPassenger(true);
        }
    }
}



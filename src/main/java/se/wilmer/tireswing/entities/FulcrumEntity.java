package se.wilmer.tireswing.entities;

import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.Transformation;

public record FulcrumEntity(Location location, BlockData blockData, Transformation transformation, double radius) {
}



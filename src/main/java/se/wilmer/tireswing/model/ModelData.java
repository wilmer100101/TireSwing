package se.wilmer.tireswing.model;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;

public record ModelData(ItemStack itemStack, Transformation transformation) {
}
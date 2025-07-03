package fr.iglee42.createqualityoflife.utils;

import fr.iglee42.createqualityoflife.utils.EnderPackagersNetworkHandler.Frequency;

import net.createmod.catnip.data.Couple;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;

public interface IEnderLinkable {

	public void setReceivedPackage(ItemStack packageStack);

	public boolean isListening();

	public boolean isAlive();

	public Couple<Frequency> getNetworkKey();

	public BlockPos getLocation();

	public boolean canAcceptPackage(ItemStack pItem);

}

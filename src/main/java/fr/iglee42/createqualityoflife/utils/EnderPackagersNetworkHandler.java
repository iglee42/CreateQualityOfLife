package fr.iglee42.createqualityoflife.utils;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import com.simibubi.create.Create;
import com.simibubi.create.infrastructure.config.AllConfigs;

import fr.iglee42.createqualityoflife.CreateQOL;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.levelWrappers.WorldHelper;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;

public class EnderPackagersNetworkHandler {

	static final Map<LevelAccessor, Map<Couple<Frequency>, Set<IEnderLinkable>>> connections =
		new IdentityHashMap<>();

	public final AtomicInteger globalPowerVersion = new AtomicInteger();

	public static class Frequency {
		public static final Frequency EMPTY = new Frequency(ItemStack.EMPTY);
		private static final Map<Item, Frequency> simpleFrequencies = new IdentityHashMap<>();
		private ItemStack stack;
		private Item item;
		private int color;

		public static Frequency of(ItemStack stack) {
			if (stack.isEmpty())
				return EMPTY;
			if (stack.getComponents().isEmpty())
				return simpleFrequencies.computeIfAbsent(stack.getItem(), $ -> new Frequency(stack));
			return new Frequency(stack);
		}

		private Frequency(ItemStack stack) {
			this.stack = stack;
			item = stack.getItem();
			color = stack.has(DataComponents.DYED_COLOR) ? stack.get(DataComponents.DYED_COLOR).rgb() : -1;
		}

		public ItemStack getStack() {
			return stack;
		}

		@Override
		public int hashCode() {
			return (item.hashCode() * 31) ^ color;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			return obj instanceof Frequency ? ((Frequency) obj).item == item && ((Frequency) obj).color == color
				: false;
		}

	}

	public void onLoadWorld(LevelAccessor world) {
		connections.put(world, new HashMap<>());
		CreateQOL.LOGGER.debug("Prepared Ender Packager Network Space for " + WorldHelper.getDimensionID(world));
	}

	public void onUnloadWorld(LevelAccessor world) {
		connections.remove(world);
		CreateQOL.LOGGER.debug("Removed Ender Packager Network Space for " + WorldHelper.getDimensionID(world));
	}

	public Set<IEnderLinkable> getNetworkOf(LevelAccessor world, IEnderLinkable actor) {
		Map<Couple<Frequency>, Set<IEnderLinkable>> networksInWorld = networksIn(world);
		Couple<Frequency> key = actor.getNetworkKey();
		if (!networksInWorld.containsKey(key))
			networksInWorld.put(key, new LinkedHashSet<>());
		return networksInWorld.get(key);
	}

	public void addToNetwork(LevelAccessor world, IEnderLinkable actor) {
		getNetworkOf(world, actor).add(actor);
	}

	public void removeFromNetwork(LevelAccessor world, IEnderLinkable actor) {
		Set<IEnderLinkable> network = getNetworkOf(world, actor);
		network.remove(actor);
		if (network.isEmpty()) {
			networksIn(world).remove(actor.getNetworkKey());
        }
	}

	public boolean canSend(LevelAccessor world,IEnderLinkable actor,ItemStack packageItem){
		Set<IEnderLinkable> network = getNetworkOf(world, actor);


		for (IEnderLinkable other : network) {
			if (other != actor && other.isListening() && withinRange(actor, other))
				if (other.canAcceptPackage(packageItem)){
					return true;
				}
		}
		return false;
	}

	public void sendPackage(LevelAccessor world, IEnderLinkable actor,ItemStack packageItem) {
		Set<IEnderLinkable> network = getNetworkOf(world, actor);


		for (IEnderLinkable other : network) {
			if (other != actor && other.isListening() && withinRange(actor, other))
				if (other.canAcceptPackage(packageItem)){
					other.setReceivedPackage(packageItem);
					break;
				}
		}
	}

	public static boolean withinRange(IEnderLinkable from, IEnderLinkable to) {
		if (from == to)
			return true;
		return from.getLocation()
			.closerThan(to.getLocation(), AllConfigs.server().logistics.linkRange.get());
	}

	public Map<Couple<Frequency>, Set<IEnderLinkable>> networksIn(LevelAccessor world) {
		if (!connections.containsKey(world)) {
			CreateQOL.LOGGER.warn("Tried to Access unprepared network space of " + WorldHelper.getDimensionID(world));
			return new HashMap<>();
		}
		return connections.get(world);
	}


}

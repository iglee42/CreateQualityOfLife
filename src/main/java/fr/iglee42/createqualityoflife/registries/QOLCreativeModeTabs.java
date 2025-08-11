package fr.iglee42.createqualityoflife.registries;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllCreativeModeTabs;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import com.simibubi.create.content.logistics.box.PackageStyles;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.TagDependentIngredientItem;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import fr.iglee42.createqualityoflife.CreateQOL;
import fr.iglee42.createqualityoflife.utils.Features;
import it.unimi.dsi.fastutil.objects.*;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public class QOLCreativeModeTabs {

	private static final DeferredRegister<CreativeModeTab> TAB_REGISTER =
		DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreateQOL.MODID);

	public static final RegistryObject<CreativeModeTab> MAIN_TAB = TAB_REGISTER.register("tab",
		() -> CreativeModeTab.builder()
			.title(Component.translatable("itemGroup.createqol"))
			.withTabsBefore(AllCreativeModeTabs.BASE_CREATIVE_TAB.getKey())
			.icon(QOLItems.SHADOW_RADIANCE::asStack)
				.displayItems(new RegistrateDisplayItemsGenerator(true, QOLCreativeModeTabs.MAIN_TAB))
			.build());

	
	public static void register(IEventBus modEventBus) {
		TAB_REGISTER.register(modEventBus);
	}

	public static CreativeModeTab getBaseTab() {
		return MAIN_TAB.get();
	}


	private static class RegistrateDisplayItemsGenerator implements CreativeModeTab.DisplayItemsGenerator {
		private static final Predicate<Item> IS_ITEM_3D_PREDICATE;
		private static final Predicate<Item> SHADOW_RADIANCE = item-> !CreateQOL.isActivate(Features.SHADOW_RADIANCE) && (BuiltInRegistries.ITEM.getKey(item).getPath().startsWith("shadow_") || BuiltInRegistries.ITEM.getKey(item).getPath().startsWith("refined_"));

		static {
			MutableObject<Predicate<Item>> isItem3d = new MutableObject<>(item -> false);
			if (CatnipServices.PLATFORM.getEnv().isClient())
				isItem3d.setValue(makeClient3dItemPredicate());
			IS_ITEM_3D_PREDICATE = isItem3d.getValue();
		}

		@OnlyIn(Dist.CLIENT)
		private static Predicate<Item> makeClient3dItemPredicate() {
			return item -> {
				ItemRenderer itemRenderer = Minecraft.getInstance()
						.getItemRenderer();
				BakedModel model = itemRenderer.getModel(new ItemStack(item), null, null, 0);
				return model.isGui3d();
			};
		}

		private final boolean addItems;
		private final RegistryObject<CreativeModeTab> tabFilter;

		public RegistrateDisplayItemsGenerator(boolean addItems, RegistryObject<CreativeModeTab> tabFilter) {
			this.addItems = addItems;
			this.tabFilter = tabFilter;
		}

		private static Predicate<Item> makeExclusionPredicate() {
			Set<Item> exclusions = new ReferenceOpenHashSet<>();

			List<ItemProviderEntry<?>> simpleExclusions = List.of(
					QOLItems.SHADOW_RADIANCE_CHESTPLATE_PLACEABLE,
					QOLItems.SHADOW_STEEL_CHESTPLATE_PLACEABLE,
					QOLItems.REFINED_RADIANCE_CHESTPLATE_PLACEABLE
			);

			List<ItemEntry<TagDependentIngredientItem>> tagDependentExclusions = List.of(
			);

			if (!CreateQOL.isActivate(Features.INVENTORY_LINKER)) {
				exclusions.add(QOLBlocks.INVENTORY_LINKER.asItem());
				exclusions.add(QOLItems.PLAYER_PAPER.asItem());
			}
			if (!CreateQOL.isChippedLoaded() || !CreateQOL.isActivate(Features.CHIPPED_SAW)){
				exclusions.add(QOLBlocks.ALCHEMY_SAW.asItem());
				exclusions.add(QOLBlocks.BOTANIST_SAW.asItem());
				exclusions.add(QOLBlocks.CARPENTERS_SAW.asItem());
				exclusions.add(QOLBlocks.LOOM_SAW.asItem());
				exclusions.add(QOLBlocks.MASON_SAW.asItem());
				exclusions.add(QOLBlocks.GLASSBLOWER_SAW.asItem());
				exclusions.add(QOLBlocks.TINKERING_SAW.asItem());
			}

			if (!CreateQOL.isActivate(Features.STATUE)){
				exclusions.add(QOLItems.STATUE.asItem());
			}

			if (!CreateQOL.isActivate(Features.SHADOW_RADIANCE)){
				exclusions.add(QOLBlocks.CHROMATIC_COMPOUND_BLOCK.asItem());
			}
			if (!CreateQOL.isActivate(Features.ENDER_PACKAGER)){
				exclusions.add(QOLBlocks.ENDER_PACKAGER.asItem());
			}

			if (!CreateQOL.isActivate(Features.TRASH_CAN)){
				exclusions.add(QOLBlocks.TRASH_CAN.asItem());
				exclusions.add(QOLBlocks.BRASS_TRASH_CAN.asItem());
			}

			if (!CreateQOL.isActivate(Features.STOCK_MANAGER)){
				exclusions.add(QOLBlocks.STOCK_MANAGER.asItem());
				exclusions.add(QOLItems.EMPTY_STOCK_MANAGER.asItem());
			}

			for (ItemProviderEntry<?> entry : simpleExclusions) {
				exclusions.add(entry.asItem());
			}

			exclusions.addAll(PackageStyles.RARE_BOXES);

			for (ItemEntry<TagDependentIngredientItem> entry : tagDependentExclusions) {
				TagDependentIngredientItem item = entry.get();
				if (item.shouldHide()) {
					exclusions.add(entry.asItem());
				}
			}

			return exclusions::contains;
		}

		private static List<ItemOrdering> makeOrderings() {
			List<ItemOrdering> orderings = new ReferenceArrayList<>();

			Map<ItemProviderEntry<?>, ItemProviderEntry<?>> simpleBeforeOrderings = Map.of(
					QOLItems.EMPTY_STOCK_MANAGER,QOLBlocks.STOCK_MANAGER
			);

			Map<ItemProviderEntry<?>, ItemProviderEntry<?>> simpleAfterOrderings = Map.of(
			);

			simpleBeforeOrderings.forEach((entry, otherEntry) -> {
				orderings.add(ItemOrdering.before(entry.asItem(), otherEntry.asItem()));
			});

			simpleAfterOrderings.forEach((entry, otherEntry) -> {
				orderings.add(ItemOrdering.after(entry.asItem(), otherEntry.asItem()));
			});

			PackageStyles.STANDARD_BOXES.forEach(item -> {
				orderings.add(ItemOrdering.after(item, AllBlocks.PACKAGER.asItem()));
			});

			return orderings;
		}

		private static Function<Item, ItemStack> makeStackFunc() {
			Map<Item, Function<Item, ItemStack>> factories = new Reference2ReferenceOpenHashMap<>();

			Map<ItemProviderEntry<?>, Function<Item, ItemStack>> simpleFactories = Map.of(
					QOLItems.SHADOW_RADIANCE_CHESTPLATE, item -> {
						ItemStack stack = new ItemStack(item);
						stack.getOrCreateTag().putFloat("Air", BacktankUtil.maxAirWithoutEnchants());
						return stack;
					},QOLItems.SHADOW_STEEL_CHESTPLATE, item -> {
						ItemStack stack = new ItemStack(item);
                        stack.getOrCreateTag().putFloat("Air", BacktankUtil.maxAirWithoutEnchants());
						return stack;
					},QOLItems.REFINED_RADIANCE_CHESTPLATE, item -> {
						ItemStack stack = new ItemStack(item);
                        stack.getOrCreateTag().putFloat("Air", BacktankUtil.maxAirWithoutEnchants());
						return stack;
					}
			);

			simpleFactories.forEach((entry, factory) -> {
				factories.put(entry.asItem(), factory);
			});

			return item -> {
				Function<Item, ItemStack> factory = factories.get(item);
				if (factory != null) {
					return factory.apply(item);
				}
				return new ItemStack(item);
			};
		}

		private static Function<Item, CreativeModeTab.TabVisibility> makeVisibilityFunc() {
			Map<Item, CreativeModeTab.TabVisibility> visibilities = new Reference2ObjectOpenHashMap<>();

			Map<ItemProviderEntry<?>, CreativeModeTab.TabVisibility> simpleVisibilities = Map.of(
			);

			simpleVisibilities.forEach((entry, factory) -> {
				visibilities.put(entry.asItem(), factory);
			});

			return item -> {
				CreativeModeTab.TabVisibility visibility = visibilities.get(item);
				if (visibility != null) {
					return visibility;
				}
				return CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS;
			};
		}

		@Override
		public void accept(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
			Predicate<Item> exclusionPredicate = makeExclusionPredicate();
			List<ItemOrdering> orderings = makeOrderings();
			Function<Item, ItemStack> stackFunc = makeStackFunc();
			Function<Item, CreativeModeTab.TabVisibility> visibilityFunc = makeVisibilityFunc();

			List<Item> items = new LinkedList<>();
			if (addItems) {
				items.addAll(collectItems(exclusionPredicate.or(IS_ITEM_3D_PREDICATE.negate()).or(SHADOW_RADIANCE)));
			}
			items.addAll(collectBlocks(exclusionPredicate.or(SHADOW_RADIANCE)));
			if (addItems) {
				items.addAll(collectItems(exclusionPredicate.or(IS_ITEM_3D_PREDICATE).or(SHADOW_RADIANCE)));
			}

			applyOrderings(items, orderings);
			outputAll(output, items, stackFunc, visibilityFunc);
		}

		private List<Item> collectBlocks(Predicate<Item> exclusionPredicate) {
			List<Item> items = new ReferenceArrayList<>();
			for (RegistryEntry<Block> entry : CreateQOL.REGISTRATE.getAll(Registries.BLOCK)) {
				if (!CreateRegistrate.isInCreativeTab(entry, tabFilter))
					continue;
				Item item = entry.get()
						.asItem();
				if (item == Items.AIR)
					continue;
				if (!exclusionPredicate.test(item))
					items.add(item);
			}
			items = new ReferenceArrayList<>(new ReferenceLinkedOpenHashSet<>(items));
			return items;
		}

		private List<Item> collectItems(Predicate<Item> exclusionPredicate) {
			List<Item> items = new ReferenceArrayList<>();
			for (RegistryEntry<Item> entry : CreateQOL.REGISTRATE.getAll(Registries.ITEM)) {
				if (!CreateRegistrate.isInCreativeTab(entry, tabFilter))
					continue;
				Item item = entry.get();
				if (item instanceof BlockItem && !entry.is(QOLItems.EMPTY_STOCK_MANAGER.asItem()))
					continue;
				if (!exclusionPredicate.test(item))
					items.add(item);
			}
			return items;
		}

		private static void applyOrderings(List<Item> items, List<ItemOrdering> orderings) {
			for (ItemOrdering ordering : orderings) {
				int anchorIndex = items.indexOf(ordering.anchor());
				if (anchorIndex != -1) {
					Item item = ordering.item();
					int itemIndex = items.indexOf(item);
					if (itemIndex != -1) {
						items.remove(itemIndex);
						if (itemIndex < anchorIndex) {
							anchorIndex--;
						}
					}
					if (ordering.type() == ItemOrdering.Type.AFTER) {
						items.add(anchorIndex + 1, item);
					} else {
						items.add(anchorIndex, item);
					}
				}
			}
		}

		private static void outputAll(CreativeModeTab.Output output, List<Item> items, Function<Item, ItemStack> stackFunc, Function<Item, CreativeModeTab.TabVisibility> visibilityFunc) {
			for (Item item : items) {
				output.accept(stackFunc.apply(item), visibilityFunc.apply(item));
			}
		}

		private record ItemOrdering(Item item, Item anchor, Type type) {
			public static ItemOrdering before(Item item, Item anchor) {
				return new ItemOrdering(item, anchor, Type.BEFORE);
			}

			public static ItemOrdering after(Item item, Item anchor) {
				return new ItemOrdering(item, anchor, Type.AFTER);
			}

			public enum Type {
				BEFORE,
				AFTER;
			}
		}
	}

}
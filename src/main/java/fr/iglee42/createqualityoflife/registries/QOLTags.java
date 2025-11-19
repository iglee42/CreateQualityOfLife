package fr.iglee42.createqualityoflife.registries;

import static fr.iglee42.createqualityoflife.registries.QOLTags.NameSpace.MOD;
import static fr.iglee42.createqualityoflife.registries.QOLTags.NameSpace.COMMON;

import com.simibubi.create.Create;
import fr.iglee42.createqualityoflife.CreateQOL;
import org.jetbrains.annotations.Nullable;

import net.createmod.catnip.lang.Lang;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

public class QOLTags {
	public enum NameSpace {
		CREATE(Create.ID),
		COMMON("c"),
        MOD(CreateQOL.MODID);

		public final String id;

		NameSpace(String id) {
			this.id = id;
		}

		public ResourceLocation id(String path) {
			return ResourceLocation.fromNamespaceAndPath(this.id, path);
		}

		public ResourceLocation id(Enum<?> entry, @Nullable String pathOverride) {
			return this.id(pathOverride != null ? pathOverride : Lang.asId(entry.name()));
		}
	}

	public enum QOLBlockTags {

		;

		public final TagKey<Block> tag;

		QOLBlockTags() {
			this(MOD);
		}

		QOLBlockTags(NameSpace namespace) {
			this(namespace, null);
		}

		QOLBlockTags(NameSpace namespace, @Nullable String pathOverride) {
			this.tag = TagKey.create(Registries.BLOCK, namespace.id(this, pathOverride));
		}

		@SuppressWarnings("deprecation")
		public boolean matches(Block block) {
			return block.builtInRegistryHolder()
				.is(tag);
		}

		public boolean matches(ItemStack stack) {
			return stack != null && stack.getItem() instanceof BlockItem blockItem && matches(blockItem.getBlock());
		}

		public boolean matches(BlockState state) {
			return state.is(tag);
		}

	}


	public enum QOLItemTags {
        SUPERHEATED_LAVA_BUCKETS(COMMON, "buckets/superheated_lava")

        ;
		public final TagKey<Item> tag;

		QOLItemTags() {
			this(MOD);
		}

		QOLItemTags(NameSpace namespace) {
			this(namespace, null);
		}

		QOLItemTags(NameSpace namespace, @Nullable String pathOverride) {
			this.tag = TagKey.create(Registries.ITEM, namespace.id(this, pathOverride));
		}

		@SuppressWarnings("deprecation")
		public boolean matches(Item item) {
			return item.builtInRegistryHolder()
				.is(tag);
		}

		public boolean matches(ItemStack stack) {
			return stack.is(tag);
		}
	}

	public enum QOLFluidsTags {

		SUPERHEATED_LAVA(COMMON),
;
		public final TagKey<Fluid> tag;

		QOLFluidsTags() {
			this(MOD);
		}

		QOLFluidsTags(NameSpace namespace) {
			this(namespace, null);
		}

		QOLFluidsTags(NameSpace namespace, @Nullable String pathOverride) {
			this.tag = TagKey.create(Registries.FLUID, namespace.id(this, pathOverride));
		}

		@SuppressWarnings("deprecation")
		public boolean matches(Fluid fluid) {
			return fluid.is(tag);
		}

		public boolean matches(FluidState state) {
			return state.is(tag);
		}
	}
}

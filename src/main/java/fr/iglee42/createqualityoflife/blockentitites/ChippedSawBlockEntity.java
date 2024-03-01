package fr.iglee42.createqualityoflife.blockentitites;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.kinetics.saw.CuttingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingInventory;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.recipe.RecipeConditions;
import com.simibubi.create.foundation.recipe.RecipeFinder;
import com.simibubi.create.foundation.utility.VecHelper;
import com.simibubi.create.infrastructure.config.AllConfigs;
import earth.terrarium.chipped.common.recipes.ChippedRecipe;
import earth.terrarium.chipped.common.registry.ModRecipeTypes;
import fr.iglee42.createqualityoflife.blocks.ChippedSawBlock;
import fr.iglee42.createqualityoflife.registries.ModBlocks;
import fr.iglee42.createqualityoflife.utils.ChippedSawFilterSlot;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ChippedSawBlockEntity extends KineticBlockEntity {

	private static Object cuttingRecipesKey = new Object();


	public ProcessingInventory inventory;
	private int recipeIndex;
	private final LazyOptional<IItemHandler> invProvider;
	private FilteringBehaviour filtering;

	private ItemStack playEvent;

	public ChippedSawBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		inventory = new ProcessingInventory(this::start).withSlotLimit(!AllConfigs.server().recipes.bulkCutting.get());
		inventory.remainingTime = -1;
		recipeIndex = 0;
		invProvider = LazyOptional.of(() -> inventory);
		playEvent = ItemStack.EMPTY;
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		super.addBehaviours(behaviours);
		filtering = new FilteringBehaviour(this, new ChippedSawFilterSlot()).forRecipes();
		behaviours.add(filtering);
		behaviours.add(new DirectBeltInputBehaviour(this).allowingBeltFunnelsWhen(this::canProcess));
		registerAwardables(behaviours, AllAdvancements.SAW_PROCESSING);
	}

	@Override
	public void write(CompoundTag compound, boolean clientPacket) {
		compound.put("Inventory", inventory.serializeNBT());
		compound.putInt("RecipeIndex", recipeIndex);
		super.write(compound, clientPacket);

		if (!clientPacket || playEvent.isEmpty())
			return;
		compound.put("PlayEvent", playEvent.serializeNBT());
		playEvent = ItemStack.EMPTY;
	}

	@Override
	protected void read(CompoundTag compound, boolean clientPacket) {
		super.read(compound, clientPacket);
		inventory.deserializeNBT(compound.getCompound("Inventory"));
		recipeIndex = compound.getInt("RecipeIndex");
		if (compound.contains("PlayEvent"))
			playEvent = ItemStack.of(compound.getCompound("PlayEvent"));
	}

	@Override
	protected AABB createRenderBoundingBox() {
		return new AABB(worldPosition).inflate(.125f);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void tickAudio() {
		super.tickAudio();
		if (getSpeed() == 0)
			return;

		if (!playEvent.isEmpty()) {
			boolean isWood = false;
			Item item = playEvent.getItem();
			if (item instanceof BlockItem) {
				Block block = ((BlockItem) item).getBlock();
				isWood = block.getSoundType(block.defaultBlockState(), level, worldPosition, null) == SoundType.WOOD;
			}
			spawnEventParticles(playEvent);
			playEvent = ItemStack.EMPTY;
			if (!isWood)
				AllSoundEvents.SAW_ACTIVATE_STONE.playAt(level, worldPosition, 3, 1, true);
			else
				AllSoundEvents.SAW_ACTIVATE_WOOD.playAt(level, worldPosition, 3, 1, true);
			return;
		}
	}

	public void tick() {
		super.tick();
		if (this.canProcess()) {
			if (this.getSpeed() != 0.0F) {
				if (this.inventory.remainingTime == -1.0F) {
					if (!this.inventory.isEmpty() && !this.inventory.appliedRecipe) {
						this.start(this.inventory.getStackInSlot(0));
					}

				} else {
					float processingSpeed = Mth.clamp(Math.abs(this.getSpeed()) / 24.0F, 1.0F, 128.0F);
					ProcessingInventory var10000 = this.inventory;
					var10000.remainingTime -= processingSpeed;
					if (this.inventory.remainingTime > 0.0F) {
						this.spawnParticles(this.inventory.getStackInSlot(0));
					}

					if (this.inventory.remainingTime < 5.0F && !this.inventory.appliedRecipe) {
						if (!this.level.isClientSide || this.isVirtual()) {
							this.playEvent = this.inventory.getStackInSlot(0);
							this.applyRecipe();
							this.inventory.appliedRecipe = true;
							this.inventory.recipeDuration = 20.0F;
							this.inventory.remainingTime = 20.0F;
							this.sendData();
						}
					} else {
						Vec3 itemMovement = this.getItemMovementVec();
						Direction itemMovementFacing = Direction.getNearest(itemMovement.x, itemMovement.y, itemMovement.z);
						if (!(this.inventory.remainingTime > 0.0F)) {
							this.inventory.remainingTime = 0.0F;

							for(int slot = 0; slot < this.inventory.getSlots(); ++slot) {
								ItemStack stack = this.inventory.getStackInSlot(slot);
								if (!stack.isEmpty()) {
									ItemStack tryExportingToBeltFunnel = ((DirectBeltInputBehaviour)this.getBehaviour(DirectBeltInputBehaviour.TYPE)).tryExportingToBeltFunnel(stack, itemMovementFacing.getOpposite(), false);
									if (tryExportingToBeltFunnel != null) {
										if (tryExportingToBeltFunnel.getCount() != stack.getCount()) {
											this.inventory.setStackInSlot(slot, tryExportingToBeltFunnel);
											this.notifyUpdate();
											return;
										}

										if (!tryExportingToBeltFunnel.isEmpty()) {
											return;
										}
									}
								}
							}

							BlockPos nextPos = this.worldPosition.offset(BlockPos.containing(itemMovement));
							DirectBeltInputBehaviour behaviour = (DirectBeltInputBehaviour)BlockEntityBehaviour.get(this.level, nextPos, DirectBeltInputBehaviour.TYPE);
							ItemStack remainder;
							if (behaviour != null) {
								boolean changed = false;
								if (behaviour.canInsertFromSide(itemMovementFacing)) {
									if (!this.level.isClientSide || this.isVirtual()) {
										for(int slot = 0; slot < this.inventory.getSlots(); ++slot) {
											ItemStack stack = this.inventory.getStackInSlot(slot);
											if (!stack.isEmpty()) {
												remainder = behaviour.handleInsertion(stack, itemMovementFacing, false);
												if (!remainder.equals(stack, false)) {
													this.inventory.setStackInSlot(slot, remainder);
													changed = true;
												}
											}
										}

										if (changed) {
											this.setChanged();
											this.sendData();
										}

									}
								}
							} else {
								Vec3 outPos = VecHelper.getCenterOf(this.worldPosition).add(itemMovement.scale(0.5).add(0.0, 0.5, 0.0));
								Vec3 outMotion = itemMovement.scale(0.0625).add(0.0, 0.125, 0.0);

								for(int slot = 0; slot < this.inventory.getSlots(); ++slot) {
									remainder = this.inventory.getStackInSlot(slot);
									if (!remainder.isEmpty()) {
										ItemEntity entityIn = new ItemEntity(this.level, outPos.x, outPos.y, outPos.z, remainder);
										entityIn.setDeltaMovement(outMotion);
										this.level.addFreshEntity(entityIn);
									}
								}

								this.inventory.clear();
								this.level.updateNeighbourForOutputSignal(this.worldPosition, this.getBlockState().getBlock());
								this.inventory.remainingTime = -1.0F;
								this.sendData();
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void invalidate() {
		super.invalidate();
		invProvider.invalidate();
	}
	
	@Override
	public void destroy() {
		super.destroy();
		ItemHelper.dropContents(level, worldPosition, inventory);
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == ForgeCapabilities.ITEM_HANDLER && side != Direction.DOWN)
			return invProvider.cast();
		return super.getCapability(cap, side);
	}

	protected void spawnEventParticles(ItemStack stack) {
		if (stack == null || stack.isEmpty())
			return;

		ParticleOptions particleData = null;
		if (stack.getItem() instanceof BlockItem)
			particleData = new BlockParticleOption(ParticleTypes.BLOCK, ((BlockItem) stack.getItem()).getBlock()
				.defaultBlockState());
		else
			particleData = new ItemParticleOption(ParticleTypes.ITEM, stack);

		RandomSource r = level.random;
		Vec3 v = VecHelper.getCenterOf(this.worldPosition)
			.add(0, 5 / 16f, 0);
		for (int i = 0; i < 10; i++) {
			Vec3 m = VecHelper.offsetRandomly(new Vec3(0, 0.25f, 0), r, .125f);
			level.addParticle(particleData, v.x, v.y, v.z, m.x, m.y, m.y);
		}
	}

	protected void spawnParticles(ItemStack stack) {
		if (stack == null || stack.isEmpty())
			return;

		ParticleOptions particleData = null;
		float speed = 1;
		if (stack.getItem() instanceof BlockItem)
			particleData = new BlockParticleOption(ParticleTypes.BLOCK, ((BlockItem) stack.getItem()).getBlock()
				.defaultBlockState());
		else {
			particleData = new ItemParticleOption(ParticleTypes.ITEM, stack);
			speed = .125f;
		}

		RandomSource r = level.random;
		Vec3 vec = getItemMovementVec();
		Vec3 pos = VecHelper.getCenterOf(this.worldPosition);
		float offset = inventory.recipeDuration != 0 ? (float) (inventory.remainingTime) / inventory.recipeDuration : 0;
		offset /= 2;
		if (inventory.appliedRecipe)
			offset -= .5f;
		level.addParticle(particleData, pos.x() + -vec.x * offset, pos.y() + .45f, pos.z() + -vec.z * offset,
			-vec.x * speed, r.nextFloat() * speed, -vec.z * speed);
	}

	public Vec3 getItemMovementVec() {
		boolean alongZ =  getBlockState().getValue(ChippedSawBlock.HORIZONTAL_FACING) == Direction.NORTH || getBlockState().getValue(ChippedSawBlock.HORIZONTAL_FACING) == Direction.SOUTH;
		int offset = getSpeed() < 0 ? -1 : 1;
		return new Vec3(offset * (alongZ ? 1 : 0), 0, offset * (alongZ ? 0 : -1));
	}

	private void applyRecipe() {
		List<? extends Recipe<?>> recipes = getRecipes();
		if (recipes.isEmpty())
			return;
		if (recipeIndex >= recipes.size())
			recipeIndex = 0;

		Recipe<?> recipe = recipes.get(recipeIndex);

		ItemStack item = inventory.getStackInSlot(0);
		int rolls = inventory.getStackInSlot(0)
			.getCount();
		inventory.clear();

		List<ItemStack> list = new ArrayList<>();
		for (int roll = 0; roll < rolls; roll++) {
			List<ItemStack> results = new LinkedList<ItemStack>();
			/*if (recipe instanceof CuttingRecipe)
				results = ((CuttingRecipe) recipe).rollResults();
			else if (recipe instanceof StonecutterRecipe)
				results.add(recipe.getResultItem()
					.copy());*/

			ChippedRecipe r = (ChippedRecipe) recipe;

			List<ItemStack> tempResults = getResults(r,item).toList();

			if (filtering.getFilter().isEmpty()){
				results.add(tempResults.get(new Random().nextInt(tempResults.size())));
			} else {
				if (tempResults.stream().anyMatch(filtering::test)){
					ItemStack filter = filtering.getFilter();
					filter.setCount(1);
					results.add(filter);
				}
				//else results.add(item);
			}

			for (int i = 0; i < results.size(); i++) {
				ItemStack stack = results.get(i);
				ItemHelper.addToList(stack, list);
			}
		}
		
		for (int slot = 0; slot < list.size() && slot + 1 < inventory.getSlots(); slot++) 
			inventory.setStackInSlot(slot + 1, list.get(slot));

		award(AllAdvancements.SAW_PROCESSING);
	}

	private List<? extends Recipe<?>> getRecipes() {
/*		Optional<CuttingRecipe> assemblyRecipe = SequencedAssemblyRecipe.getRecipe(level, inventory.getStackInSlot(0),
			ModRecipeTypes.ALCHEMY_BENCH_TYPE.getType(), CuttingRecipe.class);
		if (assemblyRecipe.isPresent() && filtering.test(assemblyRecipe.get()
			.getResultItem()))
			return ImmutableList.of(assemblyRecipe.get());*/

		Predicate<Recipe<?>> types = RecipeConditions.isOfType( ModBlocks.BOTANIST_SAW.has(this.getBlockState()) ? ModRecipeTypes.BOTANIST_WORKBENCH.get() :
				ModBlocks.CARPENTERS_SAW.has(this.getBlockState()) ? ModRecipeTypes.CARPENTERS_TABLE.get() :
						ModBlocks.GLASSBLOWER_SAW.has(this.getBlockState()) ? ModRecipeTypes.GLASSBLOWER.get()  :
								ModBlocks.MASON_SAW.has(this.getBlockState()) ? ModRecipeTypes.MASON_TABLE.get() :
										ModBlocks.TINKERING_SAW.has(this.getBlockState()) ? ModRecipeTypes.TINKERING_TABLE.get() :
												ModBlocks.LOOM_SAW.has(this.getBlockState()) ? ModRecipeTypes.LOOM_TABLE.get() :
														ModRecipeTypes.ALCHEMY_BENCH.get() );

		List<Recipe<?>> startedSearch = RecipeFinder.get(cuttingRecipesKey, level, types);
		cuttingRecipesKey = new Object();
		//type = null;
		return startedSearch.stream()
				.filter(r->doesMatch((ChippedRecipe) r,inventory.getStackInSlot(0)))
				.filter(r-> getResults((ChippedRecipe) r,inventory.getStackInSlot(0)).anyMatch(filtering::test))
				//.filter(r -> !AllRecipeTypes.shouldIgnoreInAutomation(r))
				.collect(Collectors.toList());
	}

	private boolean doesMatch(ChippedRecipe recipe, ItemStack input){
			return !input.isEmpty() && recipe.tags().stream().anyMatch((tag) -> tagIs(input, tag));
	}

	public boolean tagIs(ItemStack stack, HolderSet<Item> tag) {
		return tag.contains(stack.getItem().builtInRegistryHolder());
	}

	private Stream<ItemStack> getResults(ChippedRecipe recipe, ItemStack current) {
		if (!current.isEmpty()) {
			Item item = current.getItem();
			return recipe.tags().stream().filter((tag) -> {
				return tagIs(current, tag);
			}).flatMap((tag) -> {
				return tag.stream().filter(Holder::isBound).map(Holder::value);
			}).filter((value) -> {
				return value != item;
			}).map(ItemStack::new);
		} else {
			return Stream.empty();
		}
	}

	public void insertItem(ItemEntity entity) {
		if (!canProcess())
			return;
		if (!inventory.isEmpty())
			return;
		if (!entity.isAlive())
			return;
		if (level.isClientSide)
			return;

		inventory.clear();
		ItemStack remainder = inventory.insertItem(0, entity.getItem()
			.copy(), false);
		if (remainder.isEmpty())
			entity.discard();
		else
			entity.setItem(remainder);
	}

	public void start(ItemStack inserted) {
		if (!canProcess())
			return;
		if (inventory.isEmpty())
			return;
		if (level.isClientSide && !isVirtual())
			return;

		List<? extends Recipe<?>> recipes = getRecipes();
		boolean valid = !recipes.isEmpty();
		int time = 50;

		if (recipes.isEmpty()) {
			inventory.remainingTime = inventory.recipeDuration = 10;
			inventory.appliedRecipe = false;
			sendData();
			return;
		}

		if (valid) {
			recipeIndex++;
			if (recipeIndex >= recipes.size())
				recipeIndex = 0;
		}

		Recipe<?> recipe = recipes.get(recipeIndex);
		if (recipe instanceof CuttingRecipe) {
			time = ((CuttingRecipe) recipe).getProcessingDuration();
		}

		inventory.remainingTime = time * Math.max(1, (inserted.getCount() / 5));
		inventory.recipeDuration = inventory.remainingTime;
		inventory.appliedRecipe = false;
		sendData();
	}

	protected boolean canProcess() {
		return  true;
	}


}

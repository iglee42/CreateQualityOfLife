package fr.iglee42.createqualityoflife.blocks;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.*;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBehaviour;
import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBlockItem;
import com.simibubi.create.content.logistics.packagerLink.LogisticsNetwork;
import com.simibubi.create.foundation.block.IBE;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import fr.iglee42.createqualityoflife.blockentitites.StockManagerBlockEntity;
import fr.iglee42.createqualityoflife.items.StockManagerBlockItem;
import fr.iglee42.createqualityoflife.registries.QOLBlockEntities;
import fr.iglee42.createqualityoflife.registries.QOLBlocks;
import fr.iglee42.createqualityoflife.registries.QOLItems;
import fr.iglee42.createqualityoflife.utils.LogisticsNetworkExtension;
import fr.iglee42.createqualityoflife.utils.NetworkDestructionLevel;
import fr.iglee42.createqualityoflife.utils.NetworkPermission;
import net.createmod.catnip.data.Iterate;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StockManagerBlock extends HorizontalDirectionalBlock implements IBE<StockManagerBlockEntity>, IWrenchable {

	public static final MapCodec<StockManagerBlock> CODEC = simpleCodec(StockManagerBlock::new);

	public static final BooleanProperty HAS_BLAZE = BooleanProperty.create("has_blaze");

	public StockManagerBlock(Properties pProperties) {
		super(pProperties);
		registerDefaultState(defaultBlockState().setValue(HAS_BLAZE,false));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		Direction facing = pContext.getHorizontalDirection()
			.getOpposite();
		boolean reverse = pContext.getPlayer() != null && pContext.getPlayer()
			.isShiftKeyDown();
		ItemStack stack = pContext.getItemInHand();
		Item item = stack.getItem();
		BlockState defaultState = defaultBlockState();
		if (!(item instanceof StockManagerBlockItem))
			return defaultState;
		return super.getStateForPlacement(pContext).setValue(HAS_BLAZE,((StockManagerBlockItem) item).hasCapturedBlaze()).setValue(FACING, reverse ? facing.getOpposite() : facing);
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
		super.createBlockStateDefinition(pBuilder.add(FACING,HAS_BLAZE));
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		if (stack.getItem() instanceof LogisticallyLinkedBlockItem)
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;


		return onBlockEntityUseItemOn(level, pos, stbe -> {
			if (!stbe.behaviour.mayInteractMessage(player))
				return ItemInteractionResult.SUCCESS;

			if (player instanceof ServerPlayer sp) {
				boolean showLockOption =
						stbe.behaviour.mayAdministrate(player) && Create.LOGISTICS.isLockable(stbe.behaviour.freqId);
				boolean isCurrentlyLocked = Create.LOGISTICS.isLocked(stbe.behaviour.freqId);

				LogisticsNetwork network = Create.LOGISTICS.logisticsNetworks.get(stbe.behaviour.freqId);
				String name;
				if (network != null){
					name = ((LogisticsNetworkExtension)network).createQOL$getName();
				} else {
                    name = "Unnamed Network";
                }

				int links;
				if (network != null) links = network.totalLinks.size();
                else {
                    links = 1;
                }

				NetworkDestructionLevel desLevel;
				if (network != null) desLevel = ((LogisticsNetworkExtension)network).createQOL$getDestructionLevel();
				else desLevel = NetworkDestructionLevel.ALL;

				Map<UUID, NetworkPermission> permissions;
				if (network != null){
					permissions = new HashMap<>(((LogisticsNetworkExtension)network).createQOL$getPlayersPermission());
					if (network.owner != null) permissions.put(network.owner,NetworkPermission.OWNER);
					level.players().stream().filter(p->!permissions.containsKey(p.getUUID()))
							.forEach(p->permissions.put(p.getUUID(),NetworkPermission.NONE));
				}
				else permissions = new HashMap<>();

				boolean isOwner;
				if (network != null){
					isOwner = player.getUUID().equals(network.owner);
				} else {
					isOwner = false;
				}

                sp.openMenu(stbe.new StockManagerProvider(), buf -> {
					buf.writeBoolean(showLockOption);
					buf.writeBoolean(isOwner);
					buf.writeBoolean(isCurrentlyLocked);
					buf.writeUtf(name);
					buf.writeInt(links);
					NetworkDestructionLevel.STREAM_CODEC.encode(buf,desLevel);
					buf.writeBoolean(desLevel.canDestroy(stbe.behaviour.freqId,player));
					LogisticsNetworkExtension.PERMISSIONS_STREAM_CODEC.encode(buf,permissions);
					buf.writeBlockPos(pos);
				});
			}

			//if (level.isClientSide){
			//	stbe.playEffect();
			//}else {
			//	AllSoundEvents.STOCK_TICKER_REQUEST.playOnServer(level, pos);
			//}

			/*if (!level.isClientSide() && !stbe.receivedPayments.isEmpty()) {
				for (int i = 0; i < stbe.receivedPayments.getSlots(); i++)
					player.getInventory()
						.placeItemBackInInventory(
							stbe.receivedPayments.extractItem(i, stbe.receivedPayments.getStackInSlot(i)
								.getCount(), false));
				AllSoundEvents.playItemPickup(player);
				return ItemInteractionResult.SUCCESS;
			}

			if (player instanceof ServerPlayer sp) {
				if (stbe.isKeeperPresent())
					sp.openMenu(stbe.new CategoryMenuProvider(), stbe.getBlockPos());
				else
					CreateLang.translate("stock_ticker.keeper_missing")
						.sendStatus(player);
			}*/

			return ItemInteractionResult.SUCCESS;
		});
	}

	@Override
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		return AllShapes.STOCK_TICKER;
	}

	@OnlyIn(Dist.CLIENT)
	public PartialModel getHat(LevelAccessor level, BlockPos pos, LivingEntity keeper) {
		return AllPartialModels.LOGISTICS_HAT;
	}

	@Override
	public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
		IBE.onRemove(pState, pLevel, pPos, pNewState);
	}

	@Override
	public Class<StockManagerBlockEntity> getBlockEntityClass() {
		return StockManagerBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends StockManagerBlockEntity> getBlockEntityType() {
		return QOLBlockEntities.STOCK_MANAGER.get();
	}

	@Override
	protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
		return false;
	}

	@Override
	protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
		return CODEC;
	}

	public static int getLight(BlockState state) {
		boolean hasBlaze = state.getValue(HAS_BLAZE);
		return hasBlaze ? 15 : 0;
	}

	public static LootTable.Builder buildLootTable() {
		LootItemCondition.Builder survivesExplosion = ExplosionCondition.survivesExplosion();
		StockManagerBlock block = QOLBlocks.STOCK_MANAGER.get();
		LootTable.Builder builder = LootTable.lootTable();
		LootPool.Builder poolBuilder = LootPool.lootPool();
		for (boolean hasBlaze : Iterate.trueAndFalse) {
			ItemLike drop = hasBlaze ? QOLBlocks.STOCK_MANAGER.get() : QOLItems.EMPTY_STOCK_MANAGER.get();
			poolBuilder.add(LootItem.lootTableItem(drop)
					.when(survivesExplosion)
					.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
							.setProperties(StatePropertiesPredicate.Builder.properties()
									.hasProperty(HAS_BLAZE, hasBlaze))));
		}
		builder.withPool(poolBuilder.setRolls(ConstantValue.exactly(1)));
		return builder;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		if (!state.getValue(HAS_BLAZE))
			return null;
		return IBE.super.newBlockEntity(pos, state);
	}

	@Override
	public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
		super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
		withBlockEntityDo(pLevel, pPos, plbe -> {
			if (pPlacer instanceof Player player) {
				plbe.placedBy = player.getUUID();
				plbe.notifyUpdate();
			}
		});
	}
}

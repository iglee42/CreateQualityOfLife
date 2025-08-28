package fr.iglee42.createqualityoflife.blockentitites;

import java.util.List;
import java.util.UUID;

import com.simibubi.create.api.equipment.goggles.IHaveHoveringInformation;
import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import fr.iglee42.createqualityoflife.blocks.StockManagerBlock;
import fr.iglee42.createqualityoflife.menus.ChooseLogisticNetworkMenu;
import fr.iglee42.createqualityoflife.menus.StockManagerMenu;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.math.AngleHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class StockManagerBlockEntity extends SmartBlockEntity implements IHaveHoveringInformation {

	public LogisticallyLinkedBehaviour behaviour;

	public LerpedFloat headAnimation;
	public LerpedFloat headAngle;

	public UUID placedBy;

	public StockManagerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		headAnimation = LerpedFloat.linear();
		headAngle = LerpedFloat.angular();
		headAngle.startWithValue((AngleHelper.horizontalAngle(state.getOptionalValue(StockManagerBlock.FACING)
				.orElse(Direction.SOUTH)) + 180) % 360);
		placedBy = null;
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		behaviours.add(behaviour = new LogisticallyLinkedBehaviour(this, true));
	}


	@OnlyIn(Dist.CLIENT)
	private boolean shouldTickAnimation() {
		// Offload the animation tick to the visual when flywheel in enabled
		return !VisualizationManager.supportsVisualization(level);
	}

	@OnlyIn(Dist.CLIENT)
    public void tickAnimation(){
		float target = 0;
		LocalPlayer player = Minecraft.getInstance().player;
		if (player != null && !player.isInvisible()) {
			double x;
			double z;
			if (isVirtual()) {
				x = -4;
				z = -10;
			} else {
				x = player.getX();
				z = player.getZ();
			}
			double dx = x - (getBlockPos().getX() + 0.5);
			double dz = z - (getBlockPos().getZ() + 0.5);
			target = AngleHelper.deg(-Mth.atan2(dz, dx)) - 90;
		}
		target = headAngle.getValue() + AngleHelper.getShortestAngleDiff(headAngle.getValue(), target);
		headAngle.chase(target, .25f, LerpedFloat.Chaser.exp(5));
		headAngle.tickChaser();
		headAnimation.chase( 0, .25f, LerpedFloat.Chaser.exp(.25f));
		headAnimation.tickChaser();
	}


	@Override
	public void tick() {
		super.tick();
		if (level.isClientSide()) {
			if (shouldTickAnimation())
				tickAnimation();
			/*if (!isVirtual())
				spawnParticles(getHeatLevelFromBlock(), 1);*/
			return;
		}
	}

	public boolean hasBlazeFromBlock(){
		return getBlockState().getOptionalValue(StockManagerBlock.HAS_BLAZE).orElse(false);
	}

	@Override
	protected void write(CompoundTag tag, boolean clientPacket) {
		super.write(tag,  clientPacket);
		if (placedBy != null)
			tag.putUUID("PlacedBy", placedBy);

	}

	@Override
	protected void read(CompoundTag tag, boolean clientPacket) {
		super.read(tag,  clientPacket);
		placedBy = tag.contains("PlacedBy") ? tag.getUUID("PlacedBy") : null;

	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean addToTooltip(List<Component> tooltip, boolean isPlayerSneaking) {

		return true;
	}


	public class StockManagerProvider implements MenuProvider {

		@Override
		public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
			return StockManagerMenu.create(pContainerId, pPlayerInventory, StockManagerBlockEntity.this);
		}

		@Override
		public Component getDisplayName() {
			return Component.empty();
		}

	}

	public class ChooseNetworkProvider implements MenuProvider {

		@Override
		public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
			return ChooseLogisticNetworkMenu.create(pContainerId, pPlayerInventory, StockManagerBlockEntity.this);
		}

		@Override
		public Component getDisplayName() {
			return Component.empty();
		}

	}

}

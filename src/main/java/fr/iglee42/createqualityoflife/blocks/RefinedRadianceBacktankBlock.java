package fr.iglee42.createqualityoflife.blocks;

import com.simibubi.create.AllItems;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.equipment.armor.BacktankBlock;
import com.simibubi.create.content.equipment.armor.BacktankBlockEntity;
import com.simibubi.create.content.equipment.armor.BacktankItem;
import fr.iglee42.createqualityoflife.blockentitites.ShadowRadianceBacktankBE;
import fr.iglee42.createqualityoflife.config.CreateQOLConfigs;
import fr.iglee42.createqualityoflife.items.armors.ShadowRadianceChestplate;
import fr.iglee42.createqualityoflife.registries.QOLBlockEntities;
import fr.iglee42.createqualityoflife.registries.QOLDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Optional;

public class RefinedRadianceBacktankBlock extends BacktankBlock {


    public RefinedRadianceBacktankBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(worldIn, pos, state, placer, stack);
        if (worldIn.isClientSide)
            return;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!level.isClientSide) {

        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader pLevel, BlockPos pos, BlockState state) {
        ItemStack stack = super.getCloneItemStack(pLevel, pos, state);
        return stack;
    }

    @Override
    public Class<BacktankBlockEntity> getBlockEntityClass() {
        return super.getBlockEntityClass();
    }

    @Override
    public BlockEntityType<? extends BacktankBlockEntity> getBlockEntityType() {
        return QOLBlockEntities.REFINED_RADIANCE_CHEST_BE
                .get();
    }
}

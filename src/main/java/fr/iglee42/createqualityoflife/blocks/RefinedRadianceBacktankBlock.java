package fr.iglee42.createqualityoflife.blocks;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.equipment.armor.BacktankBlock;
import com.simibubi.create.content.equipment.armor.BacktankBlockEntity;
import com.simibubi.create.content.equipment.armor.BacktankItem;
import fr.iglee42.createqualityoflife.CreateQOLLang;
import fr.iglee42.createqualityoflife.blockentitites.RefinedRadianceBacktankBE;
import fr.iglee42.createqualityoflife.config.CreateQOLConfigs;
import fr.iglee42.createqualityoflife.items.armors.ShadowRadianceChestplate;
import fr.iglee42.createqualityoflife.registries.QOLBlockEntities;
import fr.iglee42.createqualityoflife.utils.NBTConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Map;
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
        withBlockEntityDo(worldIn, pos, be -> {
            ((RefinedRadianceBacktankBE)be).setElytra(ShadowRadianceChestplate.hasElytra(stack));
        });
    }


    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            if (level.getBlockEntity(pos) instanceof RefinedRadianceBacktankBE be){
                if (!be.hasElytra() && player.getMainHandItem().is(Items.ELYTRA)) {
                    if (!CreateQOLConfigs.server().equipments.armors.elytraAllowed.get()) {
                        player.displayClientMessage(CreateQOLLang.translateDirect("backtank.elytra_disabled").withStyle(ChatFormatting.RED), true);
                        level.playSound(null, pos, AllSoundEvents.DENY.getMainEvent(), SoundSource.PLAYERS, 1, 1.45f);
                        return InteractionResult.PASS;
                    }
                    be.setElytra(true);
                    if (be.hasElytra()) {
                        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(player.getMainHandItem());
                        if (!enchantments.isEmpty()) {
                            ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
                            EnchantmentHelper.setEnchantments(enchantments, book);
                            Block.popResource(level, pos, book);
                        }

                        player.getMainHandItem().shrink(1);
                        level.playSound(null, pos, SoundEvents.COPPER_BREAK, SoundSource.PLAYERS, 1, 1.45f);
                    }
                    return InteractionResult.CONSUME;
                }
            }
        }
        return super.use(state, level, pos, player, hand, hitResult);
    }


    @Override
    public ItemStack getCloneItemStack(BlockGetter blockGetter, BlockPos pos, BlockState state) {
        ItemStack stack = super.getCloneItemStack(blockGetter, pos, state);
        Item item = asItem();
        if (item instanceof BacktankItem.BacktankBlockItem placeable) {
            item = placeable.getActualItem();
        }
        Optional<BacktankBlockEntity> blockEntityOptional = getBlockEntityOptional(blockGetter, pos);
        blockEntityOptional.ifPresent(obe->{
            RefinedRadianceBacktankBE be = (RefinedRadianceBacktankBE) obe;
            stack.getOrCreateTag().putBoolean(NBTConstants.NBT_ELYTRA,be.hasElytra());
        });
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

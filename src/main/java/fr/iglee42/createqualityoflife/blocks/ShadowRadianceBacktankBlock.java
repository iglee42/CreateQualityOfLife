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
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Map;
import java.util.Optional;

public class ShadowRadianceBacktankBlock extends BacktankBlock {

    public static final BooleanProperty PROPELLER = BooleanProperty.create("propeller");

    public ShadowRadianceBacktankBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(PROPELLER,false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PROPELLER);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context).setValue(PROPELLER,false);
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(worldIn, pos, state, placer, stack);
        if (worldIn.isClientSide)
            return;
        withBlockEntityDo(worldIn, pos, be -> {
            ((ShadowRadianceBacktankBE)be).setPropeller(ShadowRadianceChestplate.hasPropeller(stack));
            ((ShadowRadianceBacktankBE)be).setElytra(ShadowRadianceChestplate.hasElytra(stack));
        });
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            if (level.getBlockEntity(pos) instanceof ShadowRadianceBacktankBE be){
                if (!be.hasPropeller() && AllItems.PROPELLER.is(player.getMainHandItem().getItem())) {
                    if (!CreateQOLConfigs.server().propellerAllowed.get()) {
                        player.displayClientMessage(Component.literal("Propellers are disabled by the config").withStyle(ChatFormatting.RED), true);
                        level.playSound(null, pos, AllSoundEvents.DENY.getMainEvent(), SoundSource.PLAYERS, 1, 1.45f);
                        return InteractionResult.PASS;
                    }
                    be.setPropeller(true);
                    player.getMainHandItem().shrink(1);
                    level.playSound(null, pos, SoundEvents.COPPER_BREAK, SoundSource.PLAYERS, 1, 1.45f);
                    return InteractionResult.CONSUME;
                }
                if (!be.hasElytra() && player.getMainHandItem().is(Items.ELYTRA)) {
                    if (!CreateQOLConfigs.server().elytraAllowed.get()) {
                        player.displayClientMessage(Component.literal("Elytra are disabled by the config").withStyle(ChatFormatting.RED), true);
                        level.playSound(null, pos, AllSoundEvents.DENY.getMainEvent(), SoundSource.PLAYERS, 1, 1.45f);
                        return InteractionResult.PASS;
                    }
                    be.setElytra(true);
                    Map<Enchantment,Integer> enchantments = EnchantmentHelper.getEnchantments(player.getMainHandItem());
                    if (!enchantments.isEmpty()){
                        ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
                        EnchantmentHelper.setEnchantments(enchantments,book);
                        Block.popResource(level,pos,book);
                    }

                    player.getMainHandItem().shrink(1);
                    level.playSound(null, pos, SoundEvents.COPPER_BREAK, SoundSource.PLAYERS, 1, 1.45f);
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
            ShadowRadianceBacktankBE be = (ShadowRadianceBacktankBE) obe;
            boolean propeller = be.hasPropeller();
            stack.getOrCreateTag().putBoolean(NBTConstants.NBT_PROPELLERS,propeller);
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
        return QOLBlockEntities.SHADOW_CHEST_BE
                .get();
    }
}

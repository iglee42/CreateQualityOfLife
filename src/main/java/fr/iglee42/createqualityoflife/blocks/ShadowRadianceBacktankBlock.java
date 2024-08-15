package fr.iglee42.createqualityoflife.blocks;

import com.simibubi.create.AllEnchantments;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.equipment.armor.BacktankBlock;
import com.simibubi.create.content.equipment.armor.BacktankBlockEntity;
import com.simibubi.create.content.equipment.armor.BacktankItem;
import fr.iglee42.createqualityoflife.blockentitites.ShadowRadianceBacktankBE;
import fr.iglee42.createqualityoflife.items.ShadowRadianceChestplate;
import fr.iglee42.createqualityoflife.registries.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.util.FakePlayer;

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
            ((ShadowRadianceBacktankBE)be).setFans(ShadowRadianceChestplate.isFansEnable(stack));
            ((ShadowRadianceBacktankBE)be).setHover(ShadowRadianceChestplate.isHoverEnable(stack));
        });
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (AllItems.PROPELLER.is(player.getMainHandItem().getItem()) && !world.isClientSide) {
            if (world.getBlockEntity(pos) instanceof ShadowRadianceBacktankBE be && !   be.hasPropeller()){
                be.setPropeller(true);
                player.getMainHandItem().shrink(1);
                world.playSound(null, pos, SoundEvents.COPPER_BREAK, SoundSource.PLAYERS, 1, 1.45f);
                return InteractionResult.CONSUME;
            }
        }
        return super.use(state, world, pos, player, hand, hit);
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
            boolean fans = be.isFans();
            boolean hover = be.isHover();
            stack.getOrCreateTag().putBoolean("Propeller",propeller);
            stack.getOrCreateTag().putBoolean("FansEnable",fans);
            stack.getOrCreateTag().putBoolean("HoverEnable",hover);
        });
        return stack;
    }

    @Override
    public Class<BacktankBlockEntity> getBlockEntityClass() {
        return super.getBlockEntityClass();
    }

    @Override
    public BlockEntityType<? extends BacktankBlockEntity> getBlockEntityType() {
        return ModBlockEntities.SHADOW_CHEST_BE
                .get();
    }
}

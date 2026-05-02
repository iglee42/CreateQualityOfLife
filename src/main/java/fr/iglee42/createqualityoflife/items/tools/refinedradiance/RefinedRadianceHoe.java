package fr.iglee42.createqualityoflife.items.tools.refinedradiance;

import com.simibubi.create.AllTags;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.mixin.accessor.CropBlockAccessor;
import com.simibubi.create.foundation.utility.BlockHelper;
import com.simibubi.create.infrastructure.config.AllConfigs;
import fr.iglee42.createqualityoflife.CreateQOL;
import fr.iglee42.createqualityoflife.CreateQOLLang;
import fr.iglee42.createqualityoflife.config.CreateQOLConfigs;
import fr.iglee42.createqualityoflife.registries.QOLDataComponents;
import fr.iglee42.createqualityoflife.registries.QOLItems;
import fr.iglee42.createqualityoflife.registries.QOLTiers;
import fr.iglee42.createqualityoflife.utils.ItemTooltips;
import fr.iglee42.createqualityoflife.utils.QOLConfigurableItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.common.SpecialPlantable;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class RefinedRadianceHoe extends HoeItem implements QOLConfigurableItem {
    public RefinedRadianceHoe(Properties p_42964_) {
        super(QOLTiers.REFINED_RADIANCE, p_42964_);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean offHand) {
        super.inventoryTick(stack, level, entity, slot, offHand);
        invTick(stack, level, entity, slot, offHand);
    }

    @Override
    public Type type() {
        return Type.ITEM;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable TooltipContext p_41422_, List<Component> components, TooltipFlag p_41424_) {
        if (!stack.getOrDefault(QOLDataComponents.ITEM_TOOLTIPS, ItemTooltips.DEFAULT).isEnable(ItemTooltips.Tooltip.OPTIONS)) return;
        components.add(Component.translatable("createqol.ability.tool.toggle_message", Component.translatable("createqol.ability.tool.reach").getString())
                .withStyle(ChatFormatting.GOLD)
                .append(QOLConfigurableItem.chooseState(CreateQOLConfigs.server().equipments.tools.reach.get(), true, stack.getOrDefault(QOLDataComponents.REACH,true), false, true)));
        components.add(Component.translatable("createqol.ability.tool.toggle_message", Component.translatable("createqol.ability.tool.harvesting").getString())
                .withStyle(ChatFormatting.GOLD)
                .append(QOLConfigurableItem.chooseState(CreateQOLConfigs.server().equipments.tools.harvesting.get(), true, stack.getOrDefault(QOLDataComponents.HARVESTING,false), false, true)));
        components.add(Component.translatable("createqol.ability.tool.toggle_message", Component.translatable("createqol.ability.tool.use_air").getString())
                .withStyle(ChatFormatting.GOLD)
                .append(QOLConfigurableItem.chooseState(CreateQOLConfigs.server().equipments.tools.use_air.get(), true, stack.getOrDefault(QOLDataComponents.USE_AIR,false), false, true)));
        super.appendHoverText(stack, p_41422_, components, p_41424_);
    }

    @Override
    public void addConfigurations(List<Configuration<?>> list, ItemStack stack) {
        list.add(Configuration.ofBool("Harvesting",stack.getOrDefault(QOLDataComponents.HARVESTING,false),QOLDataComponents.HARVESTING,
                List.of("Should replant destroyed crops"),(e,oe)->CreateQOLConfigs.server().equipments.tools.harvesting.get()));
        list.add(Configuration.ofBool("Use Air",stack.getOrDefault(QOLDataComponents.USE_AIR,false),QOLDataComponents.USE_AIR,
                List.of("Define if air should be used (if available) from the backtank instead of the tool's durability."),(e,oe)->CreateQOLConfigs.server().equipments.tools.use_air.get()));

    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide) return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand),true);
        if (player.isCrouching()) return InteractionResultHolder.pass(player.getItemInHand(hand));
        toggleAbility(player.getItemInHand(hand),player);
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }

    public static void toggleAbility(ItemStack stack, Player p) {
        if (!CreateQOLConfigs.server().equipments.tools.harvesting.get()){
            p.displayClientMessage(CreateQOLLang.translateDirect("ability.tool.disabled", CreateQOLLang.translateDirect("ability.tool.harvesting").getString()).withStyle(ChatFormatting.RED),true);
            return;
        }
        boolean enable = !stack.getOrDefault(QOLDataComponents.HARVESTING,false);
        stack.set(QOLDataComponents.HARVESTING, enable);
        p.displayClientMessage(CreateQOLLang.translateDirect("ability.tool.toggle_message", CreateQOLLang.translateDirect("ability.tool.harvesting").getString()).append(QOLConfigurableItem.chooseState(true,true,enable,false,true)).withStyle(enable ? ChatFormatting.GREEN : ChatFormatting.RED),true);
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, @Nullable T entity, Consumer<Item> onBroken) {
        return stack.getOrDefault(QOLDataComponents.USE_AIR, false) && BacktankUtil.canAbsorbDamage(entity, getMaxDamage(stack)) ? 0 : super.damageItem(stack, amount, entity, onBroken);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return BacktankUtil.isBarVisible(stack, getMaxDamage(stack));
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return BacktankUtil.getBarWidth(stack, getMaxDamage(stack));
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return BacktankUtil.getBarColor(stack, getMaxDamage(stack));
    }

    public static void mineBlock(BlockEvent.BreakEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (event.isCanceled()) return;
        if (!event.getPlayer().getMainHandItem().is(QOLItems.REFINED_RADIANCE_HOE.get()) && !event.getPlayer().getMainHandItem().is(QOLItems.SHADOW_RADIANCE_HOE.get())) return;
        if (!CreateQOLConfigs.server().equipments.tools.harvesting.get()) return;
        if (!event.getPlayer().getMainHandItem().getOrDefault(QOLDataComponents.HARVESTING,false)) return;
        if (tryHarvestCrop(event.getPlayer().level(),event.getPos())) event.setCanceled(true);
    }

    public static boolean tryHarvestCrop(Level world, BlockPos pos) {
        if (world.isClientSide)
            return false;

        BlockState stateVisited = world.getBlockState(pos);
        if (stateVisited.isAir() || AllTags.AllBlockTags.NON_HARVESTABLE.matches(stateVisited))
            return false;

        boolean notCropButCuttable = false;

        if (!isValidCrop(world, pos, stateVisited)) {
            if (isValidOther(world, pos, stateVisited))
                notCropButCuttable = true;
            else
                return false;
        }

        ItemStack item = ItemStack.EMPTY;
        float effectChance = 1;

        if (stateVisited.is(BlockTags.LEAVES)) {
            item = new ItemStack(Items.SHEARS);
            effectChance = .45f;
        }

        MutableBoolean seedSubtracted = new MutableBoolean(notCropButCuttable);
        BlockState state = stateVisited;
        BlockHelper.destroyBlockAs(world, pos, null, item, effectChance, stack -> {
            if (AllConfigs.server().kinetics.harvesterReplants.get() && !seedSubtracted.getValue()
                    && ItemHelper.sameItem(stack, new ItemStack(state.getBlock()))) {
                stack.shrink(1);
                seedSubtracted.setTrue();
            }
            Block.popResource(world,pos,stack);
        });

        BlockState cutCrop = cutCrop(world, pos, stateVisited);
        world.setBlockAndUpdate(pos, cutCrop.canSurvive(world, pos) ? cutCrop : Blocks.AIR.defaultBlockState());
        return true;
    }

    public static boolean isValidCrop(Level world, BlockPos pos, BlockState state) {
        boolean harvestPartial = AllConfigs.server().kinetics.harvestPartiallyGrown.get();
        boolean replant = AllConfigs.server().kinetics.harvesterReplants.get();

        if (state.getBlock() instanceof CropBlock crop) {
            if (harvestPartial)
                return state != crop.getStateForAge(0) || !replant;
            return crop.isMaxAge(state);
        }

        if (state.getCollisionShape(world, pos)
                .isEmpty() || state.getBlock() instanceof CocoaBlock) {
            for (Property<?> property : state.getProperties()) {
                if (!(property instanceof IntegerProperty ageProperty))
                    continue;
                if (!property.getName()
                        .equals(BlockStateProperties.AGE_1.getName()))
                    continue;
                int age = state.getValue(ageProperty)
                        .intValue();
                if (state.getBlock() instanceof SweetBerryBushBlock && age <= 1 && replant)
                    continue;
                if (age == 0 && replant || !harvestPartial && (ageProperty.getPossibleValues()
                        .size() - 1 != age))
                    continue;
                return true;
            }
        }

        return false;
    }

    public static boolean isValidOther(Level world, BlockPos pos, BlockState state) {
        if (state.getBlock() instanceof CropBlock)
            return false;
        if (state.getBlock() instanceof SugarCaneBlock)
            return true;
        if (state.is(BlockTags.LEAVES))
            return true;
        if (state.getBlock() instanceof CocoaBlock)
            return state.getValue(CocoaBlock.AGE) == CocoaBlock.MAX_AGE;

        if (state.getCollisionShape(world, pos)
                .isEmpty()) {
            if (state.getBlock() instanceof GrowingPlantBlock)
                return true;

            for (Property<?> property : state.getProperties()) {
                if (!(property instanceof IntegerProperty))
                    continue;
                if (!property.getName()
                        .equals(BlockStateProperties.AGE_1.getName()))
                    continue;
                return false;
            }

            if (state.getBlock() instanceof SpecialPlantable)
                return true;
        }

        return false;
    }

    private static BlockState cutCrop(Level world, BlockPos pos, BlockState state) {
        if (!AllConfigs.server().kinetics.harvesterReplants.get()) {
            if (state.getFluidState()
                    .isEmpty())
                return Blocks.AIR.defaultBlockState();
            return state.getFluidState()
                    .createLegacyBlock();
        }

        Block block = state.getBlock();
        if (block instanceof CropBlock crop) {
            BlockState newState = crop.getStateForAge(0);
            if (!newState.is(block))
                return newState;
            IntegerProperty ageProperty = ((CropBlockAccessor) crop).create$callGetAgeProperty();
            return state.setValue(ageProperty, 0);
        }
        if (block == Blocks.SWEET_BERRY_BUSH) {
            return state.setValue(BlockStateProperties.AGE_3, Integer.valueOf(1));
        }
        if (AllTags.AllBlockTags.SUGAR_CANE_VARIANTS.matches(block) || block instanceof GrowingPlantBlock) {
            if (state.getFluidState()
                    .isEmpty())
                return Blocks.AIR.defaultBlockState();
            return state.getFluidState()
                    .createLegacyBlock();
        }
        if (state.getCollisionShape(world, pos)
                .isEmpty() || block instanceof CocoaBlock) {
            for (Property<?> property : state.getProperties()) {
                if (!(property instanceof IntegerProperty))
                    continue;
                if (!property.getName()
                        .equals(BlockStateProperties.AGE_1.getName()))
                    continue;
                return state.setValue((IntegerProperty) property, Integer.valueOf(0));
            }
        }

        if (state.getFluidState()
                .isEmpty())
            return Blocks.AIR.defaultBlockState();
        return state.getFluidState()
                .createLegacyBlock();
    }

    @Override
    public ReachType reachType(ItemStack stack) {
        return ReachType.BLOCK;
    }


}

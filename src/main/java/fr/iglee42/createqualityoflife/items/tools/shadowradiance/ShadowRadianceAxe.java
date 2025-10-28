package fr.iglee42.createqualityoflife.items.tools.shadowradiance;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import com.simibubi.create.content.kinetics.deployer.ItemApplicationRecipe;
import com.simibubi.create.content.kinetics.deployer.ManualApplicationRecipe;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.advancement.CreateAdvancement;
import fr.iglee42.createqualityoflife.CreateQOLLang;
import fr.iglee42.createqualityoflife.config.CreateQOLConfigs;
import fr.iglee42.createqualityoflife.items.tools.refinedradiance.RefinedRadianceAxe;
import fr.iglee42.createqualityoflife.items.tools.shadowsteel.ShadowSteelAxe;
import fr.iglee42.createqualityoflife.registries.QOLTiers;
import fr.iglee42.createqualityoflife.utils.ItemTooltips;
import fr.iglee42.createqualityoflife.utils.NBTConstants;
import fr.iglee42.createqualityoflife.utils.QOLConfigurableItem;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ShadowRadianceAxe extends AxeItem implements QOLConfigurableItem {
    public ShadowRadianceAxe(Properties p_42964_) {
        super(QOLTiers.SHADOW_RADIANCE,5.0F, -3.0F, p_42964_);
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
    public void appendHoverText(ItemStack stack, @Nullable Level p_41422_, List<Component> components, TooltipFlag p_41424_) {
        if (!NBTConstants.getTooltipOrDefault(stack).isEnable(ItemTooltips.Tooltip.OPTIONS)) return;
        components.add(Component.translatable("createqol.ability.tool.toggle_message", Component.translatable("createqol.ability.tool.reach").getString())
                .withStyle(ChatFormatting.GOLD)
                .append(QOLConfigurableItem.chooseState(CreateQOLConfigs.server().equipments.tools.reach.get(), true, NBTConstants.getOrDefault(stack,NBTConstants.NBT_REACH,true), false, true)));
        components.add(Component.translatable("createqol.ability.tool.toggle_message", Component.translatable("createqol.ability.tool.casingifier").getString())
                .withStyle(ChatFormatting.GOLD)
                .append(QOLConfigurableItem.chooseState(CreateQOLConfigs.server().equipments.tools.casingifier.get(), true, NBTConstants.getOrDefault(stack,NBTConstants.NBT_CASINGIFIER,false), false, true)));
        components.add(Component.translatable("createqol.ability.tool.toggle_message", Component.translatable("createqol.ability.tool.tree_decapitation").getString())
                .withStyle(ChatFormatting.GOLD)
                .append(QOLConfigurableItem.chooseState(CreateQOLConfigs.server().equipments.tools.treeDecapitation.get(), true, NBTConstants.getOrDefault(stack,NBTConstants.NBT_TREE_DECAPITATION,false), false, true)));
        super.appendHoverText(stack, p_41422_, components, p_41424_);
    }


    @Override
    public void addConfigurations(List<Configuration<?>> list, ItemStack stack) {
        list.add(Configuration.ofBool("Casingifier",NBTConstants.getOrDefault(stack,NBTConstants.NBT_CASINGIFIER,false),NBTConstants.NBT_CASINGIFIER,
                List.of("When stripping a log transform it into casing if a valid casing ingredient is available in the off hand","It also transform adjacent blocks"),(e,oe)->CreateQOLConfigs.server().equipments.tools.casingifier.get()));
        list.add(Configuration.ofBool("Tree Decapitation",NBTConstants.getOrDefault(stack,NBTConstants.NBT_TREE_DECAPITATION,false),NBTConstants.NBT_TREE_DECAPITATION,
                List.of("Should destroy a tree when a log is broken like a mechanical saw"),(e,oe)->CreateQOLConfigs.server().equipments.tools.treeDecapitation.get()));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide) return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand),true);
        if (player.isCrouching()){
            if (NBTConstants.getOrDefault(player.getItemInHand(hand),NBTConstants.NBT_TREE_DECAPITATION,false)){
                player.displayClientMessage(CreateQOLLang.translateDirect("ability.tool.unavailable", CreateQOLLang.translateDirect("ability.tool.casingifier").getString(), CreateQOLLang.translateDirect("ability.tool.tree_decapitation").getString()).withStyle(ChatFormatting.RED),true);
            }else {
                RefinedRadianceAxe.toggleAbility(player.getItemInHand(hand),player);
            }
        }
        else {
            if (NBTConstants.getOrDefault(player.getItemInHand(hand),NBTConstants.NBT_CASINGIFIER,false)){
                player.displayClientMessage(CreateQOLLang.translateDirect("ability.tool.unavailable", CreateQOLLang.translateDirect("ability.tool.tree_decapitation").getString(), CreateQOLLang.translateDirect("ability.tool.casingifier").getString()).withStyle(ChatFormatting.RED),true);
            }else {
                ShadowSteelAxe.toggleAbility(player.getItemInHand(hand),player);
            }
        }
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }

    
    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, @Nullable T entity, Consumer<T> onBroken) {
        if (BacktankUtil.canAbsorbDamage(entity, getMaxDamage(stack))) return 0;
        return super.damageItem(stack, amount, entity, onBroken);
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

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        if (!CreateQOLConfigs.server().equipments.tools.casingifier.get()
                || !NBTConstants.getOrDefault(ctx.getItemInHand(),NBTConstants.NBT_CASINGIFIER,false)) return super.useOn(ctx);

        Level level = ctx.getLevel();
        BlockPos origin = ctx.getClickedPos();
        Player player = ctx.getPlayer();
        if (player == null) return InteractionResult.PASS;

        ItemStack tool = ctx.getItemInHand();
        ItemStack offHandStack = player.getOffhandItem();
        int limit = CreateQOLConfigs.server().equipments.tools.casingifierMaxBlocks.get();

        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> toVisit = new ArrayDeque<>();
        toVisit.add(origin);

        int count = 0;

        while (!toVisit.isEmpty() && count < limit) {
            BlockPos pos = toVisit.poll();
            if (!visited.add(pos)) continue;
            boolean success = transformBlock(level, pos, player, ctx, offHandStack,(bs, recipe) -> {
                level.setBlock(pos, bs, 3);
                recipe.rollResults(ctx.getLevel().random).forEach(stack -> Block.popResource(level, pos, stack));

                boolean creative = player.isCreative();
                boolean unbreakable = offHandStack.getOrCreateTag().getBoolean("Unbreakable");
                boolean keepHeld = recipe.shouldKeepHeldItem() || creative;

                if (player instanceof ServerPlayer sp) {
                    CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(sp, pos, tool);
                }

                tool.hurtAndBreak(1, player, e->{});

                if (!unbreakable && !keepHeld) {
                    consumeItem(player, offHandStack);
                }

                awardAdvancements(player, bs);
            });

            if (success) {
                count++;

                for (Direction dir : Direction.values()) {
                    BlockPos neighbor = pos.relative(dir);
                    if (!visited.contains(neighbor))
                        toVisit.add(neighbor);
                }
            }
        }


        return count > 0 ? InteractionResult.sidedSuccess(level.isClientSide) : InteractionResult.PASS;
    }

    private void consumeItem(Player player, ItemStack reference) {
        if (reference.isEmpty()) return;

        if (reference.isDamageableItem()) {
            reference.hurtAndBreak(1, player, e->{});
        } else {
            player.getOffhandItem().shrink(1);
        }
    }
    private boolean transformBlock(Level level, BlockPos blockpos, Player player, UseOnContext ctx,ItemStack offHandStack,
                                   BiConsumer<BlockState, ManualApplicationRecipe> onSuccess) {

        Optional<BlockState> optional = Optional.ofNullable(level.getBlockState(blockpos).getToolModifiedState(ctx, net.minecraftforge.common.ToolActions.AXE_STRIP, false));
        if (optional.isEmpty()) return false;


        Optional<ItemApplicationRecipe> foundRecipe = level.getRecipeManager()
                .getAllRecipesFor(AllRecipeTypes.ITEM_APPLICATION.getType())
                .stream()
                .map(ItemApplicationRecipe.class::cast)
                .filter(r -> {
                    ManualApplicationRecipe mar = (ManualApplicationRecipe) r;
                    return mar.testBlock(optional.get()) && mar.getIngredients().get(1).test(offHandStack);
                })
                .findFirst();

        if (foundRecipe.isEmpty()) return false;

        ManualApplicationRecipe recipe = (ManualApplicationRecipe) foundRecipe.get();
        level.destroyBlock(blockpos, false);

        BlockState transformedBlock = recipe.transformBlock(optional.get(),ctx.getLevel().random);
        onSuccess.accept(transformedBlock, recipe);

        return true;
    }
    private static void awardAdvancements(Player player, BlockState placed) {
        CreateAdvancement advancement = null;

        if (AllBlocks.ANDESITE_CASING.has(placed))
            advancement = AllAdvancements.ANDESITE_CASING;
        else if (AllBlocks.BRASS_CASING.has(placed))
            advancement = AllAdvancements.BRASS_CASING;
        else if (AllBlocks.COPPER_CASING.has(placed))
            advancement = AllAdvancements.COPPER_CASING;
        else if (AllBlocks.RAILWAY_CASING.has(placed))
            advancement = AllAdvancements.TRAIN_CASING;
        else
            return;

        advancement.awardTo(player);
    }

    @Override
    public ReachType reachType(ItemStack stack) {
        return ReachType.BOTH;
    }

    @Override
    public double reachModifier(ItemStack stack) {
        return 1;
    }
}

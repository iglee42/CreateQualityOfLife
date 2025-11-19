package fr.iglee42.createqualityoflife.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CreateQOLCommand {


    public CreateQOLCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> command = dispatcher.register(Commands.literal("createqol")
                .then(Commands.literal("clearnbts").requires(c->c.hasPermission(4)).executes(this::warningMessage)
                        .then(Commands.literal("force").executes(this::clearNbts))));
        dispatcher.register(Commands.literal("cqol").redirect(command));
    }

    private int warningMessage(CommandContext<CommandSourceStack> source) {
        ServerLevel level = source.getSource().getLevel();
        if (!source.getSource().isPlayer()) return 0;
        source.getSource().sendSystemMessage(Component.literal("Warning : By executing this command you will remove all NBT from the item (enchantments, custom name, lore, attributes...), no undo possible. \nIf you are sure to do this click ").withStyle(ChatFormatting.YELLOW).append(Component.literal("here").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW).withBold(true).withUnderlined(true).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/createqol clearnbts force")))));
        return 1;
    }
    private int clearNbts(CommandContext<CommandSourceStack> source) {
        ServerLevel level = source.getSource().getLevel();
        if (!source.getSource().isPlayer()) return 0;
        if (source.getSource().getPlayer().getMainHandItem().isEmpty()){
            source.getSource().sendSystemMessage(Component.literal("You don't hold any item").withStyle(ChatFormatting.RED));
            return 1;
        }
        source.getSource().getPlayer().getMainHandItem().setTag(null);
        source.getSource().sendSystemMessage(Component.literal("NBT Removed !").withStyle(ChatFormatting.GREEN));
        return 1;
    }


}
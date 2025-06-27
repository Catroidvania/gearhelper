package com.catroidvania.gearhelper.commands;

import com.catroidvania.gearhelper.GearHelper;
import com.catroidvania.gearhelper.edit.BrushShape;
import com.catroidvania.gearhelper.edit.EditHandler;
import net.minecraft.common.command.Command;
import net.minecraft.common.command.CommandErrorHandler;
import net.minecraft.common.command.ICommandListener;
import net.minecraft.common.command.IllegalCmdListenerOperation;
import net.minecraft.common.command.completion.CommandCompletion;
import net.minecraft.common.command.completion.CommandCompletionAny;
import net.minecraft.common.command.completion.CommandCompletionChain;
import net.minecraft.common.command.completion.CommandCompletionRegistry;
import net.minecraft.common.item.Item;
import net.minecraft.common.util.ChatColors;
import net.minecraft.common.world.World;

public class CommandGenerateBrush extends Command {

    public CommandGenerateBrush() {
        super("/genBrush", true, false);
    }

    @Override
    public void onExecute(String[] args, ICommandListener commandExecutor) throws IllegalCmdListenerOperation {
        if (args.length >= 4) {
            World world = commandExecutor.getWorld();
            BrushShape shape;
            int bid = CommandFill.itemIDtoBlockID(this.tryParse_onlyPositive(Item.getStringItemIDByName(args[3]), 1));
            if (bid < 0) {
                commandExecutor.log(ChatColors.RED + "Invalid block ID " + bid);
                return;
            }
            int size = this.tryParse_onlyPositive(args[2], 1);
            int metadata = 0;
            if (args.length == 5) {
                metadata = this.tryParse_onlyPositive(args[4], 0);
            }
            if (args[1].startsWith("c")) {
                shape = BrushShape.CUBE;
            } else if (args[1].startsWith("s")) {
                shape = BrushShape.SPHERE;
                size *= 2;
            } else if (args[1].startsWith("n")) {
                shape = BrushShape.NOISE;
            } else if (args[1].startsWith("v")) {
                shape = BrushShape.EMPTY;
            } else {
                commandExecutor.log(ChatColors.YELLOW + "Valid shapes are: cube sphere noise void");
                return;
            }
            GearHelper.editor.clipboard = EditHandler.generateBrush(world, shape, size, bid, metadata);
            commandExecutor.log(ChatColors.GREEN + "Created " + shape.toString() + " brush");
        } else {
            CommandErrorHandler.commandUsageMessage(this.commandSyntax(), commandExecutor);
        }
    }

    @Override
    public void printHelpInformation(ICommandListener commandExecutor) {
        commandExecutor.log("//genBrush\n\tgenerates a brush");
    }

    @Override
    public String commandSyntax() {
        return ChatColors.YELLOW + "//genBrush <shape> <size/radius> <block> <metadata>";
    }

    @Override
    protected CommandCompletion commandCompletion() {
        return new CommandCompletionChain(CommandCompletionAny.INSTANCE, CommandCompletionAny.INSTANCE, CommandCompletionRegistry.ITEM, CommandCompletionAny.INSTANCE);
    }
}
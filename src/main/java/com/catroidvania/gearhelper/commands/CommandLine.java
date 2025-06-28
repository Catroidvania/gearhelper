package com.catroidvania.gearhelper.commands;

import com.catroidvania.gearhelper.GearHelper;
import com.fox2code.foxloader.selection.PlayerSelection;
import com.fox2code.foxloader.selection.PlayerSelectionProvider;
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


public class CommandLine extends Command {

    public CommandLine() {
        super("/line", true, false);
    }

    @Override
    public void onExecute(String[] args, ICommandListener commandExecutor) throws IllegalCmdListenerOperation {
        if (args.length >= 2) {
            PlayerSelection ps = PlayerSelectionProvider.getImplementation().getPlayerSelection(commandExecutor.getPlayerEntity());
            int bid = CommandFill.itemIDtoBlockID(this.tryParse_onlyPositive(Item.getStringItemIDByName(args[1]), 0));
            int metadata = 0;
            if (bid < 0) {
                commandExecutor.log(ChatColors.RED + "Invalid block ID " + bid);
                return;
            }
            if (args.length == 3) {
                metadata = this.tryParse_onlyPositive(args[2], 0);
            }
            int changed = GearHelper.editor.line(ps, bid, metadata);
            if (changed != -1) {
                commandExecutor.log(ChatColors.GREEN + "Changed " + changed + " blocks");
            } else {
                commandExecutor.log(ChatColors.RED + "Failed to draw line");
            }
        } else {
            CommandErrorHandler.commandUsageMessage(this.commandSyntax(), commandExecutor);
        }
    }

    @Override
    public void printHelpInformation(ICommandListener commandExecutor) {
        commandExecutor.log("//line\n\tgenerates a line between selection points");
    }

    @Override
    public String commandSyntax() {
        return ChatColors.YELLOW + "//line <block> <metadata>";
    }

    @Override
    protected CommandCompletion commandCompletion() {
        return new CommandCompletionChain(CommandCompletionRegistry.ITEM, CommandCompletionAny.INSTANCE);
    }
}

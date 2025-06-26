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
import net.minecraft.common.item.Items;
import net.minecraft.common.item.block.ItemBlock;
import net.minecraft.common.util.ChatColors;

public class CommandBox extends Command {

    public CommandBox() {
        super("/box", true, false);
    }

    @Override
    public void onExecute(String[] args, ICommandListener commandExecutor) throws IllegalCmdListenerOperation {
        if (args.length >= 2) {
            String block;
            int metadata;
            PlayerSelection ps = PlayerSelectionProvider.getImplementation().getPlayerSelection(commandExecutor.getPlayerEntity());
            try {
                block = Item.getStringItemIDByName(args[1]);
                metadata = args.length == 3 ? this.tryParse(args[2], 0) : 0;
            } catch (Exception e) {
                CommandErrorHandler.commandUsageMessage(this.commandSyntax(), commandExecutor);
                return;
            }
            this.fill(commandExecutor, ps, block, metadata);
        } else {
            CommandErrorHandler.commandUsageMessage(this.commandSyntax(), commandExecutor);
        }
    }

    public void fill(ICommandListener cmdExecutor, PlayerSelection ps, String bidstr, int metadata) {
        int bid;
        try {
            bid = itemIDtoBlockID(Integer.parseInt(bidstr));
            if (bid < 0) {
                cmdExecutor.log(ChatColors.RED + "Invalid block ID " + bidstr);
                return;
            }
        } catch (NumberFormatException e) {
            cmdExecutor.log(ChatColors.RED + "Invalid block ID " + bidstr);
            return;
        }

        if (!ps.hasSelection()) {
            cmdExecutor.log(ChatColors.RED + "Invalid selection");
            return;
        }

        int changed = GearHelper.editor.box(ps, bid, metadata);
        if (changed != -1) {
            cmdExecutor.sendNoticeToOps(cmdExecutor.getUsername() + " placed " + changed + " blocks at "
                    + ChatColors.RED + ps.getX1() + " " + ChatColors.GREEN + ps.getY1() + " " + ChatColors.AQUA + ps.getZ1() + ChatColors.GRAY);
        } else {
            cmdExecutor.log(ChatColors.RED + "Failed to build selection");
        }
    }

    public static int itemIDtoBlockID(int itemid) {
        if (Items.ITEMS_LIST[itemid] instanceof ItemBlock itemBlock) {
            return itemBlock.blockID;
        }
        return -1;
    }

    @Override
    public void printHelpInformation(ICommandListener commandExecutor) {
        commandExecutor.log(ChatColors.YELLOW + "//box\n\tbuilds a hollow box");
    }

    @Override
    public String commandSyntax() {
        return ChatColors.YELLOW + "//box <block> <metadata>";
    }

    @Override
    protected CommandCompletion commandCompletion() {
        return new CommandCompletionChain(CommandCompletionRegistry.ITEM, CommandCompletionAny.INSTANCE);
    }
}

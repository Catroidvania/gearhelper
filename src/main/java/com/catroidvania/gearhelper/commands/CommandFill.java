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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CommandFill extends Command {

    private static final Logger log = LoggerFactory.getLogger(CommandFill.class);

    public CommandFill() {
        super("/fill", true, false);
    }

    @Override
    public void onExecute(String[] args, ICommandListener commandExecutor) throws IllegalCmdListenerOperation {
        if (args.length >= 2) {
            String block;
            int metadata;
            PlayerSelection ps;

            try {
                block = Item.getStringItemIDByName(args[1]);
                metadata = args.length == 3 ? this.tryParse(args[2], 0) : 0;
                ps = PlayerSelectionProvider.getImplementation().getPlayerSelection(commandExecutor.getPlayerEntity());
            } catch (Exception e) {
                CommandErrorHandler.commandUsageMessage(this.commandSyntax(), commandExecutor);
                return;
            }
            this.fill(commandExecutor, ps, block, metadata);
        } else {
            CommandErrorHandler.commandUsageMessage(this.commandSyntax(), commandExecutor);
        }
    }

    public void fill(ICommandListener cmdExecutor, PlayerSelection ps, String bidstr, int metadata)  throws IllegalCmdListenerOperation {
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

        try {
            /*
            String itemname = bid < 256
                    ? Blocks.BLOCKS_LIST[bid].getBlockName()
                    : Items.ITEMS_LIST[bid].getItemName();
            cmdExecutor.sendNoticeToOps("Filled " + itemname + (metadata != 0 ? ":" + metadata : "") + " at " +
                    ps.getMinX() + " " + ps.getMinY() + " " + ps.getMinZ() + " to " +
                    ps.getMaxX() + " " + ps.getMaxY() + " " + ps.getMaxZ());*/
            int changed = GearHelper.editor.fill(ps, bid, metadata);
            if (changed != -1) {
                cmdExecutor.sendNoticeToOps(cmdExecutor.getUsername() + " filled " + changed + " blocks at "
                    + ChatColors.RED + ps.getX1() + " " + ChatColors.GREEN + ps.getY1() + " " + ChatColors.AQUA + ps.getZ1() + ChatColors.GRAY);
            } else {
                cmdExecutor.log(ChatColors.RED + "Failed to fill selection");
            }
        } catch (Exception e) {
            cmdExecutor.log(ChatColors.RED + "Failed to fill selection");
            log.error("Exception: ", e);
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
        commandExecutor.log(ChatColors.YELLOW + "//fill\n\tfill selected region with some block");
    }

    @Override
    public String commandSyntax() {
        return ChatColors.YELLOW + "//fill <block> <metadata>";
    }

    @Override
    protected CommandCompletion commandCompletion() {
        return new CommandCompletionChain(CommandCompletionRegistry.ITEM, CommandCompletionAny.INSTANCE);
    }
}

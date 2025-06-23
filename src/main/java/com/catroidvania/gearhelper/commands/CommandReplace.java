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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandReplace extends Command {

    private static final Logger log = LoggerFactory.getLogger(CommandFill.class);

    public CommandReplace() {
        super("/replace", true, false);
    }

    @Override
    public void onExecute(String[] args, ICommandListener commandExecutor) throws IllegalCmdListenerOperation {
        String block, replacement;
        int metadata1 = -1, metadata2 = -1;
        PlayerSelection ps = PlayerSelectionProvider.getImplementation().getPlayerSelection(commandExecutor.getPlayerEntity());

        try {
            if (args.length == 3) {
                block = Item.getStringItemIDByName(args[1]);
                replacement = Item.getStringItemIDByName(args[2]);
            } else if (args.length == 4) {
                block = Item.getStringItemIDByName(args[1]);
                metadata1 = tryParse(args[2], -1);
                if (metadata1 < 0) {
                    replacement = Item.getStringItemIDByName(args[2]);
                    metadata2 = tryParse(args[3], 0);
                } else {
                    replacement = Item.getStringItemIDByName(args[3]);
                }
            } else if (args.length == 5) {
                block = Item.getStringItemIDByName(args[1]);
                metadata1 = tryParse(args[2], 0);
                replacement = Item.getStringItemIDByName(args[3]);
                metadata2 = tryParse(args[4], 0);
            } else {
                CommandErrorHandler.commandUsageMessage(this.commandSyntax(), commandExecutor);
                return;
            }
            this.replace(commandExecutor, ps, block, metadata1, replacement, metadata2);
        } catch (Exception e) {
            CommandErrorHandler.commandUsageMessage(this.commandSyntax(), commandExecutor);
        }
    }

    public void replace(ICommandListener cmdExecutor, PlayerSelection ps, String targetstr, int metadata1, String replacementstr, int metadata2) {
        int targetid, replaceid;
        try {
            targetid = CommandFill.itemIDtoBlockID(Integer.parseInt(targetstr));
            replaceid = CommandFill.itemIDtoBlockID(Integer.parseInt(replacementstr));
            if (targetid < 0 || replaceid < 0) {
                cmdExecutor.log(ChatColors.RED + "Invalid block IDs " + targetstr + " " + replacementstr);
                return;
            }
        } catch (NumberFormatException e) {
            cmdExecutor.log(ChatColors.RED + "Invalid block IDs " + targetstr + " " + replacementstr);
            return;
        }

        if (!ps.hasSelection()) {
            cmdExecutor.log(ChatColors.RED + "Invalid selection");
            return;
        }

        try {
            int changed = GearHelper.editor.replace(ps, targetid, metadata1, replaceid, metadata2);
            if (changed != -1) {
                cmdExecutor.sendNoticeToOps(cmdExecutor.getUsername() + " replaced " + changed + " blocks at "
                        + ChatColors.RED + ps.getX1() + " " + ChatColors.GREEN + ps.getY1() + " " + ChatColors.AQUA + ps.getZ1() + ChatColors.GRAY);
            } else {
                cmdExecutor.log(ChatColors.RED + "Failed to replace blocks in selection");
            }
        } catch (Exception e) {
            cmdExecutor.log(ChatColors.RED + "Failed to replace blocks in selection");
            log.error("Exception: ", e);
        }
    }

    @Override
    public void printHelpInformation(ICommandListener commandExecutor) {
        commandExecutor.log(ChatColors.YELLOW + "//replace\n\treplace blocks in selected region with another");
    }

    @Override
    public String commandSyntax() {
        return ChatColors.YELLOW + "//replace <targetID> <replacementID>";
    }

    @Override
    protected CommandCompletion commandCompletion() {
        return new CommandCompletionChain(CommandCompletionRegistry.ITEM, CommandCompletionRegistry.ITEM, CommandCompletionRegistry.ITEM, CommandCompletionAny.INSTANCE);
    }
}

package com.catroidvania.gearhelper.commands;

import com.catroidvania.gearhelper.GearHelper;
import com.fox2code.foxloader.selection.PlayerSelection;
import com.fox2code.foxloader.selection.PlayerSelectionProvider;
import net.minecraft.common.command.Command;
import net.minecraft.common.command.ICommandListener;
import net.minecraft.common.command.IllegalCmdListenerOperation;
import net.minecraft.common.util.ChatColors;

public class CommandDelete extends Command {

    public CommandDelete() {
        super("/del", true, false);
    }

    @Override
    public void onExecute(String[] args, ICommandListener commandExecutor) throws IllegalCmdListenerOperation {
        PlayerSelection ps = PlayerSelectionProvider.getImplementation().getPlayerSelection(commandExecutor.getPlayerEntity());
        if (!ps.hasSelection()) {
            commandExecutor.log(ChatColors.RED + "Invalid selection");
            return;
        }
        int changed = GearHelper.editor.fill(ps, 0, 0);
        if (changed == -1) {
            commandExecutor.log(ChatColors.RED + "Failed to delete");
        } else {
            commandExecutor.log(ChatColors.GREEN + "Deleted " + changed + " blocks");
        }
    }

    @Override
    public void printHelpInformation(ICommandListener commandExecutor) {
        commandExecutor.log("//del\n\tfill current selection with air");
    }

    @Override
    public String commandSyntax() {
        return ChatColors.YELLOW + "//del";
    }
}

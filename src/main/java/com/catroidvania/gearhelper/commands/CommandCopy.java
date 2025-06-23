package com.catroidvania.gearhelper.commands;

import com.catroidvania.gearhelper.GearHelper;
import com.fox2code.foxloader.selection.PlayerSelection;
import com.fox2code.foxloader.selection.PlayerSelectionProvider;
import net.minecraft.common.command.Command;
import net.minecraft.common.command.ICommandListener;
import net.minecraft.common.command.IllegalCmdListenerOperation;
import net.minecraft.common.util.ChatColors;

public class CommandCopy extends Command {

    public CommandCopy() {
        super("/copy", true, false);
    }

    @Override
    public void onExecute(String[] args, ICommandListener commandExecutor) throws IllegalCmdListenerOperation {
        PlayerSelection ps = PlayerSelectionProvider.getImplementation().getPlayerSelection(commandExecutor.getPlayerEntity());
        if (!ps.hasSelection()) {
            commandExecutor.log(ChatColors.RED + "Invalid selection");
            return;
        }
        if (!GearHelper.editor.copy(ps)) {
            commandExecutor.log(ChatColors.RED + "Failed to copy");
        } else {
            commandExecutor.log(ChatColors.GREEN + "Copied selection");
        }
    }

    @Override
    public void printHelpInformation(ICommandListener commandExecutor) {
        commandExecutor.log("//copy\n\tcopy the current selection for pasting");
    }

    @Override
    public String commandSyntax() {
        return ChatColors.YELLOW + "//copy";
    }
}

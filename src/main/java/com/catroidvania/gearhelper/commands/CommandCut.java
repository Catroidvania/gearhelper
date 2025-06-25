package com.catroidvania.gearhelper.commands;

import com.catroidvania.gearhelper.GearHelper;
import com.fox2code.foxloader.selection.PlayerSelection;
import com.fox2code.foxloader.selection.PlayerSelectionProvider;
import net.minecraft.common.command.Command;
import net.minecraft.common.command.ICommandListener;
import net.minecraft.common.command.IllegalCmdListenerOperation;
import net.minecraft.common.util.ChatColors;

public class CommandCut extends Command {

    public CommandCut() {
        super("/cut", true, false);
    }

    @Override
    public void onExecute(String[] args, ICommandListener commandExecutor) throws IllegalCmdListenerOperation {
        PlayerSelection ps = PlayerSelectionProvider.getImplementation().getPlayerSelection(commandExecutor.getPlayerEntity());
        if (!ps.hasSelection()) {
            commandExecutor.log(ChatColors.RED + "Invalid selection");
            return;
        }
        if (GearHelper.editor.cut(ps) == -1) {
            commandExecutor.log(ChatColors.RED + "Failed to cut");
        } else {
            commandExecutor.log(ChatColors.GREEN + "Cut selection");
        }
    }

    @Override
    public void printHelpInformation(ICommandListener commandExecutor) {
        commandExecutor.log("//cut\n\tcopy the current selection to clipboard and replace with air");
    }

    @Override
    public String commandSyntax() {
        return ChatColors.YELLOW + "//cut";
    }
}

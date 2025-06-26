package com.catroidvania.gearhelper.commands;

import com.catroidvania.gearhelper.GearHelper;
import net.minecraft.common.command.Command;
import net.minecraft.common.command.ICommandListener;
import net.minecraft.common.command.IllegalCmdListenerOperation;
import net.minecraft.common.util.ChatColors;

public class CommandCenterOrigin extends Command {

    public CommandCenterOrigin() {
        super("/center", true, false);
    }

    @Override
    public void onExecute(String[] args, ICommandListener commandExecutor) throws IllegalCmdListenerOperation {
        if (GearHelper.editor.clipboard == null) {
            commandExecutor.log(ChatColors.RED + "Nothing in clipboard");
            return;
        }
        if (!GearHelper.editor.centerAnchor(false)) {
            commandExecutor.log(ChatColors.RED + "Failed to center");
        } else {
            commandExecutor.log(ChatColors.GREEN + "Centered clipboard");
        }
    }

    @Override
    public void printHelpInformation(ICommandListener commandExecutor) {
        commandExecutor.log("//center\n\tcenters the selection origin at its base");
    }

    @Override
    public String commandSyntax() {
        return ChatColors.YELLOW + "//center";
    }
}

package com.catroidvania.gearhelper.commands;

import com.catroidvania.gearhelper.GearHelper;
import net.minecraft.common.command.Command;
import net.minecraft.common.command.ICommandListener;
import net.minecraft.common.command.IllegalCmdListenerOperation;
import net.minecraft.common.util.ChatColors;

public class CommandRotateCCW extends Command {

    public CommandRotateCCW() { super("/rotccw", true, false); }

    @Override
    public void onExecute(String[] args, ICommandListener commandExecutor) throws IllegalCmdListenerOperation {
        if (GearHelper.editor.clipboard == null) {
            commandExecutor.log(ChatColors.RED + "Nothing copied");
            return;
        }
        if (!GearHelper.editor.rotateCCW()) {
            commandExecutor.log(ChatColors.RED + "Failed to rotate");
        } else {
            commandExecutor.log(ChatColors.GREEN + "Rotated counterclockwise");
        }
    }

    @Override
    public void printHelpInformation(ICommandListener commandExecutor) {
        commandExecutor.log("//rotccw\n\trotate the current clipboard 90 degrees counterclockwise");
    }

    @Override
    public String commandSyntax() {
        return ChatColors.YELLOW + "//rotccw";
    }
}

package com.catroidvania.gearhelper.commands;

import com.catroidvania.gearhelper.GearHelper;
import net.minecraft.common.command.Command;
import net.minecraft.common.command.ICommandListener;
import net.minecraft.common.command.IllegalCmdListenerOperation;
import net.minecraft.common.util.ChatColors;

public class CommandRedo extends Command {

    public CommandRedo() {
        super("/redo", true, false);
    }

    @Override
    public void onExecute(String[] args, ICommandListener commandExecutor) throws IllegalCmdListenerOperation {
        if (GearHelper.editor.redo()) {
            //commandExecutor.log(ChatColors.GREEN + " redid action");
            commandExecutor.sendNoticeToOps( ChatColors.GREEN + commandExecutor.getUsername() + " redid action");
        } else {
            commandExecutor.log(ChatColors.RED + "Failed to redo action");
        }
    }


    @Override
    public void printHelpInformation(ICommandListener commandExecutor) {
        commandExecutor.log("//redo\n\tredo the last undone action");
    }

    @Override
    public String commandSyntax() {
        return ChatColors.YELLOW + "//redo";
    }
}

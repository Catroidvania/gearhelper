package com.catroidvania.gearhelper.commands;

import com.catroidvania.gearhelper.GearHelper;
import net.minecraft.common.command.Command;
import net.minecraft.common.command.ICommandListener;
import net.minecraft.common.command.IllegalCmdListenerOperation;
import net.minecraft.common.util.ChatColors;

public class CommandUndo extends Command {

    public CommandUndo() {
        super("/undo", true, false);
    }

    @Override
    public void onExecute(String[] args, ICommandListener commandExecutor) throws IllegalCmdListenerOperation {
        if (GearHelper.editor.undo()) {
            commandExecutor.sendNoticeToOps(commandExecutor.getUsername() + " undid action");
        } else {
            commandExecutor.log(ChatColors.RED + "Failed to undo action");
        }
    }


    @Override
    public void printHelpInformation(ICommandListener commandExecutor) {
        commandExecutor.log("//undo\n\tundo the last edit action");
    }

    @Override
    public String commandSyntax() {
        return ChatColors.YELLOW + "//undo";
    }
}

package com.catroidvania.gearhelper.commands;

import com.catroidvania.gearhelper.GearHelper;
import com.fox2code.foxloader.selection.PlayerSelection;
import com.fox2code.foxloader.selection.PlayerSelectionProvider;
import net.minecraft.common.command.Command;
import net.minecraft.common.command.ICommandListener;
import net.minecraft.common.command.IllegalCmdListenerOperation;
import net.minecraft.common.util.ChatColors;

public class CommandPaste extends Command {

    public CommandPaste() {
        super("/paste", true, false);
    }

    @Override
    public void onExecute(String[] args, ICommandListener commandExecutor) throws IllegalCmdListenerOperation {
        PlayerSelection ps = PlayerSelectionProvider.getImplementation().getPlayerSelection(commandExecutor.getPlayerEntity());
        GearHelper.editor.clipboard.worldObj = commandExecutor.getWorld();
        int changed = GearHelper.editor.pasteAtPos1(ps);
        if (changed == -1) {
            commandExecutor.log(ChatColors.RED + "Failed to paste selection");
        } else {
            commandExecutor.sendNoticeToOps(commandExecutor.getUsername() + " pasted " + changed + " blocks at "
                    + ChatColors.RED + ps.getX1() + " " + ChatColors.GREEN + ps.getY1() + " " + ChatColors.AQUA + ps.getZ1() + ChatColors.GRAY);
            //commandExecutor.log(ChatColors.GREEN + "Pasted " + changed + " blocks");
        }
    }

    @Override
    public void printHelpInformation(ICommandListener commandExecutor) {
        commandExecutor.log(ChatColors.YELLOW + "//pasteHere\n\tpaste the copied selection at the players feet");
    }

    @Override
    public String commandSyntax() {
        return ChatColors.YELLOW + "//pasteHere";
    }
}

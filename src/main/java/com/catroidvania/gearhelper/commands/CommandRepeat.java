package com.catroidvania.gearhelper.commands;

import com.catroidvania.gearhelper.GearHelper;
import com.fox2code.foxloader.selection.PlayerSelection;
import com.fox2code.foxloader.selection.PlayerSelectionProvider;
import net.minecraft.common.command.Command;
import net.minecraft.common.command.CommandErrorHandler;
import net.minecraft.common.command.ICommandListener;
import net.minecraft.common.command.IllegalCmdListenerOperation;
import net.minecraft.common.entity.player.EntityPlayer;
import net.minecraft.common.util.ChatColors;

import net.minecraft.common.util.Facing;
import net.minecraft.common.util.math.MathHelper;


public class CommandRepeat extends Command {

    public CommandRepeat() {
        super("/tile", true, false);
    }

    @Override
    public void onExecute(String[] args, ICommandListener commandExecutor) throws IllegalCmdListenerOperation {
        if (args.length >= 1) {
            PlayerSelection ps = PlayerSelectionProvider.getImplementation().getPlayerSelection(commandExecutor.getPlayerEntity());
            if (!ps.hasSelection()) {
                commandExecutor.log(ChatColors.RED + "Invalid selection");
                return;
            }
            int repeats = 1;
            if (args.length == 2) {
                repeats = this.tryParse(args[1], 1);
            }
            System.out.println(repeats);
            int direction = GearHelper.getDirectionXY(commandExecutor.getPlayerEntity());
            if (repeats < 0) {
                direction = Facing.oppositeSide[direction];
                repeats = Math.abs(repeats);
            }
            int changed = GearHelper.editor.repeat(ps, direction, repeats);
            if (changed == -1) {
                commandExecutor.log(ChatColors.RED + "Failed to tile selection");
            } else {
                commandExecutor.sendNoticeToOps(commandExecutor.getUsername() + " tiled " + changed + " blocks at "
                        + ChatColors.RED + ps.getX1() + " " + ChatColors.GREEN + ps.getY1() + " " + ChatColors.AQUA + ps.getZ1() + ChatColors.GRAY);
            }
        } else {
            CommandErrorHandler.commandUsageMessage(this.commandSyntax(), commandExecutor);
        }
    }


    @Override
    public void printHelpInformation(ICommandListener commandExecutor) {
        commandExecutor.log(ChatColors.YELLOW + "//tile\n\tcopy and duplicate the selection horizontally in the look direction");
    }

    @Override
    public String commandSyntax() {
        return ChatColors.YELLOW + "//tile <times>";
    }
}

package com.catroidvania.gearhelper.commands;

import com.catroidvania.gearhelper.GearHelper;
import com.catroidvania.gearhelper.edit.BrushMode;
import net.minecraft.common.command.Command;
import net.minecraft.common.command.CommandErrorHandler;
import net.minecraft.common.command.ICommandListener;
import net.minecraft.common.command.IllegalCmdListenerOperation;
import net.minecraft.common.util.ChatColors;


public class CommandBrushMode extends Command {

    public CommandBrushMode() {
        super("/bmode", true, false);
    }

    @Override
    public void onExecute(String[] args, ICommandListener commandExecutor) throws IllegalCmdListenerOperation {
        if (args.length == 1) {
            commandExecutor.log("Current mode: " + GearHelper.brush.mode.toString());
        } else if (args.length == 2) {
            // i should make my own command completion but lazyy
            if (args[1].startsWith("s")) {
                //GearHelper.editor.centerAnchor(false);
                GearHelper.brush.mode = BrushMode.STAMP;
                commandExecutor.log("Current mode: Stamp");
            } else if (args[1].startsWith("b")) {
                GearHelper.editor.centerAnchor(true);
                GearHelper.brush.mode = BrushMode.BRUSH;
                commandExecutor.log("Current mode: Brush");
            } else if (args[1].startsWith("p")) {
                GearHelper.editor.centerAnchor(true);
                GearHelper.brush.mode = BrushMode.PAINT;
                commandExecutor.log("Current mode: Paint");
            } else if (args[1].startsWith("e")) {
                GearHelper.editor.centerAnchor(true);
                GearHelper.brush.mode = BrushMode.EXCAVATE;
                commandExecutor.log("Current mode: Erase");
            } else if (args[1].startsWith("n")) {
                GearHelper.brush.mode = BrushMode.DISABLED;
                commandExecutor.log("Current mode: Off");
            } else if (args[1].startsWith("f")) {
                GearHelper.brush.mode = BrushMode.FOLIAGE;
                commandExecutor.log("Current mode: Foliage");
            } else {
                commandExecutor.log(ChatColors.YELLOW + "Valid modes are: stamp brush paint erase foliage none");
            }
        } else {
            CommandErrorHandler.commandUsageMessage(this.commandSyntax(), commandExecutor);
        }
    }

    @Override
    public void printHelpInformation(ICommandListener commandExecutor) {
        commandExecutor.log("//bmode\n\tchanges brush mode");
    }

    @Override
    public String commandSyntax() {
        return ChatColors.YELLOW + "//bmode <mode>";
    }
}

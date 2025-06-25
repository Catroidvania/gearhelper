package com.catroidvania.gearhelper.commands;

import net.minecraft.common.command.Command;
import net.minecraft.common.command.ICommandListener;
import net.minecraft.common.command.IllegalCmdListenerOperation;
import net.minecraft.common.item.ItemStack;
import net.minecraft.common.item.Items;
import net.minecraft.common.util.ChatColors;

public class CommandWand extends Command {

    public CommandWand() {
        super("/wand", true, false);
    }

    @Override
    public void onExecute(String[] args, ICommandListener commandExecutor) throws IllegalCmdListenerOperation {
        commandExecutor.getPlayerEntity().givePlayerItem(new ItemStack(Items.WOOD_AXE), true);
    }

    @Override
    public void printHelpInformation(ICommandListener commandExecutor) {
        commandExecutor.log(ChatColors.YELLOW + "//wand\n\tget a wood axe");
    }

    @Override
    public String commandSyntax() {
        return ChatColors.YELLOW + "//wand";
    }
}

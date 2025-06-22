package com.catroidvania.gearhelper.commands;

import net.minecraft.common.command.CommandErrorHandler;
import net.minecraft.common.command.ICommandListener;
import net.minecraft.common.command.IllegalCmdListenerOperation;
import net.minecraft.common.command.completion.CommandCompletion;
import net.minecraft.common.command.completion.CommandCompletionAny;
import net.minecraft.common.command.completion.CommandCompletionChain;
import net.minecraft.common.command.completion.CommandCompletionRegistry;
import net.minecraft.common.item.Item;
import net.minecraft.common.util.ChatColors;
import net.minecraft.common.util.math.Vec3D;


public class CommandSethere extends CommandSetblock {

    public CommandSethere() {
        super("sethere", true, false);
    }


    @Override
    public void onExecute(String[] args, ICommandListener commandExecutor) throws IllegalCmdListenerOperation {
        int metadata;

        if (args.length >= 2) {
            String block;
            Vec3D pos;
            try {
                pos = commandExecutor.getPosition();
                block = Item.getStringItemIDByName(args[1]);
                metadata = args.length == 3 ? this.tryParse(args[2], 0) : 0;
            } catch (Exception e) {
                CommandErrorHandler.commandUsageMessage(this.commandSyntax(), commandExecutor);
                return;
            }
            this.setBlock(commandExecutor, pos, block, metadata);
        } else {
            CommandErrorHandler.commandUsageMessage(this.commandSyntax(), commandExecutor);
        }
    }

    @Override
    public String commandSyntax() {
        return ChatColors.YELLOW + "/sethere <blockID> <metadata>";
    }

    @Override
    protected CommandCompletion commandCompletion() {
        return new CommandCompletionChain(CommandCompletionRegistry.ITEM, CommandCompletionAny.INSTANCE);
    }
}

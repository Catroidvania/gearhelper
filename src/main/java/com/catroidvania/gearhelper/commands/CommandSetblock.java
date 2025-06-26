package com.catroidvania.gearhelper.commands;

import com.catroidvania.gearhelper.GearHelper;
import net.minecraft.common.block.Blocks;
import net.minecraft.common.command.*;
import net.minecraft.common.command.completion.*;
import net.minecraft.common.item.Item;
import net.minecraft.common.item.Items;
import net.minecraft.common.item.block.ItemBlock;
import net.minecraft.common.util.ChatColors;
import net.minecraft.common.util.math.Vec3D;

import static com.catroidvania.gearhelper.GearHelper.blockPos;


public class CommandSetblock extends Command {

    public CommandSetblock() {
        super("/setblock", true, false);
    }

    public CommandSetblock(String name, boolean opOnly, boolean hidden) {
        super(name, opOnly, hidden);
    }

    @Override
    public void onExecute(String[] args, ICommandListener commandExecutor) throws IllegalCmdListenerOperation {
        double x, y, z;
        int metadata;

        //commandExecutor.log(Arrays.toString(args));
        if (args.length >= 4) {
            String block;

                try {
                    Vec3D origin = commandExecutor.getPosition();
                    x = parseDouble(args[1].replaceAll("~", ""));
                    y = parseDouble(args[2].replaceAll("~", ""));
                    z = parseDouble(args[3].replaceAll("~", ""));
                    block = Item.getStringItemIDByName(args[4]);
                    metadata = args.length == 6 ? this.tryParse(args[5], 0) : 0;
                    if (args[1].startsWith("~")) {
                        x += origin.xCoord;
                    }
                    if (args[2].startsWith("~")) {
                        y += origin.yCoord;
                    }
                    if (args[3].startsWith("~")) {
                        z += origin.zCoord;
                    }
                } catch (Exception e) {
                    CommandErrorHandler.commandUsageMessage(this.commandSyntax(), commandExecutor);
                    return;
                }
            //}

            Vec3D pos = new Vec3D(x, y, z);
            this.setBlock(commandExecutor, pos, block, metadata);
        } else {
            CommandErrorHandler.commandUsageMessage(this.commandSyntax(), commandExecutor);
        }
    }

    public void setBlock(ICommandListener cmdExecutor, Vec3D pos, String bidstr, int metadata)  throws IllegalCmdListenerOperation {
        int bid, x, y, z;
        try {
            bid = Integer.parseInt(bidstr);
            if (Items.ITEMS_LIST[bid] instanceof ItemBlock itemBlock) {
                bid = itemBlock.blockID;
            } else {
                throw new NumberFormatException();
            }
            x = blockPos(pos.xCoord);
            y = blockPos(pos.yCoord) - 1;   // place at feet
            z = blockPos(pos.zCoord);
        } catch (NumberFormatException e) {
            cmdExecutor.log(ChatColors.RED + "Invalid block ID " + bidstr);
            return;
        }

        try {
            String itemname = Blocks.BLOCKS_LIST[bid].getBlockName();
            cmdExecutor.getWorld().setBlockAndMetadata(x, y, z, bid, metadata);
            cmdExecutor.sendNoticeToOps("Set block " + bid + (metadata != 0 ? ":" + metadata : "") + " (" + itemname + ") at " + x + " " + y  +" " + z);
        } catch (ArrayIndexOutOfBoundsException var11) {
            cmdExecutor.log(CommandErrorHandler.INVALID_ITEM + " " + bid);
        }
    }

    @Override
    public void printHelpInformation(ICommandListener commandExecutor) {
    }

    @Override
    public String commandSyntax() {
        return ChatColors.YELLOW + "//setblock <x> <y> <z> <block> <metadata>";
    }

    @Override
    protected CommandCompletion commandCompletion() {
        return new CommandCompletionChain(CommandCompletionAny.INSTANCE, CommandCompletionAny.INSTANCE, CommandCompletionAny.INSTANCE, CommandCompletionRegistry.ITEM, CommandCompletionAny.INSTANCE);
    }

    // need custom block completion
}

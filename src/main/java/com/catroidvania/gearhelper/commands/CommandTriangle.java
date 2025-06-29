package com.catroidvania.gearhelper.commands;

import com.catroidvania.gearhelper.GearHelper;
import com.fox2code.foxloader.selection.PlayerSelection;
import com.fox2code.foxloader.selection.PlayerSelectionProvider;
import net.minecraft.common.command.Command;
import net.minecraft.common.command.CommandErrorHandler;
import net.minecraft.common.command.ICommandListener;
import net.minecraft.common.command.IllegalCmdListenerOperation;
import net.minecraft.common.command.completion.CommandCompletion;
import net.minecraft.common.command.completion.CommandCompletionAny;
import net.minecraft.common.command.completion.CommandCompletionChain;
import net.minecraft.common.command.completion.CommandCompletionRegistry;
import net.minecraft.common.item.Item;
import net.minecraft.common.util.ChatColors;
import net.minecraft.common.util.Facing;
import net.minecraft.common.util.math.Vec3D;
import net.minecraft.common.util.physics.MovingObjectPosition;
import net.minecraft.common.world.EnumMovingObjectType;

public class CommandTriangle extends Command {

    public CommandTriangle() {
        super("/tri", true, false);
    }

    @Override
    public void onExecute(String[] args, ICommandListener commandExecutor) throws IllegalCmdListenerOperation {
        if (args.length >= 2) {
            PlayerSelection ps = PlayerSelectionProvider.getImplementation().getPlayerSelection(commandExecutor.getPlayerEntity());
            if (!ps.hasSelection()) {
                commandExecutor.log(ChatColors.RED + "Invalid selection");
                return;
            }
            Vec3D pos = commandExecutor.getPosition();
            Vec3D dir = commandExecutor.getPlayerEntity().getLookVec().normalize();
            Vec3D maxDest = pos.addVector(dir.xCoord * GearHelper.CONFIG.warpMax, dir.yCoord * GearHelper.CONFIG.warpMax, dir.zCoord * GearHelper.CONFIG.warpMax);
            MovingObjectPosition mop = commandExecutor.getWorld().rayTraceBlocks(pos, maxDest);
            if (mop != null && mop.typeOfHit == EnumMovingObjectType.TILE) {
                int bid = CommandFill.itemIDtoBlockID(this.tryParse_onlyPositive(Item.getStringItemIDByName(args[1]), 0));
                int metadata = 0;

                if (bid < 0) {
                    commandExecutor.log(ChatColors.RED + "Invalid block ID " + bid);
                    return;
                }
                if (args.length == 3) {
                    metadata = this.tryParse_onlyPositive(args[2], 0);
                }

                int changed = GearHelper.editor.tri(ps, mop.blockX, mop.blockY, mop.blockZ, bid, metadata);
                if (changed != -1) {
                    commandExecutor.log(ChatColors.GREEN + "Changed " + changed + " blocks");
                } else {
                    commandExecutor.log(ChatColors.RED + "Failed to build tri");
                }
            } else {
                commandExecutor.log(ChatColors.RED + "Out of range");
            }
        } else {
            CommandErrorHandler.commandUsageMessage(this.commandSyntax(), commandExecutor);
        }
    }

    @Override
    public void printHelpInformation(ICommandListener commandExecutor) {
        commandExecutor.log("//tri\n\tgenerates a tri within selection points and the block at cursor");
    }

    @Override
    public String commandSyntax() {
        return ChatColors.YELLOW + "//tri <block> <metadata>";
    }

    @Override
    protected CommandCompletion commandCompletion() {
        return new CommandCompletionChain(CommandCompletionRegistry.ITEM, CommandCompletionAny.INSTANCE);
    }
}

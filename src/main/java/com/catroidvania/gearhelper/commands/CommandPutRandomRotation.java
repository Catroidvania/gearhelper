package com.catroidvania.gearhelper.commands;

import com.catroidvania.gearhelper.GearHelper;
import com.catroidvania.gearhelper.edit.PasteMode;
import net.minecraft.common.command.Command;
import net.minecraft.common.command.ICommandListener;
import net.minecraft.common.command.IllegalCmdListenerOperation;
import net.minecraft.common.util.ChatColors;
import net.minecraft.common.util.Facing;
import net.minecraft.common.util.math.Vec3D;
import net.minecraft.common.util.physics.MovingObjectPosition;
import net.minecraft.common.world.EnumMovingObjectType;

public class CommandPutRandomRotation extends Command {

    public CommandPutRandomRotation() {
        super("/brush", true, false);
    }

    @Override
    public void onExecute(String[] args, ICommandListener commandExecutor) throws IllegalCmdListenerOperation {
        if (GearHelper.editor.clipboard == null) {
            commandExecutor.log(ChatColors.RED + "Nothing in clipboard");
            return;
        }
        Vec3D pos = commandExecutor.getPosition();
        Vec3D dir = commandExecutor.getPlayerEntity().getLookVec().normalize();

        Vec3D maxDest = pos.addVector(dir.xCoord * GearHelper.CONFIG.warpMax, dir.yCoord * GearHelper.CONFIG.warpMax, dir.zCoord * GearHelper.CONFIG.warpMax);
        MovingObjectPosition mop = commandExecutor.getWorld().rayTraceBlocks(pos, maxDest);
        if (mop != null && mop.typeOfHit == EnumMovingObjectType.TILE) {
            int changed = GearHelper.editor.pasteWithRandomRotation(
                    mop.blockX + Facing.offsetXForSide[mop.sideHit],
                    mop.blockY + Facing.offsetYForSide[mop.sideHit],
                    mop.blockZ + Facing.offsetZForSide[mop.sideHit],
                    GearHelper.CONFIG.pasteAir ? PasteMode.DEFAULT : PasteMode.NO_REPLACE);
            if (changed != -1) {
                commandExecutor.sendNoticeToOps(commandExecutor.getUsername() + " pasted " + changed + " blocks at "
                        + ChatColors.RED + mop.blockX + " " + ChatColors.GREEN + mop.blockY + " " + ChatColors.AQUA + mop.blockZ + ChatColors.GRAY);
            } else {
                commandExecutor.log(ChatColors.RED + "Failed to paste selection");
            }
        } else {
            commandExecutor.log(ChatColors.RED + "Failed to paste selection");
        }
    }

    @Override
    public void printHelpInformation(ICommandListener commandExecutor) {
        commandExecutor.log(ChatColors.YELLOW + "//prr\n\tpaste clipboard at cursor with a random rotation");
    }

    @Override
    public String commandSyntax() {
        return ChatColors.YELLOW + "//prr";
    }
}

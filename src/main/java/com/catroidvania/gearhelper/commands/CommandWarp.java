package com.catroidvania.gearhelper.commands;

import com.catroidvania.gearhelper.GearHelper;
import net.minecraft.common.command.Command;
import net.minecraft.common.command.CommandErrorHandler;
import net.minecraft.common.command.ICommandListener;
import net.minecraft.common.command.IllegalCmdListenerOperation;
import net.minecraft.common.command.commands.CommandTP;
import net.minecraft.common.command.completion.CommandCompletion;
import net.minecraft.common.command.completion.CommandCompletionAny;
import net.minecraft.common.command.completion.CommandCompletionChain;
import net.minecraft.common.stats.StatCollector;
import net.minecraft.common.util.ChatColors;
import net.minecraft.common.util.Facing;
import net.minecraft.common.util.math.Vec3D;
import net.minecraft.common.util.physics.MovingObjectPosition;
import net.minecraft.common.world.EnumMovingObjectType;

public class CommandWarp extends Command {

    public CommandWarp() {
        super("/go", true, false);
    }

    @Override
    public void onExecute(String[] args, ICommandListener commandExecutor) throws IllegalCmdListenerOperation {
        Vec3D pos = commandExecutor.getPosition();
        Vec3D dir = commandExecutor.getPlayerEntity().getLookVec();

        if (args.length >= 1) {
            int maxLen = args.length == 2 ? this.tryParse(args[1], GearHelper.CONFIG.warpMax) : GearHelper.CONFIG.warpMax;
            Vec3D maxDest = pos.addVector(dir.xCoord * maxLen, dir.yCoord * maxLen, dir.zCoord * maxLen);
            MovingObjectPosition mop = commandExecutor.getWorld().rayTraceBlocks(pos, maxDest);
            if (mop != null && mop.typeOfHit == EnumMovingObjectType.TILE) {
                System.out.println(commandExecutor.getPosition());
                System.out.println(mop.hitVec);
                Vec3D dest = new Vec3D(
                        mop.blockX + Facing.offsetXForSide[mop.sideHit],
                        mop.blockY + (Facing.offsetYForSide[mop.sideHit] > 0 ? 2 : -1),
                        mop.blockZ + Facing.offsetZForSide[mop.sideHit]);
                teleport(commandExecutor, dest, pos);
            } else {
                teleport(commandExecutor, maxDest, pos);
            }
        } else {
            CommandErrorHandler.commandUsageMessage(this.commandSyntax(), commandExecutor);
        }
    }

    // copied from CommandTP
    public void teleport(ICommandListener executor, Vec3D to, Vec3D from) throws IllegalCmdListenerOperation {
        String string_tp_to = StatCollector.translateToLocal("command.tp.teleport_to_coords");
        String string_previously_at = StatCollector.translateToLocal("command.tp.previously_at");
        double x = to.xCoord;
        double y = to.yCoord;
        double z = to.zCoord;
        double previous_x = from.xCoord;
        double previous_y = from.yCoord;
        double previous_z = from.zCoord;
        float[] yawAndPitch = executor.getYawAndPitch();
        executor.dismountEntity();
        executor.teleportTo(x, y, z, yawAndPitch[0], yawAndPitch[1]);
        executor.log(string_tp_to + " §c" + "x: §r" + String.format("%.3f", x) + ", §a" + "y: §r" + String.format("%.3f", y) + ", §9" + "z: §r" + String.format("%.3f", z));
        executor.log("§7(" + string_previously_at + " §c" + "x: §7" + String.format("%.3f", previous_x) + ", §a" + "y: §7" + String.format("%.3f", previous_y) + ", §9" + "z: §7" + String.format("%.3f", previous_z) + ")");
    }

    @Override
    public void printHelpInformation(ICommandListener commandExecutor) {
        commandExecutor.log(ChatColors.YELLOW + "//go\n\tteleport to crosshair location, or n blocks in that direction");
    }

    @Override
    public String commandSyntax() {
        return ChatColors.YELLOW + "//go <distance>";
    }

    @Override
    protected CommandCompletion commandCompletion() {
        return new CommandCompletionChain(CommandCompletionAny.INSTANCE);
    }
}

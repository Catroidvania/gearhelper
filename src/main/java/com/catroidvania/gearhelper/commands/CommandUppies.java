package com.catroidvania.gearhelper.commands;

import net.minecraft.common.command.Command;
import net.minecraft.common.command.ICommandListener;
import net.minecraft.common.command.IllegalCmdListenerOperation;
import net.minecraft.common.stats.StatCollector;
import net.minecraft.common.util.ChatColors;
import net.minecraft.common.util.math.Vec3D;

public class CommandUppies extends Command {

    public CommandUppies() {
        super("/uppies", true, false);
    }

    @Override
    public void onExecute(String[] args, ICommandListener commandExecutor) throws IllegalCmdListenerOperation {
        Vec3D pos = commandExecutor.getPosition();
        int x = (int)pos.xCoord;
        int y = (int)pos.yCoord;
        int z = (int)pos.zCoord;
        for (int i = 0; i < 128; i++) {
            if (commandExecutor.getWorld().isLoadedAirBlock(x, y++, z)) break;
        }
        teleport(commandExecutor, new Vec3D(pos.xCoord, y, pos.zCoord), pos);
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
        commandExecutor.log(ChatColors.YELLOW + "//uppies\n\tteleport highest uncovered loaded block");
    }

    @Override
    public String commandSyntax() {
        return ChatColors.YELLOW + "//uppies";
    }
}

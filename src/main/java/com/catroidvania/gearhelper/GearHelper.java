package com.catroidvania.gearhelper;

import com.catroidvania.gearhelper.commands.*;
import com.catroidvania.gearhelper.edit.EditHandler;
import com.fox2code.foxloader.config.ConfigEntry;
import com.fox2code.foxloader.loader.Mod;
import net.minecraft.common.entity.player.EntityPlayer;
import net.minecraft.common.util.math.MathHelper;
import net.minecraft.common.world.gamerules.Gamerule;
import net.minecraft.common.world.gamerules.Gamerules;

import static com.fox2code.foxloader.registry.CommandRegistry.registerCommand;

public class GearHelper extends Mod {
    public static final GearHelperConfig CONFIG = new GearHelperConfig();
    public static final Gamerule doDaylightCycle = Gamerules.registerBooleanGamerule("doDaylightCycle", true);
    public static final Gamerule doWeatherCycle = Gamerules.registerBooleanGamerule("doWeatherCycle", true);
    public static final EditHandler editor = new EditHandler();

    @Override
    public void onPreInit() {
        this.setConfigObject(CONFIG);
        registerCommand(new CommandSetblock());
        registerCommand(new CommandSethere());
        registerCommand(new CommandFill());
        registerCommand(new CommandDelete());
        registerCommand(new CommandReplace());
        registerCommand(new CommandUndo());
        registerCommand(new CommandRedo());
        registerCommand(new CommandCopy());
        registerCommand(new CommandCut());
        registerCommand(new CommandPaste());
        registerCommand(new CommandWarp());
        registerCommand(new CommandRotateCW());
        registerCommand(new CommandRotateCCW());
        registerCommand(new CommandWand());
        registerCommand(new CommandRepeat());
        registerCommand(new CommandStack());
        registerCommand(new CommandNudge());
    }

    public static int blockPos(double pos) {
        return (int)Math.floor(pos);
    }

    public static int getDirectionXY(EntityPlayer player) {
        // taken from wait block
        int direction = (MathHelper.floor_double((double)(player.rotationYaw * 4.0F / 360.0F) + (double)0.5F) & 3);
        if (direction == 1) {
            direction = 4;
        } else if (direction == 3) {
            direction = 5;
        } else if (direction == 0) {
            direction = 3;
        }
        return direction;
    }

    public static class GearHelperConfig {
        @ConfigEntry(configName = "Enabled", configPath = "enabled")
        public boolean enabled = true;

        @ConfigEntry(configName = "Edit history length", configPath = "undo_max", lowerBounds = 1, upperBounds = 64)
        public int undoMax = 10;

        @ConfigEntry(configName = "Go Max Dist", configPath = "warp_max_dist", lowerBounds = 0, upperBounds = 128)
        public int warpMax = 32;
    }
}

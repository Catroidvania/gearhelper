package com.catroidvania.gearhelper;

import com.catroidvania.gearhelper.commands.*;
import com.catroidvania.gearhelper.edit.BrushHandler;
import com.catroidvania.gearhelper.edit.EditHandler;
import com.fox2code.foxevents.FoxEvents;
import com.fox2code.foxloader.config.ConfigEntry;
import com.fox2code.foxloader.event.FoxLoaderEvents;
import com.fox2code.foxloader.launcher.FoxLauncher;
import com.fox2code.foxloader.loader.Mod;
import net.minecraft.common.entity.player.EntityPlayer;
import net.minecraft.common.util.math.MathHelper;
import net.minecraft.common.world.gamerules.Gamerule;
import net.minecraft.common.world.gamerules.Gamerules;

import java.util.Random;

import static com.fox2code.foxloader.registry.CommandRegistry.registerCommand;

public class GearHelper extends Mod {
    public static final GearHelperConfig CONFIG = new GearHelperConfig();
    public static final Gamerule doDaylightCycle = Gamerules.registerBooleanGamerule("doDaylightCycle", true);
    public static final Gamerule doWeatherCycle = Gamerules.registerBooleanGamerule("doWeatherCycle", true);
    public static final EditHandler editor = new EditHandler();
    public static final BrushHandler brush = new BrushHandler();
    public static final Random rand = new Random();

    @Override
    public void onPreInit() {
        this.setConfigObject(CONFIG);
        FoxLoaderEvents.INSTANCE.registerEvents(brush);
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
        registerCommand(new CommandPut());
        registerCommand(new CommandPutRandomRotation());
        registerCommand(new CommandWarp());
        registerCommand(new CommandRotateCW());
        registerCommand(new CommandRotateCCW());
        registerCommand(new CommandWand());
        registerCommand(new CommandRepeat());
        registerCommand(new CommandStack());
        registerCommand(new CommandNudge());
        registerCommand(new CommandLift());
        registerCommand(new CommandBox());
        // maybe ill just merge with copy
        registerCommand(new CommandCenterOrigin());
        registerCommand(new CommandBrushMode());
        registerCommand(new CommandGenerateBrush());
        registerCommand(new CommandUppies());
        registerCommand(new CommandLine());
        registerCommand(new CommandEllipsoid());
        registerCommand(new CommandTriangle());
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

        @ConfigEntry(configName = "Go Max Dist", configPath = "warp_max_dist", lowerBounds = 0, upperBounds = 256)
        public int warpMax = 64;

        @ConfigEntry(configName = "Paste Air", configPath = "paste_air_replaces")
        public boolean pasteAir = true;

        @ConfigEntry(configName = "Auto Center", configPath = "auto_center")
        public boolean autoCenter = true;

        @ConfigEntry(configName = "Stamp Random Rotation", configPath = "random_rotation")
        public boolean randomStampRotation = true;

        @ConfigEntry(configName = "Brush Random Rotation", configPath = "random_rotation_brush")
        public boolean randomBrushRotation = false;

        @ConfigEntry(configName = "Paint Random Rotation", configPath = "random_rotation_paint")
        public boolean randomPaintRotation = false;

        @ConfigEntry(configName = "Erase Random Rotation", configPath = "random_rotation_erase")
        public boolean randomEraseRotation = false;

        @ConfigEntry(configName = "Foliage Density", configPath = "foliage_multiplier", lowerBounds = 1, upperBounds = 8)
        public int foliageMulti = 1;
    }
}

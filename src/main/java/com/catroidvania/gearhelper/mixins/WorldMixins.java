package com.catroidvania.gearhelper.mixins;

import com.catroidvania.gearhelper.GearHelper;
import net.minecraft.common.world.World;
import net.minecraft.common.world.WorldInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(World.class)
public abstract class WorldMixins {

    public World thisWorld = (World)(Object)this;

    @Redirect(method = "tick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/common/world/WorldInfo;setWorldTime(J)V", ordinal = 1))
    public void tickDaylightCycle(WorldInfo worldInfo, long worldTime) {
        if (GearHelper.doDaylightCycle.getBoolean(thisWorld.getSaveHandler())) {
            thisWorld.worldInfo.setWorldTime(worldTime);
        }
    }

    @Redirect(method = "tick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/common/world/World;updateWeather()V", ordinal = 0))
    public void tickUpdateWeather(World world) {
        if (GearHelper.doWeatherCycle.getBoolean(thisWorld.getSaveHandler())) {
            ((UpdateWeatherInvoker)thisWorld).invokeUpdateWeather();
        }
    }
}

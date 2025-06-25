package com.catroidvania.gearhelper.mixins;

import net.minecraft.common.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;


@Mixin(World.class)
interface UpdateWeatherInvoker {
    @Invoker("updateWeather")
    public void invokeUpdateWeather();
}
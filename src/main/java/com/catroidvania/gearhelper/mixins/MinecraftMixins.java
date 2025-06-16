package com.catroidvania.gearhelper.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.common.block.Block;
import net.minecraft.common.block.Blocks;
import net.minecraft.common.world.EnumMovingObjectType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixins {

    public Minecraft mc = (Minecraft)(Object)this;

    @Inject(method = "clickMiddleMouseButton()V", at = @At("HEAD"))
    public void onClickMiddleMouseButton(CallbackInfo ci) {
        if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == EnumMovingObjectType.TILE && mc.thePlayer.isSneaking()) {
            Block block;
            int x = mc.objectMouseOver.blockX;
            int y = mc.objectMouseOver.blockY;
            int z = mc.objectMouseOver.blockZ;

            if ((block = Blocks.BLOCKS_LIST[mc.theWorld.getBlockId(x, y, z)]) == null) {
                return;
            }

            int id;
            if ((id = block.idPicked(mc.theWorld, x, y, z)) == 0) {
                return;
            }

            int blockid = id < 256 && !Blocks.BLOCKS_LIST[block.blockID].randomBoolean() ? id : block.blockID;
            if (blockid == Blocks.GEAR_WAIT_ACTIVE.blockID || blockid == Blocks.GEAR_WAIT_IDLE.blockID) {
                int metadata = mc.theWorld.getBlockMetadata(x, y, z);
                Integer delay = (metadata & 12) >> 2;
                mc.thePlayer.addChatMessage(delay + " ticks");
            }
        }
    }
}

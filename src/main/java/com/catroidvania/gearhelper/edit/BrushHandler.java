package com.catroidvania.gearhelper.edit;

import com.catroidvania.gearhelper.GearHelper;
import com.fox2code.foxevents.EventHandler;
import com.fox2code.foxloader.event.interaction.PlayerUseItemOnAirEvent;
import com.fox2code.foxloader.event.interaction.PlayerUseItemOnBlockEvent;
import com.fox2code.foxloader.event.world.WorldTickEvent;
import net.minecraft.common.block.Block;
import net.minecraft.common.block.Blocks;
import net.minecraft.common.entity.player.EntityPlayer;
import net.minecraft.common.item.Items;
import net.minecraft.common.util.ChatColors;
import net.minecraft.common.util.Facing;
import net.minecraft.common.util.math.Vec3D;
import net.minecraft.common.util.physics.MovingObjectPosition;
import net.minecraft.common.world.EnumMovingObjectType;

public class BrushHandler {

    public BrushMode mode = BrushMode.BRUSH;
    public long lastTick = 0;
    public int cooldown = 0;

    @EventHandler
    public void onWorldTick(WorldTickEvent event) {
        if (this.cooldown != 0) this.cooldown--;
    }

    @EventHandler
    public void onPlayerUseItemAirEvent(PlayerUseItemOnAirEvent pui) {
        EntityPlayer player = pui.getEntityPlayer();
        if (player.capabilities.isCreativeMode && player.isOp() && pui.getHeldItem() != null && pui.getHeldItem().getItemID() == Items.WOOD_SHOVEL.itemID) {
            draw(player);
        }
    }

    @EventHandler
    public void onPlayerUseItemBlockEvent(PlayerUseItemOnBlockEvent pui) {
        EntityPlayer player = pui.getEntityPlayer();
        if (player.capabilities.isCreativeMode && player.isOp() && pui.getHeldItem() != null && pui.getHeldItem().getItemID() == Items.WOOD_SHOVEL.itemID) {
            draw(player);
        }
    }

    public void draw(EntityPlayer player) {
        if (GearHelper.editor.clipboard == null && this.mode != BrushMode.FOLIAGE) {
            return;
        }
        Vec3D pos = new Vec3D(player.posX, player.posY, player.posZ);
        Vec3D dir = player.getLookVec().normalize();

        Vec3D maxDest = pos.addVector(dir.xCoord * GearHelper.CONFIG.warpMax, dir.yCoord * GearHelper.CONFIG.warpMax, dir.zCoord * GearHelper.CONFIG.warpMax);
        MovingObjectPosition mop = player.worldObj.rayTraceBlocks(pos, maxDest);
        if (mop != null && mop.typeOfHit == EnumMovingObjectType.TILE) {
            int changed = 0;
            int x = mop.blockX + Facing.offsetXForSide[mop.sideHit];
            int y = mop.blockY + Facing.offsetYForSide[mop.sideHit];
            int z = mop.blockZ + Facing.offsetZForSide[mop.sideHit];
            switch (this.mode) {
                case DISABLED:
                    return;
                case STAMP:
                    if (this.cooldown == 0) {
                        this.cooldown = 4;
                        if (GearHelper.CONFIG.autoCenter) GearHelper.editor.centerAuto(mop.sideHit);
                        if (GearHelper.CONFIG.randomStampRotation) {
                            changed = GearHelper.editor.pasteWithRandomRotation(x, y, z, GearHelper.CONFIG.pasteAir ? PasteMode.DEFAULT : PasteMode.NO_REPLACE);
                        } else {
                            changed = GearHelper.editor.pasteAtWithMode(x, y, z, GearHelper.CONFIG.pasteAir ? PasteMode.DEFAULT : PasteMode.NO_REPLACE);
                        }
                        player.swingItem();
                    }
                    break;
                case BRUSH:
                    if (GearHelper.CONFIG.randomBrushRotation) {
                        changed = GearHelper.editor.pasteWithRandomRotation(x, y, z, PasteMode.NO_REPLACE);
                    } else {
                        changed = GearHelper.editor.pasteAtWithMode(x, y, z, PasteMode.NO_REPLACE);
                    }
                    player.swingItem();
                    break;
                case PAINT:
                    if (GearHelper.CONFIG.randomPaintRotation) {
                        changed = GearHelper.editor.pasteWithRandomRotation(x, y, z, PasteMode.PAINT);
                    } else {
                        changed = GearHelper.editor.pasteAtWithMode(x, y, z, PasteMode.PAINT);
                    }
                    player.swingItem();
                    break;
                case EXCAVATE:
                    if (GearHelper.CONFIG.randomEraseRotation) {
                        changed = GearHelper.editor.pasteWithRandomRotation(x, y, z, PasteMode.NEGATIVE);
                    } else {
                        changed = GearHelper.editor.pasteAtWithMode(x, y, z, PasteMode.NEGATIVE);
                    }
                    player.swingItem();
                    break;
                case FOLIAGE:
                    Block block = Blocks.BLOCKS_LIST[player.worldObj.getBlockId(mop.blockX, mop.blockY, mop.blockZ)];
                    for (int i = 0; i < GearHelper.CONFIG.foliageMulti; i++) {
                        block.fertilize(player.worldObj, x, y, z);
                    }
                    player.swingItem();
                    break;
                case null, default:
                    return;
            }
            if (changed == -1) {
                player.addChatMessage(ChatColors.RED + "Failed to paste clipboard");
            }

        }
    }
}

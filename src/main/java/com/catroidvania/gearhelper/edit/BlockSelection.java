package com.catroidvania.gearhelper.edit;


import com.fox2code.foxloader.selection.PlayerSelection;
import com.mojang.nbt.CompoundTag;
import net.minecraft.common.block.tileentity.*;
import net.minecraft.common.world.World;

import java.util.ArrayList;

public class BlockSelection {
    //private static final Logger log = LoggerFactory.getLogger(BlockSelection.class);
    public final PlayerSelection ps;
    public World worldObj;
    public int x1, y1, z1, x2, y2, z2, xd, yd, zd;
    public long size;
    public int[] blockIDs, metadatas;
    public ArrayList<CompoundTag> tileEntityNBTs;

    public BlockSelection(PlayerSelection ps) {
        this.ps = ps;
        this.worldObj = ps.getSelectionWorld();
        this.x1 = ps.getX1();
        this.y1 = ps.getY1();
        this.z1 = ps.getZ1();
        this.x2 = ps.getX2();
        this.y2 = ps.getY2();
        this.z2 = ps.getZ2();
        this.xd = x1 < x2 ? 1 : -1;
        this.yd = y1 < y2 ? 1 : -1;
        this.zd = z1 < z2 ? 1 : -1;
        this.size = ps.getSelectionSize();
        if (ps.hasSelection()) initBlockLists();
    }

   public BlockSelection(BlockSelection bs) {
        this.ps = bs.ps;
        this.worldObj = bs.worldObj;
        this.x1 = bs.x1;
        this.y1 = bs.y1;
        this.z1 = bs.z1;
        this.x2 = bs.x2;
        this.y2 = bs.y2;
        this.z2 = bs.z2;
        this.xd = bs.xd;
        this.yd = bs.yd;
        this.zd = bs.zd;
        this.size = bs.size;
        if (bs.isValid()) initBlockLists();
    }

    public BlockSelection(BlockSelection bs, boolean recalcBlocklist) {
        this.ps = bs.ps;
        this.worldObj = bs.worldObj;
        this.x1 = bs.x1;
        this.y1 = bs.y1;
        this.z1 = bs.z1;
        this.x2 = bs.x2;
        this.y2 = bs.y2;
        this.z2 = bs.z2;
        this.xd = bs.xd;
        this.yd = bs.yd;
        this.zd = bs.zd;
        this.size = bs.size;
        if (recalcBlocklist && bs.isValid()) {
            initBlockLists();
        } else {
            this.blockIDs = bs.blockIDs;
            this.metadatas = bs.metadatas;
            this.tileEntityNBTs = new ArrayList<>();
            for (CompoundTag nbt : bs.tileEntityNBTs) {
                this.tileEntityNBTs.add((CompoundTag)nbt.copy());
            }
        }
    }

    public void initBlockLists() {
        if (worldObj == null) return;
        int blockCount = Math.toIntExact(size);
        blockIDs = new int[blockCount];
        metadatas = new int[blockCount];
        tileEntityNBTs = new ArrayList<>();

        // this monstrosity is always relative from pos1, i hope
        int i = 0;
        for (int y = y1; y != y2+yd; y += yd) {
            for (int z = z1; z != z2+zd; z += zd) {
                for (int x = x1; x != x2+xd; x += xd) {
                    blockIDs[i] = worldObj.getBlockId(x, y, z);
                    metadatas[i] = worldObj.getBlockMetadata(x, y, z);
                    TileEntity te = worldObj.getBlockTileEntity(x, y, z);
                    if (te != null) {
                        CompoundTag nbt = new CompoundTag();
                        te.writeToNBT(nbt);
                        tileEntityNBTs.add(nbt);
                    }
                    i++;
                }
            }
        }
    }

    public boolean isValid() { return this.blockIDs != null; }

    public BlockSelection translateTo(int x, int y, int z) {
        BlockSelection newBS = new BlockSelection(this, false);
        int xd = x - x1;
        int yd = y - y1;
        int zd = z - z1;
        newBS.x1 = x;
        newBS.y1 = y;
        newBS.z1 = z;
        newBS.x2 += xd;
        newBS.y2 += yd;
        newBS.z2 += zd;
        for (CompoundTag nbt : newBS.tileEntityNBTs) {
            nbt.setInteger("x", nbt.getInteger("x") + xd);
            nbt.setInteger("y", nbt.getInteger("y") + yd);
            nbt.setInteger("z", nbt.getInteger("z") + zd);
        }
        return newBS;
    }

    public int fill(int bid, int metadata) {
        int filled = 0;

        // java needs do-for confirmed
        for (int y = y1; y != y2+yd; y += yd) {
            for (int z = z1; z != z2+zd; z += zd) {
                for (int x = x1; x != x2+xd; x += xd) {
                    if (worldObj.setBlockAndMetadata(x, y, z, bid, metadata)) filled++;
                }
            }
        }

        return filled;
    }

    public int replace(int targetid, int metadata1, int replaceid, int metadata2) {
        int filled = 0;
        int i = 0;
        for (int y = y1; y != y2+yd; y += yd) {
            for (int z = z1; z != z2+zd; z += zd) {
                for (int x = x1; x != x2+xd; x += xd) {
                    if (this.blockIDs[i] == targetid && (metadata1 < 0 || this.metadatas[i] == metadata1)) {
                        if (worldObj.setBlockAndMetadata(x, y, z, replaceid, Math.max(metadata2, 0))) filled++;
                    }
                    i++;
                }
            }
        }

        return filled;
    }

    public int paste() {
        return pasteWithOffset(0 , 0, 0);
    }

    public int pasteAtPos(int xpos, int ypos, int zpos) {
        return pasteWithOffset(xpos - x1, ypos - y1, zpos - z1);
    }

    public int pasteWithOffset(int xo, int yo, int zo) {
        int filled = 0;
        int i = 0;
        for (int y = y1; y != y2+yd; y += yd) {
            for (int z = z1; z != z2+zd; z += zd) {
                for (int x = x1; x != x2+xd; x += xd) {
                    if (worldObj.setBlockAndMetadata(xo + x, yo + y, zo + z, blockIDs[i], metadatas[i])) filled++;
                    i++;
                }
            }
        }
        try {
            for (CompoundTag nbt : tileEntityNBTs) {
                /*for (Tag tag : nbt.getValues()) {
                    //writeNamedTag((Tag)var2.next(), p0);
                    System.out.println(tag);
                }*/
                TileEntity te = TileEntity.createAndLoadEntity(worldObj, nbt);//tileEntityFromNBT(nbt);
                if (te != null) {
                    te.xCoord += xo;
                    te.yCoord += yo;
                    te.zCoord += zo;
                    worldObj.setBlockTileEntity(te.xCoord, te.yCoord, te.zCoord, te);
                }
            }
        } catch (Exception e) {
            //log.error("Exception: ", e);
        }
        return filled;
    }
}

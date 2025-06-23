package com.catroidvania.gearhelper.edit;


import com.fox2code.foxloader.selection.PlayerSelection;
import com.mojang.nbt.CompoundTag;
import net.minecraft.common.block.Block;
import net.minecraft.common.block.Blocks;
import net.minecraft.common.block.children.BlockGearBaseGate;
import net.minecraft.common.block.children.BlockGearWait;
import net.minecraft.common.block.children.BlockPistonBase;
import net.minecraft.common.block.tileentity.*;
import net.minecraft.common.world.World;

import java.util.ArrayList;

public class BlockSelection {
    //private static final Logger log = LoggerFactory.getLogger(BlockSelection.class);
    public final PlayerSelection ps;
    public World worldObj;
    public int xOriginal, yOriginal, zOriginal, xAnchor, yAnchor, zAnchor, xLength, yLength, zLength;
    public long size;
    public int[] blockIDs, metadatas;
    public ArrayList<CompoundTag> tileEntityNBTs;

    public BlockSelection(PlayerSelection ps) {
        this.ps = ps;
        this.worldObj = ps.getSelectionWorld();
        this.xOriginal = ps.getMinX();
        this.yOriginal = ps.getMinY();
        this.zOriginal = ps.getMinZ();
        this.xAnchor = ps.getMinX() - ps.getX1();
        this.yAnchor = ps.getMinY() - ps.getY1();
        this.zAnchor = ps.getMinZ() - ps.getZ1();
        this.xLength = ps.getMaxX() - ps.getMinX() + 1;
        this.yLength = ps.getMaxY() - ps.getMinY() + 1;
        this.zLength = ps.getMaxZ() - ps.getMinZ() + 1;
        this.size = ps.getSelectionSize();
        if (ps.hasSelection()) initBlockLists();
    }

   public BlockSelection(BlockSelection bs) {
        this.ps = bs.ps;
        this.worldObj = bs.worldObj;
        this.xOriginal = bs.xOriginal;
        this.yOriginal = bs.yOriginal;
        this.zOriginal = bs.zOriginal;
        this.xLength = bs.xLength;
        this.yLength = bs.yLength;
        this.zLength = bs.zLength;
        this.size = bs.size;
        if (bs.isValid()) initBlockLists();
    }

    public BlockSelection(BlockSelection bs, boolean recalcBlocklist) {
        this.ps = bs.ps;
        this.worldObj = bs.worldObj;
        this.xOriginal = bs.xOriginal;
        this.yOriginal = bs.yOriginal;
        this.zOriginal = bs.zOriginal;
        this.xLength = bs.xLength;
        this.yLength = bs.yLength;
        this.zLength = bs.zLength;
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
        try {
            int blockCount = Math.toIntExact(size);
            blockIDs = new int[blockCount];
            metadatas = new int[blockCount];
            tileEntityNBTs = new ArrayList<>();

            // monsterous...
            int i = 0;
            for (int y = yOriginal; y < yOriginal + yLength; y++) {
                for (int z = zOriginal; z < zOriginal + zLength; z++) {
                    for (int x = xOriginal; x < xOriginal + xLength; x++) {
                        blockIDs[i] = worldObj.getBlockId(x, y, z);
                        metadatas[i] = worldObj.getBlockMetadata(x, y, z);
                        TileEntity te = worldObj.getBlockTileEntity(x, y, z);
                        if (te != null) {
                            CompoundTag nbt = new CompoundTag();
                            te.writeToNBT(nbt);
                            // relative to 0 0 0
                            nbt.setInteger("x", nbt.getInteger("x") - xOriginal);
                            nbt.setInteger("y", nbt.getInteger("y") - yOriginal);
                            nbt.setInteger("z", nbt.getInteger("z") - zOriginal);
                            tileEntityNBTs.add(nbt);
                        }
                        i++;
                    }
                }
            }
        } catch (ArithmeticException e) {
        }
    }

    public boolean isValid() { return this.blockIDs != null; }

    public BlockSelection translateTo(int x, int y, int z) {
        BlockSelection newBS = new BlockSelection(this, false);
        newBS.xOriginal = x + xAnchor;
        newBS.yOriginal = y + yAnchor;
        newBS.zOriginal = z + zAnchor;
        return newBS;
    }

    public int fill(int bid, int metadata) {
        int filled = 0;

        // java needs do-for confirmed
        for (int y = yOriginal; y < yOriginal + yLength; y++) {
            for (int z = zOriginal; z < zOriginal + zLength; z++) {
                for (int x = xOriginal; x < xOriginal + xLength; x++) {
                    if (worldObj.setBlockAndMetadata(x, y, z, bid, metadata)) filled++;
                }
            }
        }

        return filled;
    }

    public int xyzToIndex(int x, int y, int z) {
        return Math.abs(x) + Math.abs(z * xLength) + Math.abs(y * xLength * zLength);
    }

    public BlockSelection rotate90D(boolean ccw) {
        BlockSelection newBS = new BlockSelection(this, false);
        int xm = ccw ? -1 : 1;
        int zm = -xm;
        newBS.xLength = zm * this.zLength;
        newBS.zLength = xm * this.xLength;
        if (newBS.xLength < 0) newBS.xLength += this.zLength;
        if (newBS.zLength < 0) newBS.zLength += this.xLength;

        System.out.println("x: " + newBS.xLength + " y: " + newBS.yLength + " z: " + newBS.zLength);
        System.out.println(newBS.size);

        int i = 0, nx, nz;
        for (int y = 0; y < yLength; y++) {
            for (int z = 0; z < zLength; z++) {
                for (int x = 0; x < xLength; x++) {
                    /*
                    newBS.blockIDs[newBS.xyzToIndex(zm * z, y, xm * x)] = this.blockIDs[i];
                    newBS.metadatas[newBS.xyzToIndex(zm * z, y, xm * x)] =
                            ccw ? rotateBlockMetadataCCW(Blocks.BLOCKS_LIST[this.blockIDs[i]], this.metadatas[i]) : rotateBlockMetadataCW(Blocks.BLOCKS_LIST[this.blockIDs[i]], this.metadatas[i]);

                     */
                    nx = z * zm;
                    nz = x * xm;
                    if (nx < 0) nx += this.zLength;
                    if (nz < 0) nz += this.xLength;

                    newBS.blockIDs[newBS.xyzToIndex(nx, y, nz)] = this.blockIDs[i];
                    newBS.metadatas[newBS.xyzToIndex(nx, y, nz)] =
                            ccw ? rotateBlockMetadataCCW(Blocks.BLOCKS_LIST[this.blockIDs[i]], this.metadatas[i]) : rotateBlockMetadataCW(Blocks.BLOCKS_LIST[this.blockIDs[i]], this.metadatas[i]);

                    ++i;
                }
            }
        }

        newBS.tileEntityNBTs = new ArrayList<>();
        for (CompoundTag nbt : this.tileEntityNBTs) {
            CompoundTag newNBT = (CompoundTag)nbt.copy();
            int xnbt = nbt.getInteger("x");
            int znbt = nbt.getInteger("z");
            newNBT.setInteger("x", zm * znbt);
            newNBT.setInteger("z", xm * xnbt);
            newBS.tileEntityNBTs.add(newNBT);
        }
        return newBS;
    }
    
    public static int rotateBlockMetadataCW(Block block, int metadata) {
        return switch (block) {
            case BlockGearWait bgw -> rotateWaitCW(metadata);
            case BlockGearBaseGate bgbg -> rotateBaseGateCW(metadata);
            case BlockPistonBase bpb -> rotatePistonCW(metadata);
            default -> metadata;
        };
    }

    public static int rotateBlockMetadataCCW(Block block, int metadata) {
        return rotateBlockMetadataCW(block, rotateBlockMetadataCW(block, rotateBlockMetadataCW(block, metadata)));
    }

    public static int rotateBaseGateCW(int metadata) { return metadata < 5 ? metadata + 1 : 0; }
    public static int rotateWaitCW(int metadata) { return metadata < 16 ? metadata + 1 : 0; }
    public static int rotatePistonCW(int metadata) {
        return switch (metadata) {
            case 3 -> 4;
            case 4 -> 2;
            case 2 -> 5;
            case 5 -> 3;
            case 11 -> 12;
            case 12 -> 10;
            case 10 -> 13;
            case 13 -> 11;
            default -> 0;
        };
    }

    public int replace(int targetid, int metadata1, int replaceid, int metadata2) {
        int filled = 0;
        int i = 0;
        for (int y = yOriginal; y < yOriginal + yLength; y++) {
            for (int z = zOriginal; z < zOriginal + zLength; z++) {
                for (int x = xOriginal; x < xOriginal + xLength; x++) {
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
        return pasteWithOffset(xOriginal, yOriginal, zOriginal);
    }

    public int pasteAtPos1(int x, int y, int z) {
        return pasteWithOffset(x + xAnchor, y + yAnchor, z + zAnchor);
    }

    public int pasteWithOffset(int xpos, int ypos, int zpos) {
        int filled = 0;
        int i = 0;
        for (int y = 0; y < yLength; y++) {
            for (int z = 0; z < zLength; z++) {
                for (int x = 0; x < xLength; x++) {
                    if (worldObj.setBlockAndMetadata(x + xpos, y + ypos, z + zpos, blockIDs[i], metadatas[i])) filled++;
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
                    worldObj.setBlockTileEntity(te.xCoord + xpos, te.yCoord + ypos, te.zCoord + zpos, te);
                }
            }
        } catch (Exception e) {
            //log.error("Exception: ", e);
        }
        return filled;
    }
}

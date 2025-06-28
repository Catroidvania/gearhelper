package com.catroidvania.gearhelper.edit;

import com.fox2code.foxloader.selection.PlayerSelection;
import com.mojang.nbt.CompoundTag;
import net.minecraft.common.block.Block;
import net.minecraft.common.block.Blocks;
import net.minecraft.common.block.children.*;
import net.minecraft.common.block.tileentity.*;
import net.minecraft.common.util.math.Vec3D;
import net.minecraft.common.world.World;

import java.util.ArrayList;

public class BlockSelection {

    public World worldObj;
    public int xOriginal, yOriginal, zOriginal, xAnchor, yAnchor, zAnchor, xMax, yMax, zMax;
    public int size;
    public int[] blockIDs, metadatas;
    public ArrayList<CompoundTag> tileEntityNBTs;

    public BlockSelection(PlayerSelection ps) {
        //this.ps = ps;
        this.worldObj = ps.getSelectionWorld();
        // original world position
        this.xOriginal = ps.getMinX();
        this.yOriginal = ps.getMinY();
        this.zOriginal = ps.getMinZ();
        // position of pos1 relative to selection 0 0 0
        this.xAnchor = ps.getX1() - this.xOriginal;
        this.yAnchor = ps.getY1() - this.yOriginal;
        this.zAnchor = ps.getZ1() - this.zOriginal;
        // selection bounds
        this.xMax = ps.getMaxX() - this.xOriginal;
        this.yMax = ps.getMaxY() - this.yOriginal;
        this.zMax = ps.getMaxZ() - this.zOriginal;

        this.size = Math.toIntExact(ps.getSelectionSize());
        if (ps.hasSelection()) initBlockLists();
    }

    public BlockSelection(World world, int x1, int y1, int z1, int x2, int y2, int z2, boolean leaveBlank) {
        this.worldObj = world;
        this.xOriginal = Math.min(x1, x2);
        this.yOriginal = Math.min(y1, y2);
        this.zOriginal = Math.min(z1, z2);
        this.xAnchor = x1 - this.xOriginal;
        this.yAnchor = y1 - this.yOriginal;
        this.zAnchor = z1 - this.zOriginal;
        this.xMax = Math.max(x1, x2) - this.xOriginal;
        this.yMax = Math.max(y1, y2) - this.yOriginal;
        this.zMax = Math.max(z1, z2) - this.zOriginal;
        this.size = (Math.abs(x1 - x2) + 1) * (Math.abs(y1 - y2) + 1) * (Math.abs(z1 - z2) + 1);
        if (leaveBlank) {
            this.blockIDs = new int[this.size];
            this.metadatas = new int[this.size];
            this.tileEntityNBTs = new ArrayList<>();
        } else {
            initBlockLists();
        }
    }

   public BlockSelection(BlockSelection bs) {
       //this.ps = bs.ps;
       this.worldObj = bs.worldObj;
       this.xOriginal = bs.xOriginal;
       this.yOriginal = bs.yOriginal;
       this.zOriginal = bs.zOriginal;
       this.xAnchor = bs.xAnchor;
       this.yAnchor = bs.yAnchor;
       this.zAnchor = bs.zAnchor;
       this.xMax = bs.xMax;
       this.yMax = bs.yMax;
       this.zMax = bs.zMax;
       this.size = bs.size;
       if (bs.isValid()) initBlockLists();
   }

    public BlockSelection(BlockSelection bs, boolean recalcBlocklist) {
        //this.ps = bs.ps;
        this.worldObj = bs.worldObj;
        this.xOriginal = bs.xOriginal;
        this.yOriginal = bs.yOriginal;
        this.zOriginal = bs.zOriginal;
        this.xAnchor = bs.xAnchor;
        this.yAnchor = bs.yAnchor;
        this.zAnchor = bs.zAnchor;
        this.xMax = bs.xMax;
        this.yMax = bs.yMax;
        this.zMax = bs.zMax;
        this.size = bs.size;
        if (recalcBlocklist && bs.isValid()) {
            initBlockLists();
        } else {
            this.blockIDs = bs.blockIDs.clone();
            this.metadatas = bs.metadatas.clone();
            this.tileEntityNBTs = new ArrayList<>();
            for (CompoundTag nbt : bs.tileEntityNBTs) {
                this.tileEntityNBTs.add((CompoundTag)nbt.copy());
            }
        }
    }

    public void initBlockLists() {
        if (worldObj == null) return;
        try {
            blockIDs = new int[size];
            metadatas = new int[size];
            tileEntityNBTs = new ArrayList<>();

            int i = 0;
            for (int y = yOriginal; y <= yOriginal + yMax; y++) {
                for (int z = zOriginal; z <= zOriginal + zMax; z++) {
                    for (int x = xOriginal; x <= xOriginal + xMax; x++) {
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
        } catch (Exception e) {
            System.err.println("Exception: " + e);
        }
    }

    public boolean isValid() { return this.blockIDs != null; }

    public BlockSelection translateToAnchor(int x, int y, int z) {
        BlockSelection newBS = new BlockSelection(this, false);
        newBS.xOriginal = x - xAnchor;
        newBS.yOriginal = y - yAnchor;
        newBS.zOriginal = z - zAnchor;
        return newBS;
    }

    public BlockSelection translateTo(int x, int y, int z) {
        BlockSelection newBS = new BlockSelection(this, false);
        newBS.xOriginal = x;
        newBS.yOriginal = y;
        newBS.zOriginal = z;
        return newBS;
    }

    public BlockSelection merge(BlockSelection bs) {
        int x1 = Math.min(this.xOriginal, bs.xOriginal);
        int x2 = Math.max(this.xOriginal + this.xMax, bs.xOriginal + bs.xMax);
        int y1 = Math.min(this.yOriginal, bs.yOriginal);
        int y2 = Math.max(this.yOriginal + this.yMax, bs.yOriginal + bs.yMax);
        int z1 = Math.min(this.zOriginal, bs.xOriginal);
        int z2 = Math.max(this.zOriginal + this.zMax, bs.zOriginal + bs.zMax);
        return new BlockSelection(this.worldObj, x1, y1, z1, x2, y2, z2, false);
    }

    public int fill(int bid, int metadata) {
        int filled = 0;

        // java needs do-for confirmed
        for (int y = yOriginal; y <= yOriginal + yMax; y++) {
            for (int z = zOriginal; z <= zOriginal + zMax; z++) {
                for (int x = xOriginal; x <= xOriginal + xMax; x++) {
                    if (setBlockAndMetadataNoUpdate(x, y, z, bid, metadata)) filled++;
                }
            }
        }

        return filled;
    }

    public int xyzToIndex(int x, int y, int z) {
        return x + (z * (this.xMax+1)) + (y * (this.xMax+1) * (this.zMax+1));
    }

    public static int rotateToPosQuad_impl(int mod, int part, int offset) {
        int result = mod * part;
        return mod < 0 ? result + offset : result;
    }

    public BlockSelection rotate90D(boolean ccw) {
        BlockSelection newBS = new BlockSelection(this, false);

        int xm = ccw ? -1 : 1;
        int zm = -xm;

        newBS.xMax = this.zMax;
        newBS.zMax = this.xMax;
        newBS.xAnchor = rotateToPosQuad_impl(zm, this.zAnchor, newBS.xMax);
        newBS.zAnchor = rotateToPosQuad_impl(xm, this.xAnchor, newBS.zMax);

        /*
        System.out.println("x: " + this.xMax + " y: " + this.yMax + " z: " + this.zMax);
        System.out.println("xa: " + this.xAnchor + " za: " + this.zAnchor);
        System.out.println(this.size);
        System.out.println("x: " + newBS.xMax + " y: " + newBS.yMax + " z: " + newBS.zMax);
        System.out.println("xa: " + newBS.xAnchor + " za: " + newBS.zAnchor);
        System.out.println(newBS.size);
         */

        int i = 0, nx, nz;
        for (int y = 0; y <= yMax; y++) {
            for (int z = 0; z <= zMax; z++) {
                for (int x = 0; x <= xMax; x++) {
                    nx = rotateToPosQuad_impl(zm, z, newBS.xMax);
                    nz = rotateToPosQuad_impl(xm, x, newBS.zMax);

                    /*
                    System.out.println("xyz: " + x + " " + y + " " + z);
                    System.out.println("nxz: " + nx + " " + y + " " + nz);
                    System.out.println(i + " -> " + newBS.xyzToIndex(nx, y, nz));
                     */
                    newBS.blockIDs[newBS.xyzToIndex(nx, y, nz)] = this.blockIDs[i];
                    newBS.metadatas[newBS.xyzToIndex(nx, y, nz)] = ccw ?
                                rotateBlockMetadataCCW(Blocks.BLOCKS_LIST[this.blockIDs[i]], this.metadatas[i]) :
                                rotateBlockMetadataCW(Blocks.BLOCKS_LIST[this.blockIDs[i]], this.metadatas[i]);

                    ++i;
                }
            }
        }

        // doesnt work
        for (CompoundTag nbt : newBS.tileEntityNBTs) {
            nbt.setInteger("x", rotateToPosQuad_impl(zm, nbt.getInteger("z"), newBS.xMax));
            nbt.setInteger("z", rotateToPosQuad_impl(xm, nbt.getInteger("x"), newBS.zMax));
        }

        /*
        System.out.println(Arrays.toString(this.blockIDs));
        System.out.println((Arrays.toString(newBS.blockIDs)));
        */
        return newBS;
    }
    
    public static int rotateBlockMetadataCW(Block block, int metadata) {
        return switch (block) {
            case BlockLog bl -> rotateLogCW(metadata);
            case BlockVertical bv -> rotateVerticalCW(metadata);
            case BlockStairs bs -> rotateStairsCW(metadata);
            case BlockFence bf -> rotateFenceCW(metadata);
            case BlockFenceGate bg -> rotateWaitCW(metadata);
            case BlockDoor bd -> rotateWaitCW(metadata);
            case BlockRail br -> rotateRailCW(metadata);
            case BlockGearConveyorBelt bgcb -> rotateBaseGateCW(metadata);
            case BlockPlushie bp -> rotateSkullCW(metadata);
            case BlockSkull bs -> rotateSkullCW(metadata);
            case BlockChair bc -> rotateSkullCW(metadata);
            case BlockTrapdoor btd -> rotateTrapdoorCW(metadata);
            case BlockBed bb -> rotateWaitCW(metadata);
            case BlockGearWait bgw -> rotateWaitCW(metadata);
            case BlockDrawer bd -> rotateBaseGateCW(metadata);
            case BlockGearBaseGate bgbg -> rotateBaseGateCW(metadata);
            case BlockLadder bl -> rotatePistonCW(metadata);
            case BlockTombstone bt -> rotateDefaultCW(metadata);
            case BlockButton bb -> rotateButtonCW(metadata);
            case BlockPistonBase bpb -> rotatePistonCW(metadata);
            case BlockPistonExtension bpe -> rotatePistonCW(metadata);
            case BlockDungeonChest bdc -> rotateDefaultCW(metadata);
            case BlockContainer bc -> rotatePistonCW(metadata); // chests are buggy as hell
            case BlockPortal bp -> rotatePortalCW(metadata);
            default -> block.isMetadataUsed(3) && !block.isMetadataUsed(4) ? rotateDefaultCW(metadata) : metadata;//block.isMetadataUsed(4) ? metadata : rotateDefaultCW(metadata);
        };
    }

    // i love being lazy
    public static int rotateBlockMetadataCCW(Block block, int metadata) {
        return rotateBlockMetadataCW(block, rotateBlockMetadataCW(block, rotateBlockMetadataCW(block, metadata)));
    }

    public static int rotateLogCW(int metadata) { return metadata == 7 ? 11 : 7; }
    public static int rotateVerticalCW(int metadata) { return metadata == 4 ? 8 : 4; }
    public static int rotateStairsCW(int metadata) {
        return switch (metadata) {
            case 2 -> 1;
            case 1 -> 3;
            case 3 -> 0;
            case 0 -> 2;
            case 6 -> 5;
            case 5 -> 7;
            case 7 -> 4;
            case 4 -> 6;
            default -> metadata;
        };
    }
    // better way to do this? probably
    public static int rotateFenceCW(int metadata) {
        return switch (metadata) {
            case 4 -> 2;
            case 2 -> 8;
            case 8 -> 1;
            case 1 -> 4;
            case 9 -> 5;
            case 5 -> 6;
            case 6 -> 10;
            case 10 -> 9;
            case 3 -> 12;
            case 12 -> 3;
            case 11 -> 13;
            case 13 -> 7;
            case 7 -> 14;
            case 14 -> 11;
            default -> metadata;
        };
    }
    public static int rotateButtonCW(int metadata) {
        return switch (metadata) {
          case 4 -> 1;
          case 1 -> 3;
          case 3 -> 2;
          case 2 -> 4;
          case 12 -> 9;
          case 9 -> 11;
          case 11 -> 10;
          case 10 -> 12;
          default -> metadata;
        };
    }
    public static int rotateRailCW(int metadata) {
        return metadata == 0 ? 1 : metadata == 1 ? 0 : metadata < 9 ? metadata + 1 : 6;
    }
    public static int rotateSkullCW(int metadata) {
        metadata += 4;
        return metadata < 16 ? metadata : metadata - 16;
    }
    public static int rotateDefaultCW(int metadata) {
        return ++metadata < 4 ? metadata : 0;
    }
    public static int rotateTrapdoorCW(int metadata) {
        return switch (metadata) {
            case 8 -> 11;
            case 11 -> 9;
            case 9 -> 10;
            case 10 -> 8;
            case 0 -> 3;
            case 3 -> 1;
            case 1 -> 2;
            case 2 -> 0;
            case 4 -> 7;
            case 7 -> 5;
            case 5 -> 6;
            case 6 -> 4;
            case 12 -> 15;
            case 15 -> 13;
            case 13 -> 14;
            case 14 -> 12;
            default -> metadata;
        };
    }
    public static int rotatePortalCW(int metadata) { return metadata == 1 ? 2 : 1; }
    public static int rotateBaseGateCW(int metadata) { return metadata < 5 ? metadata + 1 : 2; }
    public static int rotateWaitCW(int metadata) {
        int rot = metadata % 4;
        return (metadata - rot) + (++rot < 4 ? rot : 0);
    }
    public static int rotatePistonCW(int metadata) {
        return switch (metadata) {
            case 3 -> 4;
            case 4 -> 2;
            case 2 -> 5;
            case 5 -> 3;
            case 6 -> 9;
            case 9 -> 7;
            case 7 -> 8;
            case 8 -> 6;
            case 11 -> 12;
            case 12 -> 10;
            case 10 -> 13;
            case 13 -> 11;
            default -> metadata;
        };
    }

    public int replace(int targetid, int metadata1, int replaceid, int metadata2) {
        int filled = 0;
        int i = 0;
        for (int y = yOriginal; y <= yOriginal + yMax; y++) {
            for (int z = zOriginal; z <= zOriginal + zMax; z++) {
                for (int x = xOriginal; x <= xOriginal + xMax; x++) {
                    if (this.blockIDs[i] == targetid && (metadata1 < 0 || this.metadatas[i] == metadata1)) {
                        if (setBlockAndMetadataNoUpdate(x, y, z, replaceid, Math.max(metadata2, 0))) filled++;
                    }
                    i++;
                }
            }
        }

        return filled;
    }

    public int paste() {
        return pasteWithOffset_default(xOriginal, yOriginal, zOriginal);
    }

    public int pasteAtPos1(int x, int y, int z, PasteMode pm) {
        return pasteWithOffset_impl(x - xAnchor, y - yAnchor, z - zAnchor, pm);
    }

    public int pasteWithOffset(int xpos, int ypos, int zpos) {
        return pasteWithOffset_default(xpos, ypos, zpos);
    }

    public int pasteWithOffset_default(int xpos, int ypos, int zpos) {
        int filled = 0;
        int i = 0;
        for (int y = 0; y <= yMax; y++) {
            for (int z = 0; z <= zMax; z++) {
                for (int x = 0; x <= xMax; x++) {
                    if (setBlockAndMetadataNoUpdate(x + xpos, y + ypos, z + zpos, blockIDs[i], metadatas[i])) filled++;
                    i++;
                }
            }
        }

        for (CompoundTag nbt : tileEntityNBTs) {
            int x = nbt.getInteger("x") + xpos;
            int y = nbt.getInteger("y") + ypos;
            int z = nbt.getInteger("z") + zpos;
            TileEntity te = worldObj.getBlockTileEntity(x, y, z);
            if (te != null) {
                nbt.setInteger("x", x);
                nbt.setInteger("y", y);
                nbt.setInteger("z", z);
                te.readFromNBT(nbt);
            }
        }

        return filled;
    }

    public int pasteWithOffset_impl(int xpos, int ypos, int zpos, PasteMode pm) {
        int filled = 0;
        int i = 0;
        for (int y = 0; y <= yMax; y++) {
            for (int z = 0; z <= zMax; z++) {
                for (int x = 0; x <= xMax; x++) {
                    int wbid = worldObj.getBlockId(x + xpos, y + ypos, z + zpos);
                    if (pm == PasteMode.NO_REPLACE) {
                        if (!(blockIDs[i] == Blocks.AIR.blockID || wbid != Blocks.AIR.blockID)) {
                            if (setBlockAndMetadataNoUpdate(x + xpos, y + ypos, z + zpos, blockIDs[i], metadatas[i]))
                                filled++;
                        }
                    } else if (pm == PasteMode.PAINT) {
                        if (blockIDs[i] != Blocks.AIR.blockID && wbid != Blocks.AIR.blockID) {
                            if (setBlockAndMetadataNoUpdate(x + xpos, y + ypos, z + zpos, blockIDs[i], metadatas[i]))
                                filled++;
                        }
                    } else if (pm == PasteMode.NEGATIVE) {
                        if (blockIDs[i] != Blocks.AIR.blockID && wbid != Blocks.AIR.blockID) {
                            if (setBlockAndMetadataNoUpdate(x + xpos, y + ypos, z + zpos, 0, 0))
                                filled++;
                        }
                    } else {
                        if (setBlockAndMetadataNoUpdate(x + xpos, y + ypos, z + zpos, blockIDs[i], metadatas[i]))
                            filled++;
                    }
                    i++;
                }
            }
        }

        for (CompoundTag nbt : tileEntityNBTs) {
            int x = nbt.getInteger("x") + xpos;
            int y = nbt.getInteger("y") + ypos;
            int z = nbt.getInteger("z") + zpos;
            TileEntity te = worldObj.getBlockTileEntity(x, y, z);
            if (te != null) {
                nbt.setInteger("x", x);
                nbt.setInteger("y", y);
                nbt.setInteger("z", z);
                te.readFromNBT(nbt);
            }
        }

        return filled;
    }

    // it doesnt exists already rip
    public boolean setBlockAndMetadataNoUpdate(int x, int y, int z, int bid, int metadata) {
        boolean btrue, mtrue;
        //if (metadata != 0) worldObj.setBlockNoUpdate(x, y, z, 0);   // force metadata refresh
        btrue = worldObj.setBlockNoUpdate(x, y, z, bid);
        mtrue = worldObj.setBlockMetadata(x, y, z, metadata);
        //if (metadata != 0) worldObj.markBlockNeedsUpdate(x, y, z);
        return btrue || mtrue;
    }
}

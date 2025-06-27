package com.catroidvania.gearhelper.edit;

import com.catroidvania.gearhelper.GearHelper;
import com.fox2code.foxloader.selection.PlayerSelection;
import net.minecraft.common.util.Facing;
import net.minecraft.common.util.math.Vec3D;
import net.minecraft.common.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class EditHandler {
    private static final Logger log = LoggerFactory.getLogger(EditHandler.class);
    // not per player
    public ArrayList<BlockSelection> undoStack, redoStack;
    public BlockSelection clipboard;

    public EditHandler() {
        undoStack = new ArrayList<>();
        redoStack = new ArrayList<>();
    }

    public void addUndo(BlockSelection bs) {
        undoStack.addFirst(new BlockSelection(bs));
        try {
            if (undoStack.size() > GearHelper.CONFIG.undoMax) undoStack.removeLast();
        } catch (Exception e) {
            //log.error("Exception: ", e);
        }
    }

    public void addUndoAndClearRedo(BlockSelection bs) {
        undoStack.addFirst(new BlockSelection(bs));
        this.redoStack.clear();
        try {
            if (undoStack.size() > GearHelper.CONFIG.undoMax) undoStack.removeLast();
        } catch (Exception e) {
            //log.error("Exception: ", e);
        }
    }

    public void addRedo(BlockSelection bs) {
        redoStack.addFirst(new BlockSelection(bs));
        try {
            if (redoStack.size() > GearHelper.CONFIG.undoMax) redoStack.removeLast();
        } catch (Exception e) {
            //log.error("Exception: ", e);
        }
    }

    public int fill(PlayerSelection ps, int bid, int metadata) {
        BlockSelection area = new BlockSelection(ps);
        if (area.isValid()) {
            addUndoAndClearRedo(area);
            return area.fill(bid, metadata);
        }
        return -1;
    }

    public int replace(PlayerSelection ps, int targetid, int metadata1, int replacementid, int metadata2) {
        BlockSelection area = new BlockSelection(ps);
        if (area.isValid()) {
            addUndoAndClearRedo(area);
            return area.replace(targetid, metadata1, replacementid, metadata2);
        }
        return -1;
    }

    public boolean copy(PlayerSelection ps) {
        BlockSelection area = new BlockSelection(ps);
        if (area.isValid()) {
            clipboard = area;
            return true;
        }
        return false;
    }

    public int cut(PlayerSelection ps) {
        if (copy(ps)) {
            addUndoAndClearRedo(clipboard);
            return clipboard.fill(0, 0);
        }
        return -1;
    }

    public int box(PlayerSelection ps, int bid, int metadata) {
        BlockSelection area = new BlockSelection(ps);
        if (area.isValid()) {
            addUndoAndClearRedo(area);
            BlockSelection top = new BlockSelection(ps.getSelectionWorld(), ps.getMinX(), ps.getMaxY(), ps.getMinZ(), ps.getMaxX(), ps.getMaxY(), ps.getMaxZ(), false);
            BlockSelection bot = new BlockSelection(ps.getSelectionWorld(), ps.getMinX(), ps.getMinY(), ps.getMinZ(), ps.getMaxX(), ps.getMinY(), ps.getMaxZ(), false);
            BlockSelection nor = new BlockSelection(ps.getSelectionWorld(), ps.getMaxX(), ps.getMinY(), ps.getMinZ(), ps.getMaxX(), ps.getMaxY(), ps.getMaxZ(), false);
            BlockSelection sou = new BlockSelection(ps.getSelectionWorld(), ps.getMinX(), ps.getMinY(), ps.getMinZ(), ps.getMinX(), ps.getMaxY(), ps.getMaxZ(), false);
            BlockSelection eas = new BlockSelection(ps.getSelectionWorld(), ps.getMinX(), ps.getMinY(), ps.getMaxZ(), ps.getMaxX(), ps.getMaxY(), ps.getMaxZ(), false);
            BlockSelection wes = new BlockSelection(ps.getSelectionWorld(), ps.getMinX(), ps.getMinY(), ps.getMinZ(), ps.getMaxX(), ps.getMaxY(), ps.getMinZ(), false);
            int changed = 0;
            changed += top.fill(bid, metadata);
            changed += bot.fill(bid, metadata);
            changed += nor.fill(bid, metadata);
            changed += sou.fill(bid, metadata);
            changed += eas.fill(bid, metadata);
            changed += wes.fill(bid, metadata);
            return changed;
        }
        return -1;
    }

    public boolean rotateCW() {
        if (clipboard != null) {
            clipboard = clipboard.rotate90D(false);
            return true;
        }
        return false;
    }

    // repeat until good
    public boolean rotateCCW() {
        if (clipboard != null) {
            clipboard = clipboard.rotate90D(true);
            return true;
        }
        return false;
    }

    public int repeat(PlayerSelection ps, int dir, int repeats) {
        if (copy(ps)) {
            int xDir = Facing.offsetXForSide[dir];
            int zDir = Facing.offsetZForSide[dir];
            int xLen = xDir * (ps.getMaxX() - ps.getMinX() + 1);
            int zLen = zDir * (ps.getMaxZ() - ps.getMinZ() + 1);
            int changed = 0;
            int x = ps.getX1();
            int y = ps.getY1();
            int z = ps.getZ1();
            BlockSelection area = clipboard.merge(clipboard.translateToAnchor(x + (repeats * xLen), y, z + (repeats * zLen)));
            addUndoAndClearRedo(area);
            for (int i = 1; i <= repeats; i++) {
                changed += clipboard.pasteAtPos1(x + (i * xLen), y, z + (i * zLen), GearHelper.CONFIG.pasteAir ? PasteMode.DEFAULT : PasteMode.NO_REPLACE);
            }
            return changed;
        }
        return -1;
    }

    public int repeatVertical(PlayerSelection ps, int repeats) {
        if (copy(ps)) {
            int dir = repeats / Math.abs(repeats);
            int height = ps.getMaxY() - ps.getMinY() + 1;
            int changed = 0;
            int x = ps.getX1();
            int y = ps.getY1();
            int z = ps.getZ1();
            BlockSelection area = clipboard.merge(clipboard.translateToAnchor(x, y + (Math.abs(repeats) * dir * height), z));
            addUndoAndClearRedo(area);
            for (int i = 1; i <= Math.abs(repeats); i++) {
                changed += clipboard.pasteAtPos1(x, y + (i * dir * height), z, GearHelper.CONFIG.pasteAir ? PasteMode.DEFAULT : PasteMode.NO_REPLACE);
            }
            return changed;
        }
        return -1;
    }

    public int shift(PlayerSelection ps, int dir, int dist) {
        if (copy(ps)) {
            int xDir = Facing.offsetXForSide[dir];
            int yDir = Facing.offsetYForSide[dir];
            int zDir = Facing.offsetZForSide[dir];
            BlockSelection area = clipboard.merge(clipboard.translateToAnchor(ps.getX1() + (xDir * dist), ps.getY1() + (yDir * dist), ps.getZ1() + (zDir * dist)));
            addUndoAndClearRedo(area);
            clipboard.fill(0, 0);
            return clipboard.pasteAtPos1(ps.getX1() + (xDir * dist), ps.getY1() + (yDir * dist), ps.getZ1() + (zDir * dist), GearHelper.CONFIG.pasteAir ? PasteMode.DEFAULT : PasteMode.NO_REPLACE);
        }
        return -1;
    }

    public static BlockSelection generateBrush(World world, BrushShape shape, int size, int bid, int metadata) {
        BlockSelection brush = new BlockSelection(world, 0, 0, 0, size, size, size, true);
        brush.xAnchor = size / 2;
        brush.yAnchor = size / 2;
        brush.zAnchor = size / 2;
        if (shape == BrushShape.CUBE) {
            for (int i = 0; i < brush.size; i++) {
                brush.blockIDs[i] = bid;
                brush.metadatas[i] = metadata;
            }
        } else if (shape == BrushShape.SPHERE) {
            double radius = (((double)size) + 1) / 2;
            Vec3D center = new Vec3D(radius, radius, radius);
            Vec3D point = new Vec3D(0, 0, 0);
            for (int y = 0; y <= size; y++) {
                for (int z = 0; z <= size; z++) {
                    for (int x = 0; x <= size; x++) {
                        point.setComponents(x + 0.5, y + 0.5, z + 0.5);
                        if (center.distanceTo(point) < radius) {
                            brush.blockIDs[brush.xyzToIndex(x, y, z)] = bid;
                            brush.metadatas[brush.xyzToIndex(x, y, z)] = metadata;
                        }
                    }
                }
            }
        } else if (shape == BrushShape.NOISE) {
            for (int i = 0; i < brush.size; i++) {
                if (GearHelper.rand.nextInt(0, 7) == 0) {
                    brush.blockIDs[i] = bid;
                    brush.metadatas[i] = metadata;
                }
            }
        }
        return brush;
    }

    public boolean centerAnchor(boolean centerVertically) {
        if (clipboard != null) {
            clipboard.xAnchor = clipboard.xMax / 2;
            if (centerVertically) {
                clipboard.yAnchor = clipboard.yMax / 2;
            } else {
                clipboard.yAnchor = 0;
            }
            clipboard.zAnchor = clipboard.zMax / 2;
            return true;
        }
        return false;
    }

    public boolean centerAuto(int dir) {
        if (clipboard != null) {
            clipboard.xAnchor = switch (Facing.offsetXForSide[dir]) {
                case 1 -> 0;
                case -1 -> clipboard.xMax;
                default -> clipboard.xMax / 2;
            };
            clipboard.yAnchor = switch (Facing.offsetYForSide[dir]) {
                case 1 -> 0;
                case -1 -> clipboard.yMax;
                default -> clipboard.yMax / 2;
            };
            clipboard.zAnchor = switch (Facing.offsetZForSide[dir]) {
                case 1 -> 0;
                case -1 -> clipboard.zMax;
                default -> clipboard.zMax / 2;
            };
            return true;
        }
        return false;
    }

    public int pastePos1(PlayerSelection ps) {
        return pasteAt(ps.getX1(), ps.getY1(), ps.getZ1());
    }

    public int pasteWithRandomRotation(int x, int y, int z, PasteMode pm) {
        if (clipboard != null) {
            BlockSelection pasteArea = clipboard.translateToAnchor(x, y, z);
            int rot = GearHelper.rand.nextInt(0, 4);
            pasteArea = switch (rot) {
                case 1 -> pasteArea.rotate90D(false);
                case 2 -> pasteArea.rotate90D(false).rotate90D(false);
                case 3 -> pasteArea.rotate90D(true);
                default -> pasteArea;
            };
            addUndoAndClearRedo(pasteArea);
            return pasteArea.pasteAtPos1(x, y, z, pm);
        }
        return -1;
    }

    public int pasteAt(int x, int y, int z) {
        if (clipboard != null) {
            BlockSelection pasteArea = clipboard.translateToAnchor(x, y, z);
            addUndoAndClearRedo(pasteArea);
            return clipboard.pasteAtPos1(x, y, z, GearHelper.CONFIG.pasteAir ? PasteMode.DEFAULT : PasteMode.NO_REPLACE);
        }
        return -1;
    }

    public int pasteAtWithMode(int x, int y, int z, PasteMode pm) {
        if (clipboard != null) {
            BlockSelection pasteArea = clipboard.translateToAnchor(x, y, z);
            addUndoAndClearRedo(pasteArea);
            return clipboard.pasteAtPos1(x, y, z, pm);
        }
        return -1;
    }


    public boolean undo() {
        try {
            BlockSelection last = undoStack.removeFirst();
            if (last != null) {
                addRedo(last);
                last.paste();
                return true;
            }
        } catch (Exception e) {
            //log.error("Exception: ", e);
        }
        return false;
    }

    public boolean redo() {
        try {
            BlockSelection last = redoStack.removeFirst();
            if (last != null) {
                addUndo(last);
                last.paste();
                return true;
            }
        } catch (Exception e) {
            //log.error("Exception: ", e);
        }
        return false;
    }
}

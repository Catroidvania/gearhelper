package com.catroidvania.gearhelper.edit;

import com.catroidvania.gearhelper.GearHelper;
import com.fox2code.foxloader.selection.PlayerSelection;
import net.minecraft.common.block.Blocks;
import net.minecraft.common.util.Facing;
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
            addUndo(area);
            return area.fill(bid, metadata);
        }
        return -1;
    }

    public int replace(PlayerSelection ps, int targetid, int metadata1, int replacementid, int metadata2) {
        BlockSelection area = new BlockSelection(ps);
        if (area.isValid()) {
            addUndo(area);
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
            addUndo(clipboard);
            return clipboard.fill(0, 0);
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
            addUndo(area);
            for (int i = 1; i <= repeats; i++) {
                changed += clipboard.pasteAtPos1(x + (i * xLen), y, z + (i * zLen));
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
            addUndo(area);
            for (int i = 1; i <= Math.abs(repeats); i++) {
                changed += clipboard.pasteAtPos1(x, y + (i * dir * height), z);
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
            addUndo(area);
            clipboard.fill(0, 0);
            return clipboard.pasteAtPos1(ps.getX1() + (xDir * dist), ps.getY1() + (yDir * dist), ps.getZ1() + (zDir * dist));
        }
        return -1;
    }

    public int pasteAtPos1(PlayerSelection ps) {
        return pasteAt(ps.getX1(), ps.getY1(), ps.getZ1());
    }

    public int pasteAt(int x, int y, int z) {
        if (clipboard != null) {
            BlockSelection pasteArea = clipboard.translateToAnchor(x, y, z);
            addUndo(pasteArea);
            return clipboard.pasteAtPos1(x, y, z);
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

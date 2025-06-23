package com.catroidvania.gearhelper.edit;

import com.catroidvania.gearhelper.GearHelper;
import com.fox2code.foxloader.selection.PlayerSelection;
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

    public int pasteAtPos1(PlayerSelection ps) {
        return pasteAt(ps.getX1(), ps.getY1(), ps.getZ1());
    }

    public int pasteHere(PlayerSelection ps) {
        int x = GearHelper.blockPos(ps.getPlayer().posX);
        int y = GearHelper.blockPos(ps.getPlayer().posY) - 1;
        int z = GearHelper.blockPos(ps.getPlayer().posZ);
        return pasteAt(x, y, z);
    }

    public int pasteAt(int x, int y, int z) {
        if (clipboard != null) {
            /*
            System.out.println(x + " " + y + " " + z);
            System.out.println(Arrays.toString(clipboard.blockIDs));
             */
            //System.out.println(clipboard.tileEntityNBTs.toString());
            BlockSelection pasteArea = clipboard.translateTo(x, y, z);
            addUndo(pasteArea);
            return clipboard.pasteAtPos(x, y, z);
        }
        return -1;
    }

    public boolean undo() {
        try {
            BlockSelection last = undoStack.removeFirst();
            if (last != null && last.isValid()) {
                addRedo(new BlockSelection(last));
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
            if (last != null && last.isValid()) {
                addUndo(new BlockSelection(last));
                last.paste();
                return true;
            }
        } catch (Exception e) {
            //log.error("Exception: ", e);
        }
        return false;
    }
}

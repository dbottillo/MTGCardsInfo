package com.dbottillo.mtgsearchfree.util;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import com.dbottillo.mtgsearchfree.BuildConfig;
import com.dbottillo.mtgsearchfree.R;
import com.dbottillo.mtgsearchfree.helper.LOG;
import com.dbottillo.mtgsearchfree.helper.TrackingHelper;
import com.dbottillo.mtgsearchfree.resources.Deck;
import com.dbottillo.mtgsearchfree.resources.MTGCard;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public final class FileUtil {

    private FileUtil() {
    }

    public static File copyDbToSdCard(Context ctx, String name) {
        LOG.e("copy db to sd card");
        try {
            File root = getMTGSearchDirectory();
            if (root == null) {
                return null;
            }
            if (root.canWrite()) {
                File currentDB = ctx.getDatabasePath(name);
                File backupDB = new File(root, name);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB)
                            .getChannel();
                    FileChannel dst = new FileOutputStream(backupDB)
                            .getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                    return backupDB;
                } else {
                    LOG.e("current db dont exist");
                }
            } else {
                LOG.e("sd card cannot be write");
            }
        } catch (Exception e) {
            LOG.e("exception copy db: " + e.getLocalizedMessage());
        }
        return null;
    }

    private static File getMTGSearchDirectory() {
        File root = new File(Environment.getExternalStorageDirectory(), BuildConfig.DEBUG ? "MTGSearchDebug" : "MTGSearch");
        if (!root.exists()) {
            boolean created = root.mkdirs();
            if (!created) {
                return null;
            }
        }
        return root;
    }

    public static File fileNameForDeck(Deck deck) {
        File root = getMTGSearchDirectory();
        if (root == null) {
            return null;
        }
        return new File(root, deck.getName().replaceAll("\\s+", "").toLowerCase() + ".dec");
    }

    public static boolean downloadDeckToSdCard(Context context, Deck deck, ArrayList<MTGCard> cards) {
        File deckFile = fileNameForDeck(deck);
        if (deckFile == null) {
            return false;
        }
        OutputStreamWriter writer;
        TrackingHelper.getInstance(context).trackEvent(TrackingHelper.UA_CATEGORY_DECK, TrackingHelper.UA_ACTION_EXPORT);
        try {
            writer = new OutputStreamWriter(new FileOutputStream(deckFile), "UTF-8");
            writer.append("//");
            writer.append(deck.getName());
            writer.append("\n");
            for (MTGCard card : cards) {
                if (card.isSideboard()) {
                    writer.append("SB: ");
                }
                writer.append(String.valueOf(card.getQuantity()));
                writer.append(" ");
                writer.append(card.getName());
                writer.append("\n");
            }
            writer.flush();
            writer.close();
            return true;
        } catch (IOException e) {
            Toast.makeText(context, context.getString(R.string.error_export_deck), Toast.LENGTH_SHORT).show();
            TrackingHelper.getInstance(context).trackEvent(TrackingHelper.UA_CATEGORY_ERROR, TrackingHelper.UA_ACTION_EXPORT, "[deck] " + e.getLocalizedMessage());
            return false;
        }
    }
}

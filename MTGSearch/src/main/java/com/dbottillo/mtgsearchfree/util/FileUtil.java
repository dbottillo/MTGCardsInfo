package com.dbottillo.mtgsearchfree.util;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import com.dbottillo.mtgsearchfree.BuildConfig;
import com.dbottillo.mtgsearchfree.model.CardsBucket;
import com.dbottillo.mtgsearchfree.model.CardsCollection;
import com.dbottillo.mtgsearchfree.model.Deck;
import com.dbottillo.mtgsearchfree.model.MTGCard;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileUtil {

    private FileLoader fileLoader;

    public FileUtil(FileLoader fileLoader) {
        this.fileLoader = fileLoader;
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

    public static boolean copyDbFromSdCard(Context ctx, String name) {
        LOG.e("copy db to sd card");
        try {
            File root = getMTGSearchDirectory();
            if (root == null) {
                return false;
            }
            if (root.canWrite()) {
                File currentDB = ctx.getDatabasePath(name);
                File backupDB = new File(root, name);

                if (backupDB.exists()) {
                    FileChannel src = new FileInputStream(backupDB)
                            .getChannel();
                    FileChannel dst = new FileOutputStream(currentDB)
                            .getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                    return true;
                } else {
                    LOG.e("backup db dont exist");
                }
            } else {
                LOG.e("sd card cannot be write");
            }
        } catch (Exception e) {
            LOG.e("exception copy db: " + e.getLocalizedMessage());
        }
        return false;
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
        return new File(root, StringUtil.clearDeckName(deck) + ".dec");
    }

    public boolean downloadDeckToSdCard(Deck deck, CardsCollection cards) {
        File deckFile = fileNameForDeck(deck);
        if (deckFile == null) {
            return false;
        }
        OutputStreamWriter writer;
        TrackingManager.INSTANCE.trackDatabaseExport();
        try {
            writer = new OutputStreamWriter(new FileOutputStream(deckFile), "UTF-8");
            writer.append("//");
            writer.append(deck.getName());
            writer.append("\n");
            for (MTGCard card : cards.getList()) {
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
            TrackingManager.INSTANCE.trackDatabaseExportError(e.getLocalizedMessage());
            return false;
        }
    }

    public CardsBucket readFileContent(Uri uri) throws Exception {
        InputStream is = fileLoader.loadUri(uri);
        CardsBucket bucket;
        try {
            bucket = readFileStream(is);
            if (bucket.getKey() == null) {
                bucket.setKey(uri.getLastPathSegment());
            }
        } catch (Exception e) {
            is.close();
            throw e;
        }
        return bucket;
    }

    private CardsBucket readFileStream(InputStream is) throws Exception {
        List<MTGCard> cards = new ArrayList<>();
        CardsBucket bucket = new CardsBucket();
        BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
        String line;

        boolean side = false;
        int numberOfEmptyLines = 0;
        while ((line = br.readLine()) != null) {
            if (line.startsWith("//")) {
                // title
                if (bucket.getKey() == null) {
                    bucket.setKey(line.replace("//", ""));
                }
            } else if (line.isEmpty()) {
                numberOfEmptyLines++;
                // from here on all the cards belong to side
                if (numberOfEmptyLines >= 2) {
                    side = true;
                }
            } else {
                MTGCard card = generateCard(line.replace("SB: ", ""));
                if (line.startsWith("SB: ")) {
                    card.setSideboard(true);
                } else {
                    card.setSideboard(side);
                }
                cards.add(card);
            }
        }
        br.close();
        bucket.setCards(cards);
        return bucket;
    }

    private MTGCard generateCard(String line) {
        ArrayList<String> items = new ArrayList<>(Arrays.asList(line.split(" ")));
        String first = items.remove(0);
        String rest = TextUtils.join(" ", items);
        MTGCard card = new MTGCard();
        card.setQuantity(Integer.parseInt(first));
        card.setCardName(rest);
        return card;
    }

}

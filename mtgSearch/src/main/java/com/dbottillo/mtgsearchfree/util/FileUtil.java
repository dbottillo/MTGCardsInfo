package com.dbottillo.mtgsearchfree.util;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import com.dbottillo.mtgsearchfree.BuildConfig;
import com.dbottillo.mtgsearchfree.R;
import com.dbottillo.mtgsearchfree.model.CardsBucket;
import com.dbottillo.mtgsearchfree.model.Deck;
import com.dbottillo.mtgsearchfree.model.MTGCard;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

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
        return new File(root, deck.getName().replaceAll("\\s+", "").toLowerCase() + ".dec");
    }

    public static boolean downloadDeckToSdCard(Context context, Deck deck, ArrayList<MTGCard> cards) {
        File deckFile = fileNameForDeck(deck);
        if (deckFile == null) {
            return false;
        }
        OutputStreamWriter writer;
        TrackingManager.trackDatabaseExport();
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
            TrackingManager.trackDatabaseExportError(e.getLocalizedMessage());
            return false;
        }
    }

    public static CardsBucket readFileContent(Context context, Uri uri) {
        CardsBucket bucket = new CardsBucket();
        List<MTGCard> cards = new ArrayList<>();
        String extension = uri.getPath().substring(uri.getPath().lastIndexOf("."));
        if (!extension.equalsIgnoreCase(".dec")){
            return null;
        }
        try {
            InputStream is = context.getContentResolver().openInputStream(uri);
            if (is == null){
                return null;
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;

            while ((line = br.readLine()) != null) {
                if (!line.isEmpty()){
                    if (line.startsWith("//")){
                        // title
                        if (bucket.getKey() == null) {
                            bucket.setKey(line.replace("//", ""));
                        }

                    } else if (line.startsWith("SB: ")){
                        LOG.e("line: "+line.replace("SB: ", ""));
                        String split[] = line.replace("SB: ", "").split(" ");
                        MTGCard card = new MTGCard();
                        card.setQuantity(Integer.parseInt(split[0]));
                        card.setCardName(split[1]);
                        card.setSideboard(true);
                        LOG.e(split[0]+" - "+split[1]);
                        cards.add(card);
                    } else {
                        // standard
                        String split[] = line.replace("SB: ", "").split(" ");
                        LOG.e(split[0]+" - "+split[1]);

                        MTGCard card = new MTGCard();
                        card.setQuantity(Integer.parseInt(split[0]));
                        card.setCardName(split[1]);
                        cards.add(card);
                    }
                }
            }
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
        }
        bucket.setCards(cards);
        return bucket;
    }
}

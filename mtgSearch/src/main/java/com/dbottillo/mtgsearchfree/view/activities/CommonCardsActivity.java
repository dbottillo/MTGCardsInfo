package com.dbottillo.mtgsearchfree.view.activities;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import com.dbottillo.mtgsearchfree.R;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.model.storage.CardsPreferences;
import com.dbottillo.mtgsearchfree.model.storage.CardsPreferencesImpl;
import com.dbottillo.mtgsearchfree.util.ArrayUtils;
import com.dbottillo.mtgsearchfree.util.LOG;
import com.dbottillo.mtgsearchfree.util.TrackingManager;

import javax.inject.Inject;

public abstract class CommonCardsActivity extends BasicActivity {

    private MenuItem favMenuItem = null;
    private MenuItem imageMenuItem = null;
    int[] idFavourites;

    abstract MTGCard getCurrentCard();

    abstract void favClicked();

    abstract void toggleImage(boolean show);

    @Inject
    CardsPreferences cardsPreferences;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cards, menu);
        favMenuItem = menu.findItem(R.id.action_fav);
        imageMenuItem = menu.findItem(R.id.action_image);
        menu.findItem(R.id.action_fullscreen_image).setVisible(false);
        syncMenu();
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        if (id == R.id.action_fav) {
            favClicked();
            return true;
        }
        if (id == R.id.action_share) {
            MTGCard currentCard = getCurrentCard();
            TrackingManager.trackShareCard(currentCard);
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, currentCard.getName());
            String url = "http://gatherer.wizards.com/Pages/Card/Details.aspx?multiverseid=" + currentCard.getMultiVerseId();
            i.putExtra(Intent.EXTRA_TEXT, url);
            startActivity(Intent.createChooser(i, getString(R.string.share_card)));
            return true;
        }
        if (id == R.id.action_image) {
            boolean showImage = cardsPreferences.showImage();
            cardsPreferences.setShowImage(!showImage);
            toggleImage(!showImage);
            updateMenu();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void updateMenu() {
        LOG.d();
        syncMenu();
    }

    private void syncMenu() {
        if (favMenuItem == null) {
            // too early
            return;
        }
        MTGCard currentCard = getCurrentCard();
        if (currentCard != null && currentCard.getMultiVerseId() > 0) {
            favMenuItem.setVisible(true);
            if (ArrayUtils.contains(idFavourites, currentCard.getMultiVerseId())) {
                favMenuItem.setTitle(getString(R.string.favourite_remove));
                favMenuItem.setIcon(R.drawable.ab_star_colored);
            } else {
                favMenuItem.setTitle(getString(R.string.favourite_add));
                favMenuItem.setIcon(R.drawable.ab_star);
            }
        } else {
            favMenuItem.setVisible(false);
        }
        if (cardsPreferences.showImage()) {
            imageMenuItem.setChecked(true);
        } else {
            imageMenuItem.setChecked(false);
        }
    }

}
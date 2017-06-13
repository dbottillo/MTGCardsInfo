package com.dbottillo.mtgsearchfree.ui;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;

import com.dbottillo.mtgsearchfree.R;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.model.storage.CardsPreferences;
import com.dbottillo.mtgsearchfree.util.ArrayUtils;
import com.dbottillo.mtgsearchfree.util.LOG;
import com.dbottillo.mtgsearchfree.util.TrackingManager;
import com.dbottillo.mtgsearchfree.view.activities.BasicActivity;

import javax.inject.Inject;

public abstract class CommonCardsActivity extends BasicActivity {

    @Nullable protected MenuItem favMenuItem = null;
    @Nullable protected MenuItem imageMenuItem = null;

    protected abstract MTGCard getCurrentCard();

    protected abstract void favClicked();

    protected abstract void toggleImage(boolean show);

    protected abstract void syncMenu();

    @Inject
    CardsPreferences cardsPreferences;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        LOG.e("onCreateOptionsMenu");
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
            syncMenu();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
package com.dbottillo.mtgsearchfree.view.views;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dbottillo.mtgsearchfree.R;
import com.dbottillo.mtgsearchfree.model.CardsBucket;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.model.database.CardDataSource;
import com.dbottillo.mtgsearchfree.util.LOG;
import com.dbottillo.mtgsearchfree.util.UIUtil;
import com.dbottillo.mtgsearchfree.view.adapters.CardsAdapter;
import com.dbottillo.mtgsearchfree.view.adapters.OnCardListener;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class MTGCardListView extends RelativeLayout {

    boolean grid = false;
    private CardsAdapter adapter;

    @Bind(R.id.card_list)
    RecyclerView listView;
    @Bind(R.id.empty_view)
    TextView emptyView;
    @Bind(R.id.progress)
    SmoothProgressBar progressBar;
    @Bind(R.id.search_bottom_container)
    View footer;

    public MTGCardListView(Context ctx) {
        this(ctx, null);
    }

    public MTGCardListView(Context ctx, AttributeSet attrs) {
        this(ctx, attrs, -1);
    }

    public MTGCardListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.fragment_set, this, true);
        ButterKnife.bind(this, view);

        listView.setHasFixedSize(true);
        setListOn(); // default
    }

    public void loadCards(CardsBucket bucket, OnCardListener listener) {
        LOG.d();

        adapter = null;
        if (grid) {
            adapter = CardsAdapter.grid(bucket, false, R.menu.card_option);
        } else {
            adapter = CardsAdapter.list(bucket, false, R.menu.card_option);
        }
        adapter.setOnCardListener(listener);
        listView.setAdapter(adapter);

        adapter.notifyDataSetChanged();
        emptyView.setVisibility((adapter.getItemCount() == 0) ? View.VISIBLE : View.GONE);

        if (bucket.getCards().size() == CardDataSource.LIMIT) {
            TextView moreResult = (TextView) footer.findViewById(R.id.more_result);
            moreResult.setText(getResources().getQuantityString(R.plurals.search_limit, CardDataSource.LIMIT, CardDataSource.LIMIT));
            UIUtil.setHeight(footer, UIUtil.dpToPx(getContext(), 60));
        } else {
            UIUtil.setHeight(footer, 0);
        }

        progressBar.setVisibility(View.GONE);
        emptyView.setVisibility((adapter.getItemCount() == 0) ? View.VISIBLE : View.GONE);
    }

    public void setGridOn() {
        LOG.d();
        grid = true;
        GridLayoutManager glm = new GridLayoutManager(getContext(), getResources().getInteger(R.integer.cards_grid_column_count));
        listView.setLayoutManager(glm);
        tryRefresh();
    }

    public void setListOn() {
        LOG.d();
        grid = false;
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        listView.setLayoutManager(llm);
        tryRefresh();
    }

    private void tryRefresh() {
        if (adapter == null || adapter.getBucket().getCards().size() <= 0){
            return;
        }
        CardsBucket bucket = adapter.getBucket();
        OnCardListener listener = adapter.getOnCardListener();
        loadCards(bucket, listener);
    }
}

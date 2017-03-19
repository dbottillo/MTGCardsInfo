package com.dbottillo.mtgsearchfree.view.views;

import android.content.Context;
import android.graphics.Rect;
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
import com.dbottillo.mtgsearchfree.model.database.CardDataSource;
import com.dbottillo.mtgsearchfree.util.LOG;
import com.dbottillo.mtgsearchfree.util.UIUtil;
import com.dbottillo.mtgsearchfree.view.adapters.CardsAdapter;
import com.dbottillo.mtgsearchfree.view.adapters.OnCardListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class MTGCardsView extends RelativeLayout {

    boolean grid = false;
    private CardsAdapter adapter;
    private GridItemDecorator itemDecorator;

    @BindView(R.id.card_list)
    RecyclerView listView;
    @BindView(R.id.empty_view)
    TextView emptyView;
    @BindView(R.id.search_bottom_container)
    View footer;

    public MTGCardsView(Context ctx) {
        super(ctx);
        init(ctx);
    }

    public MTGCardsView(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
        init(ctx);
    }

    public MTGCardsView(Context ctx, AttributeSet attrs, int defStyle) {
        super(ctx, attrs, defStyle);
        init(ctx);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.fragment_set, this, true);
        ButterKnife.bind(this, view);

        itemDecorator = new GridItemDecorator(getResources().getDimensionPixelSize(R.dimen.cards_grid_space));
        listView.setHasFixedSize(true);
        setListOn(); // default
    }

    public void setEmptyString(int res){
        emptyView.setText(res);
    }

    public void loadCards(CardsBucket bucket, OnCardListener listener, int title) {
        loadCards(bucket, listener, getContext().getString(title));
    }

    public void loadCards(CardsBucket bucket, OnCardListener listener, String title) {
        LOG.d();

        adapter = null;
        if (grid) {
            adapter = CardsAdapter.grid(bucket, false, R.menu.card_option, title);
        } else {
            adapter = CardsAdapter.list(bucket, false, R.menu.card_option, title);
        }
        adapter.setOnCardListener(listener);
        listView.setAdapter(adapter);

        adapter.notifyDataSetChanged();

        if (bucket.getCards().size() == CardDataSource.LIMIT) {
            TextView moreResult = (TextView) footer.findViewById(R.id.more_result);
            moreResult.setText(getResources().getQuantityString(R.plurals.search_limit, CardDataSource.LIMIT, CardDataSource.LIMIT));
            UIUtil.setHeight(footer, UIUtil.dpToPx(getContext(), 60));
        } else {
            UIUtil.setHeight(footer, 0);
        }

        emptyView.setVisibility((adapter.getItemCount() == 0) ? View.VISIBLE : View.GONE);
    }

    public void setGridOn() {
        LOG.d();
        grid = true;
        int columns = getResources().getInteger(R.integer.cards_grid_column_count);
        final GridLayoutManager glm = new GridLayoutManager(getContext(), columns);
        glm.setInitialPrefetchItemCount(columns);
        glm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position == 0 ? glm.getSpanCount() : 1;
            }
        });
        listView.addItemDecoration(itemDecorator);
        listView.setLayoutManager(glm);
        tryRefresh();
    }

    public void setListOn() {
        LOG.d();
        grid = false;
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setInitialPrefetchItemCount(6);
        listView.removeItemDecoration(itemDecorator);
        listView.setLayoutManager(llm);
        tryRefresh();
    }

    private void tryRefresh() {
        if (adapter == null || adapter.getBucket().getCards().size() <= 0) {
            return;
        }
        CardsBucket bucket = adapter.getBucket();
        OnCardListener listener = adapter.getOnCardListener();
        String title = adapter.getTitle();
        loadCards(bucket, listener, title);
    }

    private class GridItemDecorator extends RecyclerView.ItemDecoration {
        private int space;

        public GridItemDecorator(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            if (parent.getChildLayoutPosition(view) == 0){
                outRect.left = 0;
                outRect.right = 0;
                outRect.bottom = 0;
                outRect.top = 0;
            } else {
                outRect.left = space / 2;
                outRect.right = space / 2;
                outRect.bottom = space / 2;

                // Add top margin only for the first item to avoid double space between items
                /*if (parent.getChildLayoutPosition(view) == 0 || parent.getChildLayoutPosition(view) == 1) {
                    outRect.top = space;
                } else {
                    outRect.top = 0;
                }*/
                outRect.top = space;
            }
        }
    }
}

package com.dbottillo.mtgsearchfree.ui.sets

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.*
import android.widget.LinearLayout
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.ui.BaseHomeFragment
import com.dbottillo.mtgsearchfree.view.activities.CardLuckyActivity
import com.dbottillo.mtgsearchfree.view.activities.SearchActivity

class SetsFragment : BaseHomeFragment() {
    
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_sets, container, false) as View
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


    override fun getPageTrack(): String {
        return "/sets"
    }

    override fun getScrollViewId(): Int {
        return R.id.scrollView
    }

    override fun getTitle(): String {
        return "Aether Reveal"
    }

    @OnClick(R.id.action_search)
    fun searchTapped(){
        startActivity(Intent(activity, SearchActivity::class.java))
    }

    @OnClick(R.id.action_lucky)
    fun luckyTapped(){
        startActivity(Intent(activity, CardLuckyActivity::class.java))
    }
}
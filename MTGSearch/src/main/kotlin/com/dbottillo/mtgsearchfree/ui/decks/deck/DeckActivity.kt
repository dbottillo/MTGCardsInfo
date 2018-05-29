package com.dbottillo.mtgsearchfree.ui.decks.deck

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.view.*
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.model.Deck
import com.dbottillo.mtgsearchfree.ui.BasicActivity
import com.dbottillo.mtgsearchfree.ui.decks.startingHand.DeckStartingHandFragment
import com.dbottillo.mtgsearchfree.ui.views.MTGLoader
import com.dbottillo.mtgsearchfree.util.*
import dagger.android.AndroidInjection
import javax.inject.Inject

class DeckActivity : BasicActivity(), DeckActivityView {

    @Inject
    lateinit var presenter: DeckActivityPresenter

    private val container: View by lazy(LazyThreadSafetyMode.NONE) { findViewById<ViewGroup>(R.id.container) }
    private val emptyView: TextView by lazy(LazyThreadSafetyMode.NONE) { findViewById<TextView>(R.id.empty_view) }
    private val loader: MTGLoader by lazy(LazyThreadSafetyMode.NONE) { findViewById<MTGLoader>(R.id.loader) }
    private val viewPager: ViewPager by lazy(LazyThreadSafetyMode.NONE) { findViewById<ViewPager>(R.id.deck_view_pager) }
    private val tabLayout: TabLayout by lazy(LazyThreadSafetyMode.NONE) { findViewById<TabLayout>(R.id.deck_cards_tab_layout) }

    override fun onCreate(bundle: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(bundle)
        setContentView(R.layout.activity_deck)

        setupToolbar()

        val deck = intent.getParcelableExtra("deck") as Deck
        title = if (deck.name.isEmpty()) getString(R.string.deck_title) else deck.name

        supportActionBar?.let {
            it.setHomeButtonEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)
        }

        emptyView.setText(R.string.empty_deck)

        tabLayout.setupWithViewPager(viewPager)

        presenter.init(this, deck)
        presenter.load()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.deck, menu)
        menu.findItem(R.id.action_copy).setTintColor(this, R.color.white)
        menu.findItem(R.id.action_export).setTintColor(this, R.color.white)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.action_export -> {
                exportDeck()
                return true
            }
            R.id.action_edit -> {
                editDeckName()
                return true
            }
            R.id.action_copy -> {
                presenter.copyDeck()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun getPageTrack(): String? {
        return "/deck"
    }

    override fun showTitle(title: String) {
        this.title = title
    }

    override fun showEmptyScreen() {
        loader.gone()
        tabLayout.gone()
        emptyView.show()
    }

    override fun showDeck(deck: Deck) {
        loader.gone()
        tabLayout.show()
        viewPager.show()
        toolbar.elevation = 0f
        viewPager.adapter = DeckPagerAdapter(supportFragmentManager, deck,
                listOf(getString(R.string.deck_list), getString(R.string.deck_starting_hand)))
    }

    override fun deckCopied() {
        Snackbar.make(container, getString(R.string.deck_copied), Snackbar.LENGTH_LONG).show()
    }

    override fun deckExported() {
        val snackbar = Snackbar
                .make(container, getString(R.string.deck_exported), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.share)) {
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.type = "text/plain"
                    intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(presenter.deck.fileNameForDeck()))
                    startActivity(Intent.createChooser(intent, getString(R.string.share)))
                    TrackingManager.trackDeckExport()
                }
        snackbar.show()
    }

    override fun deckNotExported(){
        exportDeckNotAllowed()
    }

    private fun exportDeck() {
        LOG.d()
        requestPermission(PermissionAvailable.WriteStorage, object : PermissionUtil.PermissionListener {
            override fun permissionGranted() {
                presenter.exportDeck()
            }

            override fun permissionNotGranted() {
                exportDeckNotAllowed()
            }
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun exportDeckNotAllowed() {
        Toast.makeText(this, getString(R.string.error_export_deck), Toast.LENGTH_SHORT).show()
        TrackingManager.trackDeckExportError()
    }

    private fun editDeckName() {
        LOG.d()
        val alert = AlertDialog.Builder(this, R.style.MTGDialogTheme)

        alert.setTitle(getString(R.string.edit_deck))

        val layoutInflater = LayoutInflater.from(this)
        @SuppressLint("InflateParams") val view = layoutInflater.inflate(R.layout.dialog_edit_deck, null)
        val editText = view.findViewById<EditText>(R.id.edit_text)
        editText.setText(presenter.deck.name)
        editText.setSelection(presenter.deck.name.length)
        alert.setView(view)

        alert.setPositiveButton(getString(R.string.save)) { dialog, _ ->
            val value = editText.text.toString()
            presenter.editDeck(value)
            TrackingManager.trackEditDeck()
            presenter.deck.name = value
            title = presenter.deck.name
            dialog.dismiss()
        }

        alert.setNegativeButton(getString(R.string.cancel)) { _, _ ->
            // Canceled.
        }

        alert.show()
    }
}

class DeckPagerAdapter(fragmentManager: FragmentManager,
                       val deck: Deck,
                       private val titleList: List<String>): FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        return when(position){
            0 -> {
                DeckFragment().apply { arguments = Bundle().apply { putParcelable(DECK_KEY, deck) } }
            }
            else -> DeckStartingHandFragment().apply { arguments = Bundle().apply { putParcelable(DECK_KEY, deck) } }
        }
    }

    override fun getCount() = 2

    override fun getPageTitle(position: Int): CharSequence? {
        return titleList[position]
    }
}
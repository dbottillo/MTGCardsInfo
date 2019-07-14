package com.dbottillo.mtgsearchfree.search

import android.content.Context
import android.graphics.PorterDuff
import androidx.core.content.ContextCompat
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatEditText
import android.util.AttributeSet
import android.widget.ArrayAdapter
import android.widget.RelativeLayout
import android.widget.Spinner
import com.dbottillo.mtgsearchfree.model.MTGSet
import com.dbottillo.mtgsearchfree.model.SearchParams
import com.dbottillo.mtgsearchfree.model.cmcParamCreator
import com.dbottillo.mtgsearchfree.model.ptParamCreator
import com.dbottillo.mtgsearchfree.util.LOG

class MTGSearchView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = -1) : RelativeLayout(context, attrs, defStyleAttr) {

    private var operators = arrayOf("=", ">", "<", ">=", "<=")
    private var sets = mutableListOf(MTGSet(-1, "", resources.getString(R.string.search_set_all)), MTGSet(-2, "", resources.getString(R.string.search_set_standard)))

    val name: AppCompatEditText by lazy(LazyThreadSafetyMode.NONE) { findViewById<AppCompatEditText>(R.id.search_name) }
    val types: AppCompatEditText by lazy(LazyThreadSafetyMode.NONE) { findViewById<AppCompatEditText>(R.id.search_types) }
    val text: AppCompatEditText by lazy(LazyThreadSafetyMode.NONE) { findViewById<AppCompatEditText>(R.id.search_text) }
    val cmc: AppCompatEditText by lazy(LazyThreadSafetyMode.NONE) { findViewById<AppCompatEditText>(R.id.search_cmc) }
    val power: AppCompatEditText by lazy(LazyThreadSafetyMode.NONE) { findViewById<AppCompatEditText>(R.id.search_power) }
    val tough: AppCompatEditText by lazy(LazyThreadSafetyMode.NONE) { findViewById<AppCompatEditText>(R.id.search_tough) }
    val powerOp: Spinner by lazy(LazyThreadSafetyMode.NONE) { findViewById<Spinner>(R.id.search_power_operator) }
    val toughOp: Spinner by lazy(LazyThreadSafetyMode.NONE) { findViewById<Spinner>(R.id.search_toughness_operator) }
    val cmcOp: Spinner by lazy(LazyThreadSafetyMode.NONE) { findViewById<Spinner>(R.id.search_cmc_operator) }
    val white: AppCompatCheckBox by lazy(LazyThreadSafetyMode.NONE) { findViewById<AppCompatCheckBox>(R.id.search_w) }
    val blue: AppCompatCheckBox by lazy(LazyThreadSafetyMode.NONE) { findViewById<AppCompatCheckBox>(R.id.search_u) }
    val black: AppCompatCheckBox by lazy(LazyThreadSafetyMode.NONE) { findViewById<AppCompatCheckBox>(R.id.search_b) }
    val red: AppCompatCheckBox by lazy(LazyThreadSafetyMode.NONE) { findViewById<AppCompatCheckBox>(R.id.search_r) }
    val green: AppCompatCheckBox by lazy(LazyThreadSafetyMode.NONE) { findViewById<AppCompatCheckBox>(R.id.search_g) }
    val multi: AppCompatCheckBox by lazy(LazyThreadSafetyMode.NONE) { findViewById<AppCompatCheckBox>(R.id.search_m) }
    val noMulti: AppCompatCheckBox by lazy(LazyThreadSafetyMode.NONE) { findViewById<AppCompatCheckBox>(R.id.search_nm) }
    val multiNoOthers: AppCompatCheckBox by lazy(LazyThreadSafetyMode.NONE) { findViewById<AppCompatCheckBox>(R.id.search_mno) }
    val land: AppCompatCheckBox by lazy(LazyThreadSafetyMode.NONE) { findViewById<AppCompatCheckBox>(R.id.search_l) }
    val common: AppCompatCheckBox by lazy(LazyThreadSafetyMode.NONE) { findViewById<AppCompatCheckBox>(R.id.search_common) }
    val uncommon: AppCompatCheckBox by lazy(LazyThreadSafetyMode.NONE) { findViewById<AppCompatCheckBox>(R.id.search_uncommon) }
    val rare: AppCompatCheckBox by lazy(LazyThreadSafetyMode.NONE) { findViewById<AppCompatCheckBox>(R.id.search_rare) }
    val mythic: AppCompatCheckBox by lazy(LazyThreadSafetyMode.NONE) { findViewById<AppCompatCheckBox>(R.id.search_mythic) }
    val set: Spinner by lazy(LazyThreadSafetyMode.NONE) { findViewById<Spinner>(R.id.search_set) }

    private var searchSetAdapter: SearchSetAdapter? = null

    val searchParams: SearchParams
        get() {
            val searchParams = SearchParams()
            searchParams.name = name.text.toString()
            searchParams.types = types.text.toString()
            searchParams.text = text.text.toString()
            searchParams.cmc = cmcParamCreator(operators[cmcOp.selectedItemPosition], cmc.text.toString())
            searchParams.power = ptParamCreator(operators[powerOp.selectedItemPosition], power.text.toString())
            searchParams.tough = ptParamCreator(operators[toughOp.selectedItemPosition], tough.text.toString())
            searchParams.isWhite = white.isChecked
            searchParams.isBlue = blue.isChecked
            searchParams.isBlack = black.isChecked
            searchParams.isRed = red.isChecked
            searchParams.isGreen = green.isChecked
            searchParams.setOnlyMulti(multi.isChecked)
            searchParams.isNoMulti = noMulti.isChecked
            searchParams.isOnlyMultiNoOthers = multiNoOthers.isChecked
            searchParams.isLand = land.isChecked
            searchParams.isCommon = common.isChecked
            searchParams.isUncommon = uncommon.isChecked
            searchParams.isRare = rare.isChecked
            searchParams.isMythic = mythic.isChecked
            searchParams.setId = sets[set.selectedItemPosition].id
            return searchParams
        }

    init {
        inflate(context, R.layout.search_form_view, this)

        searchSetAdapter = SearchSetAdapter(context, sets)
        set.adapter = searchSetAdapter

        val white = ContextCompat.getColor(context, R.color.white)

        val cmcAdapter = ArrayAdapter<CharSequence>(context, R.layout.row_spinner_item, operators)
        cmcAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        cmcOp.adapter = cmcAdapter
        cmcOp.background.setColorFilter(white, PorterDuff.Mode.SRC_ATOP)

        val adapter = ArrayAdapter<CharSequence>(context, R.layout.row_spinner_item, operators)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        powerOp.adapter = adapter
        powerOp.background.setColorFilter(white, PorterDuff.Mode.SRC_ATOP)

        val toughAdapter = ArrayAdapter<CharSequence>(context, R.layout.row_spinner_item, operators)
        toughAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        toughOp.adapter = toughAdapter
        toughOp.background.setColorFilter(white, PorterDuff.Mode.SRC_ATOP)

        multi.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                noMulti.isChecked = false
                multiNoOthers.isChecked = false
            }
        }
        noMulti.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                multi.isChecked = false
                multiNoOthers.isChecked = false
            }
        }
        multiNoOthers.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                noMulti.isChecked = false
                multi.isChecked = false
            }
        }
    }

    fun refreshSets(sets: List<MTGSet>) {
        LOG.d()
        this.sets.addAll(sets)
        searchSetAdapter?.notifyDataSetChanged()
    }
}

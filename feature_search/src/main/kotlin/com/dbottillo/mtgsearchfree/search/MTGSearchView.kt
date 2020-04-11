package com.dbottillo.mtgsearchfree.search

import android.content.Context
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuff.Mode.SRC_ATOP
import androidx.core.content.ContextCompat
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatEditText
import android.util.AttributeSet
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.Spinner
import androidx.constraintlayout.widget.ConstraintLayout
import com.dbottillo.mtgsearchfree.model.MTGSet
import com.dbottillo.mtgsearchfree.model.SearchParams
import com.dbottillo.mtgsearchfree.model.cmcParamCreator
import com.dbottillo.mtgsearchfree.model.ptParamCreator
import com.dbottillo.mtgsearchfree.util.LOG
import com.dbottillo.mtgsearchfree.util.themeColor
import kotlinx.android.synthetic.main.search_form_view.view.*

class MTGSearchView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = -1
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var operators = arrayOf("=", ">", "<", ">=", "<=")
    private var sets = mutableListOf(MTGSet(-1, "", resources.getString(R.string.search_set_all)), MTGSet(-2, "", resources.getString(R.string.search_set_standard)))
    private var colorsHow = arrayOf(
        context.getString(R.string.search_colors_exactly),
        context.getString(R.string.search_colors_including),
        context.getString(R.string.search_colors_at_most),
        context.getString(R.string.search_colors_excluding_others))

    private val name: AppCompatEditText by lazy(LazyThreadSafetyMode.NONE) { findViewById<AppCompatEditText>(R.id.search_name) }
    private val types: AppCompatEditText by lazy(LazyThreadSafetyMode.NONE) { findViewById<AppCompatEditText>(R.id.search_types) }
    private val text: AppCompatEditText by lazy(LazyThreadSafetyMode.NONE) { findViewById<AppCompatEditText>(R.id.search_text) }
    private val cmc: AppCompatEditText by lazy(LazyThreadSafetyMode.NONE) { findViewById<AppCompatEditText>(R.id.search_cmc) }
    private val power: AppCompatEditText by lazy(LazyThreadSafetyMode.NONE) { findViewById<AppCompatEditText>(R.id.search_power) }
    private val tough: AppCompatEditText by lazy(LazyThreadSafetyMode.NONE) { findViewById<AppCompatEditText>(R.id.search_tough) }
    private val powerOp: Spinner by lazy(LazyThreadSafetyMode.NONE) { findViewById<Spinner>(R.id.search_power_operator) }
    private val toughOp: Spinner by lazy(LazyThreadSafetyMode.NONE) { findViewById<Spinner>(R.id.search_toughness_operator) }
    private val cmcOp: Spinner by lazy(LazyThreadSafetyMode.NONE) { findViewById<Spinner>(R.id.search_cmc_operator) }
    private val white: AppCompatCheckBox by lazy(LazyThreadSafetyMode.NONE) { findViewById<AppCompatCheckBox>(R.id.search_w) }
    private val blue: AppCompatCheckBox by lazy(LazyThreadSafetyMode.NONE) { findViewById<AppCompatCheckBox>(R.id.search_u) }
    private val black: AppCompatCheckBox by lazy(LazyThreadSafetyMode.NONE) { findViewById<AppCompatCheckBox>(R.id.search_b) }
    private val red: AppCompatCheckBox by lazy(LazyThreadSafetyMode.NONE) { findViewById<AppCompatCheckBox>(R.id.search_r) }
    private val green: AppCompatCheckBox by lazy(LazyThreadSafetyMode.NONE) { findViewById<AppCompatCheckBox>(R.id.search_g) }
    private val colorsSpecification: Spinner by lazy(LazyThreadSafetyMode.NONE) { findViewById<Spinner>(R.id.search_colors_how) }
    private val land: AppCompatCheckBox by lazy(LazyThreadSafetyMode.NONE) { findViewById<AppCompatCheckBox>(R.id.search_l) }
    private val common: AppCompatCheckBox by lazy(LazyThreadSafetyMode.NONE) { findViewById<AppCompatCheckBox>(R.id.search_common) }
    private val uncommon: AppCompatCheckBox by lazy(LazyThreadSafetyMode.NONE) { findViewById<AppCompatCheckBox>(R.id.search_uncommon) }
    private val rare: AppCompatCheckBox by lazy(LazyThreadSafetyMode.NONE) { findViewById<AppCompatCheckBox>(R.id.search_rare) }
    private val mythic: AppCompatCheckBox by lazy(LazyThreadSafetyMode.NONE) { findViewById<AppCompatCheckBox>(R.id.search_mythic) }
    private val set: Spinner by lazy(LazyThreadSafetyMode.NONE) { findViewById<Spinner>(R.id.search_set) }
    private val noDuplicates: AppCompatCheckBox by lazy(LazyThreadSafetyMode.NONE) { findViewById<AppCompatCheckBox>(R.id.search_no_duplicates) }
    private val sortAZ: AppCompatCheckBox by lazy(LazyThreadSafetyMode.NONE) { findViewById<AppCompatCheckBox>(R.id.search_az) }

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
            searchParams.exactlyColors = colorsSpecification.selectedItemPosition == 0
            searchParams.includingColors = colorsSpecification.selectedItemPosition == 1
            searchParams.atMostColors = colorsSpecification.selectedItemPosition == 2
            searchParams.excludingOtherColors = colorsSpecification.selectedItemPosition == 3
            searchParams.isLand = land.isChecked
            searchParams.isCommon = common.isChecked
            searchParams.isUncommon = uncommon.isChecked
            searchParams.isRare = rare.isChecked
            searchParams.isMythic = mythic.isChecked
            searchParams.setId = sets[set.selectedItemPosition].id
            searchParams.colorless = search_colorless.isChecked
            searchParams.duplicates = !noDuplicates.isChecked
            searchParams.sortAZ = sortAZ.isChecked
            return searchParams
        }

    private val disableColorlessCheckedChangeListener: CompoundButton.OnCheckedChangeListener = CompoundButton.OnCheckedChangeListener { p0, p1 ->
        search_colorless.setOnCheckedChangeListener(null)
        search_colorless.isChecked = false
        search_colorless.setOnCheckedChangeListener(colorlessCheckedChangeListener)
    }

    private val colorlessCheckedChangeListener: CompoundButton.OnCheckedChangeListener = CompoundButton.OnCheckedChangeListener { p0, p1 ->
        white.setOnCheckedChangeListener(null)
        red.setOnCheckedChangeListener(null)
        blue.setOnCheckedChangeListener(null)
        green.setOnCheckedChangeListener(null)
        black.setOnCheckedChangeListener(null)
        white.isChecked = false
        red.isChecked = false
        blue.isChecked = false
        green.isChecked = false
        black.isChecked = false
        white.setOnCheckedChangeListener(disableColorlessCheckedChangeListener)
        red.setOnCheckedChangeListener(disableColorlessCheckedChangeListener)
        blue.setOnCheckedChangeListener(disableColorlessCheckedChangeListener)
        green.setOnCheckedChangeListener(disableColorlessCheckedChangeListener)
        black.setOnCheckedChangeListener(disableColorlessCheckedChangeListener)
    }

    init {
        inflate(context, R.layout.search_form_view, this)

        searchSetAdapter = SearchSetAdapter(context, sets)
        set.adapter = searchSetAdapter

        val colorFilter = BlendModeColorFilter(context.themeColor(R.attr.colorOnBackground), BlendMode.SRC_ATOP)

        val cmcAdapter = ArrayAdapter<CharSequence>(context, R.layout.row_spinner_item, operators)
        cmcAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        cmcOp.adapter = cmcAdapter
        cmcOp.background.colorFilter = colorFilter

        val adapter = ArrayAdapter<CharSequence>(context, R.layout.row_spinner_item, operators)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        powerOp.adapter = adapter
        powerOp.background.colorFilter = colorFilter

        val toughAdapter = ArrayAdapter<CharSequence>(context, R.layout.row_spinner_item, operators)
        toughAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        toughOp.adapter = toughAdapter
        toughOp.background.colorFilter = colorFilter

        val colorsSpecificationAdapter = ArrayAdapter<CharSequence>(context, R.layout.row_spinner_item, colorsHow)
        toughAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        colorsSpecification.adapter = colorsSpecificationAdapter
        colorsSpecification.background.colorFilter = colorFilter
        colorsSpecification.setSelection(0)

        white.setOnCheckedChangeListener(disableColorlessCheckedChangeListener)
        red.setOnCheckedChangeListener(disableColorlessCheckedChangeListener)
        blue.setOnCheckedChangeListener(disableColorlessCheckedChangeListener)
        green.setOnCheckedChangeListener(disableColorlessCheckedChangeListener)
        black.setOnCheckedChangeListener(disableColorlessCheckedChangeListener)
        search_colorless.setOnCheckedChangeListener(colorlessCheckedChangeListener)
    }

    fun refreshSets(sets: List<MTGSet>) {
        LOG.d()
        this.sets.addAll(sets)
        searchSetAdapter?.notifyDataSetChanged()
    }
}

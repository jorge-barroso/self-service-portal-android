package com.premfina.esig.selfserviceportal

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.premfina.selfservice.dto.AgreementDto
import kotlinx.android.synthetic.main.activity_view_agreements.*
import kotlinx.android.synthetic.main.app_bar_drawer.*
import kotlinx.android.synthetic.main.fragment_view_agreements.view.*
import lecho.lib.hellocharts.model.PieChartData
import lecho.lib.hellocharts.model.SliceValue
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.jetbrains.annotations.NotNull
import java.text.NumberFormat
import java.text.SimpleDateFormat

class AgreementsDetailsActivity : Drawer() {

    /**
     * The [android.support.v4.view.PagerAdapter] that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * [android.support.v4.app.FragmentStatePagerAdapter].
     */
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null
    private lateinit var agreementDtos: Array<AgreementDto>
    private var pos: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addToFrame(layoutResID = R.layout.activity_view_agreements)

        bottom_menu.menu.setGroupCheckable(0, true, true)

        bottom_menu.selectedItemId = R.id.agreements_bottom

        progressBar2.indeterminateDrawable.setColorFilter(getColor(R.color.colorPrimary), android.graphics.PorterDuff.Mode.MULTIPLY)

        doAsync {
            //initialize array of agreements
            agreementDtos = jacksonObjectMapper().readValue(agreementsDetails.getString("agreements", "")!!)

            // Create the adapter that will return a fragment for each of the three
            // primary sections of the activity.
            mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

            // Set up the ViewPager with the sections adapter.
            view_pager.adapter = mSectionsPagerAdapter

            uiThread {
                progressBar2.visibility = View.GONE
            }
        }
    }

    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            val agreementDto = agreementDtos[position]
            return PlaceholderFragment.newInstance(agreementDto)
        }

        override fun getCount(): Int {
            // Show as many pages as agreements.
            return agreementDtos.size
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    class PlaceholderFragment : Fragment() {

        private val numberFormat = NumberFormat.getCurrencyInstance()
        private val df = SimpleDateFormat.getDateInstance()
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {
            val rootView = inflater.inflate(R.layout.fragment_view_agreements, container, false)
            val agreementDtoString = arguments?.getString(AGREEMENT)
            val agreementDto = jacksonObjectMapper().readValue(agreementDtoString, AgreementDto::class.java)
            rootView.broker_name.text = agreementDto.brokerName
            rootView.total_amount.text = numberFormat.format(agreementDto.totalAmount)
            rootView.monthly_amount.text = numberFormat.format(agreementDto.monthlyAmount)
            rootView.next_due_date.text = df.format(agreementDto.nextDueDate)
            rootView.status.text = agreementDto.agreementStatus
            rootView.agreement_data_card.setOnClickListener {
                val intent = Intent(context, DocumentDetailsActivity::class.java)
                intent.putExtra(AGREEMENT, agreementDtoString)
                        .putExtra("element_id", R.id.agreements_bottom)
                startActivity(intent)
            }
            rootView.agrview_agreement_number.text = agreementDto.agreementNumber
            val cleared = agreementDto.clearedBalance.toFloat()
            val toPay = (agreementDto.totalAmount - cleared).toFloat()
            generatePieChart(rootView, cleared, toPay)
            return rootView
        }


        private fun generatePieChart(rootView: View, cleared: Float, toPay: Float) {
            val paidValue = SliceValue(cleared, activity!!.getColor(R.color.colorPrimary)).setLabel(activity!!.getString(R.string.paid))
            val toPayValue = SliceValue(toPay - cleared, activity!!.getColor(R.color.colorAccent)).setLabel(activity!!.getString(R.string.to_pay))
            val values = listOf<SliceValue>(paidValue, toPayValue)
            val data = PieChartData(values)
            data.setHasLabels(true)
                    .setHasLabelsOutside(true)
                    .slicesSpacing = 7

            rootView.pie_chart.circleFillRatio = 0.75f

            rootView.pie_chart.pieChartData = data
        }

        companion object {

            const val AGREEMENT = "agreementDto"

            /**
             * Returns a new instance of this fragment for the given section
             * number.
             */
            fun newInstance(@NotNull agreementDto: AgreementDto): PlaceholderFragment {
                val fragment = PlaceholderFragment()
                val args = Bundle()
                args.putString(AGREEMENT, jacksonObjectMapper().writeValueAsString(agreementDto))
                fragment.arguments = args
                return fragment
            }
        }
    }
}

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
import com.premfina.selfservice.dto.UserDto
import kotlinx.android.synthetic.main.activity_view_agreements.*
import kotlinx.android.synthetic.main.fragment_view_agreements.view.*
import org.jetbrains.annotations.NotNull
import java.text.NumberFormat
import java.text.SimpleDateFormat

class ViewAgreementsActivity : Drawer() {

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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addToFrame(layoutResID = R.layout.activity_view_agreements)

        //initialize array of agreements
        agreementDtos = jacksonObjectMapper().readValue<UserDto>(userPreferences.getString("userDto", "")!!).agreementDtos

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        // Set up the ViewPager with the sections adapter.
        container.adapter = mSectionsPagerAdapter
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
            // Show 3 total pages.
            return agreementDtos.size
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    class PlaceholderFragment : Fragment() {

        private val numberFormat = NumberFormat.getCurrencyInstance()
        private val df = SimpleDateFormat.getInstance()
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
            rootView.setOnClickListener {
                val intent = Intent(context, DocumentDetailsActivity::class.java)
                intent.putExtra(AGREEMENT, agreementDtoString)
                startActivity(intent)
            }
            return rootView
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

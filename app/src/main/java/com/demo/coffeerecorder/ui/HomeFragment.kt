package com.demo.coffeerecorder.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.coffeerecorder.MainActivity
import com.demo.coffeerecorder.R
import com.demo.coffeerecorder.data.CoffeeStats
import com.demo.coffeerecorder.data.local.CoffeeRecordEntity
import com.demo.coffeerecorder.databinding.FragmentHomeBinding
import com.demo.coffeerecorder.viewmodel.CoffeeViewModel
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters
import kotlin.math.roundToInt

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CoffeeViewModel by activityViewModels()
    private val timelineAdapter = HomeTimelineAdapter()
    private val dateAdapter = HomeDateAdapter { date ->
        selectedDate = date
        renderDates(allRecords)
        renderTimeline(allRecords)
    }
    private var allRecords: List<CoffeeRecordEntity> = emptyList()
    private var selectedDate: LocalDate? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupHeader()
        setupDateList()
        setupTimeline()
        setupActions()

        viewModel.records.observe(viewLifecycleOwner) { records ->
            allRecords = records
            ensureSelectedDate(records)
            renderDates(records)
            renderTimeline(records)
            renderSummary(records)
        }

        viewModel.stats.observe(viewLifecycleOwner, ::renderStats)
    }

    private fun setupHeader() {
        val currentHour = LocalTime.now().hour
        binding.tvGreeting.text = when {
            currentHour < 12 -> getString(R.string.home_greeting_morning)
            currentHour < 18 -> getString(R.string.home_greeting_afternoon)
            else -> getString(R.string.home_greeting_evening)
        }
    }

    private fun setupDateList() {
        binding.recyclerViewDates.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.recyclerViewDates.adapter = dateAdapter
    }

    private fun setupTimeline() {
        binding.recyclerViewRecent.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewRecent.adapter = timelineAdapter
        timelineAdapter.setListener(object : HomeTimelineAdapter.Listener {
            override fun onRecordClicked(recordId: Long) {
                (activity as? MainActivity)?.openRecordEditor(recordId)
            }
        })
    }

    private fun setupActions() {
        binding.cardAddCoffee.setOnClickListener {
            (activity as? MainActivity)?.openRecordEditor()
        }
        binding.buttonStatsOverview.setOnClickListener {
            (activity as? MainActivity)?.selectTab(R.id.menuStats)
        }
        binding.tvCalendarAction.setOnClickListener {
            Toast.makeText(requireContext(), R.string.home_calendar_pending, Toast.LENGTH_SHORT).show()
        }
        binding.tvFilterAction.setOnClickListener {
            Toast.makeText(requireContext(), R.string.home_filter_pending, Toast.LENGTH_SHORT).show()
        }
    }

    private fun renderStats(stats: CoffeeStats) {
        binding.tvAverageRating.text = getString(R.string.home_rating_compact_format, stats.averageRating)
    }

    private fun renderSummary(records: List<CoffeeRecordEntity>) {
        val monthRecords = records.filter { isInCurrentMonth(it.drankAt) }
        val monthCupCount = monthRecords.size
        val monthHours = monthCupCount * 0.95
        val favoriteDrink = monthRecords
            .groupingBy { it.drinkType.ifBlank { getString(R.string.summary_no_drink) } }
            .eachCount()
            .maxByOrNull { it.value }

        val favoriteRatio = if (monthCupCount == 0 || favoriteDrink == null) {
            0
        } else {
            ((favoriteDrink.value.toDouble() / monthCupCount) * 100).roundToInt()
        }

        binding.tvTotalCount.text = monthCupCount.toString()
        binding.tvFavoriteRatio.text = getString(R.string.home_favorite_ratio_format, favoriteRatio)
        binding.tvFavoriteRatioLabel.text = getString(
            R.string.home_favorite_ratio_detail,
            favoriteDrink?.key ?: getString(R.string.summary_no_drink)
        )
    }

    private fun ensureSelectedDate(records: List<CoffeeRecordEntity>) {
        if (selectedDate != null) {
            return
        }

        val today = LocalDate.now()
        selectedDate = when {
            records.any { toLocalDate(it.drankAt) == today } -> today
            records.isNotEmpty() -> toLocalDate(records.maxByOrNull { it.drankAt }!!.drankAt)
            else -> today
        }
    }

    private fun renderDates(records: List<CoffeeRecordEntity>) {
        val anchorDate = selectedDate ?: LocalDate.now()
        val startOfWeek = anchorDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val dates = (0..6).map { offset ->
            val currentDate = startOfWeek.plusDays(offset.toLong())
            HomeDateItem(
                date = currentDate,
                weekdayLabel = weekdayLabel(currentDate.dayOfWeek),
                dayOfMonth = currentDate.dayOfMonth,
                hasRecord = records.any { toLocalDate(it.drankAt) == currentDate },
                isSelected = currentDate == anchorDate
            )
        }
        dateAdapter.submitItems(dates)
    }

    private fun renderTimeline(records: List<CoffeeRecordEntity>) {
        val currentDate = selectedDate ?: LocalDate.now()
        val filteredRecords = records
            .filter { toLocalDate(it.drankAt) == currentDate }
            .sortedBy { it.drankAt }

        timelineAdapter.submitItems(filteredRecords)
        binding.tvRecentEmpty.visibility = if (filteredRecords.isEmpty()) View.VISIBLE else View.GONE
        binding.recyclerViewRecent.visibility = if (filteredRecords.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun toLocalDate(timestamp: Long): LocalDate {
        return Instant.ofEpochMilli(timestamp)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }

    private fun isInCurrentMonth(timestamp: Long): Boolean {
        val date = toLocalDate(timestamp)
        val today = LocalDate.now()
        return date.year == today.year && date.month == today.month
    }

    private fun weekdayLabel(dayOfWeek: DayOfWeek): String {
        return when (dayOfWeek) {
            DayOfWeek.MONDAY -> "一"
            DayOfWeek.TUESDAY -> "二"
            DayOfWeek.WEDNESDAY -> "三"
            DayOfWeek.THURSDAY -> "四"
            DayOfWeek.FRIDAY -> "五"
            DayOfWeek.SATURDAY -> "六"
            DayOfWeek.SUNDAY -> "日"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

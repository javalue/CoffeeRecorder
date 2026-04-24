package com.demo.coffeerecorder.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.demo.coffeerecorder.R
import com.demo.coffeerecorder.data.CoffeeStats
import com.demo.coffeerecorder.databinding.FragmentStatsBinding
import com.demo.coffeerecorder.viewmodel.CoffeeViewModel

class StatsFragment : Fragment() {

    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CoffeeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.stats.observe(viewLifecycleOwner, ::renderStats)
    }

    private fun renderStats(stats: CoffeeStats) {
        binding.tvStatTotalCount.text = stats.totalCount.toString()
        binding.tvStatTotalSpending.text = CoffeeFormatters.formatPrice(requireContext(), stats.totalSpend)
        binding.tvStatAverageCupSize.text = CoffeeFormatters.formatCupSize(requireContext(), stats.averageCupSize)
        binding.tvStatFavoriteMethod.text = stats.favoriteMethod ?: getString(R.string.summary_no_method)
        binding.tvStatFavoriteDrink.text = stats.favoriteDrink ?: getString(R.string.summary_no_drink)
        binding.tvStatLatestRecord.text = stats.latestRecord?.beanName ?: getString(R.string.summary_insight_empty)

        binding.tvInsightPrimary.text = getString(
            R.string.insight_favorite_format,
            stats.favoriteMethod ?: getString(R.string.summary_no_method),
            stats.favoriteDrink ?: getString(R.string.summary_no_drink)
        )

        binding.tvInsightSecondary.text = getString(
            R.string.insight_spending_format,
            stats.totalCount,
            CoffeeFormatters.formatPrice(requireContext(), stats.averageSpend)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

package com.demo.coffeerecorder.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.coffeerecorder.MainActivity
import com.demo.coffeerecorder.R
import com.demo.coffeerecorder.data.CoffeeStats
import com.demo.coffeerecorder.databinding.FragmentHomeBinding
import com.demo.coffeerecorder.viewmodel.CoffeeViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CoffeeViewModel by activityViewModels()
    private val adapter = CoffeeRecordAdapter()

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
        binding.recyclerViewRecent.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewRecent.adapter = adapter
        adapter.setListener(object : CoffeeRecordAdapter.Listener {
            override fun onRecordClicked(recordId: Long) {
                (activity as? MainActivity)?.openRecordEditor(recordId)
            }

            override fun onRecordLongPressed(recordId: Long) {
            }
        })

        binding.buttonQuickAdd.setOnClickListener {
            (activity as? MainActivity)?.openRecordEditor()
        }

        viewModel.recentRecords.observe(viewLifecycleOwner) { records ->
            adapter.submitItems(records)
            binding.tvRecentEmpty.visibility = if (records.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.stats.observe(viewLifecycleOwner, ::renderStats)
    }

    private fun renderStats(stats: CoffeeStats) {
        binding.tvTotalCount.text = stats.totalCount.toString()
        binding.tvAverageRating.text = CoffeeFormatters.formatAverageRating(requireContext(), stats.averageRating)
        binding.tvAverageSpend.text = CoffeeFormatters.formatPrice(requireContext(), stats.averageSpend)

        binding.tvSummaryInsight.text = when {
            stats.latestRecord != null -> getString(
                R.string.summary_insight_format,
                stats.latestRecord.beanName,
                stats.latestRecord.brewMethod
            )

            else -> getString(R.string.summary_insight_empty)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

package com.demo.coffeerecorder.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.coffeerecorder.MainActivity
import com.demo.coffeerecorder.R
import com.demo.coffeerecorder.data.local.CoffeeRecordEntity
import com.demo.coffeerecorder.databinding.FragmentRecordsBinding
import com.demo.coffeerecorder.viewmodel.CoffeeViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class RecordsFragment : Fragment() {

    private var _binding: FragmentRecordsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CoffeeViewModel by activityViewModels()
    private val adapter = CoffeeRecordAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecordsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerViewRecords.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewRecords.adapter = adapter
        binding.buttonEmptyAction.setOnClickListener {
            (activity as? MainActivity)?.openRecordEditor()
        }

        adapter.setListener(object : CoffeeRecordAdapter.Listener {
            override fun onRecordClicked(recordId: Long) {
                (activity as? MainActivity)?.openRecordEditor(recordId)
            }

            override fun onRecordLongPressed(recordId: Long) {
                confirmDelete(recordId)
            }
        })

        viewModel.records.observe(viewLifecycleOwner) { records ->
            adapter.submitItems(records)
            binding.layoutEmptyState.visibility = if (records.isEmpty()) View.VISIBLE else View.GONE
            binding.recyclerViewRecords.visibility = if (records.isEmpty()) View.GONE else View.VISIBLE
            binding.tvRecordCount.text = getString(R.string.record_count_summary_format, records.size)
        }
    }

    private fun confirmDelete(recordId: Long) {
        val record = viewModel.records.value?.firstOrNull { it.id == recordId } ?: return
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.confirm_delete_title)
            .setMessage(R.string.confirm_delete_message)
            .setNegativeButton(R.string.action_cancel, null)
            .setPositiveButton(R.string.action_delete) { _, _ ->
                deleteRecord(record)
            }
            .show()
    }

    private fun deleteRecord(record: CoffeeRecordEntity) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.deleteRecord(record)
            Toast.makeText(requireContext(), R.string.record_deleted, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

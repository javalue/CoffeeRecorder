package com.demo.coffeerecorder

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.demo.coffeerecorder.data.local.CoffeeRecordEntity
import com.demo.coffeerecorder.databinding.ActivityRecordEditorBinding
import com.demo.coffeerecorder.ui.CoffeePhotoLoader
import com.demo.coffeerecorder.viewmodel.CoffeeViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import java.io.File

class RecordEditorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecordEditorBinding
    private val viewModel: CoffeeViewModel by viewModels()
    private var currentRecord: CoffeeRecordEntity? = null
    private var currentPhotoUri: Uri? = null
    private var previousPhotoUriBeforeCapture: Uri? = null
    private val recordId: Long? by lazy {
        intent.getLongExtra(EXTRA_RECORD_ID, 0L).takeIf { it > 0L }
    }
    private val takePhotoLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            renderPhotoPreview()
        } else {
            deletePhotoIfOwned(currentPhotoUri?.toString())
            currentPhotoUri = previousPhotoUriBeforeCapture
            renderPhotoPreview()
            Toast.makeText(this, R.string.photo_capture_failed, Toast.LENGTH_SHORT).show()
        }
        previousPhotoUriBeforeCapture = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentPhotoUri = savedInstanceState?.getString(STATE_PHOTO_URI)?.let(Uri::parse)

        setupToolbar()
        setupDropdowns()
        setupActions()
        loadRecordIfNeeded()
        renderPhotoPreview()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.toolbar.title = getString(
            if (recordId == null) R.string.editor_add_title else R.string.editor_edit_title
        )
    }

    private fun setupDropdowns() {
        binding.actvDrinkType.setAdapter(createAdapter(R.array.drink_types))
        binding.actvBrewMethod.setAdapter(createAdapter(R.array.brew_methods))
        binding.actvRating.setAdapter(createAdapter(R.array.rating_levels))
        binding.actvCupSize.setAdapter(createAdapter(R.array.cup_size_presets))

        if (recordId == null) {
            binding.actvDrinkType.setText(resources.getStringArray(R.array.drink_types).first(), false)
            binding.actvBrewMethod.setText(resources.getStringArray(R.array.brew_methods).first(), false)
            binding.actvRating.setText("4", false)
            binding.actvCupSize.setText("240", false)
        }
    }

    private fun createAdapter(arrayResId: Int): ArrayAdapter<String> {
        return ArrayAdapter(
            this,
            R.layout.item_dropdown_option,
            resources.getStringArray(arrayResId).toList()
        )
    }

    private fun setupActions() {
        binding.buttonSave.setOnClickListener {
            saveRecord()
        }
        binding.buttonDelete.setOnClickListener {
            confirmDelete()
        }
        binding.buttonTakePhoto.setOnClickListener {
            launchCamera()
        }
    }

    private fun loadRecordIfNeeded() {
        val editingId = recordId ?: return
        binding.buttonDelete.visibility = android.view.View.VISIBLE

        lifecycleScope.launch {
            currentRecord = viewModel.getRecord(editingId)
            currentRecord?.let(::populateFields)
        }
    }

    private fun populateFields(record: CoffeeRecordEntity) {
        binding.etBeanName.setText(record.beanName.orEmpty())
        binding.etRoaster.setText(record.roaster.orEmpty())
        binding.etOrigin.setText(record.origin.orEmpty())
        binding.actvDrinkType.setText(record.drinkType.orEmpty(), false)
        binding.actvBrewMethod.setText(record.brewMethod.orEmpty(), false)
        binding.actvRating.setText(record.rating.toString(), false)
        binding.actvCupSize.setText(record.cupSizeMl.toString(), false)
        binding.etPrice.setText(if (record.priceYuan > 0) record.priceYuan.toString() else "")
        binding.etNotes.setText(record.notes.orEmpty())
        currentPhotoUri = record.photoUri?.takeIf { it.isNotBlank() }?.let(Uri::parse)
        renderPhotoPreview()
    }

    private fun saveRecord() {
        val beanName = binding.etBeanName.text?.toString()?.trim().orEmpty()
        val roaster = binding.etRoaster.text?.toString()?.trim().orEmpty()
        val origin = binding.etOrigin.text?.toString()?.trim().orEmpty()
        val drinkType = binding.actvDrinkType.text?.toString()?.trim().orEmpty()
        val brewMethod = binding.actvBrewMethod.text?.toString()?.trim().orEmpty()
        val ratingText = binding.actvRating.text?.toString()?.trim().orEmpty()
        val cupSizeText = binding.actvCupSize.text?.toString()?.trim().orEmpty()
        val priceText = binding.etPrice.text?.toString()?.trim().orEmpty()
        val notes = binding.etNotes.text?.toString()?.trim().orEmpty()

        clearErrors()

        var valid = true

        if (beanName.isBlank()) {
            binding.inputBeanName.error = getString(R.string.error_required)
            valid = false
        }

        if (drinkType.isBlank()) {
            binding.inputDrinkType.error = getString(R.string.error_required)
            valid = false
        }

        if (brewMethod.isBlank()) {
            binding.inputBrewMethod.error = getString(R.string.error_required)
            valid = false
        }

        val rating = ratingText.toIntOrNull()
        if (rating == null) {
            binding.inputRating.error = getString(R.string.error_required)
            valid = false
        }

        val cupSizeMl = cupSizeText.toIntOrNull()
        if (cupSizeMl == null) {
            binding.inputCupSize.error = getString(R.string.error_number)
            valid = false
        }

        val priceYuan = when {
            priceText.isBlank() -> 0.0
            priceText.toDoubleOrNull() == null -> {
                binding.inputPrice.error = getString(R.string.error_number)
                valid = false
                0.0
            }

            else -> priceText.toDouble()
        }

        if (!valid || rating == null || cupSizeMl == null) {
            return
        }

        val record = CoffeeRecordEntity().apply {
            id = currentRecord?.id ?: 0L
            this.beanName = beanName
            this.roaster = roaster
            this.origin = origin
            this.drinkType = drinkType
            this.brewMethod = brewMethod
            this.rating = rating
            this.cupSizeMl = cupSizeMl
            this.priceYuan = priceYuan
            this.notes = notes
            this.photoUri = currentPhotoUri?.toString().orEmpty()
            this.drankAt = currentRecord?.drankAt ?: System.currentTimeMillis()
        }

        lifecycleScope.launch {
            if (!currentRecord?.photoUri.isNullOrBlank() &&
                currentRecord?.photoUri != record.photoUri
            ) {
                deletePhotoIfOwned(currentRecord?.photoUri)
            }
            viewModel.saveRecord(record)
            Toast.makeText(this@RecordEditorActivity, R.string.record_saved, Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun clearErrors() {
        binding.inputBeanName.error = null
        binding.inputDrinkType.error = null
        binding.inputBrewMethod.error = null
        binding.inputRating.error = null
        binding.inputCupSize.error = null
        binding.inputPrice.error = null
    }

    private fun confirmDelete() {
        val record = currentRecord ?: return
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.confirm_delete_title)
            .setMessage(R.string.confirm_delete_message)
            .setNegativeButton(R.string.action_cancel, null)
            .setPositiveButton(R.string.action_delete) { _, _ ->
                lifecycleScope.launch {
                    deletePhotoIfOwned(record.photoUri)
                    viewModel.deleteRecord(record)
                    Toast.makeText(
                        this@RecordEditorActivity,
                        R.string.record_deleted,
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            }
            .show()
    }

    private fun launchCamera() {
        previousPhotoUriBeforeCapture = currentPhotoUri
        val newUri = createPhotoUri()
        currentPhotoUri = newUri
        takePhotoLauncher.launch(newUri)
    }

    private fun createPhotoUri(): Uri {
        val photoDirectory = File(filesDir, PHOTO_DIRECTORY).apply {
            if (!exists()) {
                mkdirs()
            }
        }
        val photoFile = File(photoDirectory, "coffee_${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(
            this,
            "$packageName.fileprovider",
            photoFile
        )
    }

    private fun renderPhotoPreview() {
        CoffeePhotoLoader.bindThumbnail(
            imageView = binding.imagePhotoPreview,
            fallbackView = binding.tvPhotoPlaceholder,
            photoUri = currentPhotoUri?.toString(),
            fallbackText = getString(R.string.photo_placeholder)
        )
        binding.buttonTakePhoto.text = if (currentPhotoUri == null) {
            getString(R.string.action_take_photo)
        } else {
            getString(R.string.action_retake_photo)
        }
    }

    private fun deletePhotoIfOwned(photoUri: String?) {
        val path = Uri.parse(photoUri.orEmpty()).path ?: return
        if (!path.contains(PHOTO_DIRECTORY)) {
            return
        }

        val marker = "/$PHOTO_DIRECTORY/"
        val index = path.indexOf(marker)
        if (index == -1) {
            return
        }

        val fileName = path.substring(index + marker.length)
        File(File(filesDir, PHOTO_DIRECTORY), fileName).takeIf { it.exists() }?.delete()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(STATE_PHOTO_URI, currentPhotoUri?.toString())
    }

    companion object {
        private const val EXTRA_RECORD_ID = "extra_record_id"
        private const val STATE_PHOTO_URI = "state_photo_uri"
        private const val PHOTO_DIRECTORY = "coffee_photos"

        fun createIntent(context: Context, recordId: Long? = null): Intent {
            return Intent(context, RecordEditorActivity::class.java).apply {
                if (recordId != null) {
                    putExtra(EXTRA_RECORD_ID, recordId)
                }
            }
        }
    }
}

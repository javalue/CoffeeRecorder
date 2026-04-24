package com.demo.coffeerecorder

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.demo.coffeerecorder.databinding.ActivityMainBinding
import com.demo.coffeerecorder.ui.HomeFragment
import com.demo.coffeerecorder.ui.RecordsFragment
import com.demo.coffeerecorder.ui.StatsFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        setupBottomNavigation(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main_actions, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.actionAddRecord -> {
                openRecordEditor()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupBottomNavigation(savedInstanceState: Bundle?) {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menuHome -> {
                    switchTab(HomeFragment(), getString(R.string.menu_home))
                    true
                }

                R.id.menuRecords -> {
                    switchTab(RecordsFragment(), getString(R.string.menu_records))
                    true
                }

                R.id.menuStats -> {
                    switchTab(StatsFragment(), getString(R.string.menu_stats))
                    true
                }

                else -> false
            }
        }

        val initialTab = savedInstanceState?.getInt(STATE_SELECTED_TAB) ?: R.id.menuHome
        binding.bottomNavigation.selectedItemId = initialTab
    }

    fun openRecordEditor(recordId: Long? = null) {
        startActivity(RecordEditorActivity.createIntent(this, recordId))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(STATE_SELECTED_TAB, binding.bottomNavigation.selectedItemId)
    }

    private fun switchTab(fragment: Fragment, title: String) {
        supportFragmentManager.commit {
            replace(R.id.fragmentContainer, fragment)
        }
        binding.toolbar.title = title
    }

    companion object {
        private const val STATE_SELECTED_TAB = "state_selected_tab"
    }
}

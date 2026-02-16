package com.example.laba1.tarrif

import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.laba1.R
import com.example.laba1.databinding.FragmentTariffsBinding
import com.example.laba1.db.TariffsDB
import com.example.laba1.helper.Singleton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class TariffsFragment : Fragment() {

    private lateinit var binding: FragmentTariffsBinding
    private lateinit var adapter: TariffAdapter

    private var originalTariffList = listOf<Tariff>()

    private var useDatabase = true
    private var localTariffs = TariffsLocalList().listOfTariffs().toMutableList()

    private val viewModel: TariffViewModel by viewModels {
        TariffViewModelFactory(
            TariffRepository(
                TariffsDB.getDB(requireContext()).getTariffDao()
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTariffsBinding.inflate(inflater)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.top_up_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    val openJsonLauncher =
        registerForActivityResult(
            androidx.activity.result.contract.ActivityResultContracts.OpenDocument()
        ) { uri ->
            if (uri != null) {
                importTariffsFromJson(uri)
            }
        }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = TariffAdapter(
            onItemLongClick = { tariff ->
                showDeleteConfirmationDialog(tariff)
            },
            onItemClick = { tariff ->
                showEditTariffDialog(tariff)
            }
        )

        switchDataSource()

        binding.sortView.setOnClickListener { sortList() }

        binding.rvTariffs.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTariffs.adapter = adapter

        viewModel.tariffs
            .asLiveData()
            .observe(viewLifecycleOwner) { tariffs ->
                if (useDatabase) {
                    originalTariffList = tariffs
                    adapter.submitList(tariffs)
                }
            }


        binding.fabAddTariff.setOnClickListener {
            showAddTariffDialog()
        }

        binding.searchView.setOnQueryTextListener(object :
            android.widget.SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?) = false

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }
        })
    }

    private fun showDeleteConfirmationDialog(tariff: Tariff) {
        AlertDialog.Builder(requireContext())
            .setTitle("Удалить тариф")
            .setMessage("Удалить тариф '${tariff.name}'?")
            .setPositiveButton("Удалить") { _, _ ->
                if (useDatabase) {
                    viewModel.deleteTariff(tariff)
                } else {
                    localTariffs.remove(tariff)
                    adapter.submitList(localTariffs.toList())
                }

            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun switchDataSource() {
        useDatabase = !useDatabase

        if (useDatabase) {
            viewModel.tariffs.asLiveData().value?.let {
                originalTariffList = it
                adapter.submitList(it)
            }
        } else {
            originalTariffList = localTariffs
            adapter.submitList(localTariffs.toList())
        }
    }


    private fun showAddTariffDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_tariff, null)
        val etName = dialogView.findViewById<EditText>(R.id.etTariffName)
        val etPrice = dialogView.findViewById<EditText>(R.id.etTariffPrice)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Добавить тариф")
            .setView(dialogView)
            .setPositiveButton("Добавить", null)
            .setNegativeButton("Отмена", null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val name = etName.text.toString().trim()
                val priceStr = etPrice.text.toString().trim()
                val price = priceStr.toDoubleOrNull()

                when {
                    name.isEmpty() -> setError(etName, "Введите название")
                    price == null -> setError(etPrice, "Неверная цена")
                    originalTariffList.any { it.name == name } ->
                        setError(etName, "Тариф уже существует")

                    else -> {
                        if (useDatabase) {
                            viewModel.addTariff(Tariff(name = name, price = price))
                        } else {
                            localTariffs.add(Tariff(name = name, price = price))
                            adapter.submitList(localTariffs.toList()) // ВАЖНО!
                        }

                        dialog.dismiss()
                    }
                }
            }
        }
        dialog.show()
    }

    private fun showEditTariffDialog(tariff: Tariff) {
        val view = layoutInflater.inflate(R.layout.dialog_edit_tariff, null)

        val details = view.findViewById<TextView>(R.id.tvTariffDetails)
        val rg = view.findViewById<android.widget.RadioGroup>(R.id.rgDiscounts)
        val rb0 = view.findViewById<RadioButton>(R.id.rb0)
        val rb10 = view.findViewById<RadioButton>(R.id.rb10)
        val rb15 = view.findViewById<RadioButton>(R.id.rb15)
        val rb20 = view.findViewById<RadioButton>(R.id.rb20)
        val updateButton = view.findViewById<View>(R.id.btnUpdateTariff)

        details.text = "Тариф: ${tariff.name}\nЦена: ${tariff.price} руб."

        when (tariff.discount) {
            0 -> rb0.isChecked = true
            10 -> rb10.isChecked = true
            15 -> rb15.isChecked = true
            20 -> rb20.isChecked = true
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Редактировать тариф")
            .setView(view)
            .setNegativeButton("Закрыть", null)
            .create()

        updateButton.setOnClickListener {
            val discount = when (rg.checkedRadioButtonId) {
                R.id.rb10 -> 10
                R.id.rb15 -> 15
                R.id.rb20 -> 20
                else -> 0
            }
            if (useDatabase) {
                viewModel.updateDiscount(tariff.id!!, discount)
            } else {
                val index = localTariffs.indexOfFirst { it.name == tariff.name }
                if (index != -1) {
                    localTariffs[index] = localTariffs[index].copy(discount = discount)
                    adapter.submitList(localTariffs.toList())
                }
            }

            dialog.dismiss()
        }

        dialog.show()
    }

    private fun filterList(query: String?) {
        val filtered = if (query.isNullOrBlank()) {
            originalTariffList
        } else {
            val q = query.lowercase()
            originalTariffList.filter {
                it.name.lowercase().contains(q) || query == "мин"
            }.let {
                if (query == "мин") it.sortedBy { t -> t.price } else it
            }
        }
        adapter.submitList(filtered)
    }
    private fun sortList(){
    val sortedTariffs = originalTariffList.sortedBy { t -> t.name }
        adapter.submitList(sortedTariffs)
    }

    private fun setError(editText: EditText, error: String) {
        editText.error = error
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun saveTariffsToJsonMediaStore(
        tariffs: List<Tariff>
    ) {
        val gson = Gson()
        val json = gson.toJson(tariffs)

        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "tariffs.json")
            put(MediaStore.MediaColumns.MIME_TYPE, "application/json")
            put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                Environment.DIRECTORY_DOWNLOADS
            )
        }

        val uri = context?.contentResolver?.insert(
            MediaStore.Downloads.EXTERNAL_CONTENT_URI,
            values
        )

        if (uri != null) {
            context?.contentResolver?.openOutputStream(uri)?.use { outputStream ->
                OutputStreamWriter(outputStream).use { writer ->
                    writer.write(json)
                    writer.flush()
                }
            }
        }
    }

    private fun importTariffsFromJson(uri: Uri) {
        viewLifecycleOwner.lifecycleScope.launch {

            if (uri.path.isNullOrEmpty()) {
                Log.e("TariffImport", "Uri is empty")
                return@launch
            }
            Log.d("TariffImport", "Chosen URI: $uri")

            val json = try {
                requireContext().contentResolver.openInputStream(uri)?.use { inputStream ->
                    InputStreamReader(inputStream).readText()
                } ?: run {
                    Log.e("TariffImport", "Cannot open input stream")
                    return@launch
                }
            } catch (e: Exception) {
                Log.e("TariffImport", "Error reading JSON: ${e.message}")
                return@launch
            }

            if (json.isBlank()) {
                Log.e("TariffImport", "JSON content is empty")
                return@launch
            }
            Log.d("TariffImport", "JSON content: $json")

            val tariffs: List<Tariff> = try {
                val type = object : TypeToken<List<Tariff>>() {}.type
                Gson().fromJson(json, type)
            } catch (e: Exception) {
                Log.e("TariffImport", "Error parsing JSON: ${e.message}")
                return@launch
            }

            if (tariffs.isEmpty()) {
                Log.e("TariffImport", "Parsed tariff list is empty")
                return@launch
            }
            Log.d("TariffImport", "Parsed tariffs: $tariffs")

            viewModel.insertTariffsFromJson(
                tariffs
            )
            Singleton.showToast(requireContext(), "Импортировано ${tariffs.size} тарифов")
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_top_up -> {
                saveTariffsToJsonMediaStore(originalTariffList)
                true
            }

            R.id.action_import -> {

                openJsonLauncher.launch(arrayOf("*/*"))
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}

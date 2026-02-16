package com.example.laba1.main

import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.InputFilter
import android.text.Spanned
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.laba1.R
import com.example.laba1.databinding.FragmentMainBinding
import com.example.laba1.db.FirmsDB
import com.example.laba1.db.TariffsDB
import com.example.laba1.helper.Singleton
import com.example.laba1.tarrif.TariffRepository
import com.example.laba1.tarrif.TariffViewModel
import com.example.laba1.tarrif.TariffViewModelFactory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private lateinit var adapter: FirmAdapter
    private var originalFirmList = listOf<Firm>()

    private val firmViewModel: FirmViewModel by viewModels {
        FirmViewModelFactory(
            FirmRepository(
                FirmsDB.getDB(requireContext()).getDao()
            )
        )
    }

    private val tariffViewModel: TariffViewModel by viewModels {
        TariffViewModelFactory(
            TariffRepository(
                TariffsDB.getDB(requireContext()).getTariffDao()
            )
        )
    }

    val openJsonLauncher =
        registerForActivityResult(
            androidx.activity.result.contract.ActivityResultContracts.OpenDocument()
        ) { uri ->
            if (uri != null) {
                importFirmsFromJson(uri)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = FirmAdapter(
            onTonnageChanged = { _, _ -> },
            onItemLongClick = { firm ->
                showDeleteConfirmationDialog(firm)
            }
        )

        binding.rvFirms.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFirms.adapter = adapter

        firmViewModel.firms
            .asLiveData()
            .observe(viewLifecycleOwner) {
                adapter.submitList(it)
                originalFirmList = it
            }

        firmViewModel.totalRevenue
            .asLiveData()
            .observe(viewLifecycleOwner) {
                binding.tvTotalRevenue.text = "Общая выручка: $it руб."
            }

        binding.fabAdd.setOnClickListener {
            showAddFirmDialog()
        }
    }

    private fun showDeleteConfirmationDialog(firm: Firm) {
        AlertDialog.Builder(requireContext())
            .setTitle("Удалить фирму")
            .setMessage("Удалить фирму '${firm.name}'?")
            .setPositiveButton("Удалить") { _, _ ->
                firmViewModel.deleteFirm(firm)
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showAddFirmDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_add_firm, null)

        val etName = view.findViewById<EditText>(R.id.et_firm_name)
        val etTonnage = view.findViewById<EditText>(R.id.et_firm_tonnage)
        val spinner = view.findViewById<Spinner>(R.id.mySpinner)

        tariffViewModel.tariffNames
            .asLiveData()
            .observe(viewLifecycleOwner) { names ->
                spinner.adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    names
                ).apply {
                    setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }
            }


        AlertDialog.Builder(requireContext())
            .setTitle("Добавить фирму")
            .setView(view)
            .setPositiveButton("Добавить") { _, _ ->
                val name = etName.text.toString()
                val tonnage = etTonnage.text.toString().toDoubleOrNull()
                val tariff = spinner.selectedItem.toString()

                if (name.isBlank() || tonnage == null) return@setPositiveButton

                lifecycleScope.launch {
                    val price = tariffViewModel.getTariffPriceByName(tariff)

                    firmViewModel.addFirm(
                        Firm(
                            name = name,
                            tonnage = tonnage,
                            tariff = tariff,
                            tariffPrice = price!!
                        )
                    )
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun saveFirmsToJsonMediaStore(
        firms: List<Firm>
    ) {
        val gson = Gson()
        val json = gson.toJson(firms)

        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "firms.json")
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

    private fun importFirmsFromJson(uri: Uri) {
        viewLifecycleOwner.lifecycleScope.launch {

            if (uri.path.isNullOrEmpty()) {
                Log.e("FirmImport", "Uri is empty")
                return@launch
            }
            Log.d("FirmImport", "Chosen URI: $uri")

            val json = try {
                requireContext().contentResolver.openInputStream(uri)?.use { inputStream ->
                    InputStreamReader(inputStream).readText()
                } ?: run {
                    Log.e("FirmImport", "Cannot open input stream")
                    return@launch
                }
            } catch (e: Exception) {
                Log.e("FirmImport", "Error reading JSON: ${e.message}")
                return@launch
            }

            if (json.isBlank()) {
                Log.e("FirmImport", "JSON content is empty")
                return@launch
            }
            Log.d("FirmImport", "JSON content: $json")

            val firms: List<Firm> = try {
                val type = object : TypeToken<List<Firm>>() {}.type
                Gson().fromJson(json, type)
            } catch (e: Exception) {
                Log.e("FirmImport", "Error parsing JSON: ${e.message}")
                return@launch
            }

            if (firms.isEmpty()) {
                Log.e("FirmImport", "Parsed tariff list is empty")
                return@launch
            }
            Log.d("FirmImport", "Parsed tariffs: $firms")

            firmViewModel.insertFirmsFromJson(firms)
            Singleton.showToast(requireContext(), "Импортировано ${firms.size} фирм")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.top_up_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_top_up -> {
                saveFirmsToJsonMediaStore(originalFirmList)
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

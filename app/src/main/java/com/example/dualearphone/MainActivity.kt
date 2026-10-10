package com.example.dualearphone

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private lateinit var btnScan: Button
    private lateinit var tvStatus: TextView
    private lateinit var listViewDevices: ListView
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private val deviceList = ArrayList<String>()
    private val deviceAddressList = ArrayList<BluetoothDevice>()
    private lateinit var adapter: ArrayAdapter<String>
    private val connectedEarphones = mutableListOf<BluetoothDevice>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnScan = findViewById(R.id.btnScan)
        tvStatus = findViewById(R.id.tvStatus)
        listViewDevices = findViewById(R.id.listViewDevices)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, deviceList)
        listViewDevices.adapter = adapter

        btnScan.setOnClickListener { checkPermissionsAndScan() }
        listViewDevices.setOnItemClickListener { _, _, position, _ ->
            handlePairing(deviceAddressList[position])
        }
    }

    private fun checkPermissionsAndScan() {
        val perms = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            arrayOf(Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (perms.any { ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED }) {
            ActivityCompat.requestPermissions(this, perms, 100)
        } else {
            loadDevices()
        }
    }

    private fun loadDevices() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) return
        deviceList.clear()
        deviceAddressList.clear()
        bluetoothAdapter?.bondedDevices?.forEach {
            deviceList.add("${it.name}\n[${it.address}]")
            deviceAddressList.add(it)
        }
        adapter.notifyDataSetChanged()
        tvStatus.text = "Sila pilih earphone."
    }

    private fun handlePairing(device: BluetoothDevice) {
        if (connectedEarphones.size < 2 && !connectedEarphones.contains(device)) {
            connectedEarphones.add(device)
            tvStatus.text = "Dipilih: ${connectedEarphones.size}/2 (${device.name})"
            Toast.makeText(this, "Tambah: ${device.name}", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Had 2 peranti dicapai / sudah dipilih", Toast.LENGTH_SHORT).show()
        }
    }
}

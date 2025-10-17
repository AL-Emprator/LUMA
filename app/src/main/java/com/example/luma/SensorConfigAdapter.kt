package com.example.luma.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.luma.R
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText

data class SensorItem(
    val id: String,
    val name: String,
    val enabled: Boolean = false,
    val hz: Int? = null,
    val mode: String? = null,
    val batching: Boolean = false
)

class SensorConfigAdapter(
    private val modeOptions: List<String> = listOf("HIGH", "NORMAL", "LOW")
) : ListAdapter<SensorItem, SensorConfigAdapter.VH>(Diff) {

    object Diff : DiffUtil.ItemCallback<SensorItem>() {
        override fun areItemsTheSame(old: SensorItem, new: SensorItem) = old.id == new.id
        override fun areContentsTheSame(old: SensorItem, new: SensorItem) = old == new
    }

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val cbEnabled: MaterialCheckBox = view.findViewById(R.id.cb_enabled)
        val tvName = view.findViewById<com.google.android.material.textview.MaterialTextView>(R.id.tv_sensor_name)
        val etHz: TextInputEditText = view.findViewById(R.id.et_hz)
        val actvMode: MaterialAutoCompleteTextView = view.findViewById(R.id.actv_mode)
        val swBatching: SwitchMaterial = view.findViewById(R.id.switch_buffering)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sensor_config, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)

        holder.tvName.text = item.name
        holder.cbEnabled.isChecked = item.enabled
        holder.etHz.setText(item.hz?.toString().orEmpty())

        val ctx = holder.itemView.context
        holder.actvMode.setAdapter(
            ArrayAdapter(ctx, android.R.layout.simple_list_item_1, modeOptions)
        )
        holder.actvMode.setText(item.mode ?: "", false)

        holder.swBatching.isChecked = item.batching
        // no listeners yet (pure UI display)
    }
}

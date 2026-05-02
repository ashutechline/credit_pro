package com.education.creditscore.calculator.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.education.creditscore.calculator.R
import com.education.creditscore.calculator.models.*
import com.education.creditscore.calculator.services.DataService

// ── Bank Adapter ──────────────────────────────────────────────────────────────
class BankAdapter(
    private val onCall: (Bank) -> Unit
) : ListAdapter<Bank, BankAdapter.ViewHolder>(BankDiff()) {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvEmoji: TextView = view.findViewById(R.id.tvBankEmoji)
        val tvName: TextView = view.findViewById(R.id.tvBankName)
        val tvPhone: TextView = view.findViewById(R.id.tvBankPhone)
        val tvCountry: TextView = view.findViewById(R.id.tvBankCountry)
        val btnCall: View = view.findViewById(R.id.btnCall)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_bank, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bank = getItem(position)
        holder.tvEmoji.text = bank.logoEmoji
        holder.tvName.text = bank.name
        holder.tvPhone.text = bank.customerCare
        holder.tvCountry.text = bank.country
        holder.btnCall.setOnClickListener { onCall(bank) }
    }

    class BankDiff : DiffUtil.ItemCallback<Bank>() {
        override fun areItemsTheSame(old: Bank, new: Bank) = old.name == new.name
        override fun areContentsTheSame(old: Bank, new: Bank) = old == new
    }
}

// ── Calculator Section Adapter ────────────────────────────────────────────────
class CalculatorSectionAdapter(
    private val categories: List<String>,
    private val allCalculators: List<Calculator>,
    private val onClick: (Calculator) -> Unit
) : RecyclerView.Adapter<CalculatorSectionAdapter.SectionViewHolder>() {

    class SectionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCategory: TextView = view.findViewById(R.id.tvCategoryTitle)
        val rvCalcs: RecyclerView = view.findViewById(R.id.rvCategoryCalcs)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionViewHolder =
        SectionViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_calc_section, parent, false))

    override fun onBindViewHolder(holder: SectionViewHolder, position: Int) {
        val category = categories[position]
        val calcs = allCalculators.filter { it.category == category }
        holder.tvCategory.text = category
        holder.rvCalcs.layoutManager = LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)
        holder.rvCalcs.adapter = CalculatorGridAdapter(calcs, onClick)
    }

    override fun getItemCount() = categories.size
}

// ── Calculator Grid Adapter ───────────────────────────────────────────────────
class CalculatorGridAdapter(
    private val calcs: List<Calculator>,
    private val onClick: (Calculator) -> Unit
) : RecyclerView.Adapter<CalculatorGridAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvEmoji: TextView = view.findViewById(R.id.tvCalcEmoji)
        val tvName: TextView = view.findViewById(R.id.tvCalcName)
        val tvDesc: TextView = view.findViewById(R.id.tvCalcDesc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_calculator_card, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val calc = calcs[position]
        holder.tvEmoji.text = calc.emoji
        holder.tvName.text = calc.name
        holder.tvDesc.text = calc.description
        holder.itemView.setOnClickListener { onClick(calc) }
    }

    override fun getItemCount() = calcs.size
}

// ── Result Detail Adapter ─────────────────────────────────────────────────────
class ResultDetailAdapter(
    private val items: List<Pair<String, String>>
) : RecyclerView.Adapter<ResultDetailAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvLabel: TextView = view.findViewById(R.id.tvDetailLabel)
        val tvValue: TextView = view.findViewById(R.id.tvDetailValue)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_result_detail, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvLabel.text = items[position].first
        holder.tvValue.text = items[position].second
    }

    override fun getItemCount() = items.size
}

// ── Tip Adapter ───────────────────────────────────────────────────────────────
class TipAdapter(
    private val tips: List<Tip>,
    private val onClick: (Tip) -> Unit
) : RecyclerView.Adapter<TipAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvEmoji: TextView = view.findViewById(R.id.tvTipEmoji)
        val tvTitle: TextView = view.findViewById(R.id.tvTipTitle)
        val tvBody: TextView = view.findViewById(R.id.tvTipBody)
        val tvCategory: TextView = view.findViewById(R.id.tvTipCategory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_tip, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tip = tips[position]
        holder.tvEmoji.text = tip.emoji
        holder.tvTitle.text = tip.title
        holder.tvCategory.text = tip.category
        
        // Just show a snippet of the body in the list
        holder.tvBody.text = if (tip.body.length > 70) "${tip.body.take(70)}..." else tip.body
        
        holder.itemView.setOnClickListener { onClick(tip) }
    }

    override fun getItemCount() = tips.size
}

// ── FAQ Adapter ───────────────────────────────────────────────────────────────
class FaqAdapter(
    private val faqs: List<Pair<String, String>>
) : RecyclerView.Adapter<FaqAdapter.ViewHolder>() {

    private val expanded = mutableSetOf<Int>()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvQuestion: TextView = view.findViewById(R.id.tvFaqQuestion)
        val tvAnswer: TextView = view.findViewById(R.id.tvFaqAnswer)
        val ivChevron: View = view.findViewById(R.id.ivChevron)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_faq, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (q, a) = faqs[position]
        holder.tvQuestion.text = q
        val isExpanded = position in expanded
        holder.tvAnswer.visibility = if (isExpanded) View.VISIBLE else View.GONE
        holder.tvAnswer.text = a
        holder.ivChevron.rotation = if (isExpanded) 180f else 0f
        holder.itemView.setOnClickListener {
            if (isExpanded) expanded.remove(position) else expanded.add(position)
            notifyItemChanged(position)
        }
    }

    override fun getItemCount() = faqs.size
}

// ── Notification Adapter ──────────────────────────────────────────────────────
class NotificationAdapter(
    private val notifications: List<AppNotification>
) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvEmoji: TextView = view.findViewById(R.id.tvNotifEmoji)
        val tvTitle: TextView = view.findViewById(R.id.tvNotifTitle)
        val tvBody: TextView = view.findViewById(R.id.tvNotifBody)
        val tvTime: TextView = view.findViewById(R.id.tvNotifTime)
        val vUnread: View = view.findViewById(R.id.vUnreadDot)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_notification, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val n = notifications[position]
        holder.tvEmoji.text = n.emoji
        holder.tvTitle.text = n.title
        holder.tvBody.text = n.body
        holder.tvTime.text = n.timeAgo
        holder.vUnread.visibility = if (!n.isRead) View.VISIBLE else View.GONE
    }

    override fun getItemCount() = notifications.size
}

// ── Selection Adapter ─────────────────────────────────────────────────────────
class SelectionAdapter(
    private val items: List<String>,
    private var selectedItem: String,
    private val onSelected: (String) -> Unit
) : RecyclerView.Adapter<SelectionAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvOptionName)
        val ivCheck: View = view.findViewById(R.id.ivCheck)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_selection, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvName.text = item
        holder.ivCheck.visibility = if (item == selectedItem) View.VISIBLE else View.GONE
        
        holder.itemView.setOnClickListener {
            val oldPos = items.indexOf(selectedItem)
            selectedItem = item
            notifyItemChanged(oldPos)
            notifyItemChanged(position)
            onSelected(item)
        }
    }

    override fun getItemCount() = items.size
}

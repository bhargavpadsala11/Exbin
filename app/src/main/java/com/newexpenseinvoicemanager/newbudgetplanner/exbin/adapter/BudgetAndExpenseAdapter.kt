@file:Suppress("SENSELESS_COMPARISON")

package com.newexpenseinvoicemanager.newbudgetplanner.exbin.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.databinding.ItemBudgetLayoutBinding
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.roomdb.BudgetAndExpense

class BudgetAndExpenseAdapter(
    var budgetAndExpenseList: List<BudgetAndExpense>,
    private val onCardClickListener: (BudgetAndExpense, String, Boolean, String) -> Unit
) :
    RecyclerView.Adapter<BudgetAndExpenseAdapter.ViewHolder>() {


    inner class ViewHolder(val binding: ItemBudgetLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemBudgetLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.catTextView.text = budgetAndExpenseList[position].budgetCat

        val item = budgetAndExpenseList[position]
        var progress: Int? = null
        var limitShow: Boolean? = null
        val remaining: String?

        val budgetAmount = item.budget.toDoubleOrNull()
        val expenseAmount = item.amount1?.toDoubleOrNull()
        val amountOfIncExpTbl = item.budgetCat

        if (amountOfIncExpTbl != null && expenseAmount == null) {
            holder.binding.catTextView.text = item.budgetCat

            holder.binding.remainingTxt.text = "Remaining ${item.amount}"
            remaining = item.amount
        } else {
            val sum = item.amount!!.toInt() - item.amount1!!.toInt()
            holder.binding.remainingTxt.text = "Remaining ${sum}"
            remaining = sum.toString()
        }

        if (amountOfIncExpTbl != null && expenseAmount == null) {
            holder.binding.catTextView.text = item.budgetCat

            holder.binding.determinateBar.progress = 0
            holder.binding.determinateBar.progressTintList =
                ColorStateList.valueOf(Color.parseColor(item.catColor))
            val color = Color.parseColor(item.catColor)
            holder.binding.viewId1.setBackgroundColor(color)
            holder.binding.amntOfamntText.text = "00 of ${item.amount}"
            holder.binding.image.visibility = View.GONE
            holder.binding.warningTxt.visibility = View.GONE
        } else {
            if (budgetAmount != null && expenseAmount != null) {
                progress = (expenseAmount / budgetAmount * 100).toInt()
                val pro = progress
                Log.d("From Adapter", "$pro")
                holder.binding.determinateBar.progress = progress
                holder.binding.determinateBar.progressTintList =
                    ColorStateList.valueOf(Color.parseColor(item.catColor))
                val color = Color.parseColor(item.catColor)
                holder.binding.viewId1.setBackgroundColor(color)
                holder.binding.amntOfamntText.text =
                    "${item.amount1} of ${item.amount}"


                if (expenseAmount > budgetAmount) {
                    limitShow = true
                    holder.binding.image.visibility = View.VISIBLE
                    holder.binding.warningTxt.visibility = View.VISIBLE
                } else {
                    holder.binding.image.visibility = View.GONE
                    holder.binding.warningTxt.visibility = View.GONE
                    limitShow = false
                }
            }
            // Log.d("Adap posi/prog/limit","$budgetAndExpenseList[position] $progress $limitShow")
        }

        if (limitShow == null && progress == null) {
            limitShow = false
            progress = 0
        }

        Log.d(
            "Adap It/prog/limit",
            "$budgetAndExpenseList[position] $progress $limitShow"
        )
        holder.binding.budgetItemCard.setOnClickListener {
            Log.d(
                "Adap It/prog/limit",
                "$it"
            )
            onCardClickListener(
                item,
                progress.toString(),
                limitShow!!,
                remaining!!
            )


        }

    }

    override fun getItemCount(): Int {
        return budgetAndExpenseList.size
    }
}
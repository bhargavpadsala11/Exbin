package com.newexpenseinvoicemanager.newbudgetplanner.exbin.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.databinding.ItemBudgetLayoutBinding
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.roomdb.BudgetAndExpense

class BudgetAndExpenseAdapter(var budgetAndExpenseList: List<BudgetAndExpense>) :
    RecyclerView.Adapter<BudgetAndExpenseAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemBudgetLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(budgetAndExpense: BudgetAndExpense) {
            //   binding.catTextView.text = budgetAndExpense.budgetCat
            binding.catTextView.text = budgetAndExpense.category

            val budgetAmount = budgetAndExpense.budget.toDoubleOrNull()
            val expenseAmount = budgetAndExpense.amount1?.toDoubleOrNull()
            val amountOfIncExpTbl = budgetAndExpense.budgetCat
            if (amountOfIncExpTbl != null) {
                binding.catTextView.text = budgetAndExpense.category

                binding.remainingTxt.text = "Remaining ${budgetAndExpense.amount}"
            } else {
                var sum = budgetAndExpense.amount!!.toInt() - budgetAndExpense.amount1!!.toInt()
                binding.remainingTxt.text = "Remaining ${sum}"
            }
            if (amountOfIncExpTbl != null) {
                binding.catTextView.text = budgetAndExpense.category

                binding.determinateBar.progress = 0
                binding.determinateBar.progressTintList =
                    ColorStateList.valueOf(Color.parseColor(budgetAndExpense.catColor))
                val color = Color.parseColor(budgetAndExpense.catColor)
                binding.viewId1.setBackgroundColor(color)
                binding.amntOfamntText.text = "00 of ${budgetAndExpense.amount}"
                binding.image.visibility = View.GONE
                binding.warningTxt.visibility = View.GONE
            } else {
                if (budgetAmount != null && expenseAmount != null) {
                    val progress = (expenseAmount / budgetAmount * 100).toInt()
                    binding.determinateBar.progress = progress
                    binding.determinateBar.progressTintList =
                        ColorStateList.valueOf(Color.parseColor(budgetAndExpense.catColor))
                    val color = Color.parseColor(budgetAndExpense.catColor)
                    binding.viewId1.setBackgroundColor(color)
                    binding.amntOfamntText.text =
                        "${budgetAndExpense.amount1} of ${budgetAndExpense.amount}"


                    if (expenseAmount > budgetAmount) {
                        binding.image.visibility = View.VISIBLE
                        binding.warningTxt.visibility = View.VISIBLE
                    } else {
                        binding.image.visibility = View.GONE
                        binding.warningTxt.visibility = View.GONE
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemBudgetLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(budgetAndExpenseList[position])
    }

    override fun getItemCount(): Int {
        return budgetAndExpenseList.size
    }
}
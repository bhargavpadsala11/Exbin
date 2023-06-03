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
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.roomdb.BudgetDb
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.roomdb.PaymentModes

class BudgetAndExpenseAdapter(
    var budgetAndExpenseList: List<BudgetAndExpense>,
    private val onCardClickListener: (BudgetAndExpense, String, Boolean,String) -> Unit
) :
    RecyclerView.Adapter<BudgetAndExpenseAdapter.ViewHolder>() {


    inner class ViewHolder(val binding: ItemBudgetLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(budgetAndExpense: BudgetAndExpense) {
            //   binding.catTextView.text = budgetAndExpense.budgetCat
            var progress: Int? = null
            var limitShow: Boolean? = null
            var remaining: String? =""

            val budgetAmount = budgetAndExpense.budget.toDoubleOrNull()
            val expenseAmount = budgetAndExpense.amount1?.toDoubleOrNull()
            val amountOfIncExpTbl = budgetAndExpense.budgetCat
            if (amountOfIncExpTbl != null && expenseAmount == null) {
                binding.catTextView.text = budgetAndExpense.budgetCat

                binding.remainingTxt.text = "Remaining ${budgetAndExpense.amount}"
                remaining = budgetAndExpense.amount
            } else {
                var sum = budgetAndExpense.amount!!.toInt() - budgetAndExpense.amount1!!.toInt()
                binding.remainingTxt.text = "Remaining ${sum}"
                remaining =sum.toString()
            }
            if (amountOfIncExpTbl != null && expenseAmount == null) {
                binding.catTextView.text = budgetAndExpense.budgetCat

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
                    progress = (expenseAmount / budgetAmount * 100).toInt()
                    val pro = progress
                    Log.d("From Adapter", "$pro")
                    binding.determinateBar.progress = progress!!
                    binding.determinateBar.progressTintList =
                        ColorStateList.valueOf(Color.parseColor(budgetAndExpense.catColor))
                    val color = Color.parseColor(budgetAndExpense.catColor)
                    binding.viewId1.setBackgroundColor(color)
                    binding.amntOfamntText.text =
                        "${budgetAndExpense.amount1} of ${budgetAndExpense.amount}"


                    if (expenseAmount > budgetAmount) {
                        limitShow = true
                        binding.image.visibility = View.VISIBLE
                        binding.warningTxt.visibility = View.VISIBLE
                    } else {
                        binding.image.visibility = View.GONE
                        binding.warningTxt.visibility = View.GONE
                        limitShow = false
                    }
                }
                Log.d("Adap posi/prog/limit","$budgetAndExpenseList[position] $progress $limitShow")
            }
            if (limitShow == null && progress == null) {
                limitShow = false
                progress = 0
            }

            binding.budgetItemCard.setOnClickListener {
                onCardClickListener(
                    budgetAndExpenseList[position],
                    progress.toString(),
                    limitShow!!,
                    remaining!!
                )
                Log.d("Adap It/prog/limit","$budgetAndExpenseList[position] $it $progress $limitShow")

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
        holder.binding.catTextView.text = budgetAndExpenseList[position].budgetCat

    }

    override fun getItemCount(): Int {
        return budgetAndExpenseList.size
    }
}
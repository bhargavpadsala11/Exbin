package com.newexpenseinvoicemanager.newbudgetplanner.exbin.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.R
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.dataBase.AppDataBase
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.databinding.PaymentModeItemLayoutBinding
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.roomdb.PaymentModes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PaymentModesAdapter(
    val context: Context,
    val list: List<PaymentModes>,
    val mainLayout: View
) :
    RecyclerView.Adapter<PaymentModesAdapter.PaymentModeViewHolder>() {


    inner class PaymentModeViewHolder(val binding: PaymentModeItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentModeViewHolder {
        val binding =
            PaymentModeItemLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return PaymentModeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PaymentModeViewHolder, position: Int) {
        holder.binding.pmntdtextview.text = list[position].paymentMode
        //val dao = AppDataBase.getInstance(context).paymentModesDao()
        holder.binding.root.setOnClickListener {
            val cardView = mainLayout.findViewById<MaterialCardView>(R.id.addcardview)
            cardView.visibility = View.VISIBLE
            val cardTextView = mainLayout.findViewById<TextInputEditText>(R.id.addPaymet)

            cardTextView.setText(list[position].paymentMode)
            cardTextView.requestFocus()
            val cardViewButtonApply = mainLayout.findViewById<Button>(R.id.btnapply)
            cardViewButtonApply.setOnClickListener {
                val newPaymentMode = cardTextView.text.toString()
                updatePaymentMode(list[position].paymentModeId, newPaymentMode)
                list[position].paymentMode = newPaymentMode
                notifyItemChanged(position)
                clearText(cardTextView)
                hideFragment(cardView)

            }


            val cardViewButtonCancel = mainLayout.findViewById<Button>(R.id.btncancel)
            cardViewButtonCancel.setOnClickListener {
                clearText(cardTextView)
                hideFragment(cardView)
            }
        }
    }


//    override fun onBindViewHolder(holder: PaymentModeViewHolder, position: Int) {
//        holder.binding.pmntdtextview.text = list[position].paymentMode
//        val dao = AppDataBase.getInstance(context).paymentModesDao()
//        holder.binding.btneditmode.setOnClickListener {
//            val cardView = mainLayout.findViewById<MaterialCardView>(R.id.addcardview)
//            cardView.visibility = View.VISIBLE
//            val cardTextView = mainLayout.findViewById<TextInputEditText>(R.id.addPaymet)
//
//            textInputEditText.setText(list[position].paymentMode)
//            textInputEditText.requestFocus()
//            val cardViewButtonApply = mainLayout.findViewById<Button>(R.id.btnapply)
//            cardViewButtonApply.setOnClickListener {
//                cardTextView.text.toString()
//                Toast.makeText(context, "+$cardTextView+", Toast.LENGTH_SHORT).show()
//                updatePaymentMode(
//                    list[position].paymentModeId,
//                    textInputEditText.getText().toString()
//                )
//                clearText(textInputEditText)
//                hideFragment(cardView)
//
//
//                val cardViewButtonCancel = mainLayout.findViewById<Button>(R.id.btncancel)
//                cardViewButtonCancel.setOnClickListener {
//                    clearText(textInputEditText)
//                    hideFragment(cardView)
//                }
//            }
//        }
//    }


    override fun getItemCount(): Int {
        return list.size
    }

    private fun updatePaymentMode(id: Int, addPaymet: String) {
        val db = AppDataBase.getInstance(context).paymentModesDao()
        GlobalScope.launch(Dispatchers.IO) {
            db.updatePaymentMode(id, addPaymet)
        }
    }

    private fun clearText(addPaymet: TextInputEditText) {
        addPaymet.setText("")
    }

    private fun hideFragment(addcardview: MaterialCardView) {
        addcardview.visibility = View.GONE
    }

}

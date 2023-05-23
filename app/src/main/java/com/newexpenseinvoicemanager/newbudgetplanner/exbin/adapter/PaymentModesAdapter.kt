package com.newexpenseinvoicemanager.newbudgetplanner.exbin.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.R
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.dataBase.AppDataBase
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.databinding.PaymentModeItemLayoutBinding
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.fragments.BudgetFragment
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.fragments.HomeFragment
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.roomdb.PaymentModes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PaymentModesAdapter(
    val context: Context,
    val list: List<PaymentModes>,
    val paymentModeItemLayout: View,
    private val listener: FragmentCallListener
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

        if (list[position].paymentModeId <= 6) {
            holder.binding.btnDelete.visibility = View.GONE
            holder.binding.btneditmode.visibility = View.GONE
        } else {
            holder.binding.btnDelete.visibility = View.VISIBLE
            holder.binding.btneditmode.visibility = View.VISIBLE
        }
        holder.binding.btneditmode.setOnClickListener {
//            paymentModeItemLayout.visibility = View.VISIBLE
            //val btnApply = paymentModeItemLayout.findViewById<MaterialButton>(R.id.btnapply)
           // Toast.makeText(context, "button pressing", Toast.LENGTH_SHORT).show()
            listener.callOtherLayoutFile()

            // btnApply.setOnClickListener {

            //val txtPaymentMode = paymentModeItemLayout.findViewById<AppCompatTextView>(R.id.pmntdtextview)

//                if (txtPaymentMode.text.isEmpty()){
//                    Toast.makeText(context, "Please add mode", Toast.LENGTH_SHORT).show()
//                }else{
//                    val dao = AppDataBase.getInstance(context).paymentModesDao()
//                    GlobalScope.launch(Dispatchers.IO) {
//                        dao.updatePaymentMode(list[position].paymentModeId,txtPaymentMode.text.toString())
//                        paymentModeItemLayout.visibility = View.GONE
//                        notifyDataSetChanged()
//
//                    }
//                    //dao.updatePaymentMode(list[position].paymentModeId,txtPaymentMode.text.toString())
//                }
            //}
        }

    }


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
interface FragmentCallListener {
    fun callOtherLayoutFile()
}
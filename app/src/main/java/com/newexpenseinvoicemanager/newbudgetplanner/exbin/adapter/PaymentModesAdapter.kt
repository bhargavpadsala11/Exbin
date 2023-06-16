package com.newexpenseinvoicemanager.newbudgetplanner.exbin.adapter


import android.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.dataBase.AppDataBase
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.databinding.PaymentModeItemLayoutBinding
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.fragments.HomeFragment
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.roomdb.PaymentModes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch


class PaymentModesAdapter(
    val context: Context,
    val list: List<PaymentModes>,
    private val onImageClickListener: (PaymentModes,String) -> Unit
) :
    RecyclerView.Adapter<PaymentModesAdapter.PaymentModeViewHolder>() {
    private val inflater = LayoutInflater.from(context)

    inner class PaymentModeViewHolder(val binding: PaymentModeItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentModeViewHolder {
        val binding =
            PaymentModeItemLayoutBinding.inflate(LayoutInflater.from(context), parent, false)


        return PaymentModeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PaymentModeViewHolder, position: Int) {
       // notifyDataSetChanged()
        holder.binding.pmntdtextview.text = list[position].paymentMode

        if (list[position].paymentModeId <= 6) {
            holder.binding.btnDelete.visibility = View.GONE
            holder.binding.btneditmode.visibility = View.GONE
        } else {
            holder.binding.btnDelete.visibility = View.VISIBLE
            holder.binding.btneditmode.visibility = View.VISIBLE
        }
        holder.binding.btneditmode.setOnClickListener {
            onImageClickListener(list[position],"EDIT")
        }
        holder.binding.btnDelete.setOnClickListener {
            onImageClickListener(list[position],"DELETE")
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

//interface FragmentCallListener {
//    fun openDialog()
//}

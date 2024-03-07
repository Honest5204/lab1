package com.example.reliatimefirebase

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.reliatimefirebase.databinding.ItemUserBinding


class UserAdapter(
    private val context:Context,
    private val list :ArrayList<Users>,
    private val onUpdateClickListener: (Users) -> Unit,
    private val onDeleteClickListener: (Users) -> Unit
): RecyclerView.Adapter<UserAdapter.ViewHolDer>() {
    private lateinit var binding: ItemUserBinding
    inner class ViewHolDer(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolDer {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_user,parent,false)
        return ViewHolDer(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolDer, position: Int) {
        binding = ItemUserBinding.bind(holder.itemView)
        val curen = list[position]
        binding.txtId.text = "ID:"+curen.id
        binding.txtName.text = "Tên người dùng:"+curen.name
        binding.txtEmail.text = "Email:"+curen.email
        binding.txtPhone.text = "SĐT:"+curen.phone
        binding.btnEdit.setOnClickListener {
            onUpdateClickListener(curen)
        }
        binding.btnDelete.setOnClickListener {
            onDeleteClickListener(curen)
        }
    }
}
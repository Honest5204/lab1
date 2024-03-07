package com.example.reliatimefirebase

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reliatimefirebase.databinding.ActivityMainBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: UserAdapter
    private var list = ArrayList<Users>()
    private lateinit var firebaseRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FirebaseApp.initializeApp(this)
        adapter = UserAdapter(this, list, onUpdateClickListener = { user -> updateUser(user) },
            onDeleteClickListener = { user -> deleteUser(user) })
        firebaseRef =
            FirebaseDatabase.getInstance("https://crudapi-94148-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Users")
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
        firebaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()

                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(Users::class.java)
                    user?.let {
                        list.add(it)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Xử lý khi có lỗi xảy ra
            }
        })

        binding.btnadd.setOnClickListener {
            showDialogAdd()
        }
    }

    private fun deleteUser(user: Users) {
        // Xác nhận xóa
        val deleteDialog = AlertDialog.Builder(this)
        deleteDialog.setTitle("Delete User")
        deleteDialog.setMessage("Bạn có chắc chắn muốn xóa người dùng này?")

        deleteDialog.setPositiveButton("Delete") { _, _ ->
            // Xóa dữ liệu trên Firebase
            firebaseRef.child(user.id).removeValue()
        }

        deleteDialog.setNegativeButton("Cancel") { _, _ ->
            // Hủy bỏ việc xóa
        }

        deleteDialog.show()
    }

    private fun updateUser(user: Users) {
        val updateDialog = AlertDialog.Builder(this)
        val updateDialogView = layoutInflater.inflate(R.layout.dialog_update_user, null)
        val etUpdatedName = updateDialogView.findViewById<EditText>(R.id.etUpdatedName)
        val etUpdatedEmail = updateDialogView.findViewById<EditText>(R.id.etUpdatedEmail)
        val etUpdatedPhone = updateDialogView.findViewById<EditText>(R.id.etUpdatedPhone)

        etUpdatedName.setText(user.name)
        etUpdatedEmail.setText(user.email)
        etUpdatedPhone.setText(user.phone)

        updateDialog.setView(updateDialogView)
        updateDialog.setTitle("Update User")

        updateDialog.setPositiveButton("Update") { _, _ ->
            val updatedName = etUpdatedName.text.toString()
            val updatedEmail = etUpdatedEmail.text.toString()
            val updatedPhone = etUpdatedPhone.text.toString()
            val updatedUser = Users(user.id, updatedName, updatedEmail, updatedPhone)
            firebaseRef.child(user.id).setValue(updatedUser)
        }

        updateDialog.setNegativeButton("Cancel") { _, _ ->
            // Hủy bỏ việc cập nhật
        }

        updateDialog.show()
    }

    private fun showDialogAdd() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Thêm Người Dùng")

        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_add_user, null)
        dialogBuilder.setView(dialogView)

        val edtName = dialogView.findViewById<EditText>(R.id.edtName)
        val edtEmail = dialogView.findViewById<EditText>(R.id.edtEmail)
        val edtPhone = dialogView.findViewById<EditText>(R.id.edtPhone)
        dialogBuilder.setPositiveButton("Thêm") { _, _ ->
            val name = edtName.text.toString()
            val email = edtEmail.text.toString()
            val phone = edtPhone.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty() && phone.isNotEmpty()) {
                addUserToFirebase(name, email, phone)
            }
        }

        dialogBuilder.setNegativeButton("Hủy") { _, _ ->
            // Đóng dialog khi người dùng nhấn Hủy
        }

        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }

    private fun addUserToFirebase(name: String, email: String, phone: String) {
        // Tạo ID ngẫu nhiên cho người dùng mới
        val userId = (list.size + 1).toString()

        // Tạo đối tượng Users mới
        val newUser = Users(userId, name, email, phone)

        // Thêm người dùng vào Firebase
        userId?.let {
            firebaseRef.child(it).setValue(newUser)
        }
    }
}
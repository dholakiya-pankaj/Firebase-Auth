package com.sagauxassignment.ui.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.sagauxassignment.R
import com.sagauxassignment.databinding.ActivityUserListBinding
import com.sagauxassignment.ui.adapter.UserListAdapter
import com.sagauxassignment.ui.viewmodel.UserListViewModel
import com.sagauxassignment.util.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserListActivity : AppCompatActivity() {

    private var binding: ActivityUserListBinding? = null
    private val userListViewModel: UserListViewModel by viewModels()
    private val userListAdapter: UserListAdapter by lazy {
        UserListAdapter {
            // TODO: Handle click event of the item view
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this , R.layout.activity_user_list)

        binding?.usersListToolbar?.tvTitle?.text = getString(R.string.users_list)
        setUpObserver()
        setUpRecyclerView()
        userListViewModel.getUserList()
    }

    private fun setUpObserver() {
        userListViewModel.showLoading.observe(this) {
            binding?.loader?.isVisible = it
        }

        userListViewModel.userList.observe(this) {
            userListAdapter.setUserList(it?.usersList)
        }

        userListViewModel.errorMessage.observe(this) {
            val errorMessage = it.ifEmpty { "Something went wrong" }
            showToast(errorMessage)
        }
    }

    private fun setUpRecyclerView() {
        binding?.run {
            rvUserList.layoutManager = LinearLayoutManager(this@UserListActivity)
            rvUserList.adapter = userListAdapter
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if(binding != null)
            binding = null
    }
}
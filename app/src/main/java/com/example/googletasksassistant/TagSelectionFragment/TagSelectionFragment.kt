package com.example.googletasksassistant.TagSelectionFragment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.fragment.app.viewModels
import com.example.googletasksassistant.R
import com.example.googletasksassistant.TaskItemAdapter
import com.example.googletasksassistant.TaskTagAdapter
import com.example.googletasksassistant.TodoApplication
import com.example.googletasksassistant.databinding.FragmentMainTaskListBinding
import com.example.googletasksassistant.databinding.FragmentTagSelectionBinding
import com.example.googletasksassistant.models.TaskItem
import com.example.googletasksassistant.models.TaskTag

class TagSelectionFragment(var taskItem: TaskItem) : DialogFragment(), TaskTagClickListener {


    private var _binding: FragmentTagSelectionBinding? = null
    private val binding get() = _binding!!

    private val tagSelectionViewModel: TagSelectionViewModel by viewModels{
        TagSelectionViewModelFactory((requireActivity().application as TodoApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTagSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRecyclerView()
    }

    private fun setRecyclerView() {
        var tagSelectionFragment = this
        tagSelectionViewModel.taskTags.observe(viewLifecycleOwner) {
            binding.tagRecyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = TaskTagAdapter(it, tagSelectionFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear the binding reference to avoid memory leaks
    }

    override fun toggleSelectTaskTag(taskTag: TaskTag) {
        TODO("Not yet implemented")
    }
}

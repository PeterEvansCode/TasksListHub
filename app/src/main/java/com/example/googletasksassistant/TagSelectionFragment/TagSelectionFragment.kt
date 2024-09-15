package com.example.googletasksassistant.TagSelectionFragment

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.fragment.app.viewModels
import com.example.googletasksassistant.NewTagSheet
import com.example.googletasksassistant.NewTaskSheet
import com.example.googletasksassistant.TaskTagAdapter
import com.example.googletasksassistant.TodoApplication
import com.example.googletasksassistant.databinding.FragmentTagSelectionBinding
import com.example.googletasksassistant.models.TaskItem
import com.example.googletasksassistant.models.TaskTag
import com.example.googletasksassistant.models.taskStores.HashOnID

class TagSelectionFragment(var taskItem: TaskItem) : DialogFragment(), ITaskTagClickListener
{
    //binding
    private var _binding: FragmentTagSelectionBinding? = null

    //to store new tags
    private val _tagsToAdd: HashOnID<TaskTag> = HashOnID()
    private val _tagsToRemove: HashOnID<TaskTag> = HashOnID()

    private val tagSelectionViewModel: TagSelectionViewModel by viewModels{
        TagSelectionViewModelFactory((requireActivity().application as TodoApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTagSelectionBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.apply{
            setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        }
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding!!.tagBackButton.setOnClickListener {
            // Close the dialog
            dismiss()
        }

        _binding!!.newTagButton.setOnClickListener {
            // Display sheet to create new tag
            NewTagSheet(null).show(parentFragmentManager, "newTaskTag")
        }

        setRecyclerView()
    }

    private fun setRecyclerView() {
        var tagSelectionFragment = this
        tagSelectionViewModel.taskTags.observe(viewLifecycleOwner) {
            _binding!!.tagRecyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = TaskTagAdapter(it, tagSelectionFragment)
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        //save changes
        tagSelectionViewModel.addTagsToTask(taskItem, _tagsToAdd.values.toList())
        tagSelectionViewModel.removeTagsFromTask(taskItem, _tagsToRemove.values.toList())
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // Clear the binding reference to avoid memory leaks
        _binding = null
    }

    //TaskTagClickListener methods
    override fun selectTaskTag(taskTag: TaskTag) {
        //only add taskTag to the add tag list if it isn't in the remove tag list
        if (_tagsToRemove.existId(taskTag.id)) _tagsToRemove.removeRecord(taskTag)
        else _tagsToAdd.addRecord(taskTag)
    }

    override fun deselectTaskTag(taskTag: TaskTag) {
        //only add taskTag to the remove tag list if it isn't in the add tag list
        if (_tagsToAdd.existId(taskTag.id)) _tagsToAdd.removeRecord(taskTag)
        else _tagsToRemove.addRecord(taskTag)
    }
}

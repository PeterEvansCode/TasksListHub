package com.example.googletasksassistant.TagSelectionFragment

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.fragment.app.viewModels
import com.example.googletasksassistant.NewTagSheet
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
        TagSelectionViewModel.TagSelectionViewModelFactory((requireActivity().application as TodoApplication).repository)
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

        // search bar
        _binding!!.tagSearchBar.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                tagSelectionViewModel.searchForTags(_binding!!.tagSearchBar.text.toString())
            }

            //necessary overrides (no functionality)
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        })

        setRecyclerView()
    }

    private fun setRecyclerView() {
        var tagSelectionFragment = this
        tagSelectionViewModel.taskTags.observe(viewLifecycleOwner) { allTags ->
            //sort tags first by association, then by name
            val taskAssociatedTags = allTags.filter { taskItem.tags.containsKey(it.id) }
                .sortedBy { it.name }
            val otherTags = allTags.filter { !taskItem.tags.containsKey(it.id) }
                .sortedBy { it.name }
            val sortedTags = taskAssociatedTags + otherTags

            _binding!!.tagRecyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = TaskTagAdapter(sortedTags, taskAssociatedTags.size, tagSelectionFragment)
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
        if (_tagsToRemove.existId(taskTag.id)) _tagsToRemove.remove(taskTag)
        else _tagsToAdd.add(taskTag)
    }

    override fun deselectTaskTag(taskTag: TaskTag) {
        //only add taskTag to the remove tag list if it isn't in the add tag list
        if (_tagsToAdd.existId(taskTag.id)) _tagsToAdd.remove(taskTag)
        else _tagsToRemove.add(taskTag)
    }
}

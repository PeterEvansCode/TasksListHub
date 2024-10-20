package com.example.taskslisthub.TagSelectionFragment

import android.app.Dialog
import android.content.DialogInterface
import android.content.res.Resources
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskslisthub.NewTagSheet
import com.example.taskslisthub.R
import com.example.taskslisthub.TaskTagAdapter
import com.example.taskslisthub.TasksListHub
import com.example.taskslisthub.databinding.FragmentTagSelectionBinding
import com.example.taskslisthub.models.TaskItem
import com.example.taskslisthub.models.TaskTag
import com.example.taskslisthub.models.taskStores.HashOnID
import com.example.taskslisthub.models.taskStores.TaskItemStore


class TagSelectionFragment(var taskItem: TaskItem) : DialogFragment(), ITaskTagClickListener
{
    //binding
    private var _binding: FragmentTagSelectionBinding? = null

    //to store new tags
    private val _tagsToAdd: HashOnID<TaskTag> = HashOnID()
    private val _tagsToRemove: HashOnID<TaskTag> = HashOnID()

    private val tagSelectionViewModel: TagSelectionViewModel by viewModels{
        TagSelectionViewModel.TagSelectionViewModelFactory((requireActivity().application as TasksListHub).repository)
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

        dialog.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setWindowAnimations(android.R.style.Animation_Dialog) // Optional for animation
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

    override fun onResume() {
        super.onResume()
        val width = getScreenWidth()
        val height = getScreenHeight()

        val dialogWidth = (width * 0.8).toInt()
        val dialogHeight = (height * 0.8).toInt()

        dialog!!.window!!.setLayout(dialogWidth, dialogHeight)
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

    override fun openMenu(view: View, taskTag: TaskTag) {

        // Create a PopupMenu
        val popupMenu = PopupMenu(requireContext(), view)
        // Inflate the menu resource
        popupMenu.menuInflater.inflate(R.menu.tag_popup, popupMenu.menu)
        // Set a click listener for menu items
        popupMenu.setOnMenuItemClickListener { item ->
            var returnVal = false
            when (item.itemId) {
                R.id.delete_button -> {
                    //remove tag references
                    if(_tagsToRemove.containsKey(taskTag.id)) _tagsToRemove.remove(taskTag)
                    else if(_tagsToAdd.containsKey(taskTag.id)) _tagsToAdd.remove(taskTag)

                    //delete tag
                    tagSelectionViewModel.deleteTaskTag(taskTag)

                    //successfully completed
                    returnVal = true
                }

                R.id.edit_button -> {
                    // Display sheet to edit tag
                    NewTagSheet(taskTag).show(parentFragmentManager, "editTaskTag")

                    returnVal = true
                }

                else -> returnVal = false
            }
            returnVal
        }
        // Show the menu
        popupMenu.show()
    }


    //screen dimensions
    private fun getScreenWidth(): Int {
        return Resources.getSystem().displayMetrics.widthPixels
    }

    private fun getScreenHeight(): Int {
        return Resources.getSystem().displayMetrics.heightPixels
    }

}

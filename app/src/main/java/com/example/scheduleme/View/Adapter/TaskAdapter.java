package com.example.scheduleme.View.Adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scheduleme.R;
import com.example.scheduleme.View.Interface.OnItemClickListener;
import com.example.scheduleme.Service.Model.Task;

import java.util.List;


public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<Task> tasks;
    private static OnItemClickListener listener;
    public TaskAdapter(List<Task> tasks,  OnItemClickListener listener) {
        this.tasks = tasks;
        this.listener = listener;
    }

    //responsible for inflating the layout for each item in the RecyclerView
    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(itemView);
    }

    /**called for each item in the list and responsible for binding the data to the views in the item layout.
     * It also sets an OnClickListener on the item view and the checkbox.*/
    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Task task = tasks.get(position);
        holder.taskNameTextView.setText(task.getTitle());
        holder.taskDesc.setText(task.getDescription());
        holder.dueTimeTextView.setText(task.getDueDate());
        // holder.checkBox.setChecked(task.isChecked());

        //Here, we set an OnClickListener on the item view, and call the onItemClick method of the listener when the view is clicked.
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, position);
            }
        });
        //Here, we set an OnClickListener on the checkbox, and call the onItemClick method of the listener when the checkbox is clicked.
        holder.bind(task, listener);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public void addTask(Task task) {
        tasks.add(task);
        notifyItemInserted(tasks.size() - 1);
    }

    public void setTasks(List<Task> tasks) {
    }
    // it holds references to the views in the item layout.
    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskNameTextView;
        TextView taskDesc;
        TextView dueTimeTextView;
        CheckBox checkBox;
        TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskNameTextView = itemView.findViewById(R.id.task_name);
            taskDesc= itemView.findViewById(R.id.task_desc);
            dueTimeTextView = itemView.findViewById(R.id.due_time);
            checkBox = itemView.findViewById(R.id.task_checkbox);

            //adding long click listener to item view
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return listener.onItemLongClick(itemView, getAdapterPosition());
                }
            });
        }

        //checkbox state and checkbox listener
        public void bind(Task task, OnItemClickListener listener) {
            checkBox.setChecked(task.isChecked());
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                listener.onCheckboxClick(itemView, getAdapterPosition(), isChecked);
            });
        }

    }
}



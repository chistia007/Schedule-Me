package com.example.scheduleme.View.Interface;

import android.view.View;

public interface OnItemClickListener {
    void onItemClick(View view, int position);

    void onCheckboxClick(View view, int position, boolean isChecked);
    boolean onItemLongClick(View view, int position);
}
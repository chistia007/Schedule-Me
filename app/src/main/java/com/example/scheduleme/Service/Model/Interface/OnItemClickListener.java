package com.example.scheduleme.Service.Model.Interface;

import android.view.View;

public interface OnItemClickListener {
    void onItemClick(View view, int position);

    void onCheckboxClick(View view, int position, boolean isChecked);
}
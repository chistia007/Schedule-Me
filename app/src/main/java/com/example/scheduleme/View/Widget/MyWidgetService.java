package com.example.scheduleme.View.Widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

import java.util.List;
public class MyWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new MyWidgetFactory(getApplicationContext());
    }
}
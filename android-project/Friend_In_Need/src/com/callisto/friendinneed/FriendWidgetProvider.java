package com.callisto.friendinneed;

import com.callisto.friendinneed.R;
import com.callisto.friendinneed.service.SensorService;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

public class FriendWidgetProvider extends AppWidgetProvider{

	@Override
	public void onUpdate(final Context context, final AppWidgetManager appWidgetManager,
			final int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		context.startService(new Intent(context, SensorService.class));
	}
	
}

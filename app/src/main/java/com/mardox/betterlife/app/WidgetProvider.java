package com.mardox.betterlife.app;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

/**
 * Created by HooMan on 28/03/2014.
 */
public class WidgetProvider extends AppWidgetProvider {



    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];

            SharedPreferences storage = context.getSharedPreferences(HomeActivity.PREFS_NAME, context.MODE_MULTI_PROCESS);
            String title = storage.getString("todayConceptTitle", "Hello");
            String description = storage.getString("todayConceptDescription", "Main Subtitle");

            // Create an Intent to launch ExampleActivity
            Intent intent = new Intent(context, HomeActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            // Get the layout for the App Widget and attach an on-click listener
            // to the button
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            views.setOnClickPendingIntent(R.id.widget_main_layout, pendingIntent);
            views.setTextViewText(R.id.widget_main, title);

            //Change the widget text color randomly
//            Random rnd = new Random();
//            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
//            views.setTextColor(R.id.widget_main,color);


//            views.setTextViewText(R.id.widget_sub, description);

            //Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

}

package com.lusle.android.soon.Util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.animation.Transformation;
import android.widget.FrameLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lusle.android.soon.Adapter.BaseRecyclerAdapter;
import com.lusle.android.soon.Adapter.ManageCompanyListAdapter;
import com.lusle.android.soon.Model.Schema.Company;
import com.lusle.android.soon.R;
import com.mukesh.countrypicker.Country;
import com.mukesh.countrypicker.CountryPicker;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;

import androidx.recyclerview.widget.RecyclerView;

import static android.content.Context.MODE_PRIVATE;

public class Util {

    public static boolean bindingData(Context context, RecyclerView recyclerView, String what) {
        Runnable runnable;
        switch (what) {
            case "FavoriteMore":
                runnable = () -> {
                    SharedPreferences pref = context.getSharedPreferences("pref", MODE_PRIVATE);
                    String list = pref.getString("favorite_company", "");
                    Type type = new TypeToken<ArrayList<Company>>() {
                    }.getType();
                    ArrayList<Company> companyList = new Gson().fromJson(list, type);
                    ((ManageCompanyListAdapter) recyclerView.getAdapter()).setList(companyList);
                };
                break;
            default:
                return false;
        }

        Thread bindingThread = new Thread(runnable);
        bindingThread.start();
        try {
            bindingThread.join();
            ((Activity) context).runOnUiThread(() -> {
                //TODO:ProgressBar
                runLayoutAnimation(recyclerView);
            });
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void runLayoutAnimation(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
        if (recyclerView.getAdapter().getItemCount() == 0)
            ((BaseRecyclerAdapter) recyclerView.getAdapter()).onEmpty();
        else ((BaseRecyclerAdapter) recyclerView.getAdapter()).onNotEmpty();
    }

    public static void runLayoutAnimation(final RecyclerView recyclerView, int position) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyItemInserted(position);
        recyclerView.scheduleLayoutAnimation();
    }

    public static int calDDay(Calendar calendar) {
        try {
            Calendar today = Calendar.getInstance();
            Calendar dday = calendar;
            long day = dday.getTimeInMillis() / 86400000;
            long tday = today.getTimeInMillis() / 86400000;
            long count = day - tday;
            return (int) count + 1;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static void expand(final View v) {
        v.measure(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? FrameLayout.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.getLayoutParams().height = FrameLayout.LayoutParams.WRAP_CONTENT;
                } else {
                    Log.d("####", String.valueOf(initialHeight - (int) (initialHeight * interpolatedTime)));
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static int dpToPx(Context context, int dp) {

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));

        return px;

    }

    public static String getRegionCode(Context context){
        Country c = new Gson().fromJson(PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.key_region), ""), Country.class);
        if(c == null) {
            c = new CountryPicker.Builder().with(context).build().getCountryFromSIM();
            PreferenceManager.getDefaultSharedPreferences(context).edit().putString(context.getString(R.string.key_region), new Gson().toJson(c)).apply();
        }
        return c.getCode();
    }
}

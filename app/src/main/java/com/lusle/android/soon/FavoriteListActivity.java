package com.lusle.android.soon;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hugocastelani.waterfalltoolbar.WaterfallToolbar;
import com.lusle.android.soon.Adapter.FavoriteListActivityRecyclerAdapter;
import com.lusle.android.soon.ItemTouchHelper.SimpleItemTouchHelperCallback;
import com.lusle.android.soon.Model.Company;
import com.lusle.android.soon.Utils.Utils;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.lang.reflect.Type;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FavoriteListActivity extends AppCompatActivity implements FavoriteListActivityRecyclerAdapter.OnDragStartListener {

    private WaterfallToolbar waterfallToolbar;
    private static RecyclerView favoriteList;
    private ItemTouchHelper mItemTouchHelper;
    private static TextView saveBtn;
    private static ArrayList<Company> startList;

    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_list);

        init();
    }

    private void init() {

        favoriteList = findViewById(R.id.activity_favorite_company_recyclerView);
        favoriteList.setLayoutManager(new LinearLayoutManager(this));
        FavoriteListActivityRecyclerAdapter favoriteListActivityRecyclerAdapter = new FavoriteListActivityRecyclerAdapter(this);
        favoriteList.setAdapter(favoriteListActivityRecyclerAdapter);
        favoriteList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(favoriteListActivityRecyclerAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(favoriteList);


        waterfallToolbar = findViewById(R.id.waterfallToolbar);
        waterfallToolbar.setRecyclerView(favoriteList);

        saveBtn = findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(v -> {
            MovieProgressDialog dialog = new MovieProgressDialog(this);
            dialog.show();
            Type type = new TypeToken<ArrayList<Company>>() {
            }.getType();
            String list = new Gson().toJson(((FavoriteListActivityRecyclerAdapter) favoriteList.getAdapter()).getList(), type);
            SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("favorite_company", list);
            editor.commit();
            dialog.dismiss();
            saveBtn.setVisibility(View.GONE);
            startList = (ArrayList<Company>) ((FavoriteListActivityRecyclerAdapter) favoriteList.getAdapter()).getList().clone();
        });

        if (!Utils.bindingData(this, favoriteList, "FavoriteMore")) {
            DynamicToast.makeError(this, "즐겨찾기 정보를 불러 올 수 없습니다.").show();
        }
        startList = (ArrayList<Company>) ((FavoriteListActivityRecyclerAdapter) favoriteList.getAdapter()).getList().clone();
    }

    @Override
    public void onDragStarted(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void onBackPressed() {
        if (saveBtn.getVisibility() == View.VISIBLE) {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            DynamicToast.makeWarning(this, "저장하지 않으면 적용이 되지 않습니다. 괜찮다면 한 번더 눌러주세요").show();

            this.doubleBackToExitPressedOnce = true;

            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
        } else {
            finish();
        }
    }

    public static void ShowUndoSnackBar(Company deletedItem, int pos) {
        if (favoriteList != null) {
            Snackbar.make(favoriteList, deletedItem.getName() + "이(가) 삭제되었습니다.", Snackbar.LENGTH_LONG)
                    .setAction("UNDO", v -> ((FavoriteListActivityRecyclerAdapter) favoriteList.getAdapter()).insertItem(deletedItem, pos)).show();
        }
    }

    public static void checkSaveBtn() {
        if (saveBtn != null) {
            saveBtn.setVisibility(
                    ((FavoriteListActivityRecyclerAdapter) favoriteList.getAdapter()).getList().equals(startList) ? View.GONE : View.VISIBLE
            );
        }
    }
}

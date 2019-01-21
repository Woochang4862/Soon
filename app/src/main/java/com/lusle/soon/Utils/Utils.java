package com.lusle.soon.Utils;

import android.app.Activity;
import android.content.Context;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.lusle.soon.Adapter.FragmentCompanyBookMarkRecyclerViewAdapter;
import com.lusle.soon.Adapter.FragmentCompanyResultRecyclerViewAdapter;
import com.lusle.soon.Model.Company;
import com.lusle.soon.Model.MovieDetail;
import com.lusle.soon.R;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class Utils {

    public static PostExcutedTask postExcutedTask;

    public static boolean bindingData(Context context, RecyclerView recyclerView, String what, Object ... inputs) {
        Runnable runnable;
        switch (what) {
            case "Company":
                runnable = () -> {
                    ArrayList<MovieDetail> tmp = new ArrayList<>();
                    tmp.add(new MovieDetail("보헤미안 렙소디", "전체이용가", 1.0));
                    tmp.add(new MovieDetail("b", "15세 이상 이용가", 2.0));
                    tmp.add(new MovieDetail("c", "청소년 관람불가", 3.0));
                    for (int i = 0; i < 100; i++) {
                        tmp.add(tmp.get(i % 3));
                    } //TODO:LOAD DATA from Server
                    ((FragmentCompanyResultRecyclerViewAdapter) recyclerView.getAdapter()).setList(tmp);
                };
                break;

            case "Favorite":
                runnable = () -> {
                    ArrayList<Company> list = new ArrayList<>();
                    for (Company company :
                            new Company[]{
                                    new Company(420, "/hUzeosd33nzE5MCNsZxCGEKTXaQ.png", "Marvel Studios"),
                                    new Company(19551, "/2WpWp9b108hizjHKdA107hFmvQ5.png", "Marvel Enterprises"),
                                    new Company(38679, "/7sD79XoadVfcgOVCjuEgQduob68.png", "Marvel Television"),
                                    new Company(11106, "/nhI2D6OlNSrvNS18cf7m7b7N9vz.png", "Marvel Knights"),
                                    new Company(2301, null, "Marvel Productions")
                            }) {
                        list.add(company);
                    } //TODO:LOAD DATA from S.P.
                    final ArrayList<String> tmp = new ArrayList<>();
                    for (Company item : list) {
                        tmp.add(item.getName());
                    }
                    ((FragmentCompanyBookMarkRecyclerViewAdapter) recyclerView.getAdapter()).setList(tmp);
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
                if(postExcutedTask!=null)
                    postExcutedTask.OnPostExcuted();
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
    }

    public interface PostExcutedTask {
        void OnPostExcuted();
    }

    public void SetPostExcutedTask(PostExcutedTask postExcutedTask){
        this.postExcutedTask = postExcutedTask;
    }
}

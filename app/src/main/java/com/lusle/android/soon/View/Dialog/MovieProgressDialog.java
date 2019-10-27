package com.lusle.android.soon.View.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;

import com.lusle.android.soon.R;

import androidx.annotation.NonNull;

public class MovieProgressDialog extends Dialog {
    public MovieProgressDialog(@NonNull Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.progress_dialog);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

}

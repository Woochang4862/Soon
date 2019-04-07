package com.lusle.android.soon.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MultiResult {
    @SerializedName("results")
    @Expose
    MovieResult movieResult;
}

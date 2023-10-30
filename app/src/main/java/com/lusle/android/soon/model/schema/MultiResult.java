package com.lusle.android.soon.model.schema;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MultiResult {
    @SerializedName("results")
    @Expose
    MovieResult movieResult;
}

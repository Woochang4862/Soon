package com.lusle.android.soon.model.schema;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SubscribeCheckResult {
    @SerializedName("is_subscribed")
    @Expose
    private Boolean is_subscribed;

    public Boolean getIs_subscribed() {
        return is_subscribed;
    }

    public void setIs_subscribed(Boolean is_subscribed) {
        this.is_subscribed = is_subscribed;
    }


}

package com.demo.ken.listing_revamp.mvp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by mac on 6/11/2017.
 */

public class AppDetail {

    @SerializedName("results")
    @Expose
    public List<Result> results = null;

    public class Result {

        @SerializedName("averageUserRating")
        @Expose
        public Float averageUserRating;
        @SerializedName("userRatingCount")
        @Expose
        public Integer userRatingCount;
        @SerializedName("trackId")
        @Expose
        public Integer trackId;

        @Override
        public String toString() {
            return "Result{" +
                    "averageUserRating=" + averageUserRating +
                    ", userRatingCount=" + userRatingCount +
                    ", trackId=" + trackId +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "AppDetail{" +
                "results=" + results +
                '}';
    }
}

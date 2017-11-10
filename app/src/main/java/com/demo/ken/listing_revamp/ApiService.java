package com.demo.ken.listing_revamp;

import com.demo.ken.listing_revamp.mvp.model.AppDetail;
import com.demo.ken.listing_revamp.mvp.model.TopAppPojo;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by mac on 6/11/2017.
 */

public interface ApiService {

    @GET("/hk/rss/topfreeapplications/limit={size}/json")
    Observable<TopAppPojo> getTopList(@Path("size") int size);

    @GET("/hk/lookup")
    Observable<AppDetail> getDetail(@Query("id") String id);

    @GET("/hk/rss/topgrossingapplications/limit={size}/json")
    Observable<TopAppPojo> getTopGrossingList(@Path("size") int size);

}

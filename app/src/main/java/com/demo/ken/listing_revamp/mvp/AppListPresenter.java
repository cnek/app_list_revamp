package com.demo.ken.listing_revamp.mvp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.demo.ken.listing_revamp.ApiService;
import com.demo.ken.listing_revamp.BaseApplication;
import com.demo.ken.listing_revamp.mvp.model.AppDetail;
import com.demo.ken.listing_revamp.mvp.model.TopAppPojo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import okhttp3.OkHttpClient;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ken.chow on 6/21/2017.
 */
public class AppListPresenter {

    Context activity;
    AppListView appListView;

    private TopAppPojo topApp;
    private List<TopAppPojo.Feed.Entry> currentList = new ArrayList<>();
    private LinkedHashMap<String, TopAppPojo.Feed.Entry> entrysMap = new LinkedHashMap<>();

    private TopAppPojo recommendPojo;

    private int currentPage = 1;
    private boolean stopLoadMore = false;

    Retrofit retrofit;

    public AppListPresenter(Context activity, AppListView appListView) {
        this.activity = activity;
        this.appListView = appListView;
    }

    public void init() {
        appListView.loading(true);
        retrofit = BaseApplication.getRetrofit();
        fetchFromServer();
    }

    public void fetchFromServer() {
        sub(true
                , Observable.zip(
                        retrofit.create(ApiService.class).getTopList(100),
                        retrofit.create(ApiService.class).getTopGrossingList(10),
                        (topAppPojo1, topAppPojo2) -> {
                            List<TopAppPojo> objectList = new ArrayList<>();
                            objectList.add(topAppPojo1);
                            objectList.add(topAppPojo2);
                            return objectList;
                        })
                , new Observer<List<TopAppPojo>>() {
                    @Override
                    public void onCompleted() {
                        appListView.loading(false);
                        appListView.isError(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        appListView.loading(false);
                        appListView.isError(true);
                    }

                    @Override
                    public void onNext(List<TopAppPojo> objects) {
                        topApp = objects.get(0);

                        for (TopAppPojo.Feed.Entry entry : topApp.feed.entry)
                            entrysMap.put(entry.id.attributes.imId, entry);

                        onFetch(currentPage);

                        recommendPojo = objects.get(1);
                        appListView.refreshRecommendList(recommendPojo.feed.entry);

                    }
                });
    }

    private <T> void sub(boolean networkCheck, Observable<? extends T> observable, Observer<T> observer) {

        if (networkCheck && !isOnline(activity)) {
            Toast.makeText(activity, "There is no internet connection", Toast.LENGTH_SHORT).show();
            appListView.isError(true);
            appListView.loading(false);
            return;
        }

        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void onFetch(int position) {

        stopLoadMore = true;

        List<TopAppPojo.Feed.Entry> queryList = new ArrayList<>();
        List<TopAppPojo.Feed.Entry> appendList = new ArrayList<>();

        int i = -1;
        for (TopAppPojo.Feed.Entry entry : entrysMap.values()) {
            i++;

            if (i < 10 * (position - 1))
                continue;

            if (i >= 10 * position)
                break;

            appendList.add(entry);

            if (entry.userRatingCount == null)
                queryList.add(entry);
        }

        sub(false
                , Observable.from(queryList).flatMap(entry -> {
                    return retrofit.create(ApiService.class).getDetail(entry.id.attributes.imId);
                }).toList()
                , new Observer<List<AppDetail>>() {
                    @Override
                    public void onCompleted() {
                        stopLoadMore = false;
                    }

                    @Override
                    public void onError(Throwable e) {
                        appListView.scrollBackOneItem();
                        Toast.makeText(activity, "fetching fail, pls retry", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(List<AppDetail> appDetails) {
                        for (AppDetail appDetail : appDetails) {
                            entrysMap.get(appDetail.results.get(0).trackId.toString()).averageUserRating = appDetail.results.get(0).averageUserRating;
                            entrysMap.get(appDetail.results.get(0).trackId.toString()).userRatingCount = appDetail.results.get(0).userRatingCount;
                        }

                        currentList.addAll(appendList);
                        currentList.remove(null);
                        if (currentList.size() > 0 && currentList.size() != entrysMap.size() && !currentList.contains(null))
                            currentList.add(null);

                        appListView.refreshSearchList(currentList);
                    }
                }
        );

    }

    public void onSearch() {

        stopLoadMore = false;

        List<TopAppPojo.Feed.Entry> currentRecommendList = recommendPojo.feed.entry.stream().filter(entry ->
                isContainKeyword(entry, appListView.keyword()))
                .collect(Collectors.toList());

        appListView.refreshRecommendList(currentRecommendList);

        currentList.clear();
        entrysMap.clear();

        for (TopAppPojo.Feed.Entry entry : topApp.feed.entry)
            if (isContainKeyword(entry, appListView.keyword()))
                entrysMap.put(entry.id.attributes.imId, entry);

        currentPage = 1;
        onFetch(currentPage);
    }

    private boolean isContainKeyword(TopAppPojo.Feed.Entry entry, String keyword) {
        return entry.imName.label.contains(keyword) ||
                entry.category.attributes.label.contains(keyword) ||
                entry.imArtist.label.contains(keyword) ||
                entry.summary.label.contains(keyword);
    }

    public void onScrolled(int visibleItemCount, int totalItemCount, int pastVisiblesItems) {

        if (currentPage >= 10)
            return;

        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
            if (!stopLoadMore) {
                currentPage++;
                onFetch(currentPage);
            }
        }

    }

    public static boolean isOnline(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnected());
    }

}

package com.demo.ken.listing_revamp.mvp;


import com.demo.ken.listing_revamp.mvp.model.TopAppPojo;

import java.util.List;

/**
 * Created by ken.chow on 4/7/2017.
 */
public interface AppListView {
    void refreshSearchList(List<TopAppPojo.Feed.Entry> items);
    void refreshRecommendList(List<TopAppPojo.Feed.Entry> items);
    void scrollBackOneItem();
    void isError(boolean isError);
    void loading(boolean isLoading);
    String keyword();
}

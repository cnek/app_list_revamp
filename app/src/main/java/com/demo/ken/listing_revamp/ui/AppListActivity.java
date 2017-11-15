package com.demo.ken.listing_revamp.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.demo.ken.listing_revamp.R;
import com.demo.ken.listing_revamp.mvp.AppListPresenter;
import com.demo.ken.listing_revamp.mvp.AppListView;
import com.demo.ken.listing_revamp.mvp.model.TopAppPojo;
import com.demo.ken.listing_revamp.view.DividerItemDecoration;

import java.util.List;

/**
 * Created by ken.chow on 4/6/2017.
 */
public class AppListActivity extends Activity implements AppListView {

    private EditText search;
    private RecyclerView searchList;
    private ProgressBar loading;
    private View mainContainer;
    private View retryContainer;

    private AppListAdapter appListAdapter;
    private LinearLayoutManager linearLayoutManager;
    private DividerItemDecoration dividerItemDecoration;

    private AppListPresenter appListPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);

        search = (EditText) findViewById(R.id.search);
        searchList = (RecyclerView) findViewById(R.id.search_list);

        loading = (ProgressBar) findViewById(R.id.loading);
        loading.setIndeterminate(true);
        mainContainer = findViewById(R.id.main_container);
        retryContainer = findViewById(R.id.retry_container);

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        appListPresenter = new AppListPresenter(this,this);

        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    appListPresenter.onSearch();
                    hideSoftInputFromWindow();
                }
                return false;
            }
        });
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                appListPresenter.onSearch();
                linearLayoutManager.scrollToPositionWithOffset(0, 0);
            }
        });
        search.clearFocus();
        hideSoftInputFromWindow();

        linearLayoutManager = new LinearLayoutManager(this);
        dividerItemDecoration = new DividerItemDecoration(getDrawable(R.drawable.item_top_app_decorator),
                false,false,false);
        appListAdapter = new AppListAdapter(this);
        appListAdapter.setHasStableIds(true);
        searchList.setLayoutManager(linearLayoutManager);
        searchList.setAdapter(appListAdapter);
        searchList.addItemDecoration(dividerItemDecoration);
        searchList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    appListPresenter.onScrolled(linearLayoutManager.getChildCount(),linearLayoutManager.getItemCount(),linearLayoutManager.findFirstVisibleItemPosition());
                }
            }
        });

        appListPresenter.init();
    }

    @Override
    public void refreshSearchList(List<TopAppPojo.Feed.Entry> items) {
        appListAdapter.setSearchItems(items);
        mainContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void refreshRecommendList(List<TopAppPojo.Feed.Entry> items) {
        dividerItemDecoration.setmShowSecondDivide(items.size()==0);
        appListAdapter.setRecommendEntries(items);
        mainContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void scrollBackOneItem() {
        linearLayoutManager.scrollToPositionWithOffset(0, 0);
    }

    @Override
    public void isError(boolean isError) {
        retryContainer.setVisibility(isError ? View.VISIBLE : View.GONE);
    }

    @Override
    public void loading(boolean isLoading) {
        loading.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    @Override
    public String keyword() {
        return search.getText().toString();
    }

    public void onClick(View view){
        if(view.getId() == R.id.retry)
            appListPresenter.fetchFromServer();
    }

    protected void hideSoftInputFromWindow() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}

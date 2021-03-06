package com.example.shristi.nss_bvcoe;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.persistence.QueryOptions;

import java.util.ArrayList;
import java.util.List;

public class Events extends Fragment {

    private BackendlessCollection<Users> users;
    private List<Users> totalusers = new ArrayList<>();
    private boolean isLoadingItems = false;
    private UsersAdapter adapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        String appVersion = "v1";
        Backendless.initApp(getContext(), "A0EC8CE8-BC06-EC54-FFE2-9B50776AAA00", "AE1E553B-0836-C1E1-FFAF-96E976956000", appVersion);
        View view = inflater.inflate(R.layout.activity_event_listing, container, false);
        ListView list = (ListView) view.findViewById(R.id.listview );
        adapter = new UsersAdapter(getContext(), R.layout.list_item_event, totalusers );
        list.setAdapter(adapter);

        QueryOptions queryOptions = new QueryOptions();
        BackendlessDataQuery query = new BackendlessDataQuery( queryOptions );
        Backendless.Data.of( Users.class ).find(query, new LoadingCallback<BackendlessCollection<Users>>(getContext(), getString(R.string.loading_events), true) {
            @Override
            public void handleResponse(BackendlessCollection<Users> usersBackendlessCollection) {
                users = usersBackendlessCollection;
                addMoreItems(usersBackendlessCollection);
                super.handleResponse(usersBackendlessCollection);
            }
        });

        list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                Log.d("Event","scrolled");
                if (needToLoadItems(firstVisibleItem, visibleItemCount, totalItemCount)) {
                    isLoadingItems = true;

                    users.nextPage(new LoadingCallback<BackendlessCollection<Users>>(getContext()) {
                        @Override
                        public void handleResponse(BackendlessCollection<Users> nextPage) {
                            users = nextPage;

                            addMoreItems(nextPage);

                            isLoadingItems = false;
                        }
                    });
                }
            }
        });

       list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
                public void onItemClick (AdapterView < ? > parent, View view,int position, long id){
                TextView cuisineView = (TextView) view.findViewById(R.id.Image_Link );
                TextView DescriptionView = (TextView) view.findViewById( R.id.EventDescription );

              Image.link = cuisineView.getText().toString();
                Image.disc= DescriptionView.getText().toString();
                Intent i = new Intent(getContext(), Image.class);
                        startActivity(i);
                }

        });

        return view;
    }

    /**
     * Determines whether is it needed to load more items as user scrolls down.
     *
     * @param firstVisibleItem number of the first item visible on screen
     * @param visibleItemCount number of items visible on screen
     * @param totalItemCount   total number of items in list
     * @return true if user is about to reach the end of a list, else false
     */
    private boolean needToLoadItems( int firstVisibleItem, int visibleItemCount, int totalItemCount )
    {
        return !isLoadingItems && totalItemCount != 0 && totalItemCount - (visibleItemCount + firstVisibleItem) < visibleItemCount / 2;
    }

    /**
     * Adds more items to list and notifies Android that dataset has changed.
     *
     * @param nextPage list of new items
     */
    private void addMoreItems( BackendlessCollection<Users> nextPage )
    {
        totalusers.addAll( nextPage.getCurrentPage() );
        adapter.notifyDataSetChanged();
    }
}

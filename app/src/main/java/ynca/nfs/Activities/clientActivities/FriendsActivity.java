package ynca.nfs.Activities.clientActivities;

import android.app.ActionBar;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.model.MapStyleOptions;

import java.util.ArrayList;

import ynca.nfs.Adapter.FriendsListAdapter;
import ynca.nfs.Models.Client;
import ynca.nfs.R;

public class FriendsActivity extends AppCompatActivity implements  FriendsListAdapter.OnItemsClickListener {


    private ArrayList<Client> friends;

    //region Views Declarations
    private RecyclerView recyclerView;
    private FriendsListAdapter adapter;
    private SearchView searchView;
    //endregion


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends_list);



        //region Toolbar podesavanja
        Toolbar toolbar = (Toolbar) findViewById(R.id.friendsToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");


       Menu menu = toolbar.getMenu();
     //   searchView = (SearchView) menu.findItem(R.id.searchFriends).getActionView();
      /*  searchView.setIconified(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //if (query.isEmpty())
                //{
                   // recyclerView = (RecyclerView) findViewById(R.id.FriendsListRecycleView);

                    //GridLayoutManager layoutManager = new GridLayoutManager(getBaseContext(),1);
                    //recyclerView.setLayoutManager(layoutManager);
                   // recyclerView.setHasFixedSize(true);
                   // FriendsActivity FA = (FriendsActivity) getParent();
                   // adapter = new FriendsListAdapter(FA);

               // }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

     */


        //endregion


//region recycler
        recyclerView = (RecyclerView) findViewById(R.id.FriendsListRecycleView);
        GridLayoutManager layoutManager = new GridLayoutManager(this,1);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        adapter = new FriendsListAdapter(this);
//endregion

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.friends_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if(id == R.id.addFriends)
        {
            //kliknuta opcija za dodavanje novog prijatelja
            startActivity(new Intent(getBaseContext(), AddFriendActivity.class));

        }
       else if (id == R.id.searchFriends)
        {




        }

        else
        {
            //back dugme
            finish();
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void OnItemClick(int clickItemIndex) {
        final String friendUID = friends.get(clickItemIndex).getUID();
        Intent intent = new Intent(getBaseContext(), FriendProfileActivity.class);
        intent.putExtra("friendUID", friendUID);
        startActivity(intent);
        }
}

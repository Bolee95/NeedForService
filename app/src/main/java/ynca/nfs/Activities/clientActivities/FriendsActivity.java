package ynca.nfs.Activities.clientActivities;

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

        friends = new ArrayList<Client>();


        //region Toolbar podesavanja
        Toolbar toolbar = (Toolbar) findViewById(R.id.friendsToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");


//endregion


        recyclerView = (RecyclerView) findViewById(R.id.FriendsListRecycleView);
        GridLayoutManager layoutManager = new GridLayoutManager(this,1);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);


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
           //pretrazivanje prijatelja
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

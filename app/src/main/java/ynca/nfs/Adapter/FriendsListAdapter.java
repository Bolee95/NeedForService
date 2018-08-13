package ynca.nfs.Adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ynca.nfs.Models.Client;
import ynca.nfs.R;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class FriendsListAdapter extends RecyclerView.Adapter<FriendsListAdapter.ItemsViewHolder> {

    final private FriendsListAdapter.OnItemsClickListener OnItemsClickListen;



    public interface OnItemsClickListener{
        void OnItemClick(int clickItemIndex);
    }




    private ArrayList<Client> friends;

    public void add(Client c){ friends.add(c); }

    private static  final String TAG = FriendsListAdapter.class.getSimpleName();

    public FriendsListAdapter( OnItemsClickListener onItemsClickListen) {
        this.OnItemsClickListen = onItemsClickListen;

        friends = new ArrayList<Client>();
    }



    @Override
    public FriendsListAdapter.ItemsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.friends_list_item;
        LayoutInflater inflater =  LayoutInflater.from(context);
        boolean shouldAttachToParentImmediatly = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediatly);
        ItemsViewHolder viewHolder = new ItemsViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FriendsListAdapter.ItemsViewHolder holder, int position) {

        if(friends.size() ==0)
            return;
        holder.bind(friends.get(position));
    }

    @Override
    public int getItemCount() {
        return 0;
    }









    class ItemsViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener
    {
        private ImageView itemImage;
        private TextView itemName;
        private TextView itemEmail;

        private StorageReference mStorageReference;

        public  ItemsViewHolder(View view)
        {
            super(view);
            itemImage = (ImageView) view.findViewById(R.id.FriendsListItemNameTextView);
            itemEmail = (TextView) view.findViewById(R.id.FriendsListItemUsernameTextView);
            itemName = (TextView) view.findViewById(R.id.FriendsListItemNameTextView);

            view.setOnClickListener(this);
        }

        void bind(Client client){

            mStorageReference = FirebaseStorage.getInstance().getReference();
            StorageReference photoRef = mStorageReference.child("photos").child(client.getUID());
            photoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
            {
                @Override
                public  void  onSuccess(Uri uri)
                {
                    if (uri != null){
                        Glide.with(itemImage.getContext())
                                .load(uri).into(itemImage);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    itemImage.setImageDrawable(itemView.getResources().getDrawable(R.drawable.user));
                }
            });

            itemName.setText(client.getFirstName() + " " + client.getLastName());
            itemEmail.setText(client.getEmail());
        }



        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            OnItemsClickListen.OnItemClick(clickedPosition);



        }

    }




}
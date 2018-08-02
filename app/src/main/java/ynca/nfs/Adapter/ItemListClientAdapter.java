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

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import ynca.nfs.R;
import ynca.nfs.Models.Servis;

/**
 * Created by bolee on 21.5.17..
 */

public class ItemListClientAdapter extends RecyclerView.Adapter<ItemListClientAdapter.ItemsViewHolder> {

    final  private OnItemsClickListener OnItemsClickListen;


    private int numberOfItems;

    public interface OnItemsClickListener{
        void OnItemClick(int clickItemIndex);
    }


    private  ArrayList<Servis> servisi;

    public void add(Servis s){
        servisi.add(s);
    }


    private static final String TAG = ItemListClientAdapter.class.getSimpleName();

    public ItemListClientAdapter(int numberOfItems,OnItemsClickListener listener) {
        this.numberOfItems = numberOfItems;
        this.OnItemsClickListen = listener;
        servisi = new ArrayList<>();

    }

    @Override
    public ItemsViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.main_screen_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        ItemsViewHolder viewHolder = new ItemsViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ItemsViewHolder holder, int position) {
        if(servisi.size() == 0)
            return;
        holder.bind(servisi.get(position));
    }


    @Override
    public int getItemCount() {
        return servisi.size();
    }

    class ItemsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView itemImage;
        private TextView itemText;
        private TextView email;

        private StorageReference mStorageReference;

        public ItemsViewHolder(View view) {
            super(view);

            itemImage = (ImageView) view.findViewById(R.id.itemImageClient);
            itemText = (TextView) view.findViewById(R.id.itemTextClient);
            email = (TextView) view.findViewById(R.id.servis_email_id);

            view.setOnClickListener(this);
        }


        void bind(Servis servis){

            mStorageReference = FirebaseStorage.getInstance().getReference();
            StorageReference photoRef = mStorageReference.child("photos").child(servis.getUID());
            photoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    if(uri != null) {
                        //showProgressDialog();
                        Glide.with(itemImage.getContext())
                                .load(uri).into(itemImage);
                        //hideProgressDialog();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    itemImage.setImageDrawable(itemView.getResources().getDrawable(R.drawable.sport_car_logos));
                }
            });

            itemText.setText(servis.getNaziv()); //
            email.setText(servis.getAdresa()); //bio je email, promeni sam na adresu, Aleksa
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            OnItemsClickListen.OnItemClick(clickedPosition);



        }

    }

}

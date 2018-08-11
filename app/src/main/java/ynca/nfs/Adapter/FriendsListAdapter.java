package ynca.nfs.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import ynca.nfs.R;
import com.google.firebase.storage.StorageReference;

public class FriendsListAdapter extends RecyclerView.Adapter<FriendsListAdapter.ItemsViewHolder> {

    final private ItemListClientAdapter.OnItemsClickListener OnItemsClickListen;

    private int numberOfItems;

    public FriendsListAdapter(ItemListClientAdapter.OnItemsClickListener onItemsClickListen) {
        OnItemsClickListen = onItemsClickListen;
    }

    @Override
    public FriendsListAdapter.ItemsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(FriendsListAdapter.ItemsViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public interface OnItemClickListener{
        void OnItemClick(int clickItemIndex);
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



        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            OnItemsClickListen.OnItemClick(clickedPosition);



        }

}




}
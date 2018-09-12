package ynca.nfs.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ynca.nfs.Models.Client;
import ynca.nfs.R;
import ynca.nfs.SQLiteHelper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class FriendsListAdapter extends RecyclerView.Adapter<FriendsListAdapter.ItemsViewHolder> {

    final private FriendsListAdapter.OnItemsClickListener OnItemsClickListen;
    private  double currentUserLatitude;
    private double currentUserLongitude;
    private Context context;
    private SQLiteHelper cashe;

    public interface OnItemsClickListener{
        void OnItemClick(int clickItemIndex);
    }

    private ArrayList<Client> friends;

    public void add(Client c){ friends.add(c); }

    private static  final String TAG = FriendsListAdapter.class.getSimpleName();

    public FriendsListAdapter(  OnItemsClickListener onItemsClickListen, double currentClientLatitude,
                                double currentClientLongitude) {
        this.OnItemsClickListen = onItemsClickListen;
        friends = new ArrayList<Client>();
        currentUserLatitude=currentClientLatitude;
        currentUserLongitude=currentClientLongitude;
    }

    @Override
    public FriendsListAdapter.ItemsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        context = parent.getContext();
        int layoutIdForListItem = R.layout.friends_list_item;
        LayoutInflater inflater =  LayoutInflater.from(context);
        boolean shouldAttachToParentImmediatly = false;


        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediatly);
        ItemsViewHolder viewHolder = new ItemsViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ItemsViewHolder holder, int position) {

        if(friends.size() ==0)
            return;
        holder.bind(friends.get(position));
    }

    @Override
    public int getItemCount() {
        return  friends.size();
    }

    class ItemsViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener
    {
        private ImageView itemImage;
        private TextView itemName;
        private TextView itemEmail;
        private  TextView itemPts;
        private TextView itemDistance;



        private StorageReference mStorageReference;

        public  ItemsViewHolder(View view)
        {
            super(view);
            itemImage = (ImageView) view.findViewById(R.id.FriendsProfilePicture);
            itemEmail = (TextView) view.findViewById(R.id.FriendsListItemEmailTextView);
            itemName = (TextView) view.findViewById(R.id.FriendsListItemNameTextView);
            itemPts = (TextView)view.findViewById(R.id.FriendsListItemPtsTextView);
            itemDistance = (TextView)view.findViewById(R.id.FriendsListItemDistanceTextView);

            view.setOnClickListener(this);

            //cashe init
            cashe = new SQLiteHelper(context);
        }

        void bind(final Client client){

            mStorageReference = FirebaseStorage.getInstance().getReference();
            if (!cashe.imageExists(client.getUID())) {
                try {
                    final File localFile = File.createTempFile("temp", "");
                    StorageReference photoRef = mStorageReference.child("photos").child(client.getUID());
                    photoRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            String filePath = localFile.getPath();
                            Bitmap image = BitmapFactory.decodeFile(filePath);

                            cashe.saveImage(client.getUID(), image);
                            Bitmap profileImage = getRoundedCornerBitmap(Bitmap.createScaledBitmap(image, 250, 250, false), 50);

                            itemImage.setImageBitmap(profileImage);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Bitmap rawImage = BitmapFactory.decodeResource(context.getResources(),
                                    R.drawable.user);
                            Bitmap profileImage = getRoundedCornerBitmap(Bitmap.createScaledBitmap(rawImage, 250, 250, false), 100);
                            itemImage.setImageBitmap(profileImage);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                Bitmap cashedImage = cashe.getImage(client.getUID());
                Bitmap profileImage = getRoundedCornerBitmap(Bitmap.createScaledBitmap(cashedImage, 250, 250, false), 50);
                itemImage.setImageBitmap(profileImage);
            }

            //itemImage.setClipToOutline(true);

            itemName.setText(client.getFirstName() + " " + client.getLastName());
            itemEmail.setText(client.getEmail());
            itemPts.setText(Integer.toString(client.getPoints()));

            float distance [] = new float[10];
            Location.distanceBetween(  currentUserLatitude ,currentUserLongitude, client.getLastKnownLat(),
                    client.getLastKnownlongi(), distance);

            DecimalFormat df = new DecimalFormat("#.##");
            df.setRoundingMode(RoundingMode.CEILING);

            String result = String.valueOf(df.format(distance[0] / 1000));
            itemDistance.setText(result+ " km away");
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            OnItemsClickListen.OnItemClick(clickedPosition);
        }

        //region roundedImage
        private Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
            Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                    .getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);

            final int color = 0xff424242;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            final RectF rectF = new RectF(rect);
            final float roundPx = pixels;

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);

            return output;
        }
        //endregion

    }
}
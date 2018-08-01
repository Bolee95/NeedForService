package ynca.nfs.Adapter;

/**
 * Created by Nikola on 5/27/2017.
 */

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import ynca.nfs.Models.Poruka;
import ynca.nfs.R;


public class CustomPorukeAdapter extends RecyclerView.Adapter<CustomPorukeAdapter.CustomPorukeViewHolder> {

    ArrayList<Poruka> poruke;

    FirebaseDatabase mFireBaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

 //final private CustomPorukeAdapter.OnListItemClickListener onItemsClickListen;


    final private CustomPorukeAdapter.OnListItemClickListener onItemsClickListen;

    public void add(Poruka p) {
        poruke.add(p);
    }

    public Poruka getPorukabyIndex(int pos) {
        return poruke.get(pos);

    }

    public interface OnListItemClickListener {
        void OnItemClick(int clickItemIndex);

    }


    public CustomPorukeAdapter(CustomPorukeAdapter.OnListItemClickListener onListItemClickListener) {
        poruke = new ArrayList<>();
        this.onItemsClickListen = onListItemClickListener;
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mFireBaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFireBaseDatabase.getReference().child("Korisnik").child("Klijent")
                .child(mUser.getUid()).child("primljenePoruke");
    }


    @Override
    public CustomPorukeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int id = R.layout.inbox_client_msg;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(id, parent, false);
        CustomPorukeViewHolder viewHolder = new CustomPorukeViewHolder(view);
        return viewHolder;

    }


    @Override
    public void onBindViewHolder(CustomPorukeAdapter.CustomPorukeViewHolder holder, int position) {

        holder.bind(poruke.get(position));
        holder.msgs = poruke;
    }

    @Override
    public int getItemCount() {
        return poruke.size();
    }


    class CustomPorukeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView posiljaocTV;
        TextView idTV;
        TextView naslovTV;
        LinearLayout cela;
        ArrayList<Poruka> msgs;
        Button dlt;


        public CustomPorukeViewHolder(final View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            posiljaocTV = (TextView) itemView.findViewById(R.id.clientMsgPosiljaoc);
            naslovTV = (TextView) itemView.findViewById(R.id.clientMsgNaslov);
            cela = (LinearLayout) itemView.findViewById(R.id.clientMSGview);
            idTV = (TextView) itemView.findViewById(R.id.nevidljiviIdPoruke);
            dlt = (Button) itemView.findViewById(R.id.deleteMSG);
            dlt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    final AlertDialog alertDialog = new AlertDialog.Builder(v.getContext()).create();
                    alertDialog.setTitle(v.getResources().getString(R.string.warrning));
                    alertDialog.setMessage("Are you sure?");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, v.getResources().getString(R.string.Yes),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    String id = idTV.getText().toString();

                                    mDatabaseReference.child(id).removeValue();
                                    Poruka toRemove = null;
                                    for(Poruka p: poruke){
                                        if(p.getId() == id)
                                            toRemove = p;
                                    }
                                    if(toRemove!=null)
                                        poruke.remove(toRemove);
                                    notifyDataSetChanged();

                                }
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, v.getResources().getString(R.string.No) , new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    });

                    alertDialog.show();



//                    String id = idTV.getText().toString();
//
//                    mDatabaseReference.child(id).removeValue();
//                    Poruka toRemove = null;
//                    for(Poruka p: poruke){
//                        if(p.getId() == id)
//                            toRemove = p;
//                    }
//                    if(toRemove!=null)
//                        poruke.remove(toRemove);
//                    notifyDataSetChanged();


                }
            });


            itemView.setOnClickListener(this);


        }

        void bind(Poruka p) {

            posiljaocTV.setText(p.getPosiljalac());
            naslovTV.setText(p.getNaslov());
            idTV.setText(p.getId());

            ObojiPorukaHolder(cela, p.isProcitana());


        }


        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            porukaKlikBojenje(cela, true);
            onItemsClickListen.OnItemClick(clickedPosition);


        }
    }

    public void ObojiPorukaHolder(View tatko, boolean procitana) {
        if (tatko instanceof LinearLayout) {
            int c  = ((LinearLayout) tatko).getChildCount();
            for (int i =0; i<c; i++)
            {   if (procitana)
                tatko.setBackgroundColor(Color.parseColor("#212121"));
                else
                tatko.setBackgroundColor(tatko.getResources().getColor(R.color.LightBlue));//("#E0E0E0"));

                ObojiPorukaHolder(((LinearLayout) tatko).getChildAt(i), procitana);
            }
        }
        else if (procitana)
        {
            if (tatko instanceof TextView & !(tatko instanceof Button)) {
                ((TextView) tatko).setTextColor(Color.WHITE);
                //((TextView) tatko).setBackgroundColor(Color.parseColor("#212121"));

            } else if (!(tatko instanceof Button))
                tatko.setBackgroundColor(Color.parseColor("#2E7D32"));


        }
        else
        {
            if (tatko instanceof TextView & !(tatko instanceof Button)) {
                ((TextView) tatko).setTextColor(Color.BLACK);
                ((TextView) tatko).setTextColor(Color.parseColor("#E0E0E0"));

            } else if (!(tatko instanceof Button))
                tatko.setBackgroundColor(Color.parseColor("#2E7D32"));


        }
    }

    public void porukaKlikBojenje(View tatko, boolean procitana)
    {
        if (tatko instanceof LinearLayout) {
            int c  = ((LinearLayout) tatko).getChildCount();
            for (int i =0; i<c; i++)
            {
                tatko.setBackgroundColor(Color.parseColor("#212121"));


                ObojiPorukaHolder(((LinearLayout) tatko).getChildAt(i), procitana);
            }
        }
        else
        {
            if (tatko instanceof TextView & !(tatko instanceof Button)) {
                ((TextView) tatko).setTextColor(Color.WHITE);
                ((TextView) tatko).setBackgroundColor(Color.parseColor("#212121"));

            } else if (!(tatko instanceof Button))
                tatko.setBackgroundColor(Color.parseColor("#2E7D32"));


        }



        }

}


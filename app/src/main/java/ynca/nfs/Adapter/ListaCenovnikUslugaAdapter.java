package ynca.nfs.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import ynca.nfs.R;
import ynca.nfs.Usluga;

/**
 * Created by Nemanja Djordjevic on 5/29/2017.
 */

public class ListaCenovnikUslugaAdapter extends RecyclerView.Adapter<ListaCenovnikUslugaAdapter.ListaCenovnikUslugaViewHolder>{

    ArrayList<Usluga> nizUsluga;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;


    public void add(Usluga u){
        nizUsluga.add(u);
    }


    private int mNumberItems;

    final  private ListaVozilaAdapter.OnListItemClickListener onItemsClickListen;
    public interface OnListItemClickListener{
        void OnItemClick(int clickItemIndex);
    }

    public ListaCenovnikUslugaAdapter(ListaVozilaAdapter.OnListItemClickListener onItemsClickListen){

        nizUsluga = new ArrayList<>();
        //mNumberItems = n;
        this.onItemsClickListen = onItemsClickListen;

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("Korisnik").child("Servis")
                .child(mUser.getUid()).child("usluge");

    }

    @Override
    public ListaCenovnikUslugaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        int id = R.layout.lista_cenovnik_usluga_item;

        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(id, parent, false);


        ListaCenovnikUslugaViewHolder viewHolder = new ListaCenovnikUslugaViewHolder(view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(ListaCenovnikUslugaViewHolder holder, int position) {

        holder.bind(nizUsluga.get(position));
    }

    @Override
    public int getItemCount() {
        return nizUsluga.size();
    }


    class ListaCenovnikUslugaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView mlistItem;
        TextView cenaU;
        Button ButtPrice;
        EditText cena;
        Button cenaSubmit;
        Button ukloniUslugu;

        public ListaCenovnikUslugaViewHolder(final View itemView) {
            super(itemView);

            ukloniUslugu = (Button) itemView.findViewById(R.id.ukloniUsluguBTN);
            mlistItem = (TextView) itemView.findViewById(R.id.lista_cenovnik_usluga_item_id);
            cenaU = (TextView) itemView.findViewById(R.id.cenaUsluga);
            itemView.setOnClickListener(this);
            ButtPrice = (Button) itemView.findViewById(R.id.DugmeCena);
            ButtPrice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String temp = String.valueOf(ButtPrice.getTag());
                    int i = 0;
                    while (!nizUsluga.get(i).getUsluga().equals(ButtPrice.getTag()))
                    { i++;}
                    final Usluga temp1 = nizUsluga.get(i);

                    final Dialog d = new Dialog(itemView.getContext());
                    d.setContentView(R.layout.izmena_cene);
                    cena = (EditText) d.findViewById(R.id.CenaIzmena);
                    cenaSubmit = (Button) d.findViewById(R.id.SubmitCena);
                    cena.setText(String.valueOf((temp1.getCena())));
                    d.setTitle(itemView.getResources().getString(R.string.NewPrice));
                    d.show();
                    cenaSubmit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //TODO U BAZU DA SE UBACI I DA SE SREDI
                            int i =0;
                            while (!nizUsluga.get(i).getUsluga().equals(ButtPrice.getTag()))
                            {i++;}
                            int newCena = Integer.valueOf(String.valueOf(cena.getText()));
                            Usluga uslugaZaUpdate = nizUsluga.get(i);
                            Toast.makeText(itemView.getContext(), itemView.getResources().getString(R.string.PriceChanged), Toast.LENGTH_LONG).show();
                            uslugaZaUpdate.setCena(newCena);
                            mDatabaseReference.child(uslugaZaUpdate.getIdUsluge()).setValue(uslugaZaUpdate);
                            cenaU.setText(Integer.toString(uslugaZaUpdate.getCena()));
                            d.dismiss();

                        }
                    });


                }

            });
            ukloniUslugu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    final AlertDialog alertDialog = new AlertDialog.Builder(v.getContext()).create();
                    alertDialog.setTitle(v.getResources().getString(R.string.warrning));
                    alertDialog.setMessage(itemView.getResources().getString(R.string.AreYouSure));
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, v.getResources().getString(R.string.Yes),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    String tag = (String)ukloniUslugu.getTag();

                                    mDatabaseReference.child(tag).removeValue();

                                    Usluga toRemove = null;
                                    for(Usluga u: nizUsluga){
                                        if(u.getIdUsluge() == tag)
                                            toRemove = u;
                                    }
                                    if(toRemove!=null)
                                        nizUsluga.remove(toRemove);
                                    notifyDataSetChanged();
                                    Toast.makeText(itemView.getContext(), R.string.succeed, Toast.LENGTH_LONG).show();

                                }
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, v.getResources().getString(R.string.No) , new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    });

                    alertDialog.show();

                }
            });
        }

        void bind (Usluga u){
            mlistItem.setText(u.getUsluga());
            cenaU.setText(Integer.toString(u.getCena()));
            ButtPrice.setTag(u.getUsluga());
            ukloniUslugu.setTag(u.getIdUsluge());
        }


        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            onItemsClickListen.OnItemClick(clickedPosition);
        }
    }
}

package ynca.nfs.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import ynca.nfs.Models.*;
import ynca.nfs.Models.Poruka;
import ynca.nfs.R;

/**
 * Created by Nemanja Djordjevic on 5/26/2017.
 */

public class ListaVozilaNaServisuAdapter extends  RecyclerView.Adapter<ListaVozilaNaServisuAdapter.ListaVozilaViewHolder>{

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mDatabaseReference1;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private ArrayList<Vehicle> automobili;
    final  private ListaVozilaNaServisuAdapter.OnListItemClickListener onItemsClickListen;

    public void add(Vehicle a) {

        automobili.add(a);

    }

    public interface OnListItemClickListener{
        void OnItemClick(int clickItemIndex);
    }


    public ListaVozilaNaServisuAdapter(ListaVozilaNaServisuAdapter.OnListItemClickListener onItemsClickListen){
        this.onItemsClickListen = onItemsClickListen;
        automobili = new ArrayList<>();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mDatabaseReference1 = mFirebaseDatabase.getReference().child("Korisnik").child("VehicleService");

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
    }



    @Override
    public ListaVozilaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context  = parent.getContext();
        int id = R.layout.lista_vozila_na_servisu_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(id, parent, false);
        ListaVozilaViewHolder theViewHolder = new ListaVozilaViewHolder(view);
        return theViewHolder;
    }

    @Override
    public void onBindViewHolder(ListaVozilaViewHolder holder, int position) {
        holder.postaviVrednost(automobili.get(position));
    }


    @Override
    public int getItemCount() {
        return automobili.size();
    }


    class ListaVozilaViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener{
        TextView listItem;
        Button markAsDone;
        TextView proizvodjacTV;
        TextView modelTV;
        TextView godinaaTV;
        TextView tipGorivaTV;
        TextView vlasnikTV;
        TextView uslugaTV;

        public ListaVozilaViewHolder(final View itemView) {
            super(itemView);

            markAsDone = (Button) itemView.findViewById(R.id.DugmeListaServisiranih);
            listItem = (TextView) itemView.findViewById(R.id.lista_vozila_na_servisu_item_id);
            proizvodjacTV = (TextView) itemView.findViewById(R.id.lista_vozila_na_servisu_proizvodjac_id);
            modelTV = (TextView) itemView.findViewById(R.id.lista_vozila_na_servisu_model_id);
            godinaaTV = (TextView) itemView.findViewById(R.id.lista_vozila_na_servisu_godina_id);
            tipGorivaTV = (TextView) itemView.findViewById(R.id.lista_vozila_na_servisu_tipgoriva_id);
            vlasnikTV = (TextView) itemView.findViewById(R.id.lista_vozila_na_servisu_vlas_id);
            uslugaTV = (TextView) itemView.findViewById(R.id.lista_vozila_na_servisu_usluga_id);
            itemView.setOnClickListener(this);
            markAsDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final String temp = String.valueOf(markAsDone.getTag());
                    int i=0;
                    while(!automobili.get(i).getVehicleID().equals(temp))
                    {i++;}
                    final Vehicle temp1 = automobili.get(i);

                    final AlertDialog alertDialog = new AlertDialog.Builder(itemView.getContext()).create();
                    alertDialog.setTitle(itemView.getResources().getString(R.string.Question));
                    alertDialog.setMessage(itemView.getResources().getString(R.string.infoAbout)+"\n" + temp1.getManufacturer() + " " + temp1.getModel()  + itemView.getResources().getString(R.string.withRegistyNum) + temp1.getRegistyNumber() +"\n" + itemView.getResources().getString(R.string.IsServiceDone));
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, itemView.getResources().getString(R.string.Yes),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //TODO ODRADI LOGIKU DA IZBRISE IZ LISTE I DA POSALJE PORUKU KORISNIKU

                                    Poruka p = new Poruka(false, mUser.getEmail(), "", "Obavestenje",
                                            "Postovani, \n Obavestavamo Vas da je servisiranje Vaseg vozila zavrseno.", "");

                                    mDatabaseReference.child("Korisnik").child("Client").child(temp1.getOwnerID())
                                            .child("primljenePoruke").push().setValue(p);

                                    mDatabaseReference1.child(mUser.getUid())
                                            .child("acceptedServices").child(temp1.getVehicleID()).removeValue();

                                    automobili.remove(temp1);
                                    notifyDataSetChanged();
                                    Toast.makeText(itemView.getContext(),itemView.getResources().getString(R.string.Done) , Toast.LENGTH_LONG).show();

                                }
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, itemView.getResources().getString(R.string.No) , new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }
            });



        }


        public void postaviVrednost(Vehicle a) {
            listItem.setText( a.getRegistyNumber());
            markAsDone.setTag(a.getVehicleID());
            proizvodjacTV.setText(a.getManufacturer());
            modelTV.setText(a.getModel());
            godinaaTV.setText(Integer.toString(a.getYearOfProduction()));
            tipGorivaTV.setText(a.getFuelType());
            vlasnikTV.setText(a.getOwnersMail());
            uslugaTV.setText(a.getTypeOfService());


        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            onItemsClickListen.OnItemClick(clickedPosition);
            //TODO URADI ONCLICK ZA OVU LISTU DA VODI NA INFO ZA AUTOMOBILE
        }
    }
}




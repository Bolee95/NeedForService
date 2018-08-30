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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import ynca.nfs.Models.Vehicle;
import ynca.nfs.Models.Request;
import ynca.nfs.Models.Poruka;
import ynca.nfs.R;

/**
 * Created by Nemanja Djordjevic on 5/29/2017.
 */

public class ListaZahtevaAdapter extends RecyclerView.Adapter<ListaZahtevaAdapter.ListaZahtevaViewHolder>{

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private ArrayList<Request> zahtevi;

    final  private ListaVozilaAdapter.OnListItemClickListener onItemsClickListen;

    public void add(Request z) {
        zahtevi.add(z);
    }

    public interface OnListItemClickListener{
        void OnItemClick(int clickItemIndex);
    }


    public ListaZahtevaAdapter(ListaVozilaAdapter.OnListItemClickListener onItemsClickListen){

        this.onItemsClickListen = onItemsClickListen;
        zahtevi = new ArrayList<>();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

    }

    @Override
    public ListaZahtevaAdapter.ListaZahtevaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        int id = R.layout.lista_zahteva_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(id, parent, false);
        ListaZahtevaAdapter.ListaZahtevaViewHolder viewHolder = new ListaZahtevaAdapter.ListaZahtevaViewHolder(view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(ListaZahtevaAdapter.ListaZahtevaViewHolder holder, int position) {

        holder.bind(zahtevi.get(position));
    }

    @Override
    public int getItemCount() {
        return zahtevi.size();
    }


    class ListaZahtevaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView fromTv;
        TextView typeTv;
        TextView suggestedTv;
        TextView noteTv;
        Button Accept;
        Button Reject;

        public ListaZahtevaViewHolder(View itemView) {
            super(itemView);

            fromTv = (TextView) itemView.findViewById(R.id.fromTV);
            typeTv = (TextView) itemView.findViewById(R.id.typeOfServiceTV);
            suggestedTv = (TextView) itemView.findViewById(R.id.suggestedTV);
            noteTv = (TextView) itemView.findViewById(R.id.napomenaTV);
            Accept = (Button)itemView.findViewById(R.id.zahtev_btn_accept);
            Reject = (Button)itemView.findViewById(R.id.zahtev_btn_decline);
            itemView.setOnClickListener(this);

        }

        void bind (Request z){

            String str = z.getId();
            Accept.setTag(str);
            Reject.setTag(z.getId());
            fromTv.setText(z.getClientName());
            typeTv.setText(z.getTypeOfService());
            suggestedTv.setText(z.getProposedDate());
            noteTv.setText(z.getNote());


            Accept.setOnClickListener(new View.OnClickListener() {
                @Override

                public void onClick(View v) {

                    final AlertDialog alertDialog = new AlertDialog.Builder(v.getContext()).create();
                    alertDialog.setTitle(v.getResources().getString(R.string.warrning));
                    alertDialog.setMessage("Are you sure?");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, v.getResources().getString(R.string.Yes),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    String tag = String.valueOf(Accept.getTag());

                                    Request z = vratiZahtev(tag);
                                    Vehicle a = z.getVehicle();
                                    a.setTypeOfService(z.getTypeOfService());

                                    mDatabaseReference.child("Korisnik").child("VehicleService").child(mUser.getUid())
                                            .child("acceptedServices").push().setValue(a);
                                    Poruka p = new Poruka(false, mUser.getEmail(), "", "Obavestenje",
                                            "Postovani, \n Obavestavamo Vas da je Vas zahtev prihvacen", "");

                                    mDatabaseReference.child("Korisnik").child("Client").child(z.getClientId())
                                            .child("primljenePoruke").push().setValue(p);

                                    mDatabaseReference.child("ServiceRequests").child(mUser.getUid()).child(z.getId()).removeValue();

                                    zahtevi.remove(z);
                                    notifyDataSetChanged();
                                    //Toast.makeText(itemView.getContext(), tag, Toast.LENGTH_LONG).show();

                                }
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, v.getResources().getString(R.string.No) , new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    });

                    alertDialog.show();




//                    String tag = String.valueOf(Accept.getTag());
//
//                    Request z = vratiZahtev(tag);
//                    Automobil a = z.getAutomobil();
//
//                    mDatabaseReference.child("Korisnik").child("VehicleService").child(mUser.getUid())
//                            .child("automobili").push().setValue(a);
//                    Poruka p = new Poruka(false, mUser.getEmail(), "", "Obavestenje",
//                            "Postovani, \n Obavestavamo Vas da je Vas zahtev prihvacen", "");
//
//                    mDatabaseReference.child("Korisnik").child("Client").child(z.getIdKlijenta())
//                            .child("primljenePoruke").push().setValue(p);
//
//                    mDatabaseReference.child("ZahteviServis").child(mUser.getUid()).child(z.getId()).removeValue();
//
//                    zahtevi.remove(z);
//                    notifyDataSetChanged();
//                    Toast.makeText(itemView.getContext(), tag, Toast.LENGTH_LONG).show();
                }
            });
            Reject.setOnClickListener(new View.OnClickListener() {
                @Override

                public void onClick(View v) {


                    final AlertDialog alertDialog = new AlertDialog.Builder(v.getContext()).create();
                    alertDialog.setTitle(v.getResources().getString(R.string.warrning));
                    alertDialog.setMessage("Are you sure?");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, v.getResources().getString(R.string.Yes),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    String tag = String.valueOf(Accept.getTag());

                                    Request z = vratiZahtev(tag);
                                    Vehicle a = z.getVehicle();


                                    Poruka p = new Poruka(false, mUser.getEmail(), "", "Obavestenje",
                                            "Postovani, \n Obavestavamo Vas da je Vas zahtev za automobil "
                                                    + a.getManufacturer() + " "+ a.getModel() + " odbijen", "");

                                    mDatabaseReference.child("Korisnik").child("Client").child(z.getClientId())
                                            .child("primljenePoruke").push().setValue(p);

                                    zahtevi.remove(z);

                                    mDatabaseReference.child("ServiceRequests").child(mUser.getUid()).child(z.getId()).removeValue();

                                    //Toast.makeText(itemView.getContext(), tag, Toast.LENGTH_LONG).show();
                                }
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, v.getResources().getString(R.string.No) , new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    });

                    alertDialog.show();



//                    String tag = String.valueOf(Accept.getTag());
//
//                    Request z = vratiZahtev(tag);
//                    Automobil a = z.getAutomobil();
//
//
//                    Poruka p = new Poruka(false, mUser.getEmail(), "", "Obavestenje",
//                            "Postovani, \n Obavestavamo Vas da je Vas zahtev za automobil "
//                            + a.getProizvodjac() + " "+ a.getModel() + " odbijen", "");
//
//                    mDatabaseReference.child("Korisnik").child("Client").child(z.getIdKlijenta())
//                            .child("primljenePoruke").push().setValue(p);
//
//                    zahtevi.remove(z);
//
//                    mDatabaseReference.child("ZahteviServis").child(mUser.getUid()).child(z.getId()).removeValue();
//
//                    Toast.makeText(itemView.getContext(), tag, Toast.LENGTH_LONG).show();

                }
            });
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            onItemsClickListen.OnItemClick(clickedPosition);
        }
    }

    private Request vratiZahtev(String tag) {
        for(Request z: zahtevi)
            if (tag.equals(z.getId()))
                return z;
        return null;
    }

}

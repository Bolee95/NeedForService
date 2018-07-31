package ynca.nfs.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

import ynca.nfs.R;
import ynca.nfs.Recenzija;

/**
 * Created by Nikola on 6/2/2017.
 */

public class ListaRecenzijaAdapter extends RecyclerView.Adapter<ListaRecenzijaAdapter.ListaRecenzijaViewHolder>{

final  private ListaRecenzijaAdapter.OnListItemClickListener onItemsClickListen;

public void add(Recenzija r) {
        recenzije.add(r);
        }

public interface OnListItemClickListener{
    void OnItemClick(int clickItemIndex);
}

    private ArrayList<Recenzija> recenzije;

    public ListaRecenzijaAdapter(ListaRecenzijaAdapter.OnListItemClickListener listener){
        this.onItemsClickListen = listener;
        recenzije = new ArrayList<Recenzija>();
    }



    @Override
    public ListaRecenzijaAdapter.ListaRecenzijaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int id = R.layout.lista_recenzija_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(id, parent, false);
        ListaRecenzijaAdapter.ListaRecenzijaViewHolder theViewHolder = new ListaRecenzijaAdapter.ListaRecenzijaViewHolder(view);
        return theViewHolder;
    }

    @Override
    public void onBindViewHolder(ListaRecenzijaAdapter.ListaRecenzijaViewHolder holder, int position) {
        holder.postaviVrednost(recenzije.get(position));
    }


    @Override
    public int getItemCount() {
        return recenzije.size();
    }


    class ListaRecenzijaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    TextView user;
    TextView komentar;
    RatingBar zvezdice;

    public ListaRecenzijaViewHolder(View itemView) {
        super(itemView);


        user = (TextView) itemView.findViewById(R.id.userTV);
        komentar = (TextView) itemView.findViewById(R.id.komentarTV);
        zvezdice = (RatingBar) itemView.findViewById(R.id.StarBar);
        zvezdice.setEnabled(false);
        itemView.setOnClickListener(this);

    }


    public void postaviVrednost(Recenzija r) {
        user.setText( r.getKorisnik());
        komentar.setText(r.getKomentar());
        user.setText( r.getKorisnik());
        komentar.setText( r.getKomentar());
        zvezdice.setRating(r.getOcena());
    }

    @Override
    public void onClick(View v) {
        int clickedPosition = getAdapterPosition();
        onItemsClickListen.OnItemClick(clickedPosition);


    }
}
}


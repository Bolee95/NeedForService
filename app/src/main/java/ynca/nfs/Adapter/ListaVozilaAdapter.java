package ynca.nfs.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import ynca.nfs.Models.Automobil;
import ynca.nfs.R;

/**
 * Created by Nemanja Djordjevic on 5/26/2017.
 */

public class ListaVozilaAdapter extends  RecyclerView.Adapter<ListaVozilaAdapter.ListaVozilaViewHolder>{

    final  private OnListItemClickListener onItemsClickListen;

    public void add(Automobil a) {
        automobili.add(a);
    }

    public interface OnListItemClickListener{
        void OnItemClick(int clickItemIndex);
    }

    private ArrayList<Automobil> automobili;

    public ListaVozilaAdapter(OnListItemClickListener listener){
        this.onItemsClickListen = listener;
        automobili = new ArrayList<Automobil>();
    }



    @Override
    public ListaVozilaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int id = R.layout.lista_vozila_item;
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


    class ListaVozilaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView listItemProiz;
        TextView model;
        TextView regBroj;

        public ListaVozilaViewHolder(View itemView) {
            super(itemView);


            listItemProiz = (TextView) itemView.findViewById(R.id.lista_vozila_item_proizvodjac);
            model = (TextView) itemView.findViewById(R.id.lista_vozila_item_model);
            regBroj = (TextView) itemView.findViewById(R.id.lista_vozila_item_reg_broj);
            itemView.setOnClickListener(this);

        }


        public void postaviVrednost(Automobil vozilo) {
            listItemProiz.setText(itemView.getResources().getString(R.string.Manufacturer)  + vozilo.getProizvodjac());
            model.setText(itemView.getResources().getString(R.string.Model)  + vozilo.getModel());
            regBroj.setText(itemView.getResources().getString(R.string.RegNumber)  + vozilo.getRegBroj());
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            onItemsClickListen.OnItemClick(clickedPosition);


        }
    }
}




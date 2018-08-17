package ynca.nfs.Adapter;

import android.app.Service;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;

import java.util.ArrayList;

import ynca.nfs.Models.Vehicle;
import ynca.nfs.Models.VehicleService;
import ynca.nfs.R;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> implements Filterable {

    public interface OnItemsClickListener{
        void OnItemClick(int clickItemIndex, VehicleService service);
    }


    private ArrayList<VehicleService> mArrayList;
    private ArrayList<VehicleService> mFilteredList;
    final private OnItemsClickListener OnItemClickListen;


    public  SearchResultAdapter(ArrayList<VehicleService> arrayList, OnItemsClickListener listener)
    {
        mArrayList = arrayList;
        mFilteredList = arrayList;
        this.OnItemClickListen = listener;
    }


    public void add(VehicleService s){
        if (mArrayList == null)
        {
            mArrayList = new ArrayList<VehicleService>();
            mArrayList.add(s);
        }
        else
        {
            if (!mArrayList.contains(s))
            {
                mArrayList.add(s);
            }
        }
        mFilteredList = mArrayList;
    }

    @Override
    public SearchResultAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.filter_map_result, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchResultAdapter.ViewHolder viewHolder, int i) {

        viewHolder.serviceName.setText(mFilteredList.get(i).getName());

    }

    @Override
    public int getItemCount() {
        if (mFilteredList == null)
        {
            return 0;
        }
        else
            return mFilteredList.size();
    }

    private boolean listContains(VehicleService service)
    {
        if (mArrayList == null)
        {
            mArrayList = new ArrayList<VehicleService>();
            return false;
        }
        else if (mArrayList.contains(service))
        {
            return  true;
        }
        else
            return  false;
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();

                if (charString.isEmpty()) {

                    mFilteredList = mArrayList;
                } else {

                    ArrayList<VehicleService> filteredList = new ArrayList<>();

                    for (VehicleService service : mArrayList) {

                        if (service.getName().toLowerCase().contains(charString)) {

                            filteredList.add(service);
                        }
                    }

                    mFilteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredList = (ArrayList<VehicleService>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }




    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView serviceName;


        public ViewHolder(View view) {
            super(view);

            serviceName = (TextView)view.findViewById(R.id.service_name);
            view.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            OnItemClickListen.OnItemClick(clickedPosition, mFilteredList.get(clickedPosition));


        }
    }
}

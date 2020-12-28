package com.example.mapint.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mapint.models.Bevanda;
import com.example.mapint.models.Menu;
import com.example.mapint.R;

import java.util.List;

public class DrinkAdapter extends
        RecyclerView.Adapter<DrinkAdapter.ViewHolder> {



    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView nameTextView, priceTextView;
        public Button messageButton;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.drink_name);
            priceTextView = (TextView) itemView.findViewById(R.id.drink_price);
            messageButton = (Button) itemView.findViewById(R.id.message_button);
        }
    }


    private List<Menu> mBevanda;

    // Pass in the contact array into the constructor
    public DrinkAdapter(List<Menu> bevande) {
        mBevanda = bevande;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View drinkView = inflater.inflate(R.layout.item_drink, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(drinkView);
        return viewHolder;    }

    @Override
    public void onBindViewHolder(DrinkAdapter.ViewHolder holder, int position) {
        // Get the data model based on position
        Menu menu = mBevanda.get(position);
        // Set item views based on your views and data model

        TextView textView = holder.nameTextView;
        textView.setText(menu.getBevanda().getName());

        TextView textView1 = holder.priceTextView;

        textView1.setText(String.valueOf(menu.getPrice()) + " €");
        //Button button = holder.messageButton;
        //button.setText(contact.isOnline() ? "Message" : "Offline");
        //button.setEnabled(contact.isOnline());
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mBevanda.size();
    }
}
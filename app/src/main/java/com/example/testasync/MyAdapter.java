package com.example.testasync;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyAdapterViewHolder>
{
    Context context;
    ArrayList<contact>al;

    public MyAdapter(Context context, ArrayList<contact> al) {
        this.context = context;
        this.al = al;
    }

    @NonNull
    @Override
    public MyAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_view,parent,false);
        return new MyAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapterViewHolder holder, int position) {

        contact c=al.get(position);
        holder.tv.setText(c.getName());
        if(c.getSyncStatus()==0)
        holder.imageView.setImageResource(R.drawable.sync);
        else holder.imageView.setImageResource(R.drawable.check);
    }

    @Override
    public int getItemCount() {
        return al.size();
    }

    public class MyAdapterViewHolder extends RecyclerView.ViewHolder {
        TextView tv;
        ImageView imageView;
        public MyAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            tv=itemView.findViewById(R.id.tv);
            imageView=itemView.findViewById(R.id.iv);
        }
    }
}

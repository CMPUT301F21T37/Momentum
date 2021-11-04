package com.example.momentum.habits;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import android.widget.TextView;
import android.widget.Toast;

import com.example.momentum.Habit;
import com.example.momentum.R;

import java.util.List;

public class HabitsAdapter extends ArrayAdapter<Habit>{

    private int resourceId;
    private Context context;
    /* Rewrite the constructor, that is, the realization of the cross-line part above */
    public HabitsAdapter(Context context, int textViewResourceId, List<Habit> objects){
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
        this.context = context;
    }    /* Override the getView() method, which will be called when each child item is scrolled to the screen */
    public View getView(int position, View convertView, final ViewGroup parent){
        final Habit habits = getItem(position); //Get the Habits instance of the current item
        View view;
        ViewHolder viewHolder;  //The internal classes defined below are used to save instances of image, name, delete, etc., instead of obtaining control instances through the findViewById method every time the slide is loaded.
        /* The converView parameter in the getView() method represents the previously loaded layout */
        if(convertView == null){            //If the converView parameter value is null, use LayoutInflater to load the layout
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            /* Call View's findViewById() method to obtain image and name instances respectively */
            viewHolder.edit = view.findViewById(R.id.edit_button);
            viewHolder.name = (TextView) view.findViewById(R.id.Habits_name);
            viewHolder.delete = view.findViewById(R.id.delete_button);
            view.setTag(viewHolder);
        }
        else{            /* Otherwise, directly reuse converView */
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }        /* Call the setImageResource() and setText() methods to set the displayed image and text*/

        viewHolder.name.setText(habits.getTitle());
        /* event monitoring response part, that is, clicking the delete icon and the avatar will display the reminder information respectively */
        viewHolder.delete = view.findViewById(R.id.delete_button);
        viewHolder.delete.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Toast.makeText(getContext(), "you clicked delete button", Toast.LENGTH_SHORT).show();
            }
        });
        viewHolder.edit.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Toast.makeText(getContext(), "you clicked edit", Toast.LENGTH_SHORT).show();
            }
        });
        return view; //back view
    }
    class ViewHolder{

        TextView name;
        View delete;
        View edit;
    }
}
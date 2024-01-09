package com.example.calenderapp.events;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.calenderapp.calenderView.CalendarUtils;
import com.example.calenderapp.R;
import com.example.calenderapp.events.model.EventModel;

import java.util.List;

public class EventAdapter extends ArrayAdapter<EventModel>
{
    public EventAdapter(@NonNull Context context, List<EventModel> events)
    {
        super(context, 0, events);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {

        EventModel event = getItem(position);


        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.event_cell, parent, false);

        if(event == null)
        {
            TextView eventCellTV = convertView.findViewById(R.id.eventCellTV);
            eventCellTV.setText("No events on this day");
            return convertView;
        }
        TextView eventCellTV = convertView.findViewById(R.id.eventCellTV);

        String eventTitle = event.getEventName() +" "+ event.getStartingTime() + "  ->  " + event.getEndingTime();
        eventCellTV.setText(eventTitle);
        return convertView;
    }
}
package com.example.ticketnew.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import com.example.ticketnew.activity.CheckInforFragment;
import com.example.ticketnew.activity.PayTicketFragment;
import com.example.ticketnew.activity.SelectTicketFragment;

public class TicketFragmentAdapter extends FragmentStatePagerAdapter {
    private int eventId; // Biến lưu trữ eventId

    public TicketFragmentAdapter(@NonNull FragmentManager fm, int behavior, int eventId) {
        super(fm, behavior);
        this.eventId = eventId; // Gán eventId
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                // Truyền eventId vào SelectTicketFragment
                return SelectTicketFragment.newInstance(eventId);
            case 1:
                // Truyền eventId vào CheckInforFragment
                return CheckInforFragment.newInstance(eventId);
            case 2:
                // Truyền eventId vào PayTicketFragment
                return PayTicketFragment.newInstance(eventId);
            default:
                return SelectTicketFragment.newInstance(eventId);
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}

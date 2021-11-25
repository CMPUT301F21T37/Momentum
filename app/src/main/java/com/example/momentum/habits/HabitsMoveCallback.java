package com.example.momentum.habits;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class HabitsMoveCallback extends ItemTouchHelper.Callback {

    private RecyclerViewRowTouchHelperContract touchHelperContract;

    public HabitsMoveCallback(RecyclerViewRowTouchHelperContract touchHelperContract) {
        this.touchHelperContract = touchHelperContract;
    }

    public boolean isLongPressDragEnabled() {
        return true;
    }

    public boolean isItemViewSwipeEnabled() {
        return false;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        // only implementing up and down features
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        return makeMovementFlags(dragFlags, 0);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        this.touchHelperContract.onRowMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder instanceof HabitsAdapter.MyViewModel) {
                HabitsAdapter.MyViewModel habitsViewHolder = (HabitsAdapter.MyViewModel) viewHolder;
                touchHelperContract.onRowSelected(habitsViewHolder);
            }
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        if (viewHolder instanceof HabitsAdapter.MyViewModel) {
            HabitsAdapter.MyViewModel habitsViewHolder = (HabitsAdapter.MyViewModel) viewHolder;
            touchHelperContract.onRowClear(habitsViewHolder);
        }
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

    }

    public interface RecyclerViewRowTouchHelperContract {
        void onRowMoved(int from, int to);
        void onRowSelected(HabitsAdapter.MyViewModel viewHolder);
        void onRowClear(HabitsAdapter.MyViewModel viewHolder);
    }
}

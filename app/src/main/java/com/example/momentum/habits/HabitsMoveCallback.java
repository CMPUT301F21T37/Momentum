package com.example.momentum.habits;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

/**
 * A class callback that extends to RecyclerView's ItemTouchHelper.Callback to help check when an item is moved.
 * @author Kaye Ena Crayzhel F. Misay
 *
 * Reference:
 * Callback class for the recyclerView
 * https://www.youtube.com/watch?v=g6ySj807iTY
 * https://github.com/ravizworldz/Recyclerview_row_Drag_And_Drop
 * By LearningWorldz (Youtube) / ravizworldz (Github)
 */
public class HabitsMoveCallback extends ItemTouchHelper.Callback {

    private RecyclerViewRowTouchHelperContract touchHelperContract;

    public HabitsMoveCallback(RecyclerViewRowTouchHelperContract touchHelperContract) {
        this.touchHelperContract = touchHelperContract;
    }

    /**
     * Class that returns if long press drag is enabled
     * @return
     * true because for the current function, drag and drop are enabled
     */
    public boolean isLongPressDragEnabled() {
        return true;
    }

    /**
     * Class that returns if an item is swiped
     * @return
     * false because for the current function, on swipe is not enabled
     */
    public boolean isItemViewSwipeEnabled() {
        return false;
    }

    /**
     * Get the movement flags given the item
     * @param recyclerView
     * current recyclerView
     * @param viewHolder
     * current viewHolder for the recyclerView
     * @return
     * make the movement flags for drag and swipe (0)
     */
    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        // only implementing up and down features
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        return makeMovementFlags(dragFlags, 0);
    }

    /**
     * A method that handles when a user is moving an item
     * It calss onRowMoved from the adapter.
     * @param recyclerView
     * @param viewHolder
     * @param target
     * @return
     */
    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        this.touchHelperContract.onRowMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    /**
     * A method that handles when a user selected an item
     * It calls onRowSelected from the adapter.
     * @param viewHolder
     * current viewHolder for the recyclerView
     * @param actionState
     * current action state of the user (touched or not)
     */
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

    /**
     * A method that handles when a user stopped clicking (long press) the item
     * It calls onRowClear from the adapter.
     * @param recyclerView
     * current recyclerView
     * @param viewHolder
     * current viewHolder for the recyclerView
     */
    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        if (viewHolder instanceof HabitsAdapter.MyViewModel) {
            HabitsAdapter.MyViewModel habitsViewHolder = (HabitsAdapter.MyViewModel) viewHolder;
            touchHelperContract.onRowClear(habitsViewHolder);
        }
    }

    /**
     * An empty method that the callback needs (the app doesn't use swipe feature)
     * @param viewHolder
     * ViewHolder of the current recycler view
     * @param direction
     * direction of the swipe
     */
    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

    }

    /**
     * an interface for when an item in a recycler view is moved, selected, and cleared (stopped)
     */
    public interface RecyclerViewRowTouchHelperContract {
        void onRowMoved(int from, int to);
        void onRowSelected(HabitsAdapter.MyViewModel viewHolder);
        void onRowClear(HabitsAdapter.MyViewModel viewHolder);
    }
}

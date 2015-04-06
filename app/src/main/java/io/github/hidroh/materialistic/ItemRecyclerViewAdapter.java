package io.github.hidroh.materialistic;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import io.github.hidroh.materialistic.data.ItemManager;

/**
 * Base {@link android.support.v7.widget.RecyclerView.Adapter} class for list items
 * @param <VH>  view holder type, should contain title, posted, source and comment views
 * @param <T>   item type, should provide title, posted, source
 */
public abstract class ItemRecyclerViewAdapter<VH extends ItemRecyclerViewAdapter.ItemViewHolder, T extends ItemManager.WebItem> extends RecyclerView.Adapter<VH> {

    private Context mContext;
    private int mCardBackgroundColorResId;
    private int mCardHighlightColorResId;

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mContext = recyclerView.getContext();
        mCardBackgroundColorResId = AppUtils.getThemedResId(mContext, R.attr.themedCardBackgroundColor);
        mCardHighlightColorResId = AppUtils.getThemedResId(mContext, R.attr.themedCardHighlightColor);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        mContext = null;
        super.onDetachedFromRecyclerView(recyclerView);
    }

    /**
     * Populates view holder with data from given item
     * @param holder    view holder to populate
     * @param item      item that contains data
     */
    protected void bindViewHolder(final VH holder, final T item) {
        holder.mTitleTextView.setText(item.getDisplayedTitle());
        holder.mPostedTextView.setText(item.getDisplayedTime(mContext));
        switch (item.getType()) {
            case job:
                holder.mSourceTextView.setText(null);
                holder.mSourceTextView.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_work_grey600_18dp, 0, 0, 0);
                break;
            case poll:
                holder.mSourceTextView.setText(null);
                holder.mSourceTextView.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_poll_grey600_18dp, 0, 0, 0);
                break;
            default:
                holder.mSourceTextView.setText(item.getSource());
                holder.mSourceTextView.setCompoundDrawables(null, null, null, null);
                break;
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleItemClick(item, holder);
            }
        });
        holder.mCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemSelected(item, holder.itemView);
            }
        });
        decorateCardSelection(holder, item.getId());
    }

    /**
     * Handles item click
     * @param item      clicked item
     * @param holder    clicked item view holder
     */
    protected void handleItemClick(T item, VH holder) {
        onItemSelected(item, holder.itemView);
        if (isSelected(item.getId())) {
            notifyDataSetChanged(); // switch selection decorator
        }
    }

    /**
     * Clears previously bind data from given view holder
     * @param holder    view holder to clear
     */
    protected void clearViewHolder(VH holder) {
        holder.mTitleTextView.setText(mContext.getString(R.string.loading_text));
        holder.mPostedTextView.setText(mContext.getString(R.string.loading_text));
        holder.mSourceTextView.setText(mContext.getString(R.string.loading_text));
        holder.mSourceTextView.setCompoundDrawables(null, null, null, null);
        holder.mCommentButton.setVisibility(View.GONE);
        holder.itemView.setOnClickListener(null);
        holder.itemView.setOnLongClickListener(null);
    }

    /**
     * Handles item selection
     * @param item  item that has been selected
     * @param itemView
     */
    protected abstract void onItemSelected(T item, View itemView);

    /**
     * Checks if item with given ID has been selected
     * @param itemId    item ID to check
     * @return  true if selected, false otherwise or if selection is disabled
     */
    protected abstract boolean isSelected(String itemId);

    private void decorateCardSelection(ItemViewHolder holder, String itemId) {
        ((CardView) holder.itemView).setCardBackgroundColor(
                mContext.getResources().getColor(isSelected(itemId) ?
                        mCardHighlightColorResId : mCardBackgroundColorResId));
    }

    /**
     * Base {@link android.support.v7.widget.RecyclerView.ViewHolder} class for list item view
     */
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        final TextView mPostedTextView;
        final TextView mTitleTextView;
        final View mCommentButton;
        final TextView mSourceTextView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mPostedTextView = (TextView) itemView.findViewById(R.id.posted);
            mTitleTextView = (TextView) itemView.findViewById(R.id.title);
            mSourceTextView = (TextView) itemView.findViewById(R.id.source);
            mCommentButton = itemView.findViewById(R.id.comment);
        }
    }
}

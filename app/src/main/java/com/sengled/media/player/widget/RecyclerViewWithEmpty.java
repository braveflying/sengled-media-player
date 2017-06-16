package com.sengled.media.player.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by admin on 2017/4/25.
 */
public class RecyclerViewWithEmpty extends RecyclerView {

    private View emptyView;

    private ItemRemovedListener removedListener;

    public void setRemovedListener(ItemRemovedListener removedListener) {
        this.removedListener = removedListener;
    }

    public RecyclerViewWithEmpty(Context context) {
        super(context);
    }

    public RecyclerViewWithEmpty(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerViewWithEmpty(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        if(adapter != null) {
            adapter.registerAdapterDataObserver(emptyAdapterDataObserver);
        }
        emptyAdapterDataObserver.onChanged();
    }

    private AdapterDataObserver emptyAdapterDataObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            Adapter<?> adapter =  getAdapter();
            if(adapter != null && emptyView != null) {
                if(adapter.getItemCount() == 0) {
                    emptyView.setVisibility(View.VISIBLE);
                    setVisibility(View.GONE);
                }
                else {
                    emptyView.setVisibility(View.GONE);
                    setVisibility(View.VISIBLE);
                }
            }

        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);
            onChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            super.onItemRangeChanged(positionStart, itemCount, payload);
            onChanged();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            onChanged();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            onChanged();
            if (removedListener != null){
                removedListener.onRemoved(positionStart, itemCount);
            }
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount);
            onChanged();
        }
    };

    public interface ItemRemovedListener{

        void onRemoved(int positionStart, int itemCount);
    }
}

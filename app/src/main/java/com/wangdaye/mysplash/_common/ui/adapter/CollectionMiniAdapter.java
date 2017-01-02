package com.wangdaye.mysplash._common.ui.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.ColorMatrixColorFilter;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash._common.utils.AnimUtils;
import com.wangdaye.mysplash._common.utils.DisplayUtils;
import com.wangdaye.mysplash._common.utils.manager.AuthManager;

/**
 * Collection mini adapter. (Recycler view)
 * */

public class CollectionMiniAdapter extends RecyclerView.Adapter<CollectionMiniAdapter.ViewHolder> {
    // widget
    private Context c;
    private OnCollectionResponseListener listener;

    // data
    private Photo photo;

    /** <br> life cycle. */

    public CollectionMiniAdapter(Context c, Photo p) {
        this.c = c;
        updatePhoto(p);
    }

    /** <br> UI. */

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == getRealItemCount() + 1) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_collection_mini_loading_view, parent, false);
            return new ViewHolder(v, true);
        } else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_collection_mini, parent, false);
            return new ViewHolder(v, false);
        }
    }

    @SuppressLint({"RecyclerView", "SetTextI18n"})
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (position == 0) {
            holder.image.setImageResource(R.color.colorTextSubtitle_light);
            holder.title.setText(c.getString(R.string.feedback_create_collection).toUpperCase());
            holder.subtitle.setVisibility(View.GONE);
            holder.icon.setVisibility(View.GONE);
            return;
        } else if (position == getRealItemCount() + 1) {
            return;
        }

        holder.title.setText(AuthManager.getInstance().getCollectionsManager().getCollectionList().get(position - 1).title.toUpperCase());
        int photoNum = AuthManager.getInstance().getCollectionsManager().getCollectionList().get(position - 1).total_photos;
        holder.subtitle.setText(photoNum + " " + c.getResources().getStringArray(R.array.user_tabs)[0]);

        if (AuthManager.getInstance().getCollectionsManager().getCollectionList().get(position - 1).cover_photo != null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                Glide.with(c)
                        .load(AuthManager.getInstance().getCollectionsManager().getCollectionList().get(position - 1).cover_photo.urls.regular)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(holder.image);
            } else {
                Glide.with(c)
                        .load(AuthManager.getInstance().getCollectionsManager().getCollectionList().get(position - 1).cover_photo.urls.regular)
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model,
                                                           Target<GlideDrawable> target,
                                                           boolean isFromMemoryCache, boolean isFirstResource) {
                                if (!AuthManager.getInstance().getCollectionsManager().getCollectionList().get(position - 1).cover_photo.hasFadedIn) {
                                    holder.image.setHasTransientState(true);
                                    final AnimUtils.ObservableColorMatrix matrix = new AnimUtils.ObservableColorMatrix();
                                    final ObjectAnimator saturation = ObjectAnimator.ofFloat(
                                            matrix, AnimUtils.ObservableColorMatrix.SATURATION, 0f, 1f);
                                    saturation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener
                                            () {
                                        @Override
                                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                            // just animating the color matrix does not invalidate the
                                            // drawable so need this update listener.  Also have to create a
                                            // new CMCF as the matrix is immutable :(
                                            holder.image.setColorFilter(new ColorMatrixColorFilter(matrix));
                                        }
                                    });
                                    saturation.setDuration(2000L);
                                    saturation.setInterpolator(AnimUtils.getFastOutSlowInInterpolator(c));
                                    saturation.addListener(new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            holder.image.clearColorFilter();
                                            holder.image.setHasTransientState(false);
                                        }
                                    });
                                    saturation.start();
                                    AuthManager.getInstance().getCollectionsManager().getCollectionList().get(position - 1).cover_photo.hasFadedIn = true;
                                }
                                return false;
                            }

                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                return false;
                            }
                        })
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(holder.image);
            }
        } else {
            holder.image.setImageResource(R.color.colorTextContent_light);
        }

        for (int i = 0; i < photo.current_user_collections.size(); i ++) {
            if (AuthManager.getInstance()
                    .getCollectionsManager()
                    .getCollectionList()
                    .get(position - 1).id == photo.current_user_collections.get(i).id) {
                Glide.with(c)
                        .load(R.drawable.ic_item_checked)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(holder.icon);
                holder.icon.setVisibility(View.VISIBLE);
                return;
            }
        }
        if (AuthManager.getInstance().getCollectionsManager().getCollectionList().get(position - 1).privateX) {
            holder.icon.setImageResource(R.drawable.ic_item_lock);
            holder.icon.setVisibility(View.VISIBLE);
        } else {
            holder.icon.setVisibility(View.GONE);
        }
    }

    /** <br> data. */

    @Override
    public int getItemCount() {
        if (AuthManager.getInstance().getCollectionsManager().isLoadFinish()) {
            return getRealItemCount() + 1;
        } else {
            return getRealItemCount() + 2;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public int getRealItemCount() {
        return AuthManager.getInstance().getCollectionsManager().getCollectionList().size();
    }

    public void updatePhoto(Photo p) {
        this.photo = p;
    }

    /** <br> interface. */

    public interface OnCollectionResponseListener {
        void onCreateCollection();
        void onClickCollectionItem(int collection_id);
    }

    public void setOnCollectionResponseListener(OnCollectionResponseListener l) {
        this.listener = l;
    }

    /** <br> inner class. */

    // view holder.

    class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        // widget
        public ImageView image;
        public TextView title;
        public TextView subtitle;
        ImageView icon;

        ViewHolder(View itemView, boolean loadingView) {
            super(itemView);
            if (!loadingView) {
                itemView.findViewById(R.id.item_collection_mini_card).setOnClickListener(this);

                this.image = (ImageView) itemView.findViewById(R.id.item_collection_mini_image);

                this.title = (TextView) itemView.findViewById(R.id.item_collection_mini_title);

                this.subtitle = (TextView) itemView.findViewById(R.id.item_collection_mini_subtitle);
                DisplayUtils.setTypeface(itemView.getContext(), subtitle);

                this.icon = (ImageView) itemView.findViewById(R.id.item_collection_icon);

                setIsRecyclable(true);
            } else {
                setIsRecyclable(false);
            }
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.item_collection_mini_card:
                    if (getAdapterPosition() == 0 && listener != null) {
                        listener.onCreateCollection();
                    } else if (listener != null) {
                        listener.onClickCollectionItem(
                                AuthManager.getInstance()
                                        .getCollectionsManager()
                                        .getCollectionList()
                                        .get(getAdapterPosition() - 1).id);
                    }
                    break;
            }
        }
    }
}
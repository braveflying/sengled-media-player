package com.sengled.media.player.widget;


import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.sengled.media.player.R;


/**
 * Created by admin on 2017/4/6.
 */
public class ShareDialog extends DialogFragment {

    private OnDialogClickListener mClickListener;
    private OnDialogDismissListener mDismissListener;

    private Bitmap mBitmap;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getDialog().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setWindowAnimations(R.style.AnimateDialog);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        View view = inflater.inflate(R.layout.media_share_dialog_layout, container);
        ImageView photo = (ImageView) view.findViewById(R.id.iv_screenshot_photo);
        ViewGroup.LayoutParams layoutParams = photo.getLayoutParams();
        layoutParams.width = getResources().getDisplayMetrics().widthPixels * 7 / 10;
        layoutParams.height = getResources().getDisplayMetrics().heightPixels * 7 / 10;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        if (layoutParams.height > layoutParams.width){
        }
        photo.setLayoutParams(layoutParams);

        if (mBitmap != null){
            photo.setImageBitmap(mBitmap);
        }

        view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        TextView tvShare = (TextView) view.findViewById(R.id.btn_share);
        tvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null) {
                    mClickListener.onShare(mBitmap, null);
                }
                dismiss();
            }
        });

        return view;
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (mDismissListener != null) {
            mDismissListener.onDismiss();
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        // 点击外部回调这里
        if (mDismissListener != null) {
            mDismissListener.onDismiss();
        }
    }

    public void setScreenshotPhoto(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    public void setClickListener(OnDialogClickListener clickListener) {
        mClickListener = clickListener;
    }

    public void setDismissListener(OnDialogDismissListener dismissListener) {
        mDismissListener = dismissListener;
    }

    public interface OnDialogClickListener {
        void onShare(Bitmap bitmap, Uri uri);
    }

    public interface OnDialogDismissListener {
        void onDismiss();
    }
}

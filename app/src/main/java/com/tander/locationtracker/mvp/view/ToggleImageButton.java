package com.tander.locationtracker.mvp.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.SoundEffectConstants;

import com.tander.locationtracker.R;

public class ToggleImageButton extends android.support.v7.widget.AppCompatImageButton {

    private boolean mIsActive;

    public boolean isActive() {
        return mIsActive;
    }

    public void setActive(boolean isActive) {
        this.mIsActive = isActive;
        if (mIsActive) {
            setBackgroundResource(R.drawable.button_active);
        } else {
            setBackgroundResource(R.drawable.button_inactive);
        }
        invalidate();
        requestLayout();
    }

    public ToggleImageButton(Context context) {
        super(context);
    }

    public ToggleImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ToggleImageButton,
                0, 0);

        try {
            mIsActive = a.getBoolean(R.styleable.ToggleImageButton_isActive, false);
        } finally {
            a.recycle();
        }
    }

    public ToggleImageButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void toggle() {
        setActive(!mIsActive);
    }

    @Override
    public boolean performClick() {
        toggle();

        final boolean handled = super.performClick();
        if (!handled) {
            // View only makes a sound effect if the onClickListener was
            // called, so we'll need to make one here instead.
            playSoundEffect(SoundEffectConstants.CLICK);
        }

        return handled;
    }
}

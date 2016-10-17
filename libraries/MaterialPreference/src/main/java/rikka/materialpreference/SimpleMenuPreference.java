package rikka.materialpreference;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckedTextView;
import android.widget.PopupWindow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A version of {@link ListPreference} that presents the options in a drop down menu rather than a dialog.
 */
public class SimpleMenuPreference extends ListPreference {

    private final Context mContext;

    private final Adapter mAdapter;

    private PreferenceViewHolder mViewHolder;
    private PopupWindow mPopupWindow;

    private final int POPUP_PADDING_X;
    private final int POPUP_PADDING_Y;
    private final int LIST_PADDING;
    private final int LIST_ITEM_HEIGHT;
    private final Drawable POPUP_BACKGROUND;
    private final float POPUP_ELEVATION;

    private RecyclerView mRecyclerView;

    private boolean mRestorePopup;

    private boolean mShouldCalcUseDialog;
    private boolean mUseDialog;

    private int mPopupWidth;

    public SimpleMenuPreference(Context context) {
        this(context, null);
    }

    public SimpleMenuPreference(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.simpleMenuPreferenceStyle);
    }

    public SimpleMenuPreference(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs, defStyle, 0);
    }

    public SimpleMenuPreference(Context context, AttributeSet attrs, int defStyleAttr,
                              int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.SimpleMenuPreference, defStyleAttr, defStyleRes);

        LIST_PADDING = (int) a.getDimension(R.styleable.SimpleMenuPreference_listPadding, 0);
        LIST_ITEM_HEIGHT = (int) a.getDimension(R.styleable.SimpleMenuPreference_listItemHeight, 0);
        POPUP_PADDING_Y = (int) a.getDimension(R.styleable.SimpleMenuPreference_popupPadding, 0);
        POPUP_ELEVATION = a.getDimension(R.styleable.SimpleMenuPreference_popupElevation, 0);
        POPUP_BACKGROUND = a.getDrawable(R.styleable.SimpleMenuPreference_popupBackground);

        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.listPreferredItemPaddingLeft, typedValue, true);

        // for shadow
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            POPUP_PADDING_X = (int) context.getResources().getDimension(typedValue.resourceId) - 6;
        } else {
            POPUP_PADDING_X = (int) context.getResources().getDimension(typedValue.resourceId) - 4;
        }

        mContext = context;
        mAdapter = createAdapter();
        mPopupWindow = createPopupMenu();
        a.recycle();

        updateEntries();
    }

    private Adapter createAdapter() {
        return new Adapter();
    }

    private PopupWindow createPopupMenu() {
        mRecyclerView = (RecyclerView) LayoutInflater.from(mContext).inflate(R.layout.simple_menu_recycler_view, null);
        mRecyclerView.setPadding(0, LIST_PADDING, 0, LIST_PADDING);
        mRecyclerView.setFocusable(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mAdapter);

        PopupWindow popupWindow = new PopupWindow(mContext);
        popupWindow.setContentView(mRecyclerView);
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(POPUP_BACKGROUND);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            popupWindow.setElevation(POPUP_ELEVATION);
        }

        return popupWindow;
    }

    @Override
    protected void onClick() {
        if (getEntries() == null || getEntries().length == 0) {
            return;
        }

        if (mPopupWindow == null) {
            return;
        }

        if (mUseDialog) {
            showDialog();
        } else {
            showPopupMenu();
        }
    }

    private void resetUseDialog() {
        if (!mShouldCalcUseDialog || mViewHolder == null) {
            return;
        }

        mShouldCalcUseDialog = false;

        boolean useDialog = measurePopupWidth();
        if (mUseDialog != useDialog) {
            mUseDialog = useDialog;

            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Show dialog
     */
    private void showDialog() {
        int index = getValueIndex();
        index = index < 0 ? 0 : index;

        final int height = LIST_ITEM_HEIGHT * getEntries().length + LIST_PADDING * 2;

        View parent = ((View) mViewHolder.itemView.getParent().getParent().getParent());
        int parentHeight = parent.getHeight();

        if (height > parentHeight) {
            mPopupWindow.setHeight(parentHeight - LIST_PADDING * 2);
            mRecyclerView.setOverScrollMode(View.OVER_SCROLL_IF_CONTENT_SCROLLS);
            mRecyclerView.scrollToPosition(index);
        } else {
            mRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mPopupWindow.setElevation(48f);
        }
        mPopupWindow.setAnimationStyle(R.style.Animation_SimpleMenuCenter);
        int width = mViewHolder.itemView.getWidth() - POPUP_PADDING_X * 2;
        mPopupWindow.setWidth(width);
        mPopupWindow.showAtLocation(mViewHolder.itemView, Gravity.CENTER_VERTICAL, 0, 0);
    }

    /**
     * Show PopupMenu and calculate to align selected menu item over list item vertically
     */
    private void showPopupMenu() {
        int index = getValueIndex();
        index = index < 0 ? 0 : index;

        Rect displayRect = new Rect();
        ((Activity) ((ContextWrapper) getContext()).getBaseContext())
                .getWindow().getDecorView().getWindowVisibleDisplayFrame(displayRect);
        int statusBarHeight = displayRect.top;

        int y_off;

        int anchor_y = mViewHolder.itemView.getTop();
        final int height = LIST_ITEM_HEIGHT * getEntries().length + LIST_PADDING * 2;

        View parent = ((View) mViewHolder.itemView.getParent().getParent().getParent());
        int top = parent.getTop();
        int parentHeight = parent.getHeight();

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT
                && ((View) parent.getParent()).getFitsSystemWindows()) {
            Activity activity = null;
            if (parent.getContext() instanceof Activity) {
                activity = (Activity) parent.getContext();
            } else if (parent.getContext() instanceof ContextWrapper) {
                if (((ContextWrapper) parent.getContext()).getBaseContext() instanceof Activity) {
                    activity = (Activity) parent.getContext();
                }
            }

            if (activity != null) {
                if ((activity.getWindow().getAttributes().flags & (WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)) != 0) {
                    statusBarHeight = 0;
                }
            }
        }

        if (height > parentHeight) {
            y_off = top + statusBarHeight + POPUP_PADDING_Y;

            // scroll to select item
            final int scroll = (int) (index * LIST_ITEM_HEIGHT - anchor_y + POPUP_PADDING_Y * 0.5);
            mRecyclerView.post(new Runnable() {
                @Override
                public void run() {
                    mRecyclerView.scrollBy(0, -height);
                    mRecyclerView.scrollBy(0, scroll);
                }
            });

            mRecyclerView.setOverScrollMode(View.OVER_SCROLL_IF_CONTENT_SCROLLS);

            mPopupWindow.setHeight(parentHeight - LIST_PADDING * 2);
        } else {
            y_off = (int) (top + statusBarHeight + POPUP_PADDING_Y + anchor_y - LIST_PADDING * 0.5 - index * LIST_ITEM_HEIGHT);

            int bottom = y_off - statusBarHeight -top + height;

            // make sure PopupWindow in window
            if (bottom > parentHeight - POPUP_PADDING_Y) {
                y_off = top + parentHeight - POPUP_PADDING_Y + statusBarHeight - height;
            } else if (y_off < top + statusBarHeight + POPUP_PADDING_Y) {
                y_off = top + statusBarHeight + POPUP_PADDING_Y;
            }

            mRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);

            mPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        ViewGroup.LayoutParams lp = mRecyclerView.getLayoutParams();
        if (lp == null) {
            lp = new ViewGroup.LayoutParams(mPopupWidth, RecyclerView.LayoutParams.WRAP_CONTENT);
        } else {
            lp.width = mPopupWidth;
        }
        mRecyclerView.setLayoutParams(lp);
        mPopupWindow.setWidth(mPopupWidth);

        // calc what animation should use
        @StyleRes int animationStyle;
        int anchor_center_y = anchor_y + mViewHolder.itemView.getHeight() / 2;
        int popup_center_y = y_off - top - statusBarHeight + height / 2;

        if (height > parentHeight) {
            animationStyle = R.style.Animation_SimpleMenuDown;
            float f = (float) anchor_center_y / popup_center_y;
            if (f > 0.7) {
                animationStyle = R.style.Animation_SimpleMenuUp;
            } else if (f > 0.4) {
                animationStyle = R.style.Animation_SimpleMenuCenter;
            }
        } else {
            if (Math.abs(popup_center_y - anchor_center_y) < LIST_ITEM_HEIGHT * getEntries().length * 0.3) {
                animationStyle = R.style.Animation_SimpleMenuCenter;
            } else if (anchor_center_y > popup_center_y) {
                animationStyle = R.style.Animation_SimpleMenuUp;
            } else {
                animationStyle = R.style.Animation_SimpleMenuDown;
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mPopupWindow.setElevation(POPUP_ELEVATION);
        }

        mPopupWindow.setAnimationStyle(animationStyle);
        mPopupWindow.showAtLocation(mViewHolder.itemView, Gravity.NO_GRAVITY, POPUP_PADDING_X, y_off);
    }

    @Override
    public void setEntries(@NonNull CharSequence[] entries) {
        super.setEntries(entries);
        updateEntries();
    }

    private void updateEntries() {
        mAdapter.notifyDataSetChanged();

        mShouldCalcUseDialog = true;
        resetUseDialog();
    }

    /**
     * Measure PopupWindow width and make sure if we should use dialog
     *
     * @return should use dialog
     */
    private boolean measurePopupWidth() {
        if (mViewHolder == null || getEntries() == null) {
            return false;
        }

        List<CharSequence> list = new ArrayList<>();
        list.addAll(Arrays.asList(getEntries()));
        Collections.sort(list, new Comparator<CharSequence>() {
            @Override
            public int compare(CharSequence o1, CharSequence o2) {
                return o2.length() - o1.length();
            }
        });

        int unit = (int) mContext.getResources().getDimension(R.dimen.simple_menu_unit);
        int maxUnits = mContext.getResources().getInteger(R.integer.simple_menu_max_units);
        int maxWidth = unit * maxUnits;

        if (mViewHolder.itemView.getWidth() < maxWidth) {
            maxWidth = mViewHolder.itemView.getWidth() - POPUP_PADDING_X * 3;
        }

        mPopupWidth = unit * 2;

        Rect bounds = new Rect();
        Paint textPaint = new TextPaint();
        textPaint.setTextSize(16 * mContext.getResources().getDisplayMetrics().scaledDensity);

        for (CharSequence chs : list) {
            textPaint.getTextBounds(chs.toString(), 0, chs.length(), bounds);
            int width = bounds.width() + POPUP_PADDING_X * 3;
            //Log.d("SimpleMenuPreference", "" + width + " " + mPopupMaxWidth);

            if (width > mPopupWidth) {
                mPopupWidth = width;
            }

            if (width > maxWidth) {
                return true;
            }
        }

        // a multiple of a 56dp unit
        int w = 0;
        while (mPopupWidth > w) {
            w += unit;
        }
        mPopupWidth = w;

        return false;
    }

    @Override
    public void setValueIndex(int index) {
        setValue(getEntryValues()[index].toString());
	    if (getOnPreferenceChangeListener() != null) {
		    getOnPreferenceChangeListener().onPreferenceChange(this, getValue());
	    }
    }

    private int getValueIndex() {
        return findIndexOfValue(getValue());
    }

    @Override
    protected void notifyChanged() {
        super.notifyChanged();
        mAdapter.notifyDataSetChanged();
    }

    /*private class RestoreRunnable implements Runnable {
        View mView;

        public RestoreRunnable(View view) {
            mView = view;
            mView.postDelayed(this, 100);
        }

        @Override
        public void run() {
            if (mViewHolder.itemView.getParent() != null
                    && mViewHolder.itemView.getParent().getParent() != null
                    && mViewHolder.itemView.getParent().getParent().getParent() != null) {

                onClick();
                mRestorePopup = false;
            } else {
                mViewHolder.itemView.postDelayed(this, 100);
            }
        }
    }*/

    @Override
    public void onBindViewHolder(PreferenceViewHolder view) {
        super.onBindViewHolder(view);

        mViewHolder = view;

        /*if (mRestorePopup) {
            new RestoreRunnable(mViewHolder.itemView);
        }*/

        mViewHolder.itemView.post(new Runnable() {
            @Override
            public void run() {
                resetUseDialog();
            }
        });
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();

        final SavedState myState = new SavedState(superState);
        myState.value =
                mPopupWindow != null && mPopupWindow.isShowing() ? 1 : 0;

        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }

        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state == null || !state.getClass().equals(SavedState.class)) {
            // Didn't save state for us in onSaveInstanceState
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());
        if (myState.value == 1) {
            mRestorePopup = true;
        }
    }

    private static class SavedState extends BaseSavedState {
        int value;

        public SavedState(Parcel source) {
            super(source);
            value = source.readInt();
        }

        @Override
        public void writeToParcel(@NonNull Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(value);
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_menu_item, parent, false));
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mCheckedTextView.setText(getEntries()[position]);
            holder.mCheckedTextView.setChecked(position == findIndexOfValue(getValue()));
            holder.mCheckedTextView.setMaxLines(mUseDialog ? 99 : 1);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setValueIndex(holder.getAdapterPosition());

                    if (mPopupWindow != null && mPopupWindow.isShowing()) {
                        mPopupWindow.dismiss();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return getEntries() == null ? 0 : getEntries().length;
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        public CheckedTextView mCheckedTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            mCheckedTextView = (CheckedTextView) itemView.findViewById(android.R.id.text1);
        }
    }
}

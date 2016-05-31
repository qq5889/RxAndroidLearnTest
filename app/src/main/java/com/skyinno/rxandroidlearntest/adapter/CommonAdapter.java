package com.skyinno.rxandroidlearntest.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.skyinno.rxandroidlearntest.App;

import java.util.List;

/**
 * 通用适配器
 *
 * @param <T>
 */
public abstract class CommonAdapter<T> extends BaseAdapter {
    protected LayoutInflater mInflater;
    protected Context mContext;
    protected List<T> mDatas;
    protected final int mItemLayoutId;

    public CommonAdapter(Context context, List<T> mDatas, int itemLayoutId) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(mContext);
        this.mDatas = mDatas;
        this.mItemLayoutId = itemLayoutId;
    }

    @Override
    public int getCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    @Override
    public T getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder = getViewHolder(position, convertView,
                parent);
        convert(viewHolder, getItem(position));
        return viewHolder.getConvertView();

    }

    /**
     * 用户实现，用于插入参数
     */
    public abstract void convert(ViewHolder holder, T item);

    private ViewHolder getViewHolder(int position, View convertView,
                                     ViewGroup parent) {
        return ViewHolder.get(mContext, convertView, parent, mItemLayoutId,
                position);
    }

    public static class ViewHolder {
        private final SparseArray<View> mViews;
        private int mPosition;
        private View mConvertView;
        private Context mContext;

        private ViewHolder(Context context, ViewGroup parent, int layoutId, int position) {
            this.mContext = context;
            this.mPosition = position;
            this.mViews = new SparseArray<View>();
            mConvertView = LayoutInflater.from(context).inflate(layoutId, parent,
                    false);
            // setTag
            mConvertView.setTag(this);
        }

        /**
         * 拿到一个ViewHolder对象
         *
         * @param context     上下文
         * @param convertView itmeView
         * @param parent
         * @param layoutId    itemLayoutId
         * @param position    当前item的位置
         * @return ViewHolder
         */
        public static ViewHolder get(Context context, View convertView,
                                     ViewGroup parent, int layoutId, int position) {
            if (convertView == null) {
                return new ViewHolder(context, parent, layoutId, position);
            }
            return (ViewHolder) convertView.getTag();
        }

        public View getConvertView() {
            return mConvertView;
        }

        /**
         * 通过控件的Id获取对于的控件，如果没有则加入views
         *
         * @param viewId item中控件的id
         * @return T 自定义实体
         */
        @SuppressWarnings("unchecked")
        public <T extends View> T getView(int viewId) {
            View view = mViews.get(viewId);
            if (view == null) {
                view = mConvertView.findViewById(viewId);
                mViews.put(viewId, view);
            }
            return (T) view;
        }

        /**
         * 给item中id为viewId的textView设置字符串
         *
         * @param viewId itemlayout中textView的id
         * @param text   显示字符串
         * @return ViewHolder
         */
        public ViewHolder setText(int viewId, String text) {
            TextView view = getView(viewId);
            view.setText(text);
            return this;
        }

        /**
         * 给item中id为viewId的ImageView设置图片
         *
         * @param viewId     itemlayout中textView的id
         * @param drawableId 资源id
         * @return ViewHolder
         */
        public ViewHolder setImageResource(int viewId, int drawableId) {
            ImageView view = getView(viewId);
            view.setImageResource(drawableId);
            return this;
        }

        /**
         * 给item中id为viewId的ImageView设置图片
         *
         * @param viewId itemlayout中textView的id
         * @param bm     bitmap
         * @return ViewHolder
         */
        public ViewHolder setImageBitmap(int viewId, Bitmap bm) {
            ImageView view = getView(viewId);
            view.setImageBitmap(bm);
            return this;
        }

        public ViewHolder setImageByName(int viewId, String name) {
            if (!TextUtils.isEmpty(name)) {
                ImageView view = getView(viewId);
                int icId = mContext.getResources().getIdentifier(name, "mipmap", mContext.getPackageName());
                if (icId != 0) {
                    view.setImageResource(icId);
                }
            }
            return this;
        }

        /**
         * 给item中id为viewId的ImageView设置图片 url根据isFromNet参数来确定是本地还是网络
         *
         * @param viewId viewIditemlayout中textView的id
         * @param url    本地or网络url
         * @return ViewHolder
         */
        public ViewHolder setImageByUrl(int viewId, String url) {
            Glide.with(App.getAppContext()).load(url).centerCrop().into((ImageView) getView(viewId));
//			Glide.with(mActivity).load(img_url).centerCrop()
//            /*
//             * 缺省的占位图片，一般可以设置成一个加载中的进度GIF图
//             */
//					.placeholder(R.drawable.loading).crossFade().into(iv);
            return this;
        }

        public int getPosition() {
            return mPosition;
        }

    }
}

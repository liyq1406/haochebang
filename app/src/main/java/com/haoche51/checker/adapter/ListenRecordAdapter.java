package com.haoche51.checker.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haoche51.checker.GlobalData;
import com.haoche51.checker.R;
import com.haoche51.checker.entity.RecordEntity;
import com.haoche51.checker.util.UnixTimeUtil;

import java.util.List;

/**
 * Created by mac on 15/11/11.
 */
public class ListenRecordAdapter extends ArrayAdapter<RecordEntity> {

  private PlayListener playListener;

  public ListenRecordAdapter(Context context, List<RecordEntity> infos, PlayListener playListener) {
    super(context, 0, infos);
    this.playListener = playListener;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup viewGroup) {
    if (convertView == null) {
      convertView = View.inflate(this.getContext(), R.layout.layout_phone_record_list_item, null);
    }

    ViewHolder holder = getHolder(convertView);
    setData(getItem(position), holder);
    return convertView;
  }

  private void setData(final RecordEntity item, ViewHolder holder) {

    holder.textViewType.setText(item.getCall_type() == 0 ? GlobalData.resourceHelper.getString(R.string.answer) : GlobalData.resourceHelper.getString(R.string.callout));
    holder.textViewTime.setText(UnixTimeUtil.formatTransferTime(item.getCreate_time()));
    holder.textViewDuration.setText(UnixTimeUtil.getFormatTime(item.getCall_duration()));
    holder.ivPlayState.setVisibility(item.isPaly() ? View.VISIBLE : View.GONE);

    holder.llRecordItem.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (!item.isPaly()) {
          //把之前的播放状态都设为未播放
          for (int i = 0; i < getCount(); i++) {
            getItem(i).setPaly(false);
          }
          //刷新状态
          item.setPaly(true);
          ListenRecordAdapter.this.notifyDataSetChanged();
          if (playListener != null) {
            playListener.play(item.getRecord_url());
          }
        }
      }
    });
  }

  private ViewHolder getHolder(View convertView) {
    ViewHolder holder = (ViewHolder) convertView.getTag();
    if (holder == null)
      holder = new ViewHolder(convertView);
    convertView.setTag(holder);
    return holder;
  }


  class ViewHolder {
    LinearLayout llRecordItem;
    TextView textViewType, textViewTime, textViewDuration, textViewOperator, textViewRole;
    ImageView ivPlayState;

    public ViewHolder(View convertView) {
      llRecordItem = (LinearLayout) convertView.findViewById(R.id.ll_layout_phone_record_list_item);
      textViewOperator = (TextView) convertView.findViewById(R.id.tv_layout_phone_record_list_item_operator);
      textViewRole = (TextView) convertView.findViewById(R.id.tv_layout_phone_record_list_item_role);
      textViewType = (TextView) convertView.findViewById(R.id.tv_layout_phone_record_list_item_type);
      textViewTime = (TextView) convertView.findViewById(R.id.tv_layout_phone_record_list_item_time);
      textViewDuration = (TextView) convertView.findViewById(R.id.tv_layout_phone_record_list_item_duration);
      ivPlayState = (ImageView) convertView.findViewById(R.id.iv_layout_phone_record_list_item_play_state);
    }
  }

  public interface PlayListener {
    void play(String url);
  }
}
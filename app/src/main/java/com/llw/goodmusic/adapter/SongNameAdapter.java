package com.llw.goodmusic.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.android.material.textview.MaterialTextView;
import com.llw.goodmusic.R;
import com.llw.goodmusic.bean.Song;

import java.util.List;

/**
 * 歌名适配器 用于底部播放布局的滑动切换歌名
 *
 * @author llw
 */
public class SongNameAdapter extends BaseQuickAdapter<Song, BaseViewHolder> {
    public SongNameAdapter(int layoutResId, @Nullable List<Song> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Song item) {
        MaterialTextView tvSongName = helper.getView(R.id.tv_song_name);
        tvSongName.setText(item.getSong() +" - "+ item.getSinger());
        tvSongName.setSelected(true);
    }
}

package cn.jinmiao.bbs.perfectstoryapp.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cn.jinmiao.bbs.perfectstoryapp.R;
import cn.jinmiao.bbs.perfectstoryapp.bean.HomeCatalog;

/**
 * Created by Administrator on 2016/12/14 0014.
 */

public class MyHomeAdapter extends SimpleBaseAdapter<String> {

    private int[] icons = new int[]{
            R.mipmap.sleep,
            R.mipmap.children,
            R.mipmap.shige,
            R.mipmap.country,
            R.mipmap.listen,
            R.mipmap.child_dt,
            R.mipmap.english,
            R.mipmap.listen_ben,
            R.mipmap.qita};



    /**
     * 实例化该对象，传递过来数据
     *
     * @param context
     * @param data
     */
    public MyHomeAdapter(Context context, List<String> data) {
        super(context, data);
    }

    @Override
    public int getItemResource() {
        return R.layout.m_gradview_item_main;
    }

    @Override
    public View getItemView(final int position, View convertView, ViewHolder holder) {
        ImageView iv = (ImageView) holder.getView( R.id.m_image);
        final TextView tv = (TextView) holder.getView(R.id.m_text);

        iv.setImageResource(icons[position]);
        tv.setText(data.get(position));
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position==8){
                    Toast.makeText(v.getContext(),"音频正在收集中...",Toast.LENGTH_SHORT).show();
                    return;
                }


               Intent intent = new Intent(context,SecondMenuActivity.class);
               intent.putExtra("position",position);
                intent.putExtra("text",tv.getText().toString());
              context.startActivity(intent);
            }
        });

        return convertView;
    }
}

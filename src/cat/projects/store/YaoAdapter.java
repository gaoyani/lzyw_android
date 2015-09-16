package cat.projects.store;

import java.util.List;
import java.util.Map;

import com.huiwei.roomreservation.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class YaoAdapter extends BaseAdapter {

	private List<Map<String, Object>> list;
	private Context context;
	private LayoutInflater inflater;
	private int[] imgs = { R.drawable.img0, R.drawable.img1, R.drawable.img2,
			R.drawable.img3, R.drawable.img5, R.drawable.img6, R.drawable.img7,
			R.drawable.img8 };

	public YaoAdapter(List<Map<String, Object>> list, Context context) {
		this.list = list;
		inflater = LayoutInflater.from(context);
		this.context = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ImageView imageView;
		final int llposition=position;
		if (convertView == null) {

			convertView = inflater.inflate(R.layout.yao_da, null);
			imageView = (ImageView) convertView.findViewById(R.id.yao_img);
			convertView.setTag(imageView);

		} else {
			imageView = (ImageView) convertView.getTag();
		}
		if (llposition==0) {
//			LayoutParams params=imageView.getLayoutParams();
//			params.height=300;
//			params.width=172;
//			imageView.setLayoutParams(params);
		}
		imageView.setImageResource(imgs[position]);
		
		return convertView;
	}

}

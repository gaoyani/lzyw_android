package cat.projects.store;

import java.util.List;
import java.util.Map;

import com.huiwei.roomreservation.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class YaoListadapter extends BaseAdapter {
	private List<Map<String, Object>> list;
	private Context context;
	public int[] imgs = { R.drawable.img0, R.drawable.img1, R.drawable.img2,
			R.drawable.img3, R.drawable.img5, R.drawable.img6, R.drawable.img7,
			R.drawable.img8, R.drawable.img0, R.drawable.img1, R.drawable.img2,
			R.drawable.img3, R.drawable.img5, R.drawable.img6, R.drawable.img7,
			R.drawable.img8, R.drawable.img0, R.drawable.img1, R.drawable.img2,
			R.drawable.img3, R.drawable.img5, R.drawable.img6, R.drawable.img7,
			R.drawable.img8 };

	public YaoListadapter(List<Map<String, Object>> list, Context context) {
		this.list = list;
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

		ImageView imageView = new ImageView(context);
		imageView.setImageResource(imgs[position]);

		return imageView;
	}
}

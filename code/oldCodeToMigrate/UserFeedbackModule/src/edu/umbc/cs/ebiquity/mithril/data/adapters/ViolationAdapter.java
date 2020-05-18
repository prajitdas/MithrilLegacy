package edu.umbc.cs.ebiquity.mithril.data.adapters;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import edu.umbc.cs.ebiquity.mithril.MithrilApplication;
import edu.umbc.cs.ebiquity.mithril.R;
import edu.umbc.cs.ebiquity.mithril.data.model.Violation;

public class ViolationAdapter extends ArrayAdapter<Violation> {

	private List<Violation> violationList;
	private Context context;

	public ViolationAdapter(Context context, int resource,
			List<Violation> objects) {
		super(context, resource, objects);

		this.context = context;
		this.violationList = objects;
	}

	@Override
	public int getCount() {
		return ((violationList != null) ? violationList.size() : 0);
	}

	@Override
	public Violation getItem(int position) {
		return 	((violationList != null) ? violationList.get(position) : null);
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		
		if(view == null) {
			LayoutInflater layoutInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = layoutInflater.inflate(R.layout.violation_list_item, null);
		}
		
		Log.v(MithrilApplication.getConstDebugTag(), Boolean.toString(violationList==null));
		Log.v(MithrilApplication.getConstDebugTag(), Integer.toString(violationList.size()));
		Log.v(MithrilApplication.getConstDebugTag(), violationList.get(0).toString());
		Violation data = violationList.get(position);
		
		if(data != null) {
			TextView violationName = (TextView) view.findViewById(R.id.violation_name_text_view_id);
//			CheckBox violationCheckBox = (CheckBox) view.findViewById(R.id.violation_check_box_id);
			
			violationName.setText(data.getViolationDescription());
//			violationCheckBox.setChecked(false);
		}
		return view;
	}
}
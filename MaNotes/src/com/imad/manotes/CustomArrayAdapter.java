package com.imad.manotes;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


class CustomArrayAdapter<T> extends ArrayAdapter<T> {
	public CustomArrayAdapter(Context ctx, T[] objects) {
		super(ctx, android.R.layout.simple_spinner_item, objects);
	}

	// other constructors

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);

		// we know that simple_spinner_item has android.R.id.text1 TextView:

		/* if(isDroidX) { */
		// LayoutParams params = new
		// LayoutParams(LayoutParams.MATCH_PARENT,100);

		TextView text = (TextView) view.findViewById(android.R.id.text1);
		text.setTextColor(Color.LTGRAY);
		text.setBackgroundColor(Color.BLACK);
		text.setHeight(85);
		/* } */

		return view;

	}

}
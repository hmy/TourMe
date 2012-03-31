package com.cs194.tourme;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class CustomItemizedOverlay extends ItemizedOverlay<OverlayItem> {

	private ArrayList<OverlayItem> mapOverlays = new ArrayList<OverlayItem>();

	private Context context;

	public CustomItemizedOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
	}

	public CustomItemizedOverlay(Drawable defaultMarker, Context context) {
		this(defaultMarker);
		this.context = context;
	}

	@Override
	protected OverlayItem createItem(int i) {
		return mapOverlays.get(i);
	}

	@Override
	public int size() {
		return mapOverlays.size();
	}

	@Override
	protected boolean onTap(int index) {
		// when you click you can trigger the event
		OverlayItem item = mapOverlays.get(index);
		AlertDialog.Builder dialogBuilding = new AlertDialog.Builder(context);
		dialogBuilding.setTitle(item.getTitle());
		dialogBuilding.setMessage(item.getSnippet());

		//button to dismiss
		/*
		dialog.setPositiveButton("Dismiss",	new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog,int which) {
				dialog.cancel();
			}
		});
		*/
		AlertDialog dialog = dialogBuilding.create();
		dialog.show();
		dialog.setCanceledOnTouchOutside(true); 


		return true;
	}

	public void addOverlay(OverlayItem overlay) {
		mapOverlays.add(overlay);
		this.populate();
	}

}

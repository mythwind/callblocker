package com.vincent.callblocker;

import java.util.Map;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;

public class MainActivity extends Activity {

	private CheckBox checkBox;
	private ListView listView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		checkBox =  (CheckBox) findViewById(R.id.checkbox);
		unregistRecever(checkBox.isChecked());
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				unregistRecever(isChecked);
			}
		});
		
		listView = (ListView) findViewById(R.id.listview);
		
		
		SharedPreferences phonenumSP = getSharedPreferences("in_phone_num", Context.MODE_PRIVATE);  
        Map<?, ?> map = phonenumSP.getAll();  
        Object[] array = map.keySet().toArray();  
        Log.v("tag",map.toString()+map.size());
        ArrayAdapter<Object> adapter = new ArrayAdapter<Object>(this, android.R.layout.simple_list_item_1, array);  
        listView.setAdapter(adapter);
         
	}
	
	private void unregistRecever(boolean checked) {
		PackageManager pm = getPackageManager();
		int flag = checked ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED : PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
		pm.setComponentEnabledSetting(new ComponentName(this, PhoneStatReceiver.class),
				flag, PackageManager.DONT_KILL_APP);
	}

}

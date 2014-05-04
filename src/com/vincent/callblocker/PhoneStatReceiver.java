package com.vincent.callblocker;

import java.lang.reflect.Method;
import java.util.ArrayList;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;

public class PhoneStatReceiver extends BroadcastReceiver {

	private static final String TAG = "PhoneStatReceiver";
	private static boolean mIncomingFlag = false;
	private static String mIncomingNumber = null;
	
	private TelephonyManager mTelephonyManager;

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		// 如果是去电
		if (action.equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
			mIncomingFlag = false;
			String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
			Log.i(TAG, "call OUT:" + phoneNumber);
		} else if (action.equals("android.intent.action.PHONE_STATE")) {
			// 如果是来电
			mTelephonyManager = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
			switch (mTelephonyManager.getCallState()) {
			case TelephonyManager.CALL_STATE_RINGING:
				mIncomingFlag = true;// 标识当前是来电

				AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
				String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);   
                Log.v(TAG,"number:"+number);  
                // if (!getPhoneNum(context).contains(number)) {
                if(!"18768121416".equals(number)) {
                	//先静音处理   
                    mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    
                    SharedPreferences preferences = context.getSharedPreferences("in_phone_num", Context.MODE_PRIVATE);  
                    SharedPreferences.Editor editor = preferences.edit();  
                    editor.putString(number, number);  
                    editor.commit();  
                    endCall(); 
                    
                    //再恢复正常铃声   
                    mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                }  
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				if (mIncomingFlag) {
					Log.i(TAG, "incoming ACCEPT :" + mIncomingNumber);
				}
				break;

			case TelephonyManager.CALL_STATE_IDLE:
				if (mIncomingFlag) {
					Log.i(TAG, "incoming IDLE");
				}
				break;
			}
		}
	}
	
//	//电话拦截
//	  public void stop(String incoming_number) {
//		  AudioManager mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
//		  mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);//静音处理
//		  iTelephony = getITelephony(mContext); //获取电话接口
//		  try {
//			  iTelephony.endCall();//结束电话
//		  } catch (RemoteException e) {
//			  e.printStackTrace();
//		  }
//		  //再恢复正常铃声  
//		  mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
//		  Log.i("----", "来电 :"+ incoming_number); 
//	}
	  
	  /** 
	     * 挂断电话 
	     */  
	    private void endCall() {  
	        Class<TelephonyManager> mClazz = TelephonyManager.class;           
	        try  
	        {  
	            Method getITelephonyMethod = mClazz.getDeclaredMethod("getITelephony", (Class[]) null);  
	            getITelephonyMethod.setAccessible(true);  
	            ITelephony iTelephony = (ITelephony) getITelephonyMethod.invoke(mTelephonyManager, (Object[]) null);
	            Log.e(TAG, "End call.");  
	            iTelephony.endCall();  
	        } catch (Exception e) {  
	            Log.e(TAG, "Fail to answer ring call.", e);  
	        }          
	    }  
	    @SuppressWarnings("unused")
		private ArrayList<String> getPhoneNum(Context context) {  
	        ArrayList<String> numList = new ArrayList<String>();  
	        //得到ContentResolver对象     
	        ContentResolver cr = context.getContentResolver();       
	        //取得电话本中开始一项的光标     
	        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);     
	        while (cursor.moveToNext()) {                 
	            // 取得联系人ID     
	            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));     
	            Cursor phone = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,  
	                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);     
	            // 取得电话号码(可能存在多个号码)     
	            while (phone.moveToNext()){     
	                String strPhoneNumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));     
	                numList.add(strPhoneNumber);    
	                Log.v("tag","strPhoneNumber:"+strPhoneNumber);  
	            }     
	              
	            phone.close();     
	        }     
	        cursor.close();  
	        return numList;  
	    }

}

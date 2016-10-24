package lcpckp.my49ersense;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class CommunityHomeActivity extends Activity {
	/** Called when the activity is first created. */

	// this spin loads the houses as an array
	static ArrayList<String> houseList = new ArrayList<String>();

	// loads the appliances
	static ArrayList<String> appList = new ArrayList<String>();
	static ArrayList<Appliance> appliances = new ArrayList<Appliance>();
	private String[] attributes = {"Appliance ID","Lumens", "Size", "Voltage","Watts"};
	static int housePosition, attributePosition, appliancePosition;
	static String selAppliance;;
	static int watts, Lumens, startTime, endTime;
	private Handler handler;
	private Bundle bundle;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.community);
		final Spinner houseSpinner = (Spinner) findViewById(R.id.spinner1);
		final Spinner appSpinner= (Spinner) findViewById(R.id.spinner2);
		final Spinner appAttributeSpinner= (Spinner) findViewById(R.id.spinner3);
		final TextView attrName = (TextView)findViewById(R.id.textView1);
		final TextView attrValue = (TextView)findViewById(R.id.textView2);
		final Button blogButton = (Button)findViewById(R.id.button5);
		final Button inboxButton = (Button)findViewById(R.id.button3);
		final Button questionButton = (Button)findViewById(R.id.button4);
		final Button statsButton = (Button)findViewById(R.id.show_stats);
		Thread threader = new Thread(getData);
		threader.start();

		//handler to deal with the new connection and message passing
		handler = new Handler(new Handler.Callback() {
			public boolean handleMessage(Message msg) {
				if(msg.getData().containsKey("ERROR")){
					Toast.makeText(getBaseContext(), msg.getData().getString("ERROR"), Toast.LENGTH_LONG).show();
				} 
				else if (msg.getData().containsKey("ListHouse")){

					houseList = msg.getData().getStringArrayList("ListHouse");

					//Array adapter for spinner 1 (houses)
					ArrayAdapter<String> houseAdapter = new ArrayAdapter<String>(CommunityHomeActivity.this, android.R.layout.simple_spinner_item,houseList);
					houseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
					houseSpinner.setPrompt("Choose the House");
					houseSpinner.setAdapter(houseAdapter);


					//following line is the listener will call the function after oncreate
					houseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> arg0,
								View arg1, int housePos, long arg3) {
							housePosition = housePos;
							attrName.setText("");
							attrValue.setText("");
							blogButton.setEnabled(false);
							new Thread(getAppliances).start();
						}

						@Override
						public void onNothingSelected(AdapterView<?> arg0) {
							// TODO Auto-generated method stub
						}
					});
				}
				else if(msg.getData().containsKey("ListAppliance")){
					appList = msg.getData().getStringArrayList("ListAppliance");
					//Array adapter for spinner 1 (houses)
					ArrayAdapter<String> appAdapter = new ArrayAdapter<String>(CommunityHomeActivity.this, android.R.layout.simple_spinner_item,appList);
					appAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
					appSpinner.setPrompt("Choose the Appliance");
					appSpinner.setAdapter(appAdapter);


					//following line is the listener will call the function after oncreate
					appSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> arg0,
								View arg1, int arg2, long arg3) {
							// TODO Auto-generated method stub

							selAppliance = appSpinner.getItemAtPosition(arg2).toString();
							appliancePosition = arg2;
								
							attrName.setText("");
							attrValue.setText("");
							blogButton.setEnabled(false);
						}

						@Override
						public void onNothingSelected(AdapterView<?> arg0) {
							// TODO Auto-generated method stub

						}
					});
				}
				return true;
			}
		});

		//following line is the listener will call the function after oncreate

		ArrayAdapter<String> attrAdapter = new ArrayAdapter<String>(CommunityHomeActivity.this, 
				android.R.layout.simple_spinner_item,attributes);
		attrAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
		appAttributeSpinner.setPrompt("Choose the Attribute");
		appAttributeSpinner.setAdapter(attrAdapter);
		appAttributeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0,
					View arg1, int attrPos, long arg3) {
				attributePosition = attrPos;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});

		Button submitButton = (Button)findViewById(R.id.button1);
		submitButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				
				attrName.setText(attributes[attributePosition].toString());
				blogButton.setEnabled(true);
				
				switch (attributePosition){
				case 0:
					attrValue.setText(Integer.toString(appliances.get(appliancePosition).getApplianceId()));
					break;
				case 1:
					attrValue.setText(Integer.toString(appliances.get(appliancePosition).getLumens()));
					break;
				case 2:
					attrValue.setText(Integer.toString(appliances.get(appliancePosition).getSize()));
					break;
				case 3:
					attrValue.setText(Integer.toString(appliances.get(appliancePosition).getVoltage()));
					break;
				case 4:
					attrValue.setText(Integer.toString(appliances.get(appliancePosition).getWatts()));
					break;
				default:
					Toast.makeText(getBaseContext(), "Something wrong!", Toast.LENGTH_SHORT).show();
				}
			}
		});

		
		blogButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				Intent blogActivity = new Intent(getBaseContext(),BlogSpotActivity.class);
				blogActivity.getExtras();
				bundle = getIntent().getExtras();
				bundle.putString("Appliance", selAppliance);
				blogActivity.putExtras(bundle);
				startActivity(blogActivity);
			}
		});

		statsButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent statsActivity = new Intent(getBaseContext(),StatsActivity.class);
				statsActivity.getExtras();
				bundle = getIntent().getExtras();
				bundle.putString("Appliance", selAppliance);
				statsActivity.putExtras(bundle);
				startActivity(statsActivity);
			}
		});
		
		questionButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent questionActivity = new Intent(getBaseContext(),QuestionsActivity.class);
				questionActivity.getExtras();
				bundle = getIntent().getExtras();
				questionActivity.putExtras(bundle);
				startActivity(questionActivity);
			}
		});

		inboxButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent inboxHomeIntent = new Intent(getBaseContext(),InboxHomeActivity.class);
				inboxHomeIntent.getExtras();
				bundle = getIntent().getExtras();
				inboxHomeIntent.putExtras(bundle);
				startActivity(inboxHomeIntent);	
			}
		});
		
		//new thread to start a new connection to retreive the xml

	}
	/* 
	 * the runnable started by new thread
	 *    
	 */
	private Runnable getData = new Runnable() {
		public void run() {
			Bundle bundle = new Bundle();
			Message msg = new Message();
			String result = getConnection("http://192.168.0.10/index.php","House");
			houseList.clear();

			//parse json data
			try{
				JSONArray jArray = new JSONArray(result);
				for(int i=0;i<jArray.length();i++){
					JSONObject json_data = jArray.getJSONObject(i);

					// LIST Of houses will be loaded into spin
					houseList.add(json_data.getString("TABLE_NAME"));
				}
			}
			catch(JSONException e){
				//Log.e("log_tag", "Error parsing data "+e.toString());
				Log.e("log_tag", "Error parsing data "+e.toString());
			}
			bundle.putStringArrayList("ListHouse", houseList);
			msg.setData(bundle);
			handler.sendMessage(msg);
		}
	};


	private Runnable getAppliances = new Runnable() {
		public void run() {

			Bundle bundle = new Bundle();
			Message msg = new Message();
			Appliance appliance;
			InputStream newIS =null;
			String newResult = getConnection("http://192.168.0.10/index.php","Appliances");
			appList.clear();
			appliances.clear();


			//parse json data following codes:
			// important: here we are getting all the data of the appliance, but 
			// now we are only parsing the "applianceName" for listing. we can for eg: parse 'watts' using
			// json.getInt("watts") and save it to one arraylist
			// all the data now present on phone
			try{
				JSONArray jsArray = new JSONArray(newResult);
				for(int i=0;i<jsArray.length();i++){

					JSONObject json_data = jsArray.getJSONObject(i);
					appliance = new Appliance();
					appliance.setApplianceId(json_data.getInt("applianceID"));
					appliance.setApplianceName(json_data.getString("applianceName"));
					appliance.setLumens(json_data.getInt("lumens"));
					appliance.setSize(json_data.getInt("size"));
					appliance.setVoltage(json_data.getInt("Voltage"));
					appliance.setWatts(json_data.getInt("watts"));

					appliances.add(appliance);

					appList.add(json_data.getString("applianceName"));

					/*watts = json_data.getInt("watts");
					Lumens = json_data.getInt("lumens");
					startTime = json_data.getInt("size");
					endTime = json_data.getInt("Voltage");*/
				}
			}
			catch(JSONException e){
				//Log.e("log_tag", "Error parsing data "+e.toString());
				Log.e("log_tag", "Error parsing data "+e.toString());

			}

			bundle.putStringArrayList("ListAppliance", appList);
			msg.setData(bundle);
			handler.sendMessage(msg);
		}

	};

	public String getConnection(String url, String request){

		Bundle bundle = new Bundle();
		Message msg = new Message();
		InputStream inputStream = null;
		String result = "";
		ArrayList<NameValuePair> nameValuePairs1 = new ArrayList<NameValuePair>();
		nameValuePairs1.add(new BasicNameValuePair("request",request));
		if (request.equals("Appliances")){
			nameValuePairs1.add(new BasicNameValuePair("house",houseList.get(housePosition)));
		}

		//http postappSpinners
		try{
			HttpClient httpclient = new DefaultHttpClient();

			// have to change the ip here to your ip
			HttpPost httppost = new HttpPost(url);
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs1));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			inputStream = entity.getContent();
		}
		catch(Exception e){
			Log.e("log_tag", "Error in http connection "+e.toString());
		}
		//convert response to string
		try{
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"),8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			inputStream.close();
			result=sb.toString();
		}
		catch(Exception e){
			Log.e("log_tag", "Error converting result "+e.toString());
		}
		return result;

	}
}


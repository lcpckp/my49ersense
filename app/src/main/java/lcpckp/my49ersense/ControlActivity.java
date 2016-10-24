package lcpckp.my49ersense;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ControlActivity extends Activity{

	static ArrayList<String> houseList = new ArrayList<String>();

	// loads the appliances

	private String[] attributes = {"Appliance ID","Lumens", "Size", "Voltage","Watts"};
	public int wattsNum, lumensNum, sizeNum, voltageNum;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stats);

		TextView watts = (TextView)findViewById(R.id.watts);
		TextView lumens = (TextView)findViewById(R.id.lumens);
		TextView size = (TextView)findViewById(R.id.size);
		TextView voltage = (TextView)findViewById(R.id.voltage);

		getUsageTotals();

		watts.setText(wattsNum + "");
		lumens.setText(lumensNum + "");
		size.setText(sizeNum + "");
		voltage.setText(voltageNum + "");


	}

	private void getUsageTotals() {

			String newResult = getConnection("http://192.168.0.10/stats.php","Appliances");

			try{
				JSONArray jsArray = new JSONArray(newResult);
				for(int i=0;i<jsArray.length();i++){

					JSONObject json_data = jsArray.getJSONObject(i);
					wattsNum = json_data.getInt("watts");
					lumensNum = json_data.getInt("lumens");
					sizeNum = json_data.getInt("size");
					voltageNum = json_data.getInt("voltage");
				}
			}
			catch(JSONException e){
				//Log.e("log_tag", "Error parsing data "+e.toString());
				Log.e("log_tag", "Error parsing data "+e.toString());

			}
		}



	public String getConnection(String url, String request){

		Bundle bundle = new Bundle();
		Message msg = new Message();
		InputStream inputStream = null;
		String result = "";
		ArrayList<NameValuePair> nameValuePairs1 = new ArrayList<NameValuePair>();
		nameValuePairs1.add(new BasicNameValuePair("request",request));


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

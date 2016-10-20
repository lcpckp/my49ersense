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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class InboxHomeActivity extends Activity{

	private Bundle bundle;
	private String name;
	private String answer,answerImage, askedTo;
	static int count = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.main);

		setContentView(R.layout.inbox_home);
		InputStream inputStream = null;
		String result = "";
		
		final Button viewRepliesButton = (Button) findViewById(R.id.button1);
		final Button viewInboxButton = (Button) findViewById(R.id.button2);
		final Button exitButton = (Button) findViewById(R.id.button3);

		viewRepliesButton.setEnabled(false);
		name = getIntent().getExtras().getString("username");

		//getting connection
		try{ 
			HttpClient httpclient = new DefaultHttpClient();
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("username",name));
			//nameValuePairs.add(new BasicNameValuePair("Appliance", appliance));

			// have to change the ip here to your ip
			HttpPost httppost = new HttpPost("http://70.63.101.46/inboxHome.php");
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			inputStream = entity.getContent();
		}
		catch(Exception e){
			Log.e("log_tag", "Error in http connection "+e.toString());
			Toast.makeText(getBaseContext(), "Server Not Responding", Toast.LENGTH_SHORT).show();
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
		
		if(!result.equals("null")){
			try{
				JSONArray jArray = new JSONArray(result);
				
				//for(int i=0;i<jArray.length();i++){
				for(int i=jArray.length()-1;i>=0;i--){		
					JSONObject json_data = jArray.getJSONObject(i);
					
					
					answer = (json_data.getString("answer_text"));
					answerImage = (json_data.getString("answer_picture"));
					askedTo = (json_data.getString("asked_to"));
					
					if(! (answer.equals("null")|| answerImage.equals("null"))){
						count++;
					}
					
				}
				
				if(count > 0){
					viewRepliesButton.setEnabled(true);
				}
			}

			catch(JSONException e){
				//Log.e("log_tag", "Error parsing data "+e.toString());
				Log.e("log_tag", "Error parsing data "+e.toString());
			}
		}

		viewRepliesButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent viewReplyIntent = new Intent(getBaseContext(),ViewReplyActivity.class);
				viewReplyIntent.getExtras();
				bundle = getIntent().getExtras();
				bundle.putString("answer", answer);
				bundle.putString("answerImage", answerImage);
				bundle.putString("answeredBy", askedTo);
				viewReplyIntent.putExtras(bundle);
				startActivity(viewReplyIntent);	

			}
		});


		viewInboxButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent inboxIntent = new Intent(getBaseContext(),InboxActivity.class);
				inboxIntent.getExtras();
				bundle = getIntent().getExtras();
				inboxIntent.putExtras(bundle);
				startActivity(inboxIntent);	
			}
		});

		exitButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

	}
}

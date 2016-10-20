package lcpckp.my49ersense;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class BlogSpotActivity extends Activity{

	private String name, appliance;
	private int listViewTouchAction;
	private int COMMENT_LIMIT = 100;
	private int limitCounter = 0;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.blog);
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 


		final ListView lv = (ListView)findViewById(R.id.listView1);
		final Button submitButton = (Button) findViewById(R.id.button2);
		final Button homeButton = (Button) findViewById(R.id.button1);
		final EditText comment= (EditText)findViewById(R.id.editText1);
		ArrayList<String> commentsAL = new ArrayList<String>();
		name = getIntent().getExtras().getString("username");
		appliance = getIntent().getExtras().getString("Appliance");

		TextView applianceTextView = (TextView)findViewById(R.id.textView1);
		applianceTextView.setText(appliance+" blog");
		Log.d("The name is ",name);
		InputStream inputStream = null;
		String result = "";
		try{ 
			HttpClient httpclient = new DefaultHttpClient();	
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("request","load"));
			nameValuePairs.add(new BasicNameValuePair("Appliance", appliance));

			// have to change the ip here to your ip
			HttpPost httppost = new HttpPost("http://70.63.101.46/blog.php");
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

		Comments commentObject =null;
		//parse json data
		if(!result.equals("")){
			try{
				JSONArray jArray = new JSONArray(result);
				commentObject = new Comments();
				commentsAL.clear();


				//for(int i=0;i<jArray.length();i++){
				for(int i=jArray.length()-1;i>=(jArray.length()-COMMENT_LIMIT);i--){	
					JSONObject json_data = jArray.getJSONObject(i);

					commentObject.setUsername(json_data.getString("name"));
					commentObject.setComment(json_data.getString("comment"));
					commentObject.setDateTime(json_data.getString("date"));
					commentsAL.add(commentObject.toString());

					String user = json_data.getString("name");
					if(user.equals("null")){
						break;
					}
					if(user.equals(name)){
						limitCounter++;
					}

					String comments = json_data.getString("comment");
					String dates = json_data.getString("date");
					Log.d("returned dates", dates);
					Log.d("returned comment",comments);
					Log.d("returned user",user);
				}
			}

			catch(JSONException e){
				//Log.e("log_tag", "Error parsing data "+e.toString());
				Log.e("log_tag", "Error parsing data "+e.toString());
			}
		}
		if (!(result.equals(""))){
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.txtName, commentsAL);
			lv.setAdapter(adapter);
			lv.refreshDrawableState();
			setListViewScrollable(lv);

		}

		homeButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();

			}
		});

		submitButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				String stringComment = comment.getText().toString();
				// all the validations are done in sequence
				if (stringComment.length() == 0){
					comment.setError("Please enter your comment!");
				}
				else{

					InputStream inputStream = null;

					DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
					Date date = new Date();
					String strDate = dateFormat.format(date);
					String result = "";
					Log.d("the date and time is ",strDate);
					Log.d("the date and time is ",date.toString());

					ArrayList<NameValuePair> nameValuePairs1 = new ArrayList<NameValuePair>();
					nameValuePairs1.add(new BasicNameValuePair("request","store"));
					nameValuePairs1.add(new BasicNameValuePair("comment",stringComment));
					nameValuePairs1.add(new BasicNameValuePair("userName",name));
					nameValuePairs1.add(new BasicNameValuePair("date",date.toString()));
					nameValuePairs1.add(new BasicNameValuePair("Appliance", appliance));


					//http postappSpinners
					try{ 
						HttpClient httpclient = new DefaultHttpClient();

						// have to change the ip here to your ip
						HttpPost httppost = new HttpPost("http://70.63.101.46/blog.php");
						httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs1));
						HttpResponse response = httpclient.execute(httppost);
						HttpEntity entity = response.getEntity();
						inputStream = entity.getContent();
						Toast.makeText(getBaseContext(), "Thanks for your comment!", Toast.LENGTH_SHORT).show();
						finish();
					}
					catch(Exception e){
						Log.e("log_tag", "Error in http connection "+e.toString());
						Toast.makeText(getBaseContext(), "Server Not Responding", Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
	}
	private void setListViewScrollable(final ListView list) {
		list.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				listViewTouchAction = event.getAction();
				if (listViewTouchAction == MotionEvent.ACTION_MOVE)
				{
					list.scrollBy(0, 1);
				}
				return false;
			}
		});
		list.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view,
					int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (listViewTouchAction == MotionEvent.ACTION_MOVE)
				{
					list.scrollBy(0, -1);
				}
			}
		});
	}
}

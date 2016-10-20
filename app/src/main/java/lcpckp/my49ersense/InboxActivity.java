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

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class InboxActivity extends ListActivity {

	private int listViewTouchAction;
	private String name;
	ArrayList<String> inboxAL = new ArrayList<String>();
	ArrayList<InboxList> inboxItems = new ArrayList<InboxList>();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.inbox);

		final ListView list= getListView();
		InputStream inputStream = null;
		String result = "";
		
		name = getIntent().getExtras().getString("username");
		
		//getting connection
		try{ 
			HttpClient httpclient = new DefaultHttpClient();
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("username",name));
			//nameValuePairs.add(new BasicNameValuePair("Appliance", appliance));

			// have to change the ip here to your ip
			HttpPost httppost = new HttpPost("http://70.63.101.46/questionInbox.php");
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

		InboxList inboxList =null;
		//parse json data
		if(!result.equals("")){
			try{
				JSONArray jArray = new JSONArray(result);
				
				inboxAL.clear();
				inboxItems.clear();

				//for(int i=0;i<jArray.length();i++){
				for(int i=jArray.length()-1;i>=0;i--){		
					JSONObject json_data = jArray.getJSONObject(i);
					inboxList = new InboxList();
					inboxList.setUser(json_data.getString("username"));
					inboxList.setQuestionText(json_data.getString("question_text"));
					inboxList.setDomain(json_data.getString("domain"));
					inboxList.setQuestionImage(json_data.getString("question_picture"));
					inboxList.setAnswerText(json_data.getString("answer_text"));
					inboxList.setAnswerImage(json_data.getString("answer_picture"));
					inboxList.setAnsweredBy(json_data.getString("asked_to"));
					inboxItems.add(inboxList);
					inboxAL.add(inboxList.toString());
					
					String user = json_data.getString("username");
					
					String question = json_data.getString("question_text");
					String domain = json_data.getString("domain");
					Log.d("returned question", question);
					Log.d("returned domain",domain);
					Log.d("returned user",user);
				}
			}

			catch(JSONException e){
				//Log.e("log_tag", "Error parsing data "+e.toString());
				Log.e("log_tag", "Error parsing data "+e.toString());
			}
		}
		if (!(result.equals(""))){

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.txtName, inboxAL);
			list.setAdapter(adapter);
			list.refreshDrawableState();
			setListViewScrollable(list);
		}
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
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		/*Object o = this.getListAdapter().getItem(position);
		String keyword = o.toString();*/
		
		String pictureName = inboxItems.get(position).getQuestionImage();
		String question = inboxItems.get(position).getQuestionText();
		String userName = inboxItems.get(position).getUser();
		String answer = inboxItems.get(position).getAnswerText();
        Toast.makeText(this, "You selected: " + Integer.toString(position).toString(), Toast.LENGTH_LONG)
                .show();
        Intent inboxItemIntent = new Intent(getBaseContext(),InboxItemActivity.class);
       // inboxItemIntent.getExtras();
		Bundle bundle = getIntent().getExtras();
		bundle.putString("pictureName", pictureName);
		bundle.putString("question", question);
		bundle.putString("askedBy", userName);
		bundle.putString("currentUser", name);
		bundle.putString("answer", answer);
		inboxItemIntent.putExtras(bundle);
		startActivity(inboxItemIntent);	

	}
	
}


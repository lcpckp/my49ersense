package lcpckp.my49ersense;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class InboxItemActivity extends Activity{

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.inbox_item);
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		final Button answerButton = (Button)findViewById(R.id.button2);
		final String pictureName = getIntent().getExtras().getString("pictureName");
		String askedUser = getIntent().getExtras().getString("askedBy");
		final String currentUser = getIntent().getExtras().getString("currentUser");
		String question = getIntent().getExtras().getString("question");


		try {
			InputStream is = new URL("http://192.168.0.10/questionImages/"+pictureName).openStream();
			Bitmap bitmap = BitmapFactory.decodeStream(is);
			is.close(); 
			ImageView iv = (ImageView) findViewById(R.id.imageView1);
			iv.setImageBitmap(bitmap);
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		TextView askedBy = (TextView) findViewById(R.id.textView1);
		askedBy.setText("Asked by - "+askedUser);
		TextView quest = (TextView) findViewById(R.id.textView2);
		quest.setText(question);

		answerButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent answerIntent = new Intent(getBaseContext(),AnswerActivity.class);
				// inboxItemIntent.getExtras();
				Bundle bundle = getIntent().getExtras();
				bundle.putString("pictureName", pictureName);
				bundle.putString("currentUser", currentUser);
				answerIntent.putExtras(bundle);
				startActivity(answerIntent);	

			}
		});

		
	}

}

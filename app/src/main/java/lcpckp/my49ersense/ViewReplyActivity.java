package lcpckp.my49ersense;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewReplyActivity extends Activity{

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_answer);
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		final String answerImage = getIntent().getExtras().getString("answerImage");
		String askedTo = getIntent().getExtras().getString("answeredBy");
		String answer = getIntent().getExtras().getString("answer");
		final Button exitButton = (Button)findViewById(R.id.button2);


		try {
			InputStream is = new URL("http://192.168.0.10/answerImages/"+answerImage).openStream();
			Bitmap bitmap = BitmapFactory.decodeStream(is);
			is.close(); 
			ImageView iv = (ImageView) findViewById(R.id.imageView1);
			iv.setImageBitmap(bitmap);
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		TextView ansBy = (TextView) findViewById(R.id.textView1);
		ansBy.setText("Answered by - "+askedTo);
		TextView ans = (TextView) findViewById(R.id.textView2);
		ans.setText(answer);
		
		exitButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}
	
	
}
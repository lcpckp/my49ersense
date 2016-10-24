package lcpckp.my49ersense;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

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

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

public class QuestionsActivity extends Activity {
	private final static String DEBUG_TAG = "MakePhotoActivity";
	private Camera camera;
	private int cameraId = 0;
	private CameraPreview mPreview;
	private String name, askedToUser;
	ArrayList<NameValuePair> nameValuePairs;
	static ArrayList<String> userList = new ArrayList<String>();
	static int domainPosition, userPosition;
	InputStream is;
	SurfaceHolder holder;
	private Context context;
	private Handler handler;
	private ArrayList<String> domainList = new ArrayList<String>();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.question);
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		final FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
		final EditText question= (EditText)findViewById(R.id.editText1);

		final Spinner domainSpinner = (Spinner)findViewById(R.id.spinner1);
		final Spinner userSpinner = (Spinner)findViewById(R.id.spinner2);
		
		domainList.add("grid");
		domainList.add("appliances");
		ArrayAdapter<String> domainAdapter = new ArrayAdapter<String>(QuestionsActivity.this, android.R.layout.simple_spinner_item,domainList);
		domainAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
		domainSpinner.setPrompt("");
		domainSpinner.setAdapter(domainAdapter);
		//following line is the listener will call the function after oncreate
		domainSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0,
					View arg1, int domainPos, long arg3) {
				domainPosition = domainPos;
				new Thread(getData).start();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});


		handler = new Handler(new Handler.Callback() {
			public boolean handleMessage(Message msg) {
				if(msg.getData().containsKey("ERROR")){
					Toast.makeText(getBaseContext(), msg.getData().getString("ERROR"), Toast.LENGTH_LONG).show();
				} 
				else if (msg.getData().containsKey("ListUsers")){

					userList = msg.getData().getStringArrayList("ListUsers");

					//Array adapter for spinner 1 (houses)
					ArrayAdapter<String> userAdapter = new ArrayAdapter<String>(QuestionsActivity.this, android.R.layout.simple_spinner_item,userList);
					userAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
					userSpinner.setPrompt("Select a User");
					userSpinner.setAdapter(userAdapter);
				}
				return true;
			}
		});

		
		userSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0,
					View arg1, int userPos, long arg3) {
				askedToUser = userList.get(userPos);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});
		context = this;
		name = getIntent().getExtras().getString("username");
		// do we have a camera?
		if (!getPackageManager()
				.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
			Toast.makeText(this, "No camera on this device", Toast.LENGTH_LONG)
			.show();
		} else {
			cameraId = findBackCamera();
			camera = Camera.open(cameraId);
			Camera.Parameters params = camera.getParameters();
			List<Camera.Size> sizes = params.getSupportedPictureSizes();
			params.setPictureSize((sizes.get((sizes.size()-2)).width), (sizes.get((sizes.size())-2)).height);
			camera.setParameters(params);
			//int widthe = (sizes.get((sizes.size()-2)).width);
			//int heighte = (sizes.get((sizes.size())-2)).height;
			if (cameraId < 0) {
				Toast.makeText(this, "No front facing camera found.",
						Toast.LENGTH_LONG).show();
			}
			
			camera.setDisplayOrientation(90);

			mPreview = new CameraPreview(this, camera);
			preview.addView(mPreview);
			holder = mPreview.getHolder();
		}

		Button submitButton = (Button)findViewById(R.id.button2);
		submitButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String result = "";
				String stringQuestion = question.getText().toString();
				String domainValue = (String) domainSpinner.getSelectedItem();
				// all the validations are done in sequence
				if (stringQuestion.length() == 0){
					question.setError("Please enter your question!");
				}
				else{

					Bitmap bitmapOrg = BitmapFactory.decodeFile(PhotoHandler.imageName);
					ByteArrayOutputStream bao = new ByteArrayOutputStream();
					bitmapOrg.compress(Bitmap.CompressFormat.JPEG, 50, bao);
					byte [] ba = bao.toByteArray();
					String ba1=Base64.encodeBytes(ba);
					nameValuePairs = new ArrayList<NameValuePair>();
					nameValuePairs.add(new BasicNameValuePair("image",ba1));
					nameValuePairs.add(new BasicNameValuePair("username",name));
					nameValuePairs.add(new BasicNameValuePair("imageName",PhotoHandler.fileName));
					nameValuePairs.add(new BasicNameValuePair("question",stringQuestion));
					nameValuePairs.add(new BasicNameValuePair("domain",domainValue));
					nameValuePairs.add(new BasicNameValuePair("asked_to",askedToUser));

					try{

						HttpClient httpclient = new DefaultHttpClient();
						HttpPost httppost = new HttpPost("http://192.168.0.10/submitQuestion.php");
						httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
						HttpResponse response = httpclient.execute(httppost);
						HttpEntity entity = response.getEntity();
						is = entity.getContent();
					}
					catch(Exception e){
						Log.e("log_tag", "Error in http connection "+e.toString());
						Toast.makeText(getBaseContext(), "Server Not Responding", Toast.LENGTH_SHORT).show();
						return;
					}
					try{
						BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
						StringBuilder sb = new StringBuilder();
						String line = null;
						while ((line = reader.readLine()) != null) {
							sb.append(line + "\n");
						}
						is.close();
						result=sb.toString();
					}
					catch(Exception e){
						Log.e("log_tag", "Error converting result "+e.toString());
					}
					result.substring(0, 6);

					AlertDialog.Builder builder = new AlertDialog.Builder(context);
					builder.setMessage("Question successfully submitted! Question ID is "+result)
					.setCancelable(false)
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							finish();
						}
					});
					AlertDialog alert = builder.create();
					alert.show();
				}
			}

		});


		Button captureButton = (Button)findViewById(R.id.captureFront);
		captureButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				//preview.removeAllViews();
				preview.destroyDrawingCache();
				preview.removeAllViews();
				camera.takePicture(null, null,
						new PhotoHandler(getApplicationContext()));

				final Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						//Do something after 2000ms
						Bitmap bitmapImage = BitmapFactory.decodeFile(PhotoHandler.imageName);
						int newWidth = bitmapImage.getWidth();
						int newHeight = bitmapImage.getHeight();
						Matrix mat = new Matrix();
						mat.postRotate(Integer.parseInt("90"));
						Bitmap bMapRotate = Bitmap.createBitmap(bitmapImage, 0, 0,bitmapImage.getWidth(),bitmapImage.getHeight(), mat, true);
						preview.setBackgroundDrawable(new BitmapDrawable(getResources(), bMapRotate));
					}
				}, 1700);

			}
		});

		Button discardButton = (Button)findViewById(R.id.discard);
		discardButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				preview.removeAllViews();
				preview.addView(mPreview);
				camera.startPreview();

			}
		});

	}



	private int findBackCamera() {
		int cameraId = -1;
		// Search for the front facing camera
		int numberOfCameras = Camera.getNumberOfCameras();
		for (int i = 0; i < numberOfCameras; i++) {
			CameraInfo info = new CameraInfo();
			Camera.getCameraInfo(i, info);
			if (info.facing == CameraInfo.CAMERA_FACING_BACK) {
				Log.d(DEBUG_TAG, "Camera found");
				cameraId = i;
				break;
			}
		}
		return cameraId;
	}

	@Override
	protected void onPause() {
		if (camera != null) {
			camera.release();
			camera = null;
		}
		super.onPause();
	}

	private Runnable getData = new Runnable() {
		public void run() {
			Bundle bundle = new Bundle();
			Message msg = new Message();
			String result = getConnection("http://192.168.0.10/getDomainUsers.php","House");
			userList.clear();

			//parse json data
			try{
				JSONArray jArray = new JSONArray(result);
				for(int i=0;i<jArray.length();i++){
					JSONObject json_data = jArray.getJSONObject(i);

					// LIST Of houses will be loaded into spin
					userList.add(json_data.getString("username"));
				}
			}
			catch(JSONException e){
				//Log.e("log_tag", "Error parsing data "+e.toString());
				Log.e("log_tag", "Error parsing data "+e.toString());
			}
			bundle.putStringArrayList("ListUsers", userList);
			msg.setData(bundle);
			handler.sendMessage(msg);
		}
	};

	public String getConnection(String url, String domain){

		Bundle bundle = new Bundle();
		Message msg = new Message();
		InputStream inputStream = null;
		String result = "";
		ArrayList<NameValuePair> nameValuePairs1 = new ArrayList<NameValuePair>();
		nameValuePairs1.add(new BasicNameValuePair("domain",domainList.get(domainPosition)));

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

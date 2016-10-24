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
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

public class AnswerActivity extends Activity {
	private final static String DEBUG_TAG = "MakePhotoActivity";
	private Camera camera;
	private int cameraId = 0;
	private CameraPreview mPreview;
	private String questionImageName, currentUser;
	ArrayList<NameValuePair> nameValuePairs;
	InputStream is;
	SurfaceHolder holder;
	private Context context;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.answer);
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		final FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
		final EditText answer= (EditText)findViewById(R.id.editText1);
	
		context = this;
		questionImageName = getIntent().getExtras().getString("pictureName");
		currentUser = getIntent().getExtras().getString("currentUser");
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
			if (cameraId < 0) {
				Toast.makeText(this, "No front facing camera found.",
						Toast.LENGTH_LONG).show();
			}
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
				String stringAnswer = answer.getText().toString();
				// all the validations are done in sequence
				if (stringAnswer.length() == 0){
					answer.setError("Please enter your answer!");
				}
				else{

					Bitmap bitmapOrg = BitmapFactory.decodeFile(PhotoHandler.imageName);
					ByteArrayOutputStream bao = new ByteArrayOutputStream();
					bitmapOrg.compress(Bitmap.CompressFormat.JPEG, 50, bao);
					byte [] ba = bao.toByteArray();
					String ba1=Base64.encodeBytes(ba);
					nameValuePairs = new ArrayList<NameValuePair>();
					nameValuePairs.add(new BasicNameValuePair("image",ba1));
					nameValuePairs.add(new BasicNameValuePair("questionImage",questionImageName));
					nameValuePairs.add(new BasicNameValuePair("answerImage",PhotoHandler.fileName));
					nameValuePairs.add(new BasicNameValuePair("answer",stringAnswer));
					nameValuePairs.add(new BasicNameValuePair("currentUser",currentUser));

					try{

						HttpClient httpclient = new DefaultHttpClient();
						HttpPost httppost = new HttpPost("http://192.168.0.10/submitAnswer.php");
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

					AlertDialog.Builder builder = new AlertDialog.Builder(context);
					builder.setMessage("Answer successfully submitted!")
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
						Matrix mat = new Matrix();
						mat.postRotate(Integer.parseInt("90"));
						Bitmap bMapRotate = Bitmap.createBitmap(bitmapImage, 0, 0,bitmapImage.getWidth(),bitmapImage.getHeight(), mat, true);
						preview.setBackgroundDrawable(new BitmapDrawable(bMapRotate));
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

}

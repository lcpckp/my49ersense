package lcpckp.my49ersense;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class UserHomeActivity extends Activity{
	
	private Bundle bundle;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.main);

		setContentView(R.layout.user_home);
		
		final Button applianceButton = (Button) findViewById(R.id.button1);
		final Button accountButton = (Button) findViewById(R.id.button2);
		final Button logoutButton = (Button) findViewById(R.id.button3);
		
		applianceButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				bundle = getIntent().getExtras();
				Intent communityActivity = new Intent(getBaseContext(),CommunityHomeActivity.class);
				communityActivity.putExtras(bundle);
				startActivity(communityActivity);
			}
		});
		
		
		logoutButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		accountButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				bundle = getIntent().getExtras();
				Intent accountActivity = new Intent(getBaseContext(),ManageAccountActivity.class);
				accountActivity.putExtras(bundle);
				startActivity(accountActivity);
			}
		});

	}
}

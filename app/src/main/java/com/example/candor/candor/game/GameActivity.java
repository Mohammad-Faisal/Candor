package com.example.candor.candor.game;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.candor.candor.R;
import com.example.candor.candor.profile.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static android.content.ContentValues.TAG;

public class GameActivity extends Activity {

	private Garbagegame garbagegame;
	private GameGraphics gameGraphics;
	private SoundHandler soundHandler;
	private TextView bestScoreView;
	private SharedPreferences pref;
	private Dialog overDialog;
	private boolean gameOverBool;
	private int bestScore;
	public boolean soundsOn;
	protected ImageButton reset_btn;
	public Typeface typeface;
	private DatabaseReference mDatabase;
	private FirebaseAuth mAuth;

	private ArrayList<String> edu=new ArrayList<>(
            Arrays.asList(
                    "Do not throw garbage in water.",
                    "Water is our national asset.",
                    "Stop the waste of water.",
                    "Prevent water pollution.",
                    "Water saved our lives. Save water.",
                    "Keep up the water, stay healthy.",
                    "Never put garbage in the drain water.",
                    "Keep your environment healthy.",
                    "Clean water and healthy living conditions are necessary for life.",
                    "Be careful about the use of water.",
                    "Do not pollute the river water and canals.",
                    "Filling river, canal causes severe damages to the environment.",
                    "Drop dirt rubbish in the specified places.",
                    "Use Dustbin. Keep the environment clean.",
                    "Disease does spread if dirt rubbish aren’t dropped in certain places.",
                    "One of your dirt rubbish may cause problems to thousands.",
                    "Do not throw your chip's packet in road or drain",
                    "Do not throw water bottle  in road or drain",
                    "Do not throw plastic  in road or drain",
                    "Use dustbin. You always find one if you want",
                    "Our city is like our home,we should keep it clean"
            )
    );




	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		getGamePreferences();
		setGameTypeface();

		mDatabase = FirebaseDatabase.getInstance().getReference();
		mAuth = FirebaseAuth.getInstance();
		
		gameGraphics = new GameGraphics(this);
		soundHandler = new SoundHandler(this, soundsOn);
		gameGraphics.initializeGameGraphics();
		soundHandler.initializeSoundFx();
		
		gameOverBool = false;
		reset_btn = (ImageButton) findViewById(R.id.btn_reset);
		bestScoreView = (TextView) findViewById(R.id.best_view);
		bestScoreView.setTypeface(typeface);
		updateBest();

		garbagegame = new Garbagegame(this, gameGraphics, soundHandler);
		garbagegame.startGame();
		Log.d("OnCreate", "started game");
		
		//reset button
		reset_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				garbagegame.stopGame();
				garbagegame.resetGame();
				gameOverBool = false;
			}
		});

	}// end OnCreate

	private void getGamePreferences() {
	
		//get existing high score
		pref = this.getSharedPreferences("ca.gsalisi.garbages", Context.MODE_PRIVATE);
		bestScore = pref.getInt("best", 0);
		soundsOn = pref.getBoolean("soundfx", true);
				
	}
	
	private void setGameTypeface() {
		
		typeface = Typeface.createFromAsset(this.getAssets(),
		        "fonts/roostheavy.ttf");
	}


	/*cancel timers when the window is closed-- when 
	**back button or home button is pressed to prevent
	**garbages falling even when out of game*/
	@Override
	protected void onStop() {
		super.onStop();
		if(!gameOverBool){
			try{
				soundHandler.getSoundPool().autoPause();
				soundHandler.getSoundPool().release();
				garbagegame.stopGame();
				Log.d("On Stop", "Called cancel timers");
			} catch(Exception e) {
				Log.d("On Stop", "exception caught");
			}
		}else{
			overDialog.dismiss();
		}
		finish();
		
		
	}//end onStop()
	

	
	//show game over dialog
	public void gameOver() {
		
		gameOverBool = true;
		
		if(garbagegame.scoreCount > bestScore){//save high score if it is beaten
			bestScore = garbagegame.scoreCount;
			pref = this.getSharedPreferences("ca.gsalisi.garbages", Context.MODE_PRIVATE);
			Editor editor = pref.edit();
			editor.putInt("best", bestScore);
			editor.commit();
			updatedata();
			updateBest();
		}
		
		garbagegame.stopGame();
		
		//creates dialog
		overDialog = new Dialog(GameActivity.this, R.style.CustomDialog);
		overDialog.setContentView(R.layout.game_over);
		overDialog.setCanceledOnTouchOutside(false);
	
		TextView tv = new TextView(this);
		tv.setText("Game Over!");
		tv.setTypeface(typeface);

		TextView education;
		education=overDialog.findViewById(R.id.education);
		int sije=edu.size();
        Random random=new Random();
        int mod=(random.nextInt(1000))%sije;
        education.setText(edu.get(mod));
        education.setTypeface(typeface);

		
		overDialog.setTitle("Game Over!");
		
		TextView scoreOver = (TextView) overDialog.findViewById(R.id.score_view2);
		TextView bestOver = (TextView) overDialog.findViewById(R.id.best_view2);
		
		scoreOver.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 22);
		bestOver.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 22);
		
		scoreOver.setTypeface(typeface);
		bestOver.setTypeface(typeface);
		
		scoreOver.setText(gameGraphics.getScoreView().getText().toString());
		bestOver.setText(bestScoreView.getText().toString());
		
		ImageButton restartbtn = (ImageButton) overDialog.findViewById(R.id.btn_gameover);
		restartbtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				overDialog.dismiss();
				garbagegame.resetGame();
				gameOverBool = false;
			}
		});
		overDialog.show();
		
		overDialog.setOnCancelListener(new Dialog.OnCancelListener(){

			@Override
			public void onCancel(DialogInterface arg0) {
				garbagegame.resetGame();
				gameOverBool = false;
			}
		});
		
	}//end of gameOver()
	
	//updates best score view
	protected void updateBest() {
		bestScoreView.setText("Best: " + bestScore);
	}

	public void updatedata(){
		final String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
		FirebaseDatabase.getInstance().getReference().child("users").child(uid).addValueEventListener(
				new ValueEventListener() {
					@Override
					public void onDataChange(DataSnapshot dataSnapshot) {
						// Get user value
						Users user = dataSnapshot.getValue(Users.class);

						Log.d("vetore1", dataSnapshot.getKey());


						// [START_EXCLUDE]
						if (user == null) {
							// User is null, error out
							Log.e(TAG, "User " + uid + " is unexpectedly null");
							Toast.makeText(GameActivity.this,
									"Error: could not fetch user.",
									Toast.LENGTH_SHORT).show();
						} else {
							// Write new post
							writeNewUser(uid, user.name, bestScore,user.getProfile_image_url());
						}

						// Finish this Activity, back to the stream
						// [END_EXCLUDE]
					}

					@Override
					public void onCancelled(DatabaseError databaseError) {
						Log.w(TAG, "getUser:onCancelled", databaseError.toException());
						// [START_EXCLUDE]

					}
				});
	}

	private void writeNewUser(String userId, String name, int score,String proimage) {
		Score score1 = new Score(name, score,userId,proimage);

		mDatabase.child("scores").child(userId).setValue(score1);
	}

}
package com.example.candor.candor.game;

import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.example.candor.candor.R;

public class GameGraphics {
	
	private GameActivity main;
	private RelativeLayout rLayout;
	private ImageView basketView;

	private ImageView villianViewLeft;
	private ImageView villianViewCenter;
	private ImageView villianViewRight;

	private ImageView garbageBrokenLeft;
	private ImageView garbageBrokenCenter;
	private ImageView garbageBrokenRight;
	private TextView livesView;
	private TextView levelView;
	private Typeface typeface;
	private ImageView garbageView;
	private MyScrollView hScroll;
	private TextView scoreView;
	private TextView countdownView;
	private TextView livesLabel;
	private LinearLayout linearLayout;


	private ImageView water1;
	private ImageView water2;
	private ImageView water3;
	private ImageView water4;
	private ImageView back;


	private ImageView kola;
	private ImageView paper;
	private ImageView can;
	private ImageView poly;
	
	
	public GameGraphics(GameActivity gameActivity) {
		this.main = gameActivity;

	}

	protected void initializeGameGraphics() {
		
		typeface = Typeface.createFromAsset(main.getAssets(),
		        "fonts/roostheavy.ttf");
		rLayout = (RelativeLayout) main.findViewById(R.id.rLayout);

		villianViewLeft = (ImageView) main.findViewById(R.id.villianLeft);
		villianViewCenter = (ImageView) main.findViewById(R.id.villianCenter);
		villianViewRight = (ImageView) main.findViewById(R.id.villianRight);

		Animation fallout = AnimationUtils.loadAnimation(main, R.anim.dustbin_place);
		villianViewLeft.startAnimation(fallout);
		Animation fallout2 = AnimationUtils.loadAnimation(main, R.anim.dustbin_place);
		fallout2.setDuration(2250);
		villianViewCenter.startAnimation(fallout2);
		Animation fallout3 = AnimationUtils.loadAnimation(main, R.anim.dustbin_place);
		fallout3.setDuration(2500);
		villianViewRight.startAnimation(fallout3);


		linearLayout = (LinearLayout) main.findViewById(R.id.linearLayout);


		//---- Initialize broken garbages ----//
		//needs to create a function to make the code better and shorter!!
		
		garbageBrokenLeft = (ImageView) main.findViewById(R.id.garbageBrokenLeft);
		garbageBrokenCenter = (ImageView) main.findViewById(R.id.garbageBrokenCenter);
		garbageBrokenRight = (ImageView) main.findViewById(R.id.garbageBrokenRight);

		garbageBrokenLeft.setVisibility(View.INVISIBLE);
		garbageBrokenCenter.setVisibility(View.INVISIBLE);
		garbageBrokenRight.setVisibility(View.INVISIBLE);



		water1=main.findViewById(R.id.water3);
		water2=main.findViewById(R.id.water4);
		water3=main.findViewById(R.id.water5);
		water4=main.findViewById(R.id.water6);
		back=main.findViewById(R.id.background);

		// ----------------  create basket view -------------------//

		hScroll = (MyScrollView) main.findViewById(R.id.hScrollView);
		basketView = (ImageView) main.findViewById(R.id.basketView);



		//dynamic changes
		int devWidthDp = getDeviceWidth();
		if( devWidthDp <  convertToPx(400) ){
			basketView.getLayoutParams().width = getDeviceWidth()*2 - convertToPx(170);
			basketView.getLayoutParams().height=getDeviceHeight()/12;
		}else{
			basketView.getLayoutParams().width = convertToPx(800) - convertToPx(170);
			basketView.getLayoutParams().height=getDeviceHeight()/12;
		}
		hScroll.getLayoutParams().height=getDeviceHeight()/3;
		//water1.getLayoutParams().height=(getDeviceHeight()/3-(getDeviceHeight()/12+convertToPx(35))) /4;
		//water2.getLayoutParams().height=(getDeviceHeight()/3-(getDeviceHeight()/12+convertToPx(35))*3)/5;
		//water3.getLayoutParams().height=(getDeviceHeight()/3-(getDeviceHeight()/12+convertToPx(35))*4)/5;
		//water4.getLayoutParams().height=getDeviceHeight()/3-(getDeviceHeight()/12+convertToPx(35))+getDeviceHeight()/6;







			
		//-------------- create view references -----------------//
		
		livesLabel = (TextView) main.findViewById(R.id.lives_label);
		livesLabel.setTypeface(typeface);
		
		livesView = (TextView) main.findViewById(R.id.lives_view);
		livesView.setTypeface(typeface);
		
		countdownView = (TextView) main.findViewById(R.id.countdown_view);
		getCountdownView().setTypeface(typeface);
		
		scoreView = (TextView) main.findViewById(R.id.score_view);
		getScoreView().setTypeface(typeface);
		
		levelView = (TextView) main.findViewById(R.id.level_view);
		levelView.setTypeface(typeface);
		
	
	}// end initializeGraphics()
	
	public ImageView creategarbage(int position, String object) {
		
		LayoutParams layoutParams = getMyLayoutParams(RelativeLayout.ALIGN_PARENT_TOP);
		

		switch (position) {//0 for left; 1 for center; 2 for right
		case 0:
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			layoutParams.setMargins(convertToPx(25), 0, 0, 0);
			layoutParams.height = convertToPx(50);
			

			break;
		case 1:
		
			layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL,
					RelativeLayout.TRUE);
			layoutParams.setMargins(0, 0, 0, 0);
			layoutParams.height = convertToPx(50);
			break;
		case 2:
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			layoutParams.setMargins(0, 0, convertToPx(25), 0);
			layoutParams.height = convertToPx(50);
			break;
		default:
			break;
		}

		garbageView = new ImageView(main);
		if(object.equals("volt")){
			getgarbageView().setImageResource(R.drawable.rsz_voltfinal);
		}
		else if(object.equals("poly")){
			getgarbageView().setImageResource(R.drawable.poly200);

			//playSoundEffect(0, 50);
		}else if(object.equals("can")){
			getgarbageView().setImageResource(R.drawable.can200);
			
		}else if(object.equals("kola")){
			getgarbageView().setImageResource(R.drawable.kola200);
		}
		else getgarbageView().setImageResource(R.drawable.paper200);
		getgarbageView().setLayoutParams(layoutParams);

		rLayout.addView(getgarbageView());
		bringvilliansToFront();
		bringBasketToFront();
		//kola=getgarbageView();
		
		return getgarbageView();
		
	}
	
	public void showBrokengarbage(int position, int scoreInc,String object) {
		
		//reveal broken garbage view and then fade it out
		Animation garbageFade = AnimationUtils.loadAnimation(main,
				R.anim.fadeout);

		switch (position) {
		case 0:
			if(object.equals("volt")){
				garbageBrokenLeft.setImageResource(R.drawable.rsz_voltfinal);
			}
			else if(object.equals("poly")){
				garbageBrokenLeft.setImageResource(R.drawable.poly200);
			}else if(object.equals("can")){
				garbageBrokenLeft.setImageResource(R.drawable.can200);
			}else if(object.equals("paper")){
				garbageBrokenLeft.setImageResource(R.drawable.paper200);
			}else if(object.equals("kola")){
				garbageBrokenLeft.setImageResource(R.drawable.kola200);
			}
			garbageBrokenLeft.setVisibility(View.VISIBLE);
			garbageBrokenLeft.startAnimation(garbageFade);
			break;
		case 1:
			if(object.equals("volt")){
				garbageBrokenCenter.setImageResource(R.drawable.rsz_voltfinal);
			}
			else if(object.equals("poly")){
				garbageBrokenCenter.setImageResource(R.drawable.poly200);
			}else if(object.equals("can")){
				garbageBrokenCenter.setImageResource(R.drawable.can200);
			}else if(object.equals("paper")){
				garbageBrokenCenter.setImageResource(R.drawable.paper200);
			}else if(object.equals("kola")){
				garbageBrokenCenter.setImageResource(R.drawable.kola200);
			}
			garbageBrokenCenter.setVisibility(View.VISIBLE);
			garbageBrokenCenter.startAnimation(garbageFade);
			break;
		case 2:
			if(object.equals("volt")){
				garbageBrokenRight.setImageResource(R.drawable.rsz_voltfinal);
			}
			else if(object.equals("poly")){
				garbageBrokenRight.setImageResource(R.drawable.poly200);
			}else if(object.equals("can")){
				garbageBrokenRight.setImageResource(R.drawable.can200);
			}else if(object.equals("paper")){
				garbageBrokenRight.setImageResource(R.drawable.paper200);
			}else if(object.equals("kola")){
				garbageBrokenRight.setImageResource(R.drawable.kola200);
			}
			garbageBrokenRight.setVisibility(View.VISIBLE);
			garbageBrokenRight.startAnimation(garbageFade);
			break;
		default:
			break;
		}
		
	}// end of showBrokengarbage()


	void bringvilliansToFront() {

		villianViewLeft.bringToFront();
		villianViewCenter.bringToFront();
		villianViewRight.bringToFront();

	}

	private void bringBasketToFront() {
		// TODO Auto-generated method stub
		hScroll.bringToFront();
	}

	public int getWidthReference() {
		
		return convertToDp(basketView.getLayoutParams().width/2) - 55;
	}

	protected LayoutParams getMyLayoutParams(int rule) {
		LayoutParams layoutParams = new LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		layoutParams.addRule(rule);
		return layoutParams;
	}

	//check if basket is in the right position
	//if it is then we add score, if not we subtract from lives


	public void shakevillian(int pos) {
		Animation anim = AnimationUtils.loadAnimation(main, R.anim.shake);

		switch (pos) {
			case 0:
				villianViewLeft.startAnimation(anim);
				break;
			case 1:
				villianViewCenter.startAnimation(anim);
				break;
			case 2:
				villianViewRight.startAnimation(anim);
				break;
			default:
				break;
		}
	}
	


	// updates lives text view
	public void updateLives(int numberOfLives) {
		livesView.setText(String.valueOf(numberOfLives));
		
	}
	// updates score text view
	public void updateScore(int score) {
		String countStr = "Score: " + String.valueOf(score);
		getScoreView().setText(countStr);
	}
	// converts dp to pixels.
	public int convertToPx(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				main.getResources().getDisplayMetrics());
	}
	public int convertToDp(int px){
		
		float scale = main.getResources().getDisplayMetrics().density;
		int dp = (int) (px / scale + 0.5f);
		return dp; 
	}
	public int getDeviceWidth(){
		return (int) main.getResources().getDisplayMetrics().widthPixels;  // displayMetrics.density;
	}
	public int getDeviceHeight(){
		return (int) main.getResources().getDisplayMetrics().heightPixels;
	}


	public void updateLevel(int level) {
		levelView.setText("Level "+String.valueOf(level));
		
	}

	public HorizontalScrollView getBasketScrollView() {
		return hScroll;
	}

	public ImageView getgarbageView() {
		return garbageView;
	}

	public TextView getScoreView() {
		return scoreView;
	}


	public TextView getCountdownView() {
		return countdownView;
	}

	public ImageView getWater1(){
		return water1;
	}

	public ImageView getWater2(){
		return water2;
	}
	public ImageView getWater3(){
		return water3;
	}
	public ImageView getWater4(){
		return water4;
	}



}

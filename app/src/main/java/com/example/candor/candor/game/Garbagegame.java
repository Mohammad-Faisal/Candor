package com.example.candor.candor.game;

import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import java.util.Random;
import com.example.candor.candor.R;

public class Garbagegame {
 
	private GameActivity main;
	
	private GameGraphics gameGraphics;
	private SoundHandler soundHandler;
	private AnimationListener animpolyListener;
	private AnimationListener animcanListener;
	private AnimationListener animnormalListener;
	private AnimationListener animvoltListener;
	private AnimationListener animkolaListener;
	private Animation garbageAnimation;
	private int garbageDelayTime;
	private boolean animationStarted;
	
	private Handler garbageDelayHandler;
	private Handler levelHandler;
	private Handler garbageIntervalHandler;
	private Handler countdownHandler;
	
	private int changeCount;
	private int prevPosition;
	
	private Runnable garbageDelayRunnable;
	private Runnable levelRunnable;
	private Runnable garbageIntervalRunnable;
	private Runnable countdownRunnable;
	
	protected int level;
	protected int scoreCount;
	protected int numberOfLives;
	protected CountDownTimer countdownTimer;
	
	public boolean gameInSession;
	public boolean handlerStarted;
	private int countdown;
	private AnimationListener animBadListener;
	private int animDuration;
	
	final private static int NULL = 0;
	final private static int DEFAULT_NUMBER_OF_LIVES = 3;
	final private static int NO_PREVIOUS_POSITION=-1;
	final private static int LEFT = 0;
	final private static int CENTER = 1;
	final private static int RIGHT = 2;
	final private static int garbage_DELAY_TIME_DEFAULT = 1300;
	final private static int garbage_DELAY_TIME_DECREMENT = 200;
	final private static int garbage_DELAY_TIME_DECREMENT_MED = 75;
	final private static int garbage_DELAY_TIME_DECREMENT_SMALL = 10;
	final private static int MAX_LEVEL = 50;
	final private static int TIME_PER_LEVEL = 10000;
	final private static int TIMING_RANDOMIZER = 150;
	final private static int DURATION_DEFAULT = 1800;
	final private static int DURATION_DECREMENT = 60;
	final private static int DURATION_DECREMENT_MED = 20;
	final private static int DURATION_DECREMENT_SMALL = 5;
	final private static String WHITE = "white";
	final private static String CRACKED = "cracked";
	final private static String GOLD = "gold";
	
	//garbage Game Constructor
	public Garbagegame(GameActivity gameActivity, GameGraphics gameGraphics, SoundHandler soundHandler) {
		this.main = gameActivity;
		this.soundHandler = soundHandler;
		this.gameGraphics = gameGraphics;
		scoreCount = NULL;
		numberOfLives = DEFAULT_NUMBER_OF_LIVES;
		handlerStarted = false;
		gameInSession = false;

		
	}//end of constructor

	//starts the game
	protected void startGame() {
		
		//signals that the game started but no timers yet
		gameInSession = true;
		
		//initiate game variables
		prevPosition = NO_PREVIOUS_POSITION;
		changeCount = NULL;
		level = NULL;
		garbageDelayTime = garbage_DELAY_TIME_DEFAULT;
		animDuration = DURATION_DEFAULT;
		gameGraphics.bringvilliansToFront();
		gameGraphics.updateLevel(1);

		startCountdown();
		
		
	}// end startGame
	
	//count down before garbages starts falling
	private void startCountdown() {

		gameGraphics.getCountdownView().setVisibility(View.VISIBLE);
		main.reset_btn.setEnabled(false);
		gameGraphics.getCountdownView().bringToFront();
		
		//initialize the animation for flashing count down
		final Animation fadeOut = AnimationUtils.loadAnimation(main, R.anim.fadeout);
				
		countdown = 3;
		countdownHandler = new Handler();
		countdownRunnable = new Runnable(){

			@Override
			public void run() {
				if(countdown == 3 ){
					gameGraphics.getBasketScrollView().smoothScrollTo(gameGraphics.convertToPx(120),0);
				}
				if(countdown>0){
					soundHandler.playSoundEffect(5, 50);
					gameGraphics.getCountdownView().setTextSize(TypedValue.COMPLEX_UNIT_DIP, 300);
					gameGraphics.getCountdownView().setText(String.valueOf(countdown));
					gameGraphics.getCountdownView().startAnimation(fadeOut);
					countdownHandler.postDelayed(countdownRunnable, 1000);
					countdown--;
				}else{
					gameGraphics.getWater4().setVisibility(View.GONE);
					gameGraphics.getWater3().setVisibility(View.GONE);
					gameGraphics.getWater2().setVisibility(View.GONE);
					gameGraphics.getWater1().setVisibility(View.VISIBLE);
					soundHandler.playSoundEffect(6, 50);
					gameGraphics.getCountdownView().setTextSize(TypedValue.COMPLEX_UNIT_DIP, 180);
					gameGraphics.getCountdownView().setText("Go!");
					gameGraphics.getCountdownView().startAnimation(fadeOut);
					if(gameInSession){
						initiateGameHandlers();
						countdownHandler.removeCallbacks(countdownRunnable);
					}
				}
			}
		};
				
		countdownHandler.postDelayed(countdownRunnable, 300);
	}

	//initiate game handlers
	private void initiateGameHandlers() {
		
		levelHandler = new Handler();
		levelRunnable = new Runnable() {

			@Override
			public void run() {
				
				decrementgarbageDelayTime();
				decrementAnimDuration();
				gameGraphics.updateLevel(level+1);
				
				if(level == NULL){	
					creategarbageFallHandler();	
				}
				if(level <= MAX_LEVEL ){
					level++;
				}
				if(gameInSession){
					levelHandler.postDelayed(levelRunnable, TIME_PER_LEVEL);
				}
			}
			// garbage ANIMATION DURATION CONTROLLER
			private void decrementAnimDuration() {
				
				Random rand = new Random();
				if( level % 2 == 0 && level < 20){
					if( level < 7 ){
						animDuration -= (DURATION_DECREMENT + rand.nextInt(TIMING_RANDOMIZER));
					}else if( level < 15 ){
						animDuration -= (DURATION_DECREMENT_MED + rand.nextInt(TIMING_RANDOMIZER));
					}else{
						animDuration -= (DURATION_DECREMENT_SMALL + rand.nextInt(TIMING_RANDOMIZER));
					}
				}
			}
			// garbage DELAYS CONTROLLER
			private void decrementgarbageDelayTime() {
				
				if( level % 2 == 1 && garbageDelayTime >= 300){
					
					if (level < 7) {
						garbageDelayTime -= garbage_DELAY_TIME_DECREMENT;
					}else if( level < 15 ){
						garbageDelayTime -= garbage_DELAY_TIME_DECREMENT_MED;
					}else{
						garbageDelayTime -= garbage_DELAY_TIME_DECREMENT_SMALL;
					}
				}
			}
		};

		levelHandler.post(levelRunnable);
		
		//enable reset button when the handlers are running 
		main.reset_btn.setEnabled(true);
		handlerStarted = true;
	}

	// handler for individual garbage fall event
	protected void creategarbageFallHandler() {

		garbageIntervalHandler = new Handler();
		garbageIntervalRunnable = new Runnable() {

			@Override
			public void run() {
				//get random garbage position
				int position = generateRandomPosition();
				
				startgarbageFall(position);//start an garbage fall
				
				if(gameInSession){
					//set delay time of every garbage fall
					garbageIntervalHandler.postDelayed(garbageIntervalRunnable, garbageDelayTime);
				}
			}
		};
		if(gameInSession){
			garbageIntervalHandler.post(garbageIntervalRunnable);
		}
		

	}// end startgarbageFallTimer

	// Generates a random position for the garbage fall
	// 0 for left; 1 for center; 2 for right
	protected int generateRandomPosition() {
		
		Random rand = new Random();
		int pos = rand.nextInt(3);
		//conditions prevent same position > 3 times
		if(prevPosition == pos){
			changeCount++;
		}else{
			changeCount = NULL;
		}
		if(changeCount == 3){
			
			if(pos == RIGHT){
				pos = rand.nextInt(1);
			}else if(pos == CENTER){
				pos = (rand.nextFloat() > 0.5) ? RIGHT : LEFT;
			}else{
				pos = (rand.nextFloat() > 0.5) ? CENTER : RIGHT;
			}
			changeCount = NULL;
			
		}
		prevPosition = pos;
		return pos;
				
	

	}// end generateRandomPosition

	//method that creates and animates the garbage
	protected void startgarbageFall(int position) {
		
		//set to final so it's accessible inside runnable
		final int pos = position;
		
		//create a random delay for every garbage fall
		Random r = new Random();
		int delaygarbageFall = r.nextInt(TIMING_RANDOMIZER);
		
//		Log.d("Delay garbage Fall", "garbage fall delay: " +String.valueOf(delaygarbageFall));
		garbageDelayHandler = new Handler();
		garbageDelayRunnable = new Runnable(){

			@Override
			public void run() {
				//shakes villian
				gameGraphics.shakevillian(pos);
				//randomize type of garbage
				String object = getTypeOfgarbage();
				
				//creates the garbage
				final ImageView garbageView = gameGraphics.creategarbage(pos, object);

				//garbage animation
				garbageAnimation = AnimationUtils.loadAnimation(main,
						R.anim.garbagedrop);
				garbageAnimation.setDuration(animDuration);
				garbageView.startAnimation(garbageAnimation);
				animationStarted = true;
				
				//set Listener
				setMyAnimListener(garbageAnimation, pos, garbageView, object);
							
			}

		};
		garbageDelayHandler.postDelayed(garbageDelayRunnable, delaygarbageFall);

	}// end startgarbageFall()
	
	private String getTypeOfgarbage() {
		
		Random rand = new Random();
		Float randF = rand.nextFloat();
		String object;

		if(randF<.25) object="volt";
		else if(randF<.5)object="kola";
		else if(randF<.7)object="paper";
		else if(randF<.9)object="can";
		else object="poly";

		
		return object;
	}

	protected void setMyAnimListener(Animation garbageAnimation2, final int pos,
									 final ImageView garbageView, final String object) {



		animvoltListener = new AnimationListener() {

			@Override
			public void onAnimationEnd(Animation animation) {
				if(handlerStarted){
					//condition prevents showing of broken garbage
					garbageView.setVisibility(View.GONE);
					checkIfScored(pos, -1,object);
					animationStarted = false;
				}
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationStart(Animation animation) {

			}

		};

		animcanListener = new AnimationListener() {

			@Override
			public void onAnimationEnd(Animation animation) {
				if(handlerStarted){
					//condition prevents showing of broken garbage
					garbageView.setVisibility(View.GONE);
					checkIfScored(pos, 2,object);
					animationStarted = false;
				}
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationStart(Animation animation) {

			}

		};
		//set the listener
		animpolyListener = new AnimationListener() {

			@Override
			public void onAnimationEnd(Animation arg0) {
				if(handlerStarted){//condition prevents showing of broken garbage
					garbageView.setVisibility(View.GONE);
					checkIfScored(pos, 3,object);
					animationStarted = false;
				}
			}
			@Override
			public void onAnimationStart(Animation animation) {
			}
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			
		};
		animnormalListener = new AnimationListener() {

			@Override
			public void onAnimationEnd(Animation arg0) {
				if(handlerStarted){//condition prevents showing of broken garbage
					garbageView.setVisibility(View.GONE);
					checkIfScored(pos, 1,object);
					animationStarted = false;
				}
			}

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			
		};


		if(object.equals("volt")){
			garbageAnimation.setAnimationListener(animvoltListener);
		}
		else if(object.equals("poly")){
			garbageAnimation.setAnimationListener(animpolyListener);
		}else if(object.equals("can")){
			garbageAnimation.setAnimationListener(animcanListener);
		}else{
			garbageAnimation.setAnimationListener(animnormalListener);
		}
	}
	
	public void checkIfScored(int position, int scoreInc,String object) {
		boolean caught = false;
		
		int xBasketPosition = gameGraphics.getBasketScrollView().getScrollX();
		int	widthReference = gameGraphics.getWidthReference();
		
		xBasketPosition = gameGraphics.convertToDp(xBasketPosition);
				
		int leftCond = widthReference - 47;
		int centerCondL = (widthReference / 2) + 48;
		int centerCondR = (widthReference / 2) - 48;
		int rightCond = 47;
		
		switch (position) {
		case 0:
			
			if(xBasketPosition > leftCond){
				caught = true;
			}
			
			break;
		case 1:
			if(xBasketPosition < centerCondL 
						&& xBasketPosition > centerCondR){
				caught = true;
			}
			break;
		case 2:
			if(xBasketPosition < rightCond){
				caught = true;
			}
			break;
		default:
			break;
		}
		
		if(caught){
			
			if(scoreInc==-1){
				main.gameOver();
				soundHandler.playSoundEffect(0, 50);
			}
			else{
				scoreCount += scoreInc;
				gameGraphics.updateScore(scoreCount);
				if(scoreInc == 2 || scoreInc==1){
					soundHandler.playSoundEffect(3, 50);
				}else if(scoreInc == 3){
					soundHandler.playSoundEffect(4, 50);
				}
			}
						
		}

		else if(scoreInc!=-1){
			
			gameGraphics.showBrokengarbage(position, scoreInc,object);
			soundHandler.playSoundEffect(2, 50);
			numberOfLives--;
			if(numberOfLives==2){
				gameGraphics.getWater1().setVisibility(View.GONE);
				gameGraphics.getWater2().setVisibility(View.VISIBLE);
			}
			else if(numberOfLives==1){
				gameGraphics.getWater2().setVisibility(View.GONE);
				gameGraphics.getWater3().setVisibility(View.VISIBLE);
			}

			if(numberOfLives <= 0){
				gameGraphics.getWater3().setVisibility(View.GONE);
				gameGraphics.getWater4().setVisibility(View.VISIBLE);
				main.gameOver();


			}else{
				gameGraphics.updateLives(numberOfLives);
			}

		}
		
	}//end of checkIfScored()

	//restarts the game
	public void resetGame() {
		Log.d("resetGame", "RESET");

		gameGraphics.getgarbageView().setVisibility(View.GONE);
		animationStarted = false;
		numberOfLives = 3;
		gameGraphics.updateLives(numberOfLives);
		scoreCount = 0;

		gameGraphics.updateScore(scoreCount);


		startGame();
		
	}//end resetGame();

	//stops the game
	public void stopGame() {
		Log.d("stopGame", "STOPPED");
		
		gameInSession = false;
		level = 0;
		
		cancelTimers();
		
		if(animationStarted){
			Log.d("stopGame","animation cleared!");
			garbageAnimation.cancel();
		}
		
	}//end of stopGame()

	//cancels timers
	void cancelTimers() {
		Log.d("cancelTimer", "TIMER CANCELLED");
		
		garbageDelayHandler.removeCallbacks(garbageDelayRunnable);
		levelHandler.removeCallbacks(levelRunnable);
		garbageIntervalHandler.removeCallbacks(garbageIntervalRunnable);
		countdownHandler.removeCallbacks(countdownRunnable);
		handlerStarted = false;
	
	}//end of cancelTimers()
	
}

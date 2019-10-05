package com.nina.vaja1;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.math.Rectangle;

import java.util.Iterator;
import java.util.Random;

public class vaja1Game extends ApplicationAdapter {
	private SpriteBatch batch;
	private Texture backgroundImg;
	private Texture grayCarImage;
	private Texture greenCarImage;
	private Texture coinImage;
	private Texture gasolineImage;
	private Sound carCrashSound;
	private Sound coinPickupSound;
	private Sound gasFillSound;
	private OrthographicCamera camera;
	private Rectangle greenCar;
	private Array<Rectangle> grayCars;
	private Array<Rectangle> coins;
	private Array<Rectangle> gasolines;
	private long lastCarTime;
	private long lastCoinTime;
	private long lastGasTime;
	private int moneyCount;
	private int carHealth;
	private double gas;
	private boolean isEnd;
	private Array<Integer> spawnCarsCoordinates;

	private BitmapFont font;

	private static int speed = 600;
	private static int grayCarSpeed = 200;
	private static int coinSpeed = 150;
	private static int gasolineSpeed = 120;
	private static long createNewCoins=3000000000L;
	private static long createNewGrayCar = 700000000;
	private static long createNewGasoline = 3000000000L;

	private void commandMoveLeft() {
		greenCar.x -= speed * Gdx.graphics.getDeltaTime();
		if(greenCar.x < 170) greenCar.x = 170;
	}

	private void commandMoveRight() {
		greenCar.x += speed * Gdx.graphics.getDeltaTime();
		if(greenCar.x > Gdx.graphics.getWidth() - greenCarImage.getWidth() - 170)
			greenCar.x = Gdx.graphics.getWidth() - greenCarImage.getWidth() - 170;
	}

	private void commandMoveDown() {
		greenCar.y -= speed * Gdx.graphics.getDeltaTime();
		if(greenCar.y < 0) greenCar.y = 0;
	}

	private void commandMoveUp() {
		greenCar.y += speed * Gdx.graphics.getDeltaTime();

		if(greenCar.y > Gdx.graphics.getHeight() - greenCarImage.getHeight())
			greenCar.y = Gdx.graphics.getHeight() - greenCarImage.getHeight();
	}

	private void commandTouched() {
		Vector3 touchPos = new Vector3();
		touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
		camera.unproject(touchPos);
		greenCar.x = (int)touchPos.x - greenCarImage.getWidth() / 2;
	}

	private void commandExitGame() {
		Gdx.app.exit();
	}

	@Override
	public void create () {

		isEnd = false;
		backgroundImg = new Texture("road.png");

		font = new BitmapFont();
		font.getData().setScale(2);
		moneyCount = 0;
		carHealth = 100;
		gas = 100;

		greenCarImage = new Texture(Gdx.files.internal("greenCar.png"));
		grayCarImage = new Texture(Gdx.files.internal("grayCar.png"));
		coinImage = new Texture(Gdx.files.internal("coin.png"));
		gasolineImage = new Texture(Gdx.files.internal("gasoline.png"));

		carCrashSound = Gdx.audio.newSound(Gdx.files.internal("carCrash.wav"));
		coinPickupSound = Gdx.audio.newSound(Gdx.files.internal("coinDrop.wav"));
		gasFillSound = Gdx.audio.newSound(Gdx.files.internal("gasFill.wav"));

		camera = new OrthographicCamera();
		camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch = new SpriteBatch();

		greenCar = new Rectangle();
		greenCar.x = Gdx.graphics.getWidth()/2 - greenCarImage.getWidth()/2;
		greenCar.y = 20;
		greenCar.width = greenCarImage.getWidth();
		greenCar.height = greenCarImage.getHeight();

		grayCars = new Array<Rectangle>();
		coins = new Array<Rectangle>();
		gasolines = new Array<Rectangle>();
		spawnCarsCoordinates = new Array<Integer>();

		spawnCarsCoordinates.add(250);
		spawnCarsCoordinates.add(280);
		spawnCarsCoordinates.add(350);
		spawnCarsCoordinates.add(370);
		spawnCarsCoordinates.add(430);
		spawnCarsCoordinates.add(510);
		spawnCarsCoordinates.add(530);
		spawnCarsCoordinates.add(580);
		spawnCarsCoordinates.add(680);
		spawnCarsCoordinates.add(700);
		spawnCarsCoordinates.add(750);
	}

	private void spawnGrayCars(){
		Rectangle car = new Rectangle();
		Random rnd = new Random();
		car.x = spawnCarsCoordinates.get(MathUtils.random(0, 10));
		car.y = Gdx.graphics.getHeight();
		car.width  = grayCarImage.getWidth();
		car.height = grayCarImage.getHeight();
		grayCars.add(car);
		lastCarTime = TimeUtils.nanoTime();
	}

	private void spawnCoins(){
		Rectangle coin = new Rectangle();
		coin.x = MathUtils.random(170, Gdx.graphics.getWidth() - coinImage.getWidth() - 170);
		coin.y = Gdx.graphics.getHeight();
		coin.width  = coinImage.getWidth();
		coin.height = coinImage.getHeight();
		coins.add(coin);
		lastCoinTime = TimeUtils.nanoTime();
	}

	private void spawnGas(){
		Rectangle gas = new Rectangle();
		gas.x = MathUtils.random(170, Gdx.graphics.getWidth() - gasolineImage.getWidth() - 170);
		gas.y = Gdx.graphics.getHeight();
		gas.width  = gasolineImage.getWidth();
		gas.height = gasolineImage.getHeight();
		gasolines.add(gas);
		lastGasTime = TimeUtils.nanoTime();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.update();

		batch.setProjectionMatrix(camera.combined);

		batch.begin();
		batch.draw(backgroundImg, 0, 0);
		batch.draw(greenCarImage, greenCar.x, greenCar.y);

		for(Rectangle car : grayCars){
			batch.draw(grayCarImage, car.x, car.y);
		}

		for(Rectangle coin : coins){
			batch.draw(coinImage, coin.x, coin.y);
		}

		for(Rectangle gasoline : gasolines){
			batch.draw(gasolineImage, gasoline.x, gasoline.y);
		}

		font.setColor(Color.YELLOW);
		font.draw(batch, "Money: " + moneyCount, Gdx.graphics.getWidth() - 150, Gdx.graphics.getHeight()-20);
		font.setColor(Color.RED);
		font.draw(batch, "Health: " + carHealth, 20, Gdx.graphics.getHeight()-20);
		font.setColor(Color.GREEN);
		font.draw(batch, "Gas: " + (double)Math.round(gas * 100)/100, 20, Gdx.graphics.getHeight()-70);

		batch.end();

		if(Gdx.input.isTouched()) commandTouched(); //mouse or touch screen
		if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) commandMoveLeft();
		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) commandMoveRight();
		if(Gdx.input.isKeyPressed(Input.Keys.UP)) commandMoveUp();
		if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) commandMoveDown();
		if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) commandExitGame();

		if(TimeUtils.nanoTime() - lastCoinTime > createNewCoins) spawnCoins();
		if(TimeUtils.nanoTime() - lastCarTime > createNewGrayCar) spawnGrayCars();
		if(TimeUtils.nanoTime() - lastGasTime > createNewGasoline) spawnGas();

		if(!isEnd && gas > 0){
			gas -=0.05;
		}

		if (carHealth > 0 && gas > 0){

			for(Iterator<Rectangle> iter = coins.iterator(); iter.hasNext();){
				Rectangle coin = iter.next();
				coin.y -= coinSpeed * Gdx.graphics.getDeltaTime();
				if(coin.y + greenCarImage.getHeight() < 0){
					iter.remove();
				}
				if(coin.overlaps(greenCar)){
					coinPickupSound.play();
					moneyCount++;
					if(moneyCount%10==0){
						grayCarSpeed +=30;
					}
					iter.remove();
				}
			}

			for(Iterator<Rectangle> iter = grayCars.iterator(); iter.hasNext();){
				Rectangle car = iter.next();
				car.y -= grayCarSpeed * Gdx.graphics.getDeltaTime();
				if(car.y + grayCarImage.getHeight() < 0){
					iter.remove();
				}
				if(car.overlaps(greenCar)){
					carCrashSound.play();
					carHealth-=10;
					iter.remove();
				}
			}

			for(Iterator<Rectangle> iter = gasolines.iterator(); iter.hasNext();){
				Rectangle gasoline = iter.next();
				gasoline.y -= gasolineSpeed * Gdx.graphics.getDeltaTime();
				if(gasoline.y + gasolineImage.getHeight() < 0){
					iter.remove();
				}
				if(gasoline.overlaps(greenCar)){
					gasFillSound.play();
					iter.remove();
					gas +=10;
				}
			}
		}
		else{
			batch.begin();
			font.setColor(Color.RED);
			font.draw(batch, "THE END", Gdx.graphics.getHeight() /2, Gdx.graphics.getWidth()/2);
			isEnd=true;
			batch.end();
		}
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		backgroundImg.dispose();
		greenCarImage.dispose();
		grayCarImage.dispose();
		coinImage.dispose();
		font.dispose();
	}
}

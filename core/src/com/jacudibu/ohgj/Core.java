package com.jacudibu.ohgj;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.List;
import java.util.Random;

public class Core extends ApplicationAdapter {
	SpriteBatch batch;

	int dodged = 0;

	float playerSpeed = 200f;
	float rocketSpeed = 75f;

	Vector2 playerPosition;
	Array<Vector2> rocketPositions;

	Texture rocketTexture;
	Texture playerTexture;
	BitmapFont bitmapFont;

	float rocketSpawnInterval = 1f;
	float currentRocketTimer = 0f;
	float nextRandomRocketHeight = 100f;

	@Override
	public void create () {
		batch = new SpriteBatch();
		rocketTexture = new Texture("rocket.png");
		playerTexture = new Texture("ship.png");

		playerPosition = new Vector2(Gdx.graphics.getWidth() / 2 - 16, 20);
		rocketPositions = new Array<Vector2>();

		bitmapFont = new BitmapFont();
	}

	@Override
	public void render () {
		update();

		Gdx.gl.glClearColor(0.75f, 0.75f, 0.75f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();

		batch.draw(playerTexture, playerPosition.x, playerPosition.y);
		for (int i = 0; i < rocketPositions.size; i++) {
			Vector2 current = rocketPositions.get(i);
			batch.draw(rocketTexture, current.x, current.y);
		}

		String text = "Dodged: " + dodged;
		bitmapFont.draw(batch, text, Gdx.graphics.getWidth() - 100, Gdx.graphics.getHeight() - 20);

		batch.end();
	}

	void update() {
		float delta = Gdx.graphics.getDeltaTime();

		movePlayer(delta);

		spawnRockets(delta);
		moveRockets(delta);
		collideRockets(delta);
	}

	@Override
	public void dispose () {
		batch.dispose();
		playerTexture.dispose();
		rocketTexture.dispose();
		bitmapFont.dispose();
	}

	void movePlayer(float delta) {
		if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			playerPosition.x += playerSpeed * delta;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			playerPosition.x -= playerSpeed * delta;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
			playerPosition.y += playerSpeed * delta;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
			playerPosition.y -= playerSpeed * delta;
		}

		playerPosition.x = MathUtils.clamp(playerPosition.x, 0, Gdx.graphics.getWidth() - 32f);
		playerPosition.y = MathUtils.clamp(playerPosition.y, 0, Gdx.graphics.getHeight() - 32f);
	}

	void spawnRockets(float delta) {
		currentRocketTimer -= delta;
		if (currentRocketTimer < 0f) {
			currentRocketTimer += rocketSpawnInterval;

			Vector2 newPos = new Vector2();
			newPos.x = MathUtils.random(0, Gdx.graphics.getWidth());
			newPos.y = Gdx.graphics.getHeight() + 64;

			rocketPositions.add(newPos);
		}
	}

	void moveRockets(float delta) {
		Vector2 rocketVelocity = new Vector2(0f, -5f * delta * rocketSpeed);

		for (int i = 0; i < rocketPositions.size; i++) {
			rocketPositions.get(i).add(rocketVelocity);
		}
	}

	void collideRockets(float delta) {

		Rectangle ship = new Rectangle();
		ship.height = 16f;
		ship.width = 32f;
		ship.x = playerPosition.x;
		ship.y = playerPosition.y;

		Rectangle rocket = new Rectangle();
		rocket.width = 6f;
		rocket.height = 16f;

		for (int i = 0; i < rocketPositions.size; i++) {
			Vector2 current = rocketPositions.get(i);
			rocket.x = current.x;
			rocket.y = current.y;

			if (rocket.overlaps(ship)) {
				die();
			}

			if (current.y < -32f) {
				dodged++;

				current.y = Gdx.graphics.getHeight() + nextRandomRocketHeight;
				current.x = MathUtils.random(0, Gdx.graphics.getWidth());

				nextRandomRocketHeight = MathUtils.random(100f, 400f);
			}
		}
	}

	void die() {
		reset();
	}

	void reset() {
		playerPosition = new Vector2(Gdx.graphics.getWidth() / 2 - 16, 20);
		rocketPositions = new Array<Vector2>();
		dodged = 0;
	}
}

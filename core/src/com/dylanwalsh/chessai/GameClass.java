package com.dylanwalsh.chessai;
import com.badlogic.gdx.Game;
import com.dylanwalsh.chessai.screens.GameScreen;
public class GameClass extends Game {
	@Override
	public void create() {
		setScreen(new GameScreen());
	}

	@Override
	public void render() {
		super.render();
	}

	@Override
	public void dispose() {
		super.dispose();
	}
}
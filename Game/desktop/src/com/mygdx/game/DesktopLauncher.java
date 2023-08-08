package com.mygdx.game;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.mygdx.game.TriviaGame;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setWindowIcon("graphics/desktop.png");
		config.setForegroundFPS(60);
		config.setTitle("TriviaGPT");
		config.setResizable(false); // We don't support variable resolutions
        config.setWindowedMode(1200, 800); // Add this line to set window dimensions
		new Lwjgl3Application(new TriviaGame(), config);
	}
}

package com.example.demo;

import java.util.List;
import java.util.stream.Collectors;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;


public class InvadersApplication extends Application{

	
	private Pane root = new Pane();
	
	private Sprite player = new Sprite(300, 750, 40, 40, "player", Color.BLUE);
	
	double t = 0;
	
	/*Crea la pantalla y añade contenido al panel*/
	private Parent createContent() {
		
		root.setPrefSize(600, 800);
		root.getChildren().add(player);
		
		AnimationTimer timer = new AnimationTimer() {

			@Override
			public void handle(long now) {
				update();				
			}			
		};
		
		timer.start();
		
		nextLevel();
		
		return root;
	}
	
	/*Crea a los enemigos los posciona y los añade*/
	private void nextLevel() {
		for(int i=0; i < 5; i++) {
			Sprite enemy = new Sprite(90 + i*100, 150, 30, 30, "enemy", Color.RED);			
			root.getChildren().add(enemy);
		}
	}
	
	/*Crea una lista con los objetos guadados haciendo un casteo*/
	private List<Sprite> sprites() {
		return root.getChildren().stream().map(s-> (Sprite)s).collect(Collectors.toList());
	}
	
	
	/*Recorre la lista para ver si hay algun disparo de alguien y hace mover la bala
	 * en el caso de que la bala le de a alguien, este objeto y la bala desaparecen*/
	private void update() {
		t += 0.016;
		sprites().forEach(s -> {
			switch (s.type){
				case "enemybullet":
					s.moveDown();
					
					if(s.getBoundsInParent().intersects(player.getBoundsInParent())) {
						player.dead = true;
						s.dead = true; //s es la bala y desaparece
					}
				break;
				
				case "playerbullet":
					s.moveUp();
					
					sprites().stream().filter(e -> e.type.equals("enemy")).forEach(enemy -> {
						if(s.getBoundsInParent().intersects(enemy.getBoundsInParent())) {
							enemy.dead = true; // el enemigo con el que haya colisionado la bala muere
							s.dead = true; //s es la bala y desaparece							
						}
					});
				break;
				
				/*Disparos random, se crean disparos del enemigo*/
				case "enemy":
					
					if(t > 2) {
						if(Math.random() < 0.3) {
							shoot(s);
						}
					}
			}
		});

		/*Esto es lo que hace que realmente desaparezca el ojeto de la pantalla una vez haya muerto*/
		root.getChildren().removeIf(n -> {
			Sprite s = (Sprite) n;
			return s.dead;
		});
		
		if(t > 2) {
			t = 0;
		}
		
	}
	
	/*Crea la bala y guarda el nombre de quien la dispara*/
	private void shoot(Sprite who) {
		Sprite s = new Sprite((int)who.getTranslateX() + 20, (int)who.getTranslateY(), 5, 20, who.type + "bullet", Color.BLACK);
		root.getChildren().add(s);
	}

	/* Se crea la escena principal con todo el contenido y se mueve el jugador con las teclas*/
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		Scene scene = new Scene(createContent());
		
		scene.setOnKeyPressed(e -> {
			switch (e.getCode()) {
				case LEFT:
					player.moveLeft();
				break;
				case RIGHT:
					player.moveRight();
				break;
				case SPACE:
					shoot(player);
				break;
			}
		});
		
		primaryStage.setScene(scene);
		primaryStage.show();		
	}
	
	/* Clase para crear los objetos*/
	private static class Sprite extends Rectangle {
		
		boolean dead = false;
		final String type;
		
		Sprite(int x, int y, int w, int h, String type, Color color){
			super(w, h, color);
				
			this.type = type;
			setTranslateX(x);
			setTranslateY(y);
		}
		
		void moveLeft() {
			setTranslateX(getTranslateX() - 5);
		}
		
		void moveRight() {
			setTranslateX(getTranslateX() + 5);
		}
		
		void moveUp() {
			setTranslateY(getTranslateY() - 5);
		}
		
		void moveDown() {
			setTranslateY(getTranslateY() + 5);
		}
		
	}
	
	
	
	public static void main(String [] args) {
		launch(args);
	}

}

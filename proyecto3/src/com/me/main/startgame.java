/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.me.main;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.WorldManifold;
import com.badlogic.gdx.utils.Array;
import com.gushikustudios.rube.RubeScene;
import com.gushikustudios.rube.loader.RubeSceneLoader;
 
public class startgame extends InputAdapter implements ApplicationListener,ContactListener {
 
	final static float MAX_VELOCITY = 3f;		
	boolean jump = false;	
	World world;
	Body player;
	Fixture playerPhysicsFixture;
	Fixture playerSensorFixture;
	OrthographicCamera cam;
	Box2DDebugRenderer renderer;
	float stillTime = 0;
	long lastGroundTime = 0;
	SpriteBatch batch;
	BitmapFont font;
	private RubeSceneLoader loader;
	private RubeScene scene;
	private boolean drif = false;
	
	/*>>>> Tipos de body <<<<<<*/
	static int TIPOPLAYER = 4;
	static int TIPOPISO = 1;
	static int TIPODERRAPE = 3;
	static int TIPOSIERRA = 2;
 
	@Override
	public void create() {
		loader = new RubeSceneLoader();
	    scene = loader.loadScene(Gdx.files.internal("data/nivel1/nivel1.json"));
		world = scene.getWorld();	
		renderer = new Box2DDebugRenderer();
		cam = new OrthographicCamera(10, 10*480/800f);
		createWorld();
		Gdx.input.setInputProcessor(this);
		batch = new SpriteBatch();
		font = new BitmapFont();
	}
 
	private void createWorld() {
		/*float y1 = 1; //(float)Math.random() * 0.1f + 1;
		float y2 = y1;
		for(int i = 0; i < 50; i++) {
			Body ground = createEdge(BodyType.StaticBody, -50 + i * 2, y1, -50 + i * 2 + 2, y2, 0);			
			y1 = y2;
			y2 = 1; //(float)Math.random() + 1;
		}
 
		/*Body box = createBox(BodyType.StaticBody, 1, 1, 0);
		box.setTransform(30, 3, 0);
		box = createBox(BodyType.StaticBody, 1.2f, 1.2f, 0);
		box.setTransform(5, 2.4f, 0);*/
		player = createPlayer();
		player.setTransform(10.0f, 4.0f, 0);
		player.setFixedRotation(true);						
 
		/*for(int i = 0; i < 20; i++) {
			box = createBox(BodyType.DynamicBody, (float)Math.random(), (float)Math.random(), 3);
			box.setTransform((float)Math.random() * 10f - (float)Math.random() * 10f, (float)Math.random() * 10 + 6, (float)(Math.random() * 2 * Math.PI));
		}
 
		for(int i = 0; i < 20; i++) {
			Body circle = createCircle(BodyType.DynamicBody, (float)Math.random() * 0.5f, 3);
			circle.setTransform((float)Math.random() * 10f - (float)Math.random() * 10f, (float)Math.random() * 10 + 6, (float)(Math.random() * 2 * Math.PI));
		}*/
 
		/*platforms.add(new MovingPlatform(-2, 3, 2, 0.5f, 2, 0, 4));
		platforms.add(new MovingPlatform(17, 3, 5, 0.5f, 0, 2, 5));		
		platforms.add(new MovingPlatform(-7, 5, 2, 0.5f, -2, 2, 8));		
//		platforms.add(new MovingPlatform(40, 3, 20, 0.5f, 0, 2, 5));*/
	}
 
	private Body createBox(BodyType type, float width, float height, float density) {
		BodyDef def = new BodyDef();
		def.type = type;
		Body box = world.createBody(def);
 
		PolygonShape poly = new PolygonShape();
		poly.setAsBox(width, height);
		box.createFixture(poly, density);
		poly.dispose();
 
		return box;
	}	
 
	private Body createEdge(BodyType type, float x1, float y1, float x2, float y2, float density) {
		BodyDef def = new BodyDef();
		def.type = type;
		Body box = world.createBody(def);
 
		PolygonShape poly = new PolygonShape();		
		poly.setAsBox(x2 - x1, y2 - y1);
		box.createFixture(poly, density);
		box.setTransform(x1, y1, 0);
		poly.dispose();
 
		return box;
	}
 
	private Body createCircle(BodyType type, float radius, float density) {
		BodyDef def = new BodyDef();
		def.type = type;
		Body box = world.createBody(def);
 
		CircleShape poly = new CircleShape();
		poly.setRadius(radius);
		box.createFixture(poly, density);
		poly.dispose();
 
		return box;
	}	
 
	private Body createPlayer() {
		BodyDef def = new BodyDef();
		def.type = BodyType.DynamicBody;
 
		PolygonShape poly = new PolygonShape();		
		poly.setAsBox(0.2f, 0.3f);
		
		FixtureDef sqrdef = new FixtureDef();
		sqrdef.shape = poly;	
		sqrdef.density = 1;
 
		CircleShape circle = new CircleShape();
		circle.setRadius(0.2f);
		circle.setPosition(new Vector2(0, -0.25f));
		
		FixtureDef circledef = new FixtureDef();
		circledef.shape = circle;
		circledef.density = 1;
		
		Body box = world.createBody(def);
		scene.setCustom(box, "tipo", TIPOPLAYER);		
		box.setBullet(true);
		playerPhysicsFixture = box.createFixture(sqrdef);
		playerSensorFixture = box.createFixture(circledef);
		
 
		return box;
	}
 
	@Override
	public void resume() {
 
	}
 
	@Override
	public void render() {
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		cam.position.set(player.getPosition().x, player.getPosition().y, 0);
		cam.update();
		renderer.render(world,cam.combined);
		world.setContactListener(this);
		Vector2 vel = player.getLinearVelocity();
		Vector2 pos = player.getPosition();		
		boolean grounded = isPlayerGrounded(Gdx.graphics.getDeltaTime());
		if(grounded) {
			lastGroundTime = System.nanoTime();
		} else {
			if(System.nanoTime() - lastGroundTime < 100000000) {
				grounded = true;
			}
		}
 
		// cap max velocity on x		
		if(Math.abs(vel.x) > MAX_VELOCITY) {			
			vel.x = Math.signum(vel.x) * MAX_VELOCITY;
			player.setLinearVelocity(vel.x, vel.y);
		}
 
		// calculate stilltime & damp
		if(!Gdx.input.isKeyPressed(Keys.A) && !Gdx.input.isKeyPressed(Keys.D)) {			
			stillTime += Gdx.graphics.getDeltaTime();
			player.setLinearVelocity(vel.x * 0.95f, vel.y);
		}
		else { 
			stillTime = 0;
		}
		
		//System.out.println(grounded);
 
		// disable friction while jumping
		if(!grounded) {			
			/*playerPhysicsFixture.setFriction(0f);
			playerSensorFixture.setFriction(0f);*/		
		} else {
			/*if(!Gdx.input.isKeyPressed(Keys.A) && !Gdx.input.isKeyPressed(Keys.D) && stillTime > 0.2) {
				playerPhysicsFixture.setFriction(100f);
				playerSensorFixture.setFriction(100f);
			}
			else {
				playerPhysicsFixture.setFriction(0.2f);
				playerSensorFixture.setFriction(0.2f);
			}*/
		}		
 
		// apply left impulse, but only if max velocity is not reached yet
		if(Gdx.input.isKeyPressed(Keys.A) && vel.x > -MAX_VELOCITY) {
			player.applyLinearImpulse(-0.5f, 0, pos.x, pos.y,true);
		}
 
		// apply right impulse, but only if max velocity is not reached yet
		if(Gdx.input.isKeyPressed(Keys.D) && vel.x < MAX_VELOCITY) {
			player.applyLinearImpulse(0.5f, 0, pos.x, pos.y,true);
		}
		
 
		// jump, but only when grounded
		if(jump) {			
			jump = false;
			if(grounded) {
				player.setLinearVelocity(vel.x, 0);			
				System.out.println("jump before: " + player.getLinearVelocity());
				player.setTransform(pos.x, pos.y + 0.01f, 0);
				player.applyLinearImpulse(0, 2f, pos.x, pos.y,true);			
				System.out.println("jump, " + player.getLinearVelocity());				
			}
		}
		
		if(drif){
			if(vel.y < -3)
				player.applyLinearImpulse(0, 0.2f, pos.x, pos.y,true);
		}
 
		// le step...			
		world.step(Gdx.graphics.getDeltaTime(), 4, 4);
		player.setAwake(true);		
 
		cam.project(point.set(pos.x, pos.y, 0));
		batch.begin();
		font.drawMultiLine(batch, "friction: " + playerPhysicsFixture.getFriction() + "\ngrounded: " + grounded, point.x+20, point.y);
		batch.end();
	}	
 
	private boolean isPlayerGrounded(float deltaTime) {		
		Array<Contact> contactList = world.getContactList();
		for(int i = 0; i < contactList.size; i++) {
			Contact contact = contactList.get(i);
			if(contact.isTouching() && (contact.getFixtureA() == playerSensorFixture ||
			   contact.getFixtureB() == playerSensorFixture)) {			
 
				Vector2 pos = player.getPosition();
				WorldManifold manifold = contact.getWorldManifold();
				boolean below = true;
				for(int j = 0; j < manifold.getNumberOfContactPoints(); j++) {
					below &= (manifold.getPoints()[j].y < pos.y - 0.1f);
				}
				if(below) {                                                                                      
                    return true;                        
				}
				return false;
			}
		}
		return false;
	}
 
	@Override
	public void resize(int width, int height) {
 
	}
 
	@Override
	public void pause() {
 
	}
 
	@Override
	public void dispose() {
 
	}
 
	@Override
	public boolean keyDown(int keycode) {
		if(keycode == Keys.W) jump = true;
		return false;
	}
 
	@Override
	public boolean keyUp(int keycode) {
		if(keycode == Keys.W) jump = false;
		return false;
	}
 
	Vector2 last = null;
	Vector3 point = new Vector3();
 
	@Override
	public boolean touchDown(int x, int y, int pointerId, int button) {
		return false;
	}

	@Override
	public void beginContact(Contact contact) {
		// TODO Auto-generated method stub
		Body objcontact1;
	    Body objcontact2;
	    Boolean isPlayer = false;
	    int TipoA = (Integer) scene.getCustom(contact.getFixtureA().getBody(), "tipo", 0);
	    int TipoB = (Integer) scene.getCustom(contact.getFixtureB().getBody(), "tipo", 0);
	    if(TipoA==4){		
	    	objcontact1=contact.getFixtureB().getBody();
	    	objcontact2 = null;
	    	isPlayer = true;
	    }else if(TipoB==4){
	    	objcontact1=contact.getFixtureA().getBody();	
	    	objcontact2 = null;
	    	isPlayer = true;
	    }else{
	    	objcontact1=contact.getFixtureA().getBody();
	    	objcontact2=contact.getFixtureB().getBody();		
	    }
	    int tipopbj1 = (Integer)scene.getCustom(objcontact1, "tipo", 0);
	    int tipopbj2 = (Integer)scene.getCustom(objcontact2, "tipo", 0);
	    
	    if(isPlayer){
	    	if(tipopbj1==3){
	    		drif = true;
	    		//playerPhysicsFixture.setFriction(0.2f);
	    		//playerSensorFixture.setFriction(0.2f);
	    		System.out.println(true);
	    	}
	    }
	    if(!isPlayer){
	    	
	    }
	}

	@Override
	public void endContact(Contact contact) {
		// TODO Auto-generated method stub
		Body objcontact1;
	    Body objcontact2;
	    Boolean isPlayer = false;
	    int TipoA = (Integer) scene.getCustom(contact.getFixtureA().getBody(), "tipo", 0);
	    int TipoB = (Integer) scene.getCustom(contact.getFixtureB().getBody(), "tipo", 0);
	    if(TipoA==4){		
	    	objcontact1=contact.getFixtureB().getBody();
	    	objcontact2 = null;
	    	isPlayer = true;
	    }else if(TipoB==4){
	    	objcontact1=contact.getFixtureA().getBody();	
	    	objcontact2 = null;
	    	isPlayer = true;
	    }else{
	    	objcontact1=contact.getFixtureA().getBody();
	    	objcontact2=contact.getFixtureB().getBody();		
	    }
	    int tipopbj1 = (Integer)scene.getCustom(objcontact1, "tipo", 0);
	    int tipopbj2 = (Integer)scene.getCustom(objcontact2, "tipo", 0);
	    
	    if(isPlayer){
	    	if(tipopbj1==3){
	    		drif = false;
	    	}
	    }
	    if(!isPlayer){
	    	
	    }
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub
		
	}
}
package com.gushikustudios.rube;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.gushikustudios.rube.loader.RubeSceneLoader;
import com.gushikustudios.rube.loader.serializers.utils.RubeImage;

/**
 * Use the left-click to pan. Scroll-wheel zooms.
 * 
 * @author cvayer, tescott
 * 
 */
public class RubeLoaderTest implements ApplicationListener, InputProcessor, ContactListener
{
   private OrthographicCamera camera;
   private OrthographicCamera textCam;
   private RubeSceneLoader loader;
   private RubeScene scene;
   private Box2DDebugRenderer debugRender;

   private Array<SimpleSpatial> spatials; // used for rendering rube images
   private Array<PolySpatial> polySpatials;
   private Map<String, Texture> textureMap;
   private Map<Texture, TextureRegion> textureRegionMap;

   private static final Vector2 mTmp = new Vector2(); // shared by all objects
   private static final Vector2 mTmp3 = new Vector2(); // shared during polygon creation
   private SpriteBatch batch;
   private PolygonSpriteBatch polygonBatch;

   // used for pan and scanning with the mouse.
   private Vector3 mCamPos;
   private Vector3 mCurrentPos;

   private World mWorld;

   private float mAccumulator; // time accumulator to fix the physics step.

   private int mVelocityIter = 8;
   private int mPositionIter = 3;
   private float mSecondsPerStep = 1 / 60f;
   
   private static final float MAX_DELTA_TIME = 0.25f;
   
   private BitmapFont bitmapFont;

   @Override
   public void create()
   {
      float w = Gdx.graphics.getWidth();
      float h = Gdx.graphics.getHeight();
      
      bitmapFont = new BitmapFont(Gdx.files.internal("data/arial-15.fnt"), false);

      Gdx.input.setInputProcessor(this);

      mCamPos = new Vector3();
      mCurrentPos = new Vector3();

      camera = new OrthographicCamera(100, 100 * h / w);
      camera.position.set(50, 50, 0);
      camera.zoom = 1.8f;
      camera.update();
      
      textCam = new OrthographicCamera(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
      textCam.position.set(Gdx.graphics.getWidth()/2,Gdx.graphics.getHeight()/2,0);
      textCam.zoom = 1;
      textCam.update();

      loader = new RubeSceneLoader();

      scene = loader.loadScene(Gdx.files.internal("data/palm.json"));

      debugRender = new Box2DDebugRenderer();

      batch = new SpriteBatch();
      polygonBatch = new PolygonSpriteBatch();

      textureMap = new HashMap<String, Texture>();
      textureRegionMap = new HashMap<Texture, TextureRegion>();

      createSpatialsFromRubeImages(scene);
      createPolySpatialsFromRubeFixtures(scene);

      mWorld = scene.getWorld();
      // configure simulation settings
      mVelocityIter = scene.velocityIterations;
      mPositionIter = scene.positionIterations;
      if (scene.stepsPerSecond != 0)
      {
         mSecondsPerStep = 1f / scene.stepsPerSecond;
      }
      mWorld.setContactListener(this);
      //
      // example of custom property handling
      //
      Array<Body> bodies = scene.getBodies();
      if ((bodies != null) && (bodies.size > 0))
      {
         for (int i = 0; i < bodies.size; i++)
         {
            Body body = bodies.get(i);
            String gameInfo = (String)scene.getCustom(body, "GameInfo", null);
            if (gameInfo != null)
            {
               System.out.println("GameInfo custom property: " + gameInfo);
            }
         }
      }

      // Example of accessing data based on name
      System.out.println("body0 count: " + scene.getNamed(Body.class, "body0").size);
      // Note: the scene has two fixture9 names defined, but these are in turn subdivided into multiple fixtures and thus appear several times...
      System.out.println("fixture9 count: " + scene.getNamed(Fixture.class, "fixture9").size);
      scene.printStats();
      
      // 
      // validate the custom settings attached to world object..
      //
      boolean testBool = (Boolean)scene.getCustom(mWorld, "testCustomBool", false);
      int testInt = (Integer)scene.getCustom(mWorld, "testCustomInt", 0);
      float testFloat = (Float)scene.getCustom(mWorld, "testCustomFloat", 0);
      Color color = (Color)scene.getCustom(mWorld, "testCustomColor", null);
      Vector2 vec = (Vector2)scene.getCustom(mWorld, "testCustomVec2", null);
      String string = (String)scene.getCustom(mWorld, "testCustomString", null);
      
      if (testBool == false)
      {
         throw new GdxRuntimeException("testCustomBool not read correctly! Expected: " + true + " Actual: " + testBool);
      }
      if (testInt != 8675309)
      {
         throw new GdxRuntimeException("testCustomInt not read correctly! Expected: " + 8675309 + " Actual: "  + testInt);
      }
      if (testFloat != 1.25f)
      {
         throw new GdxRuntimeException("testCustomFloat not read correctly! Expected: " + 1.25f + " Actual: " + testFloat);
      }
      if (color == null) 
      {
         throw new GdxRuntimeException("testCustomColor is reporting null!");
      }
      if ((color.r != 17f/255) || (color.g != 29f/255) || (color.b != 43f/255) || (color.a != 61f/255))
      {
         throw new GdxRuntimeException("testCustomColor not read correctly!  Expected: " + new Color(17f/255,29f/255,43f/255,61f/255) + " Actual: " + color);
      }
      if (vec == null)
      {
         throw new GdxRuntimeException("testCustomVec2 is reporting null!");
      }
      if ((vec.x != 314159) || (vec.y != 21718))
      {
         throw new GdxRuntimeException("testCustomVec2 is not read correctly!  Expected: " + new Vector2(314159,21718) + " Actual: " + vec);
      }
      if (string == null)
      {
         throw new GdxRuntimeException("testCustomString is reporting null!");
      }
      if (!string.equalsIgnoreCase("excelsior!"))
      {
         throw new GdxRuntimeException("testCustomString is not read correctly!  Expected: Excelsior! Actual: " + string);
      }
      scene.clear(); // no longer need any scene references
   }

   @Override
   public void dispose()
   {
   }

   @Override
   public void render()
   {
      Gdx.gl.glClearColor(0, 0, 0, 1);
      Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

      float delta = Gdx.graphics.getDeltaTime();
      
      if (delta > MAX_DELTA_TIME)
      {
         delta = MAX_DELTA_TIME;
      }

      mAccumulator += delta;

      while (mAccumulator >= mSecondsPerStep)
      {
         mWorld.step(mSecondsPerStep, mVelocityIter, mPositionIter);
         mAccumulator -= mSecondsPerStep;
      }

      if ((spatials != null) && (spatials.size > 0))
      {
         batch.setProjectionMatrix(camera.combined);
         batch.begin();
         for (int i = 0; i < spatials.size; i++)
         {
            spatials.get(i).render(batch, 0);
         }
         batch.end();
      }

      if ((polySpatials != null) && (polySpatials.size > 0))
      {
         polygonBatch.setProjectionMatrix(camera.combined);
         polygonBatch.begin();
         for (int i = 0; i < polySpatials.size; i++)
         {
            polySpatials.get(i).render(polygonBatch, 0);
         }
         polygonBatch.end();
      }
      
      batch.setProjectionMatrix(textCam.combined);
      batch.begin();
      bitmapFont.draw(batch,"fps: " + Gdx.graphics.getFramesPerSecond(),10,20);
      batch.end();

      debugRender.render(mWorld, camera.combined);
   }

   /**
    * Creates an array of SimpleSpatial objects from RubeImages.
    * 
    * @param scene2
    */
   private void createSpatialsFromRubeImages(RubeScene scene)
   {

      Array<RubeImage> images = scene.getImages();
      if ((images != null) && (images.size > 0))
      {
         spatials = new Array<SimpleSpatial>();
         for (int i = 0; i < images.size; i++)
         {
            RubeImage image = images.get(i);
            mTmp.set(image.width, image.height);
            String textureFileName = "data/" + image.file;
            Texture texture = textureMap.get(textureFileName);
            if (texture == null)
            {
               texture = new Texture(textureFileName);
               textureMap.put(textureFileName, texture);
            }
            SimpleSpatial spatial = new SimpleSpatial(texture, image.flip, image.body, image.color, mTmp, image.center,
                  image.angleInRads * MathUtils.radiansToDegrees);
            spatials.add(spatial);
         }
      }
   }

   /**
    * Creates an array of PolySpatials based on fixture information from the scene. Note that
    * fixtures create aligned textures.
    * 
    * @param scene
    */
   private void createPolySpatialsFromRubeFixtures(RubeScene scene)
   {
      Array<Body> bodies = scene.getBodies();
      
      EarClippingTriangulator ect = new EarClippingTriangulator();

      if ((bodies != null) && (bodies.size > 0))
      {
         polySpatials = new Array<PolySpatial>();
         Vector2 bodyPos = new Vector2();
         // for each body in the scene...
         for (int i = 0; i < bodies.size; i++)
         {
            Body body = bodies.get(i);
            bodyPos.set(body.getPosition());

            Array<Fixture> fixtures = body.getFixtureList();

            if ((fixtures != null) && (fixtures.size > 0))
            {
               // for each fixture on the body...
               for (int j = 0; j < fixtures.size; j++)
               {
                  Fixture fixture = fixtures.get(j);

                  String textureName = (String)scene.getCustom(fixture, "TextureMask", null);
                  if (textureName != null)
                  {
                     String textureFileName = "data/" + textureName;
                     Texture texture = textureMap.get(textureFileName);
                     TextureRegion textureRegion = null;
                     if (texture == null)
                     {
                        texture = new Texture(textureFileName);
                        texture.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
                        textureMap.put(textureFileName, texture);
                        textureRegion = new TextureRegion(texture);
                        textureRegionMap.put(texture, textureRegion);
                     }
                     else
                     {
                        textureRegion = textureRegionMap.get(texture);
                     }

                     // only handle polygons at this point -- no chain, edge, or circle fixtures.
                     if (fixture.getType() == Shape.Type.Polygon)
                     {
                        PolygonShape shape = (PolygonShape) fixture.getShape();
                        int vertexCount = shape.getVertexCount();
                        float[] vertices = new float[vertexCount * 2];

                        // static bodies are texture aligned and do not get drawn based off of the related body.
                        if (body.getType() == BodyType.StaticBody)
                        {
                           for (int k = 0; k < vertexCount; k++)
                           {

                              shape.getVertex(k, mTmp);
                              mTmp.rotate(body.getAngle() * MathUtils.radiansToDegrees);
                              mTmp.add(bodyPos); // convert local coordinates to world coordinates to that textures are
                                                 // aligned
                              vertices[k * 2] = mTmp.x * PolySpatial.PIXELS_PER_METER;
                              vertices[k * 2 + 1] = mTmp.y * PolySpatial.PIXELS_PER_METER;
                           }
                           
                           short [] triangleIndices = ect.computeTriangles(vertices).toArray();
                           PolygonRegion region = new PolygonRegion(textureRegion, vertices, triangleIndices);
                           PolySpatial spatial = new PolySpatial(region, Color.WHITE);
                           polySpatials.add(spatial);
                        }
                        else
                        {
                           // all other fixtures are aligned based on their associated body.
                           for (int k = 0; k < vertexCount; k++)
                           {
                              shape.getVertex(k, mTmp);
                              vertices[k * 2] = mTmp.x * PolySpatial.PIXELS_PER_METER;
                              vertices[k * 2 + 1] = mTmp.y * PolySpatial.PIXELS_PER_METER;
                           }
                           short [] triangleIndices = ect.computeTriangles(vertices).toArray();
                           PolygonRegion region = new PolygonRegion(textureRegion, vertices, triangleIndices);
                           PolySpatial spatial = new PolySpatial(region, body, Color.WHITE);
                           polySpatials.add(spatial);
                        }
                     }
                     else if (fixture.getType() == Shape.Type.Circle)
                     {
                        CircleShape shape = (CircleShape)fixture.getShape();
                        float radius = shape.getRadius();
                        int vertexCount = (int)(12f * radius);
                        float [] vertices = new float[vertexCount*2];
                        System.out.println("SpatialFactory: radius: " + radius);
                        if (body.getType() == BodyType.StaticBody)
                        {
                           mTmp3.set(shape.getPosition());
                           for (int k = 0; k < vertexCount; k++)
                           {
                              // set the initial position
                              mTmp.set(radius,0);
                              // rotate it by 1/vertexCount * k
                              mTmp.rotate(360f*k/vertexCount);
                              // add it to the position.
                              mTmp.rotate(body.getAngle()*MathUtils.radiansToDegrees);
                              mTmp.add(mTmp3);
                              mTmp.add(bodyPos); // convert local coordinates to world coordinates to that textures are aligned
                              vertices[k*2] = mTmp.x*PolySpatial.PIXELS_PER_METER;
                              vertices[k*2+1] = mTmp.y*PolySpatial.PIXELS_PER_METER;
                           }
                           short [] triangleIndices = ect.computeTriangles(vertices).toArray();
                           PolygonRegion region = new PolygonRegion(textureRegion, vertices, triangleIndices);
                           PolySpatial spatial = new PolySpatial(region, Color.WHITE);
                           polySpatials.add(spatial);
                        }
                        else
                        {
                           mTmp3.set(shape.getPosition());
                           for (int k = 0; k < vertexCount; k++)
                           {
                              // set the initial position
                              mTmp.set(radius,0);
                              // rotate it by 1/vertexCount * k
                              mTmp.rotate(360f*k/vertexCount);
                              // add it to the position.
                              mTmp.add(mTmp3);
                              vertices[k*2] = mTmp.x*PolySpatial.PIXELS_PER_METER;
                              vertices[k*2+1] = mTmp.y*PolySpatial.PIXELS_PER_METER;
                           }
                           short [] triangleIndices = ect.computeTriangles(vertices).toArray();
                           PolygonRegion region = new PolygonRegion(textureRegion, vertices, triangleIndices);
                           PolySpatial spatial = new PolySpatial(region, body, Color.WHITE);
                           polySpatials.add(spatial);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @Override
   public void resize(int width, int height)
   {
   }

   @Override
   public void pause()
   {
   }

   @Override
   public void resume()
   {
   }

   @Override
   public boolean keyDown(int keycode)
   {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public boolean keyUp(int keycode)
   {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public boolean keyTyped(char character)
   {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public boolean touchDown(int screenX, int screenY, int pointer, int button)
   {
      mCamPos.set(screenX, screenY, 0);
      camera.unproject(mCamPos);
      return true;
   }

   @Override
   public boolean touchUp(int screenX, int screenY, int pointer, int button)
   {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public boolean touchDragged(int screenX, int screenY, int pointer)
   {
      mCurrentPos.set(screenX, screenY, 0);
      camera.unproject(mCurrentPos);
      camera.position.sub(mCurrentPos.sub(mCamPos));
      camera.update();
      return true;
   }

   @Override
   public boolean mouseMoved(int screenX, int screenY)
   {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public boolean scrolled(int amount)
   {
      camera.zoom += (amount * 0.1f);
      if (camera.zoom < 0.1f)
      {
         camera.zoom = 0.1f;
      }
      camera.update();
      return true;
   }

   @Override
   public void beginContact(Contact contact)
   {
      // TODO Auto-generated method stub
   }

   @Override
   public void endContact(Contact contact)
   {
      // TODO Auto-generated method stub
   }

   @Override
   public void preSolve(Contact contact, Manifold oldManifold)
   {
      // TODO Auto-generated method stub
   }

   @Override
   public void postSolve(Contact contact, ContactImpulse impulse)
   {
      // TODO Auto-generated method stub
   }
}

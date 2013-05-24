package display;


import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.lwjgl.BufferUtils;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import data.Toggle;
import data.TwoDModel;
import data.Location;
import data.Rectangle;
import data.StaticEntity;
import data.Entity;
import data.ResourceLoader;
import data.UIEntity;
import data.World;
import data.WorldCuller;
import framework.Framework;


public class FragChangeColor04 extends LWJGLWindow {


	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
	@Override
	protected void init() {
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glEnable( GL_BLEND );
		rl=new ResourceLoader();
		float[] UNIT_TEXTURE_ARRAY={0.0f,0.0f,1.0f,0.0f,1.0f,1.0f,0f,1.0f};
		FloatBuffer tFlo=BufferUtils.createFloatBuffer(UNIT_TEXTURE_ARRAY.length);
		tFlo.put(UNIT_TEXTURE_ARRAY);
		tFlo.flip();
		rl.addFloatBuffer("ALLFLOAT", tFlo);
		int bo=glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, bo);
		glBufferData(GL_ARRAY_BUFFER, tFlo, GL_STREAM_DRAW);
		rl.addBuffer("ALLBUFFER", bo);
		initializeProgram();
		initializeVertexBuffer(); 
		vao = glGenVertexArrays();
		xlock=new Toggle(Keyboard.KEY_X);
		ylock=new Toggle(Keyboard.KEY_Y);
		//locktogrid=false;
		clicked=false;
		unclicked=true;
		updown=false;
		leftdown=false;
		rightdown=false;
		downdown=false;
		ylocktoggle=false;
		xlocktoggle=false;
		gridXcoord=0;
		gridYcoord=0;//position on the grid
		posx=0;//position of the grid
		posy=0;
		scale=1;
		newscale=1;
		lastscale=1;
		scaleanimationcounter=60;
		System.out.println("blah:"+getInitialHeight());
		wheight=getInitialHeight();
		wwidth=getInitialWidth();		
		glBindVertexArray(vao);
		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);
		glFrontFace(GL_CW);
		aspect=1.0f;
		glUseProgram(theProgram);
		glUniform1f(uniformAspect, aspect);
		glUseProgram(0);
		final float depthZNear = 0.0f;
		final float depthZFar = 1.0f;//i may need these later if i do 3d
		gridXtranslate=500/2;
		gridYtranslate=500/2;
		glEnable(GL_DEPTH_TEST);
		glDepthMask(true);
		glDepthFunc(GL_LEQUAL);
		glDepthRange(depthZNear, depthZFar);
		glEnable(GL_DEPTH_CLAMP);
		sr=new StringRenderer("",0.1f,1);
		sr2=new StringRenderer("",1f,0);
		
		
//experimental entity drawing code
		world=new World(100, 100);
		e=new StaticEntity(new TwoDModel(new Rectangle(.5f, .5f ,"ass"),0,0),4,4);
		masterEntityList=new ArrayList<Entity>();
		masterUIList=new ArrayList<Entity>();

		world.addEntity(e);
		wcull=new WorldCuller(world);
		activeEntity=e;
		float y=-0.6f;
		Entity testui=new UIEntity(new Location(0f,y),new TwoDModel(new Rectangle(1.6f, 0.2f, "background"), 0f, 0f) );
		Entity test1=new UIEntity(new Location(-1.6f/2,y),new TwoDModel(new Rectangle(.2f, 0.2f, "wood"), 0f, 0f) );
		Entity test2=new UIEntity(new Location(-1.2f/2,y),new TwoDModel(new Rectangle(.2f, 0.2f, "stone"), 0f, 0f) );
		Entity test3=new UIEntity(new Location(-0.8f/2,y),new TwoDModel(new Rectangle(.2f, 0.2f, "snow"), 0f, 0f) );
		Entity test4=new UIEntity(new Location(-0.4f/2,y),new TwoDModel(new Rectangle(.2f, 0.2f, "vine"), 0f, 0f) );
		Entity test5=new UIEntity(new Location(0f,y),new TwoDModel(new Rectangle(.2f, 0.2f, "sand"), 0f, 0f) );
		Entity test6=new UIEntity(new Location(0.4f/2,y),new TwoDModel(new Rectangle(.2f, 0.2f, "dirt"), 0f, 0f) );
		Entity test7=new UIEntity(new Location(0.8f/2,y),new TwoDModel(new Rectangle(.2f, 0.2f, "rose"), 0f, 0f) );
		Entity test8=new UIEntity(new Location(1.2f/2,y),new TwoDModel(new Rectangle(.2f, 0.2f, "grass_side"), 0f, 0f) );
		Entity test9=new UIEntity(new Location(1.6f/2,y),new TwoDModel(new Rectangle(.2f, 0.2f, "hellrock"), 0f, 0f) );

		masterUIList.add(testui);
		masterUIList.add(test1);
		masterUIList.add(test2);
		masterUIList.add(test3);
		masterUIList.add(test4);
		masterUIList.add(test5);
		masterUIList.add(test6);
		masterUIList.add(test7);
		masterUIList.add(test8);
		masterUIList.add(test9);
	}
	@Override
	protected void initialwh() {
		wheight=getInitialHeight();
		wwidth=getInitialWidth();
	}

	@Override
	protected void display() {	
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		glClear(GL_COLOR_BUFFER_BIT);
		rl.async.executeQueuedJobs();
		//System.out.println("aspect:"+aspect);
		sr.render("X:"+finalgridx+" Y:"+finalgridy, -0.91f, 0.91f);
		sr.render("XL:"+xlocktoggle+"   YL:"+ylocktoggle,-0.91f,0.80f);
		System.out.println("gridYcoord:"+gridYcoord);
		glUseProgram(theProgram);
		glUniform1f(elapsedTimeUniform, getElapsedTime() / 1000.0f);
		glBindBuffer(GL_ARRAY_BUFFER, positionBufferObject);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glVertexAttribPointer(0, 4, GL_FLOAT, false, 0, 0);
		glVertexAttribPointer(1, 4, GL_FLOAT, false, 0, 16000);//1000vertexes*16//where the color data starts
		glDrawArrays(GL_LINES, 0, 1000);
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glUseProgram(0);
		for(int i=0;i<masterEntityList.size();i++){
			//System.out.println("index is:"+i);
			masterEntityList.get(i).draw();
		}
		for(int i=0;i<masterUIList.size();i++){
			//System.out.println("index is:"+i);
			masterUIList.get(i).draw();
		}
		
	}

	@Override
	protected void reshape(int width, int height) {
		aspect = 1.0f / ((float)width / (float) height);
		System.out.println("aspect:"+aspect);
		glUseProgram(theProgram);
		glUniform1f(uniformAspect, aspect);
		glUseProgram(0);
		glViewport(0, 0, width, height);
		gridXtranslate=width/2;
		gridYtranslate=height/2;
		wwidth=width;
		wheight=height;
	}


	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

	@Override
	protected void update(){
		//need a general event queue
		xlock.toggleUpdate();
		ylock.toggleUpdate();
		while (Keyboard.next()) {
			if (Keyboard.getEventKey() == Keyboard.KEY_ESCAPE) {
				leaveMainLoop();
			}else if(Keyboard.getEventKey() == Keyboard.KEY_F11){
				fullscreentoggle=true;
			}
		    if (Keyboard.getEventKeyState()) {
		        if (Keyboard.getEventKey() == Keyboard.KEY_UP) {
		        	//System.out.println("UP Key Pressed");
		        	updown=true;
		        }
		    }
		    else {
		        if (Keyboard.getEventKey() == Keyboard.KEY_UP) {
		        	//System.out.println("UP Key Released");
		        	updown=false;
		        }
		    }
		    if (Keyboard.getEventKeyState()) {
		        if (Keyboard.getEventKey() == Keyboard.KEY_RIGHT) {
		        	//System.out.println("Right Key Pressed");
		        	rightdown=true;
		        }
		    }
		    else {
		        if (Keyboard.getEventKey() == Keyboard.KEY_RIGHT) {
		        	//System.out.println("Right Key Released");
		        	rightdown=false;
		        }
		    }
		    if (Keyboard.getEventKeyState()) {
		        if (Keyboard.getEventKey() == Keyboard.KEY_LEFT) {
		        	//System.out.println("left Key Pressed");
		        	leftdown=true;
		        }
		    }
		    else {
		        if (Keyboard.getEventKey() == Keyboard.KEY_LEFT) {
		        	//System.out.println("left Key Released");
		        	leftdown=false;
		        }
		    }
		    if (Keyboard.getEventKeyState()) {
		        if (Keyboard.getEventKey() == Keyboard.KEY_DOWN) {
		        	//System.out.println("down Key Pressed");
		        	downdown=true;
		        }
		    }
		    else {
		        if (Keyboard.getEventKey() == Keyboard.KEY_DOWN) {
		        	//System.out.println("down Key Released");
		        	downdown=false;
		        }
		    }
		}
		while(Mouse.next()){
			if(Mouse.getEventDWheel()>0){
				newscale=lastscale*2;
				lastscale=newscale;
				System.out.println("greater than 0");
				scaleanimationcounter=0;
				scalediff=newscale-scale;
			}else if (Mouse.getEventDWheel()<0){
				newscale=lastscale/2;
				lastscale=newscale;
				System.out.println("greater than 0");
				scaleanimationcounter=0;
				scalediff=newscale-scale;
			}
	
			gridXcoord=Mouse.getX();
			gridYcoord=Mouse.getY();
			gridXcoord-=gridXtranslate;
			gridYcoord-=gridYtranslate;
		}
		
		if(clicked==false&&unclicked){
			if(Mouse.isButtonDown(0)){
				clicked=true;
				unclicked=false;
			}
		}else if(!unclicked){
			clicked=false;
			if(!Mouse.isButtonDown(0)){
				unclicked=true;
			}
		}
		if(scaleanimationcounter<60&&scale!=newscale){
			scale+=scalediff/60f;
			scaleanimationcounter++;
		}else{
			scale=newscale;
		}
		if(updown){
			posy-=.02f/scale;
		}
		if(rightdown){
			posx-=.02f/scale;
		}
		if(leftdown){
			posx+=.02f/scale;
		}
		if(downdown){
			posy+=.02f/scale;
		}
	    glUseProgram(theProgram);
	    glUniform1f(uniformScale,scale);
	    glUniform1f(uniformXoffset, posx);
	    glUniform1f(uniformYoffset, posy);
	    glUseProgram(0);
	  //  System.out.println("clicked:"+clicked);
	   // System.out.println("unclicked:"+unclicked);
	    gridXfake=(((gridXcoord)/(wwidth/2)/scale/aspect)-posx/aspect);//recently refactored the x, too lazy to update the y
	    //gridYfake=(((gridYcoord)/(wheight/2))-posy*scale)/scale;
	    gridYfake=(2*gridYcoord)/(scale*wheight)-posy;
	    ///////////////////////now i'm doing world cull and stuff like that
	    if(ylock.isOn()){
	    	ylocktoggle=!ylocktoggle;
	    }
	    if(xlock.isOn()){
	    	xlocktoggle=!xlocktoggle;
	    }
	    if(ylocktoggle&&xlocktoggle){
	    	finalgridx=Math.round(gridXfake);
	    	finalgridy=Math.round(gridYfake);
	    	float q=(wwidth*scale)/(2*aspect);
	    	Mouse.setCursorPosition((int)(finalgridx*q+gridXtranslate+posx*q),(int)(gridYtranslate+finalgridy*wheight*scale/2));
	    }else if(ylocktoggle){
	    	finalgridy=gridYfake;
	    	finalgridy=Math.round(gridYfake);
	    	Mouse.setCursorPosition(Mouse.getX(),(int)(gridYtranslate+finalgridy*wheight*scale/2));
	    }else if(xlocktoggle){
	    	finalgridx=Math.round(gridXfake);
	    	finalgridy=gridYfake;
	    	float q=(wwidth*scale)/(2*aspect);
	    	Mouse.setCursorPosition((int)(finalgridx*q+gridXtranslate+posx*q),Mouse.getY());
	    }else{
	    	finalgridx=gridXfake;
	    	finalgridy=gridYfake;
	    }
	    	
	    if(clicked){
	    	world.addEntity(new StaticEntity(((StaticEntity)activeEntity).getModel(), gridXfake,gridYfake));
	    	wcull.updateEntity();
	    	/*try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
	    }
	    wcull.update();
	  //  Mouse.setGrabbed(true);
	};
	private void initializeProgram() {			
		//my code for making the grid// should probably be a resource but i'm just hardcoding it in
		vertexPositions=new float[8000];
		for(float i=0;i<2000;i+=8){
			vertexPositions[(int) i]=i/8;
			vertexPositions[(int) i+1]=250;
			vertexPositions[(int) i+2]=0;
			vertexPositions[(int) i+3]=1;
			vertexPositions[(int) i+4]=i/8;
			vertexPositions[(int) i+5]=0;
			vertexPositions[(int) i+6]=0;
			vertexPositions[(int) i+7]=1;
		}
		for(float i=0;i<2000;i+=8){
			vertexPositions[(int) i+2000]=0;
			vertexPositions[(int) i+1+2000]=i/8;
			vertexPositions[(int) i+2+2000]=0;
			vertexPositions[(int) i+3+2000]=1;
			vertexPositions[(int) i+4+2000]=250;
			vertexPositions[(int) i+5+2000]=i/8;
			vertexPositions[(int) i+6+2000]=0;
			vertexPositions[(int) i+7+2000]=1;
		}
		//colors
		for(float i=0;i<2000;i+=8){
			vertexPositions[(int) i+0+4000]=1;
			vertexPositions[(int) i+1+4000]=0;
			vertexPositions[(int) i+2+4000]=0;
			vertexPositions[(int) i+3+4000]=1;
			vertexPositions[(int) i+4+4000]=1;
			vertexPositions[(int) i+5+4000]=1;
			vertexPositions[(int) i+6+4000]=1;
			vertexPositions[(int) i+7+4000]=1;
		}
		for(float i=0;i<2000;i+=8){
			vertexPositions[(int) i+0+6000]=0;
			vertexPositions[(int) i+1+6000]=1;
			vertexPositions[(int) i+2+6000]=0;
			vertexPositions[(int) i+3+6000]=1;
			vertexPositions[(int) i+4+6000]=1;
			vertexPositions[(int) i+5+6000]=1;
			vertexPositions[(int) i+6+6000]=1;
			vertexPositions[(int) i+7+6000]=1;
		}

	
		ArrayList<Integer> shaderList = new ArrayList<>();
		ArrayList<Integer> textshaderList = new ArrayList<>();
		shaderList.add(Framework.loadShader(GL_VERTEX_SHADER, 	"CalcOffset.vert"));
		shaderList.add(Framework.loadShader(GL_FRAGMENT_SHADER, "CalcColor.frag"));
		textshaderList.add(Framework.loadShader(GL_VERTEX_SHADER, 	"Text.vert"));
		textshaderList.add(Framework.loadShader(GL_FRAGMENT_SHADER, "Text.frag"));
		theProgram = Framework.createProgram(shaderList);

		elapsedTimeUniform = glGetUniformLocation(theProgram, "time");
		uniformAspect=glGetUniformLocation(theProgram,"aspect");
		uniformScale=glGetUniformLocation(theProgram,"scaler");
	    uniformXoffset = glGetUniformLocation(theProgram, "xOffset");
	    uniformYoffset = glGetUniformLocation(theProgram, "yOffset");
	    glUseProgram(theProgram);
	    glUniform1f(uniformXoffset, 0.0f);
	    glUniform1f(uniformYoffset, 0.0f);
	    glUseProgram(0);
	}

	private void initializeVertexBuffer() {
		FloatBuffer vertexPositionsBuffer = BufferUtils.createFloatBuffer(vertexPositions.length);
		vertexPositionsBuffer.put(vertexPositions);
		vertexPositionsBuffer.flip();

        positionBufferObject = glGenBuffers();	       
		glBindBuffer(GL_ARRAY_BUFFER, positionBufferObject);
	    glBufferData(GL_ARRAY_BUFFER, vertexPositionsBuffer, GL_STREAM_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}

	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
	private float wheight,wwidth;
	private float gridXfake,gridYfake=0;//these are the coords we print out which are really different from world coords
	private float gridXtranslate,gridYtranslate;
	private float gridXcoord,gridYcoord;
	String fontPath;
	public static List <Entity>masterEntityList;
	public static List <Entity>masterUIList;
	private World world;
	private WorldCuller wcull;
	private Entity activeEntity;
	private boolean clicked,unclicked;
	//private static boolean locktogrid;
	//public AsyncExecution async;
	//public TextureManager tm;
	private Toggle xlock,ylock;
	private float finalgridx,finalgridy;
	public static float aspect;
	private StringRenderer sr,sr2;
	private float vertexPositions[];
	public static float posx,posy,scale,scalediff;
	private float lastscale,newscale,scaleanimationcounter;
	boolean ispos=false;
	private int uniformAspect;
	private int uniformScale;
	private int uniformXoffset;
	private int uniformYoffset;
	private int positionBufferObject;
	private boolean updown,leftdown,rightdown,downdown,xlocktoggle,ylocktoggle;
	private int theProgram;
	private int elapsedTimeUniform;
	private int vao;
	private Entity e,e2,e3;
	public static ResourceLoader rl;
}
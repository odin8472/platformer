package display;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STREAM_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import org.lwjgl.BufferUtils;

import data.Entity;
import data.StaticRenderer;
import framework.Framework;

public class StringRenderer implements StaticRenderer{
	public StringRenderer(){
		this("Hello World", 0.1f,0);
	}
	public StringRenderer(String t,float size,int orig){//xy coordinate plane with -1 to +1 x and y
		text=t;
		origin=orig;
		this.size=size;
		initializeVertexBuffer();
		glUseProgram(textProgram);
		glUniform1f(uniformAspect, 1.0f);
		
		glUseProgram(0);
	}
	public void render(){
		this.render(text, -1.0f, 1.0f);
	}
	public void render(String t,float x,float y){
		if(t==text&&isFormatted&&x==xcoord&&y==ycoord){
			draw();
			return;
		}
		if(t!=text){
			isFormatted=false;
		}
		if(!isFormatted){
			//format text
			text=t;
			xcoord=x;
			ycoord=y;
		    glUseProgram(textProgram);
			glUniform1f(xOffsetUniform, xcoord);
			glUniform1f(yOffsetUniform, ycoord);
			glUniform1f(uniformAspect,1.0f);
		    glUseProgram(0);

			isFormatted=true;
		}
		draw();
	}
	public void draw(float x, float y){
		render(text,x,y);
	}
	public void draw(Entity e){
		draw(e.getLocation().getX(),e.getLocation().getY());
	}
	private void draw(){
		//glUseProgram(FragChangeColor04.rl.retrieveProgram("rect"));
		//glUniform1f(uniformAspect, FragChangeColor04.aspect);
		//glUseProgram(0);
		glUseProgram(textProgram);
		glUniform1f(uniformAspect, FragChangeColor04.aspect);
		glUniform1f(uniformScale, 1.0f);
		FragChangeColor04.rl.retrieveTexture("H");
		//async.executeQueuedJobs();//this goes in the update code or somewhere equivalent and it's for the texture loading
		if(xFlip==1f){
			tempoffset=0;
			for(tempoffset=0;tempoffset<text.length();tempoffset++){
				incrementCurser();
				changeLetter(text.charAt((int) tempoffset));
				glEnableVertexAttribArray(0);
				glBindBuffer(GL_ARRAY_BUFFER, positionBufferObject);
				glVertexAttribPointer(0, 4, GL_FLOAT, false, 0, 0);//just drawing a quad
				glBindBuffer(GL_ARRAY_BUFFER, texBufferObject);
				glEnableVertexAttribArray(1);
				glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);//defining texcoords
				//gonna need to change attribute data or more likely just include it all and choose which one like an array
				glDrawArrays(GL_QUADS, 0, 4);
			}

		}else if(xFlip==-1f){
			tempoffset=0;
			for(tempoffset=0;tempoffset<text.length();tempoffset++){
				incrementCurser();
				changeLetter(text.charAt((int) (text.length()-1-tempoffset)));
				glEnableVertexAttribArray(0);
				glBindBuffer(GL_ARRAY_BUFFER, positionBufferObject);
				glVertexAttribPointer(0, 4, GL_FLOAT, false, 0, 0);//just drawing a quad
				glBindBuffer(GL_ARRAY_BUFFER, texBufferObject);
				glEnableVertexAttribArray(1);
				glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);//defining texcoords
				//gonna need to change attribute data or more likely just include it all and choose which one like an array
				glDrawArrays(GL_QUADS, 0, 4);
			}
		}else{
			System.out.println("this should not happen");
		}
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glUseProgram(0);
	}
	private void incrementCurser(){
		float originXoffset=0.5f*size*originArray[origin*2];
		float originYoffset=0.5f*size*originArray[origin*2+1];
		//System.out.println("xFlip:"+xFlip);
		
		quadArray[0]=-0.5f*size+xcoord*size+(float)tempoffset*size*xFlip+originXoffset;
		quadArray[1]=0.5f*size+ycoord*size+originYoffset;
		quadArray[2]=0;
		quadArray[3]=1;
		
		quadArray[4]=0.5f*size+xcoord*size+(float)tempoffset*size*xFlip+originXoffset;
		quadArray[5]=0.5f*size+ycoord*size+originYoffset;
		quadArray[6]=0;
		quadArray[7]=1;
		
		quadArray[8]= 0.5f*size+xcoord*size+(float)tempoffset*size*xFlip+originXoffset;
		quadArray[9]=-0.5f*size+ycoord*size+originYoffset;
		quadArray[10]=0;
		quadArray[11]=1;
		
		quadArray[12]=-0.5f*size+xcoord*size+(float)tempoffset*size*xFlip+originXoffset;
		quadArray[13]=-0.5f*size+ycoord*size+originYoffset;
		quadArray[14]=0;
		quadArray[15]=1;
		
		vertexPositionsBuffer.clear();
		vertexPositionsBuffer.put(quadArray);
		vertexPositionsBuffer.flip();
		
		glBindBuffer(GL_ARRAY_BUFFER, positionBufferObject);
	    glBufferData(GL_ARRAY_BUFFER, vertexPositionsBuffer, GL_STREAM_DRAW);
	}
	private void changeLetter(char letter){
		int ascii=(int)letter;
		int row=(int)(ascii/16);
		int col=ascii%16;

		texArray[0]=0+(float)col/16;
		texArray[1]=0+(float)row/16;
		
		texArray[2]=1f/16f+(float)col/16;
		texArray[3]=0+(float)row/16;
		
		texArray[4]=1f/16f+(float)col/16;
		texArray[5]=1f/16f+(float)row/16;
		
		texArray[6]=0+(float)col/16;
		texArray[7]=1f/16f+(float)row/16;
		texturePositionsBuffer.clear();
		texturePositionsBuffer.put(texArray);
		texturePositionsBuffer.flip();
		glBindBuffer(GL_ARRAY_BUFFER, texBufferObject);
		glBufferData(GL_ARRAY_BUFFER, texturePositionsBuffer, GL_STREAM_DRAW);
	}
	private void initializeVertexBuffer() {
		quadArray=new float[16];
		
		quadArray[0]=-0.5f;
		quadArray[1]=0.5f;
		quadArray[2]=0;
		quadArray[3]=1;
		
		quadArray[4]=0.5f;
		quadArray[5]=0.5f;
		quadArray[6]=0;
		quadArray[7]=1;
		
		quadArray[8]=0.5f;
		quadArray[9]=-0.5f;
		quadArray[10]=0;
		quadArray[11]=1;
		
		quadArray[12]=-0.5f;
		quadArray[13]=-0.5f;
		quadArray[14]=0;
		quadArray[15]=1;
		
		vertexPositionsBuffer = BufferUtils.createFloatBuffer(quadArray.length);
		vertexPositionsBuffer.put(quadArray);
		vertexPositionsBuffer.flip();
		
		texArray=new float[8];
		texArray[0]=0;
		texArray[1]=0;
		texArray[2]=1f/16f;
		texArray[3]=0;
		texArray[4]=1f/16f;
		texArray[5]=1f/16f;
		texArray[6]=0;
		texArray[7]=1f/16f;
		texturePositionsBuffer = BufferUtils.createFloatBuffer(texArray.length);
		texturePositionsBuffer.put(texArray);
		texturePositionsBuffer.flip();
		
		positionBufferObject =glGenBuffers();
        texBufferObject=glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, positionBufferObject);
	    glBufferData(GL_ARRAY_BUFFER, vertexPositionsBuffer, GL_STREAM_DRAW);
		//glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindBuffer(GL_ARRAY_BUFFER, texBufferObject);
	    glBufferData(GL_ARRAY_BUFFER, texturePositionsBuffer, GL_STREAM_DRAW);
		//glBindBuffer(GL_ARRAY_BUFFER, 0);

		//may not quite be necessary, i may only need the int textprogram from the other file
		ArrayList<Integer> textshaderList = new ArrayList<>();
		textshaderList.add(Framework.loadShader(GL_VERTEX_SHADER, 	"Text.vert"));
		textshaderList.add(Framework.loadShader(GL_FRAGMENT_SHADER, "Text.frag"));
		textProgram = Framework.createProgram(textshaderList);
	    glUseProgram(textProgram);
	    uniformAspect=glGetUniformLocation(textProgram,"aspect");
		uniformScale=glGetUniformLocation(textProgram,"scale");
	    xOffsetUniform = glGetUniformLocation(textProgram, "xOffset");
	    yOffsetUniform = glGetUniformLocation(textProgram, "yOffset");
	    if(origin==2||origin==3){
			xFlip=-1.0f;
	    }else{
	    	xFlip=1.0f;
	    }
		glUseProgram(0);
	}

	public enum TextOrigin {CENTER, UPPERLEFT, UPPERRIGHT, LOWERRIGHT, LOWERLEFT};
	private boolean isFormatted=false;
	private float tempoffset;
	private int origin;
	private FloatBuffer vertexPositionsBuffer; 
	private FloatBuffer texturePositionsBuffer;
	//public static AsyncExecution async;
	//public static TextureManager tm;
	
	private int xOffsetUniform;
	private int yOffsetUniform;
	private int uniformAspect;
	private int uniformScale;
	private float xFlip;
	private int textProgram;
	private int positionBufferObject;
	private int texBufferObject;
	private String text;
	private float size;
	private float xcoord;
	private float ycoord;
	private float quadArray[];
	private float texArray[];
	private final float []originArray={0.0f,0.0f,1.0f,-1.0f,-1.0f,-1.0f,-1.0f,1.0f,1.0f,1.0f};//convoluted way of offsetting it from the center so i can have the origin at the upper left
}

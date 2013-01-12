package display;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
//import static org.lwjgl.opengl.GL32.*;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;

import framework.Framework;


public class FragChangeColor04 extends LWJGLWindow {





	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

	protected void init() {
		initializeProgram();
		initializeVertexBuffer(); 

		vao = glGenVertexArrays();
		glBindVertexArray(vao);
	}


	@Override
	protected void display() {	
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		glClear(GL_COLOR_BUFFER_BIT);

		glUseProgram(theProgram);

		glUniform1f(elapsedTimeUniform, getElapsedTime() / 1000.0f);

		glBindBuffer(GL_ARRAY_BUFFER, positionBufferObject);
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(0, 4, GL_FLOAT, false, 0, 0);

		glDrawArrays(GL_TRIANGLES, 0, 3);

		glDisableVertexAttribArray(0);
		glUseProgram(0);
	}



	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

	private int theProgram;
	private int elapsedTimeUniform;
	private int vao;


	private void initializeProgram() {			
		ArrayList<Integer> shaderList = new ArrayList<>();
		shaderList.add(Framework.loadShader(GL_VERTEX_SHADER, 	"CalcOffset.vert"));
		shaderList.add(Framework.loadShader(GL_FRAGMENT_SHADER, "CalcColor.frag"));

		theProgram = Framework.createProgram(shaderList);

		elapsedTimeUniform = glGetUniformLocation(theProgram, "time");

	    int uniformLoopDuration = glGetUniformLocation(theProgram, "loopDuration");
		int fragLoopDurUnf = glGetUniformLocation(theProgram, "fragLoopDuration");

	    glUseProgram(theProgram);
	    glUniform1f(uniformLoopDuration, 5.0f);
	    glUniform1f(fragLoopDurUnf, 10.0f);
	    glUseProgram(0);
	}



	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

	private final float vertexPositions[] = {
			 0.25f,  0.25f, 0.0f, 1.0f,
			 0.25f, -0.25f, 0.0f, 1.0f,
			-0.25f, -0.25f, 0.0f, 1.0f};

	private int positionBufferObject;


	private void initializeVertexBuffer() {
		FloatBuffer vertexPositionsBuffer = BufferUtils.createFloatBuffer(vertexPositions.length);
		vertexPositionsBuffer.put(vertexPositions);
		vertexPositionsBuffer.flip();

        positionBufferObject = glGenBuffers();	       
		glBindBuffer(GL_ARRAY_BUFFER, positionBufferObject);
	    glBufferData(GL_ARRAY_BUFFER, vertexPositionsBuffer, GL_STREAM_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}
}
package com.sunteorum.pinktoru.view;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class LinkGLSurfaceView extends GLSurfaceView {
	Context mContext;
	
	SceneRenderer mRenderer;
	
	public LinkGLSurfaceView(Context context) {
		super(context);
		mContext = context;
		mRenderer = new SceneRenderer();
		
		setRenderer(mRenderer);
		setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
	}
	
	private class SceneRenderer implements GLSurfaceView.Renderer { 
	
		public void onDrawFrame(GL10 gl) {
			gl.glShadeModel(GL10.GL_SMOOTH);
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT|GL10.GL_DEPTH_BUFFER_BIT);
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glTranslatef(0f, 0f, -10f);
			
	    	
		}
	    
		public void onSurfaceChanged(GL10 gl, int width, int height) {
			
			gl.glViewport(0, 0, width, height);
			gl.glMatrixMode(GL10.GL_PROJECTION);
			gl.glLoadIdentity();
	       	
	    }
	
		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			gl.glDisable(GL10.GL_DITHER);
			gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT,GL10.GL_FASTEST);
			gl.glClearColor(0,0,0,0);
			gl.glShadeModel(GL10.GL_SMOOTH);
			gl.glEnable(GL10.GL_DEPTH_TEST);
			
			gl.glEnable(GL10.GL_BLEND);  
			gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);  
			gl.glEnable(GL10.GL_ALPHA_TEST);
			gl.glAlphaFunc(GL10.GL_GREATER,0.1f);
			
		
		}
	            
	}
	

}

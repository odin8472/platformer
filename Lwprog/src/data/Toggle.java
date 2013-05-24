package data;

import org.lwjgl.input.Keyboard;


public class Toggle {
	public Toggle(int key){
		this.key=key;
		isOn=false;
		unhit=true;
	}
	public boolean isOn(){
		return isOn;
	}
	public void toggleUpdate(){
		if(isOn==false&&unhit){
			if(Keyboard.isKeyDown(key)){
				isOn=true;
				unhit=false;
			}
		}else if(!unhit){
			isOn=false;
			if(!Keyboard.isKeyDown(key)){
				unhit=true;
			}
		}
	}
	
	private boolean isOn,unhit;;
	private int key;
}

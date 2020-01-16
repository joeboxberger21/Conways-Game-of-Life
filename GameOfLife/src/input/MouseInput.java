package input;

import org.lwjgl.glfw.GLFWCursorPosCallback;

public class MouseInput extends GLFWCursorPosCallback{
    public static double x, y;
    @Override
        public void invoke(long window, double xpos, double ypos) {
            //System.out.println("X: " + xpos + "\nY: " + ypos);
            this.x = xpos;
            this.y = -(ypos - 900);
    }
}

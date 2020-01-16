import input.KeyboardInput;
import input.MouseButtons;
import input.MouseInput;
import org.lwjgl.Version;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.ArrayList;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class LWJGL_GameOfLife {

    // The window handle
    private long window;
    private GLFWCursorPosCallback cursorCallback;
    private GLFWMouseButtonCallback buttonCallback;
    private GLFWKeyCallback keyCallback;

    boolean mouse3Clicked = false;
    boolean isPlaying = false;
    float initX;
//    ArrayList<Cell> willDie = new ArrayList<Cell>();
//    ArrayList<Cell> willSpawn = new ArrayList<Cell>();
    ArrayList<Integer> willDie = new ArrayList<Integer>();
    ArrayList<Integer> willSpawn = new ArrayList<Integer>();

    double lastUpdate = 0;


    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        // Create the window
        window = glfwCreateWindow(900, 900, "Hello World!", NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
        });

        // Get the thread stack and push a new frame
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);

        glfwSetCursorPosCallback(window, cursorCallback = new MouseInput());
        glfwSetMouseButtonCallback(window, buttonCallback = new MouseButtons());
        glfwSetKeyCallback(window, keyCallback = new KeyboardInput());
    }

    private void loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0, 900, 0, 900, 1, -1);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.

        ArrayList<Cell> cellGrid = new ArrayList<Cell>();
        int gridW = 30;
        int gridH = 30;
        int yInc = 28;
        int xIndex, yIndex = 0;
        while (yInc < gridH * 28 + 25) {
            int xInc = 28;
            xIndex = 0;
            while (xInc < gridW * 28 + 25) {
                int[] tempIndex = {xIndex, yIndex};
                Cell cell = new Cell(xInc, yInc, 25, 25, tempIndex);
                cellGrid.add(cell);
                xInc += 28;
                xIndex++;
            }
            yInc += 28;
            yIndex++;
        }

        while ( !glfwWindowShouldClose(window) ) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            for (int i = 0; i < cellGrid.size(); i++) {
                if (cellGrid.get(i).inBounds(MouseInput.x, MouseInput.y) && MouseButtons.action == 1) {
                    if (MouseButtons.button == 0) {
                        cellGrid.get(i).alive = true;
                    }
                    if (MouseButtons.button == 1) {
                        cellGrid.get(i).alive = false;
                    }
                }
                cellGrid.get(i).renderSquare();
            }

            //TODO Drag Grid
            if (MouseButtons.button == 2 && MouseButtons.action == 1) {
                for (int i = 0; i < cellGrid.size(); i++) {
                    cellGrid.get(i).x = (float)MouseInput.x;
                }
            }
//            else if (MouseButtons.button == 2 && MouseButtons.action == 0) {
//                mouse3Clicked = false;
//            }
            //TODO Zoom Grid

            //TODO Start Game
            if (KeyboardInput.key == GLFW_KEY_ENTER && KeyboardInput.action == 1 && !isPlaying) {
                System.out.println("PLAY!");
                isPlaying = true;
            }
            if (KeyboardInput.key == GLFW_KEY_SPACE && KeyboardInput.action == 1 && isPlaying) {
                System.out.println("PAUSE!");
                isPlaying = false;
            }

            //Update in Loop
            if (isPlaying && glfwGetTime() - lastUpdate >= 0.2) {
                lastUpdate = glfwGetTime();
                for (int i = 0; i < cellGrid.size(); i++) {
                    int aliveNeigbors = 0;
                    try {
                        if (cellGrid.get(i + 1).alive) {
                            aliveNeigbors++;
                        }
                    } catch(java.lang.IndexOutOfBoundsException ex) {

                    }
                    try {
                        if (cellGrid.get(i - 1).alive) {
                            aliveNeigbors++;
                        }
                    } catch(java.lang.IndexOutOfBoundsException ex) {

                    }
                    try {
                        if (cellGrid.get(i + gridW).alive) {
                            aliveNeigbors++;
                        }
                    } catch(java.lang.IndexOutOfBoundsException ex) {

                    }
                    try {
                        if (cellGrid.get(i - gridW).alive) {
                            aliveNeigbors++;
                        }
                    } catch(java.lang.IndexOutOfBoundsException ex) {

                    }
                    try {
                        if (cellGrid.get(i + gridW - 1).alive) {
                            aliveNeigbors++;
                        }
                    } catch(java.lang.IndexOutOfBoundsException ex) {

                    }
                    try {
                        if (cellGrid.get(i + gridW + 1).alive) {
                            aliveNeigbors++;
                        }
                    } catch(java.lang.IndexOutOfBoundsException ex) {

                    }
                    try {
                        if (cellGrid.get(i - gridW - 1).alive) {
                            aliveNeigbors++;
                        }
                    } catch(java.lang.IndexOutOfBoundsException ex) {

                    }
                    try {
                        if (cellGrid.get(i - gridW + 1).alive) {
                            aliveNeigbors++;
                        }
                    } catch(java.lang.IndexOutOfBoundsException ex) {

                    }
                    if (cellGrid.get(i).alive) {
                        if (aliveNeigbors > 3 && !willDie.contains(i)) {
                            willDie.add(i);
                        }
                        if (aliveNeigbors < 2 && !willDie.contains(i)) {
                            willDie.add(i);
                        }
                    }
                    if (!cellGrid.get(i).alive && aliveNeigbors == 3 && !willSpawn.contains(i)) {
                        willSpawn.add(i);
                    }
                }
                try {
                    for (int i = 0; i < willDie.size(); i++) {
                        cellGrid.get((willDie.get(i))).alive = false;
                    }
                    willDie.clear();
                    for (int i = 0; i < willSpawn.size(); i++) {
                        cellGrid.get((willSpawn.get(i))).alive = true;
                    }
                    willSpawn.clear();
                } catch (java.lang.IndexOutOfBoundsException ex) {

                }
            }
            glfwSwapBuffers(window); // swap the color buffers
            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }
    }

    private static class Cell {

        public float x,y,w,h;
        public int[] index;
        float red;
        float green;
        float blue;
        boolean alive;

        Cell (float x, float y, float w, float h, int[] index){
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
            this.index = index;
            this.alive = false;
        }

        void renderSquare() {
            if (alive) {
                red = 1.0f;
                green = 1.0f;
                blue = 1.0f;
            }
            if (!alive) {
                red = 0.2f;
                green = 0.2f;
                blue = 0.2f;
            }
//            System.out.println(alive);
            glColor3f(red, green, blue);
            glBegin(GL_QUADS);
            glVertex2f(x, y);
            glVertex2f(x + w, y);
            glVertex2f(x + w, y + h);
            glVertex2f(x, y + h);
            glEnd();
        }

        boolean inBounds(double mouseX, double mouseY) {
            red = 0.75f;
            green = 0.75f;
            blue = 0.75f;
            return mouseX > x && mouseX < x + w && mouseY > y && mouseY < y + h;
        }

//        void update (Cell neigbor1, Cell neigbor2, Cell neigbor3, Cell neigbor4, Cell neigbor5, Cell neigbor6, Cell neigbor7, Cell neigbor8) {
//            int livingNeigbors;
//        }

    }

    public static void main(String[] args) {
        new LWJGL_GameOfLife().run();
    }

}
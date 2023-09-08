package edu.curtin.saed.assignment1;

import javafx.scene.canvas.*;
import javafx.geometry.VPos;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A JavaFX GUI element that displays a grid on which you can draw images, text and lines.
 */
public class JFXArena extends Pane
{
    
    private static final String CITADEL_RES = "rg1024-isometric-tower.png";
    private Image citadel;

    // Represents an image to draw, retrieved as a project resource.
    private static final String IMAGE_FILE = "1554047213.png";
    private Image robot1;
    private Image robot;
    private String robotId = "";
    private int delay;
    private int robotType;

    private static final String ROBOT_2_RES = "droid2.png";
    private Image robot2;

    private static final String ROBOT_3_RES = "rg1024-robot-carrying-things-4.png";
    private Image robot3;

    private static final String WALL_RES = "181478.png";
    private Image wall;

    private static final String DESTRO_WALL_RSE = "181479.png";
    private Image destroWall;
    
    // The following values are arbitrary, and you may need to modify them according to the 
    // requirements of your application.
    private int gridWidth = 9;
    private int gridHeight = 9;
    private double robotX = 0.0;
    private double robotY = 0.0;

    private double citadelx = 0.0;
    private double citadely = 0.0;

    private double wallx = 0.0;
    private double wally = 0.0;

    private double gridSquareSize; // Auto-calculated
    private Canvas canvas; // Used to provide a 'drawing surface'.

    private List<ArenaListener> listeners = null;
    private Map<String, String> images = new ConcurrentHashMap<>();
    private Object mutex = new Object();
    
    /**
     * Creates a new arena object, loading the robot image and initialising a drawing surface.
     */
    public JFXArena()
    {
        // Here's how (in JavaFX) you get an Image object from an image file that's part of the 
        // project's "resources". If you need multiple different images, you can modify this code 
        // accordingly.
        
        // (NOTE: _DO NOT_ use ordinary file-reading operations here, and in particular do not try
        // to specify the file's path/location. That will ruin things if you try to create a 
        // distributable version of your code with './gradlew build'. The approach below is how a 
        // project is supposed to read its own internal resources, and should work both for 
        // './gradlew run' and './gradlew build'.)

        try(InputStream is = getClass().getClassLoader().getResourceAsStream(CITADEL_RES))
        {
            if(is == null)
            {
                throw new AssertionError("Cannot find image file " + IMAGE_FILE);
            }
            citadel = new Image(is);
        }
        catch(IOException e)
        {
            throw new AssertionError("Cannot load image file " + IMAGE_FILE, e);
        }

        try(InputStream is = getClass().getClassLoader().getResourceAsStream(IMAGE_FILE))
        {
            if(is == null)
            {
                throw new AssertionError("Cannot find image file " + IMAGE_FILE);
            }
            robot1 = new Image(is);
        }
        catch(IOException e)
        {
            throw new AssertionError("Cannot load image file " + IMAGE_FILE, e);
        }

        try(InputStream is = getClass().getClassLoader().getResourceAsStream(ROBOT_2_RES))
        {
            if(is == null)
            {
                throw new AssertionError("Cannot find image file " + ROBOT_2_RES);
            }
            robot2 = new Image(is);
        }
        catch(IOException e)
        {
            throw new AssertionError("Cannot load image file " + ROBOT_2_RES, e);
        }

        try(InputStream is = getClass().getClassLoader().getResourceAsStream(ROBOT_3_RES))
        {
            if(is == null)
            {
                throw new AssertionError("Cannot find image file " + ROBOT_3_RES);
            }
            robot3 = new Image(is);
        }
        catch(IOException e)
        {
            throw new AssertionError("Cannot load image file " + ROBOT_3_RES, e);
        }

        try(InputStream is = getClass().getClassLoader().getResourceAsStream(WALL_RES))
        {
            if(is == null)
            {
                throw new AssertionError("Cannot find image file " + IMAGE_FILE);
            }
            wall = new Image(is);
        }
        catch(IOException e)
        {
            throw new AssertionError("Cannot load image file " + IMAGE_FILE, e);
        }

        try(InputStream is = getClass().getClassLoader().getResourceAsStream(DESTRO_WALL_RSE))
        {
            if(is == null)
            {
                throw new AssertionError("Cannot find image file " + IMAGE_FILE);
            }
            destroWall = new Image(is);
        }
        catch(IOException e)
        {
            throw new AssertionError("Cannot load image file " + IMAGE_FILE, e);
        }

        
        canvas = new Canvas();
        canvas.widthProperty().bind(widthProperty());
        canvas.heightProperty().bind(heightProperty());
        getChildren().add(canvas);
    }

    public void setCitadelLocation() {
        citadelx = gridWidth / 2;
        citadely = gridHeight / 2;
        images.put(getKey(citadelx, citadely), "citadel");
    }
    
    /**
     * Moves a robot image to a new grid position. This is highly rudimentary, as you will need
     * many different robots in practice. This method currently just serves as a demonstration.
     */
    public void setRobotPosition(double x, double y)
    {
        //if (robotX > 0.0 || robotX < (double)gridWidth - 1.0 && robotY < (double)gridHeight - 1.0 || robotY > 0.0 ) {
            
            double oldX = robotX;
            double oldY = robotY;
            robotX = x;
            robotY = y;
            String key = getKey(x, y);
            images.put(key, robotId);
            requestLayout();
            String oldKey = getKey(oldX, oldY);
            images.remove(oldKey);
        //}
    }

    public void addKey(String robotId, double x, double y, int type) {
        String key = getKey(x, y);
        images.put(key, type + "," + robotId);
    }

    public void removeKey(double x, double y) {
        String oldKey = getKey(x, y);
        images.remove(oldKey);
    }

    public void occupySquare(double x, double y) {
        String key = getKey(x, y);
        images.put(key, "null");
    }

    public void removeSquare(double x, double y) {
        String oldKey = getKey(x, y);
        images.remove(oldKey);
    }

    public double getGridHeight() {
        return (double) gridHeight;
    }

    public double getGridWidth() {
        return (double) gridWidth;
    }

    public Map<String, String> getMap() {
        return images;
    }

    public double getCitadelX() {
        return citadelx;
    }

    public double getCitadelY() {
        return citadely;
    }

    public void setRobotX(double robotX) {
        this.robotX = robotX;
    }

    public void setRobotY(double robotY) {
        this.robotY = robotY;
    }

    public void addNewRobot(double x, double y, String id, int delayTime, int typeId)
    {
        robotX = x;
        robotY = y;
        robotId = id;
        robotType = typeId;
        delay = delayTime;
        String key = getKey(x, y);
        images.put(key, robotType + "," + robotId);
        requestLayout();
    }

    public double[] getSpawnCoordinates() {
        double[] topLeft = {0.0, 0.0};
        double[] bottomLeft = {0.0, (double) gridHeight - 1.0};
        double[] topRight  = {(double) gridWidth - 1.0, 0.0};
        double[] bottomRight = {(double) gridWidth - 1.0, (double) gridHeight - 1.0};
    
        if (!containsImage(topLeft[0], topLeft[1])) {
            //System.out.println("Spawning " + topLeft[0] + ", " + topLeft[1]);
            return topLeft;
        }
        if (!containsImage(bottomLeft[0], bottomLeft[1])) {
            //System.out.println(bottomLeft[0] + ", " + bottomLeft[1]);
            return bottomLeft;
        }
        if (!containsImage(topRight[0], topRight[1])) {
            //System.out.println(topRight[0] + ", " + topRight[1]);
            return topRight;
        }
        if (!containsImage(bottomRight[0], bottomRight[1])) {
            //System.out.println(bottomRight[0] + ", " + bottomRight[1]);
            return bottomRight;
        }
        
        return null;
    }
    

    public void addNewWall(double x, double y)
    {
        wallx = x;
        wally = y;
        String key = getKey(x, y);
        images.put(key, "wall");
        requestLayout();
    }

    public void destroyWall(double x, double y) {
        String key = getKey(x, y);
        images.remove(key);
        images.put(key, "destrowall");
        requestLayout();
    }

    public boolean containsImage(double x, double y) {
        String key = getKey(x, y);
        return images.containsKey(key);
    }

    public boolean containsRobot(double x, double y) {
        String key = getKey(x, y);
        String value = images.get(key);

        if (value != null) {
            if (value.contains("Robot") || value.equals("null")) {
                return true;
            }
        }
        return false;
    }

    public boolean containsWall(double x, double y) {
        String key = getKey(x, y);
        String value = images.get(key);
        
        return value != null && value.equals("wall");
    }

    public boolean containsDestroWall(double x, double y) {
        String key = getKey(x, y);
        String value = images.get(key);
        
        return value != null && value.equals("destrowall");
    }

    public boolean containsCitadel(double x, double y) {
        String key = getKey(x, y);
        String value = images.get(key);
        
        return value != null && value.equals("citadel");
    }

    public String getKey(double x, double y) {
        return String.valueOf(x) + "," + String.valueOf(y);
    }
    
    /**
     * Adds a callback for when the user clicks on a grid square within the arena. The callback 
     * (of type ArenaListener) receives the grid (x,y) coordinates as parameters to the 
     * 'squareClicked()' method.
     */
    public void addListener(ArenaListener newListener)
    {
        if(listeners == null)
        {
            listeners = new LinkedList<>();
            setOnMouseClicked(event ->
            {
                int gridX = (int)(event.getX() / gridSquareSize);
                int gridY = (int)(event.getY() / gridSquareSize);
                
                if(gridX < gridWidth && gridY < gridHeight)
                {
                    for(ArenaListener listener : listeners)
                    {   
                        listener.squareClicked(gridX, gridY);
                    }
                }
            });
        }
        listeners.add(newListener);
    }
        
        
    /**
     * This method is called in order to redraw the screen, either because the user is manipulating 
     * the window, OR because you've called 'requestLayout()'.
     *
     * You will need to modify the last part of this method; specifically the sequence of calls to
     * the other 'draw...()' methods. You shouldn't need to modify anything else about it.
     */
    @Override
    public void layoutChildren()
    {
        super.layoutChildren(); 
        GraphicsContext gfx = canvas.getGraphicsContext2D();
        gfx.clearRect(0.0, 0.0, canvas.getWidth(), canvas.getHeight());
        
        // First, calculate how big each grid cell should be, in pixels. (We do need to do this
        // every time we repaint the arena, because the size can change.)
        gridSquareSize = Math.min(
            getWidth() / (double) gridWidth,
            getHeight() / (double) gridHeight);
            
        double arenaPixelWidth = gridWidth * gridSquareSize;
        double arenaPixelHeight = gridHeight * gridSquareSize;
            
            
        // Draw the arena grid lines. This may help for debugging purposes, and just generally
        // to see what's going on.
        gfx.setStroke(Color.DARKGREY);
        gfx.strokeRect(0.0, 0.0, arenaPixelWidth - 1.0, arenaPixelHeight - 1.0); // Outer edge

        for(int gridX = 1; gridX < gridWidth; gridX++) // Internal vertical grid lines
        {
            double x = (double) gridX * gridSquareSize;
            gfx.strokeLine(x, 0.0, x, arenaPixelHeight);
        }
        
        for(int gridY = 1; gridY < gridHeight; gridY++) // Internal horizontal grid lines
        {
            double y = (double) gridY * gridSquareSize;
            gfx.strokeLine(0.0, y, arenaPixelWidth, y);
        }

        // Invoke helper methods to draw things at the current location.
        // ** You will need to adapt this to the requirements of your application. **

        for (Map.Entry<String, String> sprite : images.entrySet()) {
            String coordinates = sprite.getKey();
            String value = sprite.getValue();
            
            // Split the coordinates into x and y
            String[] coordinatesArray = coordinates.split(",");
            double x = Double.parseDouble(coordinatesArray[0]);
            double y = Double.parseDouble(coordinatesArray[1]);
            
            // Check the value to determine what to draw
            if (value.contains("Robot")) {
                String[] parts = value.split(",");
                int type = Integer.parseInt(parts[0]);
                String robot_id = parts[1];

                // Draw the robot image and label
                switch (type) {
                    case 1:
                        drawImage(gfx, robot1, x, y);
                        drawLabel(gfx, robot_id, x, y);
                        break;
                    case 2:
                        drawImage(gfx, robot2, x, y);
                        drawLabel(gfx, robot_id, x, y);
                        break;
                    case 3:
                        drawImage(gfx, robot3, x, y);
                        drawLabel(gfx, robot_id, x, y);
                        break;
                    default:
                        break;
                }
                
            } else if (value.equals("wall")) {
                // Draw the wall image
                drawImage(gfx, wall, x, y);
            }else if (value.equals("destrowall")) {
                // Draw the destroyed wall image
                drawImage(gfx, destroWall, x, y);
            } else if (value.equals("citadel")) {
                // Draw the citadel image and label
                drawImage(gfx, citadel, x, y);
                drawLabel(gfx, "Citadel", x, y);
            }
        }
    }
    
    
    /** 
     * Draw an image in a specific grid location. *Only* call this from within layoutChildren(). 
     *
     * Note that the grid location can be fractional, so that (for instance), you can draw an image 
     * at location (3.5,4), and it will appear on the boundary between grid cells (3,4) and (4,4).
     *     
     * You shouldn't need to modify this method.
     */
    private void drawImage(GraphicsContext gfx, Image image, double gridX, double gridY)
    {
        // Get the pixel coordinates representing the centre of where the image is to be drawn. 
        double x = (gridX + 0.5) * gridSquareSize;
        double y = (gridY + 0.5) * gridSquareSize;
        
        // We also need to know how "big" to make the image. The image file has a natural width 
        // and height, but that's not necessarily the size we want to draw it on the screen. We 
        // do, however, want to preserve its aspect ratio.
        double fullSizePixelWidth = citadel.getWidth();
        double fullSizePixelHeight = citadel.getHeight();
        
        double displayedPixelWidth, displayedPixelHeight;
        if(fullSizePixelWidth > fullSizePixelHeight)
        {
            // Here, the image is wider than it is high, so we'll display it such that it's as 
            // wide as a full grid cell, and the height will be set to preserve the aspect 
            // ratio.
            displayedPixelWidth = gridSquareSize;
            displayedPixelHeight = gridSquareSize * fullSizePixelHeight / fullSizePixelWidth;
        }
        else
        {
            // Otherwise, it's the other way around -- full height, and width is set to 
            // preserve the aspect ratio.
            displayedPixelHeight = gridSquareSize;
            displayedPixelWidth = gridSquareSize * fullSizePixelWidth / fullSizePixelHeight;
        }

        // Actually put the image on the screen.
        gfx.drawImage(image,
            x - displayedPixelWidth / 2.0,  // Top-left pixel coordinates.
            y - displayedPixelHeight / 2.0, 
            displayedPixelWidth,              // Size of displayed image.
            displayedPixelHeight);
    }
    
    
    /**
     * Displays a string of text underneath a specific grid location. *Only* call this from within 
     * layoutChildren(). 
     *     
     * You shouldn't need to modify this method.
     */
    private void drawLabel(GraphicsContext gfx, String label, double gridX, double gridY)
    {
        gfx.setTextAlign(TextAlignment.CENTER);
        gfx.setTextBaseline(VPos.TOP);
        gfx.setStroke(Color.BLUE);
        gfx.strokeText(label, (gridX + 0.5) * gridSquareSize, (gridY + 1.0) * gridSquareSize);
    }
    
    /** 
     * Draws a (slightly clipped) line between two grid coordinates.
     *     
     * You shouldn't need to modify this method.
     */
    private void drawLine(GraphicsContext gfx, double gridX1, double gridY1, 
                                               double gridX2, double gridY2)
    {
        gfx.setStroke(Color.RED);
        
        // Recalculate the starting coordinate to be one unit closer to the destination, so that it
        // doesn't overlap with any image appearing in the starting grid cell.
        final double radius = 0.5;
        double angle = Math.atan2(gridY2 - gridY1, gridX2 - gridX1);
        double clippedGridX1 = gridX1 + Math.cos(angle) * radius;
        double clippedGridY1 = gridY1 + Math.sin(angle) * radius;
        
        gfx.strokeLine((clippedGridX1 + 0.5) * gridSquareSize, 
                       (clippedGridY1 + 0.5) * gridSquareSize, 
                       (gridX2 + 0.5) * gridSquareSize, 
                       (gridY2 + 0.5) * gridSquareSize);
    }
}

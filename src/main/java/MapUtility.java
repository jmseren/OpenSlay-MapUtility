import processing.core.PApplet;

import processing.core.*;

import java.io.File;
import java.io.PrintWriter;
import java.util.*;
public class MapUtility extends PApplet {
    // Global variables
    public static EventHandler eventHandler;
    public HashMap<String, GUI> guiElements;
    public programState state;
    public static HashMap<String, PImage> textures;
    public static PFont font;
    public static int hexSize = 0;
    public static int mapWidth = 0;
    public static int mapHeight = 0;
    public static GUI focused = null;
    public static HexMap map;
    public static Hex recent;
    public static int toolCode = 1;

    public File mapFile;
    public String fileName;

    public static Pos playAreaOffset = new Pos(100, 100);


    public static void main(String[] args){
        PApplet.main("MapUtility");
    }

    public void settings(){
        size(1280,720);
        noSmooth();    
    }

    public void setup(){
        frameRate(60);
        imageMode(CENTER);
        textAlign(CENTER, CENTER);
        font = createFont("fonts/pixeloidsans.ttf", 32);
        textFont(font, 32);
        
        eventHandler = new EventHandler();
        guiElements = new HashMap<String, GUI>();
        textures = new HashMap<String, PImage>();
        
        importTexture("background", "textures/bg.png", 32);
        changeState(programState.MENU);


    }

    public void draw(){
        background(0, 0, 255);
        drawBackground();
        switch(state){
            case EDIT:
                edit();
                break;
        }
        drawGUI();
        processEvents();
    }

    // Program states
    public void edit(){
        drawMap(map);
        drawToolBar();
    }

    // User Input
    public void mousePressed(){
        for(GUI gui : guiElements.values()){
            if(gui.click(mouseX, mouseY)){
                eventHandler.pushEvent(gui.onClick());
                return;
            }
        }

        switch(state){
            case EDIT:
                Hex h = getClosestHex();
                if(h == null) return;
                recent = h;
                h.setCode(toolCode);
                break;
        }
    }
    public void mouseDragged(){
        switch(state){
            case EDIT:
                Hex h = getClosestHex();
                if(h == null) return;
                if(h != recent){
                    h.setCode(toolCode);
                    recent = h;
                }
        }
    }
    public void keyPressed(){
        switch(state){
            case SETTINGS_HEXSIZE:
                if(focused != null && keyCode > 47 && keyCode < 58){
                    ((TextInput)focused).addChar(key);
                }else if(keyCode == BACKSPACE){
                    ((TextInput)focused).removeChar();
                }else if(keyCode == ENTER){
                    hexSize = Integer.parseInt(((TextInput)focused).text);
                    changeState(programState.SETTINGS_MAPWIDTH);
                }
                break;
            case SETTINGS_MAPWIDTH:
                if(focused != null && keyCode > 47 && keyCode < 58){
                    ((TextInput)focused).addChar(key);
                }else if(keyCode == BACKSPACE){
                    ((TextInput)focused).removeChar();
                }else if(keyCode == ENTER){
                    mapWidth = Integer.parseInt(((TextInput)focused).text);
                    changeState(programState.SETTINGS_MAPHEIGHT);
                }
                break;
            case SETTINGS_MAPHEIGHT:
                if(focused != null && keyCode > 47 && keyCode < 58){
                    ((TextInput)focused).addChar(key);
                }else if(keyCode == BACKSPACE){
                    ((TextInput)focused).removeChar();
                }else if(keyCode == ENTER){
                    mapHeight = Integer.parseInt(((TextInput)focused).text);
                    changeState(programState.EDIT);
                }
                break;
            case EDIT:
                switch(keyCode){
                    case LEFT:
                        playAreaOffset.x -= 5;
                        break;
                    case RIGHT:
                        playAreaOffset.x += 5;
                        break;
                    case UP:
                        playAreaOffset.y -= 5;
                        break;
                    case DOWN: 
                        playAreaOffset.y += 5;
                        break;
                }
                if(key == ' '){
                    toolCode = (toolCode + 1) % 4;
                }
                break;
            case SAVE_MAP:
                String k = String.valueOf(key);
                if(focused != null && k.matches("[a-zA-Z0-9]")){
                    ((TextInput)focused).addChar(key);

                }else if(keyCode == BACKSPACE){
                    ((TextInput)focused).removeChar();
                }
                if(keyCode == ENTER){
                    focused = null;
                    fileName = ((TextInput)focused).text;
                    changeState(programState.SAVE);
                }
                break;
        }
    }


    public Hex getClosestHex(){
        Hex closestHex = map.getHex(0,0);
        Pos p = closestHex.rawPos(playAreaOffset.x, playAreaOffset.y);
        float distance = dist(mouseX, mouseY, p.x, p.y);
        for(int x = 0; x < map.width; x++){
            for(int y = 0; y < map.height; y++){
                p = map.getHex(x,y).rawPos(playAreaOffset.x, playAreaOffset.y);
                float d = dist(mouseX, mouseY, p.x, p.y);
                if(d < distance){
                    distance = d;
                    closestHex = map.getHex(x, y);
                }
            }
        }
        if(distance > hexSize){
            return null;
        }
        return closestHex;
    }

    //GUI
    
    public void drawBackground(){
        imageMode(CORNER);
        int size = textures.get("background").width;
        for(int x = 0; x < width; x += size){
            for(int y = 0; y < height; y += size){
                image(textures.get("background"), x, y);
            }
        }
        imageMode(CENTER);
    }

    public void drawGUI(){
        for(GUI gui : guiElements.values()){
            gui.draw(this);
        }
    }

    public void drawMap(HexMap map){
        for(int x = 0; x < map.width; x++){
            for(int y = 0; y < map.height; y++){
                Hex hex = map.hexes[x][y];
                drawHex(hex);
            }
        }

    }
    public void drawHex(Hex hex){
        float h = (float)(Math.sqrt(3) * hexSize);
        int x = (int)(hex.x * (hexSize*2 * 0.75));
        int y = (int)((float)hex.y * h);
        Color fillColor;
        if(hex.x % 2 == 1){
            y += h / 2.0;
        }
        fillColor = hex.filled ? hex.color : new Color(255, 255, 255, 100);

        fill(fillColor.toProcessingColor());
        polygon(x + playAreaOffset.x, y + playAreaOffset.y, hexSize, 6, false);

        switch(hex.code){
            case 2:
                image(textures.get("pine"), x + playAreaOffset.x, y + playAreaOffset.y);
                break;
            case 3:
                image(textures.get("palm"), x + playAreaOffset.x, y + playAreaOffset.y);
                break;   
        }
    }

    public void drawToolBar(){
        fill(161, 161, 161);
        rect(width-(width * .25f), 0, width * 0.25f, height);
        fill(0, 0, 0);
        int offset = height/10;
        text("Map Editor", width-(width * .25f) + (width * 0.25f / 2), height / 10);
        text("Hex Size: " + hexSize, width-(width * .25f) + (width * 0.25f / 2), height / 10 + offset);
        text("Map Width: " + mapWidth, width-(width * .25f) + (width * 0.25f / 2), height / 10 + offset * 2);
        text("Map Height: " + mapHeight, width-(width * .25f) + (width * 0.25f / 2), height / 10 + offset * 3);
        text("X Offset: " + playAreaOffset.x, width-(width * .25f) + (width * 0.25f / 2), height / 10 + offset * 4);
        text("Y Offset: " + playAreaOffset.y, width-(width * .25f) + (width * 0.25f / 2), height / 10 + offset * 5);
        text("Tool:", width-(width * .25f) + (width * 0.25f / 2), height / 10 + offset * 6);

        Color fillColor = new Color(255, 255, 255);
        if(toolCode == 0){
            fillColor = new Color(0, 100, 247);
        }
        fill(fillColor.toProcessingColor());
        polygon((int)(width-(width * .25f) + (width * 0.25f / 2)), height / 10 + offset * 7, 32, 6, false);
        if(toolCode == 2){
            image(textures.get("pine") , (int)(width-(width * .25f) + (width * 0.25f / 2)), height / 10 + offset * 7);
        }else if(toolCode == 3){
            image(textures.get("palm"), (int)(width-(width * .25f) + (width * 0.25f / 2)), height / 10 + offset * 7);
        }
        
    }


    // This method is adapted from the Processing Documentation
    // https://processing.org/examples/regularpolygon.html
    public void polygon(int x, int y, int radius, int npoints, boolean highlight){ 
        float angle = TWO_PI / npoints;

        PShape s = createShape();
        if(highlight) s.setStroke(255);        
        
        s.beginShape();
        if(highlight) s.strokeWeight(3);
        for (float a = 0; a < TWO_PI; a += angle) {
            float sx = x + cos(a) * radius;
            float sy = y + sin(a) * radius;
            s.vertex(sx, sy);
        }
        s.endShape(CLOSE);
        shape(s, 0,0);
    }

    //Events
    public void processEvents(){
        while(eventHandler.queueSize() > 0){
            Event e = eventHandler.nextEvent();
            switch(e.getEventType()){
                case NO_EVENT:
                    break;
                case CHANGE_STATE:
                    changeState((programState)e.getEventData());
                    break;
                case FOCUS:
                    focused = (GUI)e.getEventData();
                    break;
            }
        }
    }

    // File input callback
    public void fileLoaded(File file){
        mapFile = file;
        changeState(programState.LOAD);
    }

    // Texture loading
    public void loadTextures(){
        importTexture("pine", "textures/pine.png", (int)(hexSize * 0.85));
        importTexture("palm", "textures/palm.png", (int)(hexSize * 0.85));
    }
    public void importTexture(String name, String path, int size){
        PImage img = loadImage(path);
        img.resize(size, size);
        textures.put(name, img);
    }

    public void changeState(programState newState){
        
        state = initState(newState) ? newState : state;
    }
    // Initialize state GUI elements

    //Returns whether or not to set the new state
    public boolean initState(programState state){
        guiElements.clear();
        switch(state){
            case MENU:
                GUI menu = new TextElement("OpenSlay Mapping Utility", width/2, height/4);
                GUI newButton = new TextButton("New Map", width/2, height/2, width/4, height/10, new Event(Events.CHANGE_STATE, programState.SETTINGS_HEXSIZE));
                GUI loadButton = new TextButton("Load Map", width/2, height/2 + (height/10)*2, width/4, height/10, new Event(Events.CHANGE_STATE, programState.LOAD_MAP));
                guiElements.put("menu", menu);
                guiElements.put("newButton", newButton);
                guiElements.put("loadButton", loadButton);
                break;
            case SETTINGS_HEXSIZE:
                GUI title = new TextElement("Hex Size (8-128)", width/2, height/4);
                GUI input = new TextInput(width/2, height/2, 3, 5, height/10);
                guiElements.put("title", title);
                guiElements.put("input", input);
                break;
            case SETTINGS_MAPWIDTH:
                title = new TextElement("Map Width", width/2, height/4);
                input = new TextInput(width/2, height/2, 2, 5, height/10);
                guiElements.put("title", title);
                guiElements.put("input", input);
                break;
            case SETTINGS_MAPHEIGHT:
                title = new TextElement("Map Height", width/2, height/4);
                input = new TextInput(width/2, height/2, 2, 5, height/10);
                guiElements.put("title", title);
                guiElements.put("input", input);
                break;
            case EDIT:
                loadTextures();
                if(map == null) map = new HexMap(mapWidth, mapHeight);
                GUI save = new TextButton("Save", (int)(width-(width * .25f) + (width * 0.25f / 2)), height / 10 + (height/10) * 8, width/6, height/10, new Event(Events.CHANGE_STATE, programState.SAVE_MAP));
                guiElements.put("save", save);
                break;
            case SAVE_MAP:
                title = new TextElement("Map Name", width/2, height/4);
                input = new TextInput(width/2, height/2, 20, 25, height/10);
                if(fileName != "" && fileName != null) ((TextInput)input).text = fileName;
                guiElements.put("title", title);
                guiElements.put("input", input);
                break;
            case SAVE:
                boolean success = saveMap(fileName);
                if(success){
                    title = new TextElement("Map Saved", width/2, height/4);
                }else{
                    title = new TextElement("Map Save Failed", width/2, height/4);
                }
                GUI back = new TextButton("Back", (int)(width-(width * .25f) + (width * 0.25f / 2)), height / 10 + (height/10) * 8, width/6, height/10, new Event(Events.CHANGE_STATE, programState.EDIT));
                guiElements.put("title", title);
                guiElements.put("back", back);
                break;
            case LOAD_MAP:
                selectInput("Select Map File", "fileLoaded");
                changeState(programState.LOADING);
                break;
            case LOAD:
                success = false;
                if(!(mapFile == null)){
                    success = loadMap();
                }   
                if(success){
                    changeState(programState.EDIT);
                    return false;
                }else{
                    title = new TextElement("Map Load Failed", width/2, height/4);
                    back = new TextButton("Back", width/2, height / 2, width/6, height/10, new Event(Events.CHANGE_STATE, programState.MENU));
                }
                guiElements.put("title", title);
                guiElements.put("back", back);
                break;
        }
        return true;
    }

    // File IO
    public boolean saveMap(String name){
        boolean success = false;
        try{
            PrintWriter writer = new PrintWriter(name + ".slay", "UTF-8");
            writer.println("[OPENSLAY]");
            writer.println("[SETTINGS]");
            writer.println("3");
            writer.println(mapWidth + " " + mapHeight);
            writer.println(hexSize);
            writer.println(playAreaOffset.x + " " + playAreaOffset.y);
            writer.println("[MAP]");
            for(int i = 0; i < mapHeight; i++){
                for(int j = 0; j < mapWidth; j++){
                    writer.print(map.getHex(j, i).code + (j == mapWidth - 1 ? "" : " "));
                }
                writer.println();
            }
            writer.println("[END]");
            writer.close();
            success = true;
        }catch(Exception e){
            System.out.println("Error saving map");
        }
        return success;
    }

    public boolean loadMap(){
        try{
            Scanner lineScanner = new Scanner(mapFile);

            // Load default number of players
            while(lineScanner.hasNextLine()){
                String line = lineScanner.nextLine();
                String[] lineSplit = line.split(" ");
                if(line.charAt(0) == '[') continue; // This line is the header or a comment.
                if(line.charAt(0) == '#') continue; // This line is a comment.
                if(lineSplit.length != 1) throw new Exception("Invalid map file: player line is invalid.");
                int playerCount = Integer.parseInt(lineSplit[0]);
                break;
            }

            // Load dimensions of map
            while(lineScanner.hasNextLine()){
                String line = lineScanner.nextLine();
                String[] lineSplit = line.split(" ");
                if(line.charAt(0) == '[') continue; // This line is the header or a comment.
                if(line.charAt(0) == '#') continue; // This line is a comment.
                if(lineSplit.length != 2) throw new Exception("Invalid map file: dimension line is invalid.");
                mapWidth = Integer.parseInt(lineSplit[0]);
                mapHeight = Integer.parseInt(lineSplit[1]);
                map = new HexMap(mapWidth, mapHeight);   
                break;
            }
            if(map == null) throw new Exception("Invalid map file: size line not found.");

            // Load hex size
            while(lineScanner.hasNextLine()){
                String line = lineScanner.nextLine();
                String[] lineSplit = line.split(" ");
                if(line.charAt(0) == '[') continue; // This line is the header or a comment.
                if(line.charAt(0) == '#') continue; // This line is a comment.
                if(lineSplit.length != 1) throw new Exception("Invalid map file: hex size line is invalid.");
                hexSize = Integer.parseInt(lineSplit[0]);
                break;
            }

            // Load offset values
            while(lineScanner.hasNextLine()){
                String line = lineScanner.nextLine();
                String[] lineSplit = line.split(" ");
                if(line.charAt(0) == '[') continue; // This line is the header or a comment.
                if(line.charAt(0) == '#') continue; // This line is a comment.
                if(lineSplit.length != 2) throw new Exception("Invalid map file: offset line is invalid.");
                playAreaOffset.x = Integer.parseInt(lineSplit[0]);
                playAreaOffset.y = Integer.parseInt(lineSplit[1]);
                break;
            }

            // Load tile data from map file
            int x = 0;
            int y = 0;
            while(lineScanner.hasNextLine()){
                String line = lineScanner.nextLine();
                String[] lineSplit = line.split(" ");
                if(line.charAt(0) == '[') continue; // This line is the header or a comment.
                if(line.charAt(0) == '#') continue; // This line is a comment.
                if(lineSplit.length != map.width) throw new Exception("Invalid map file: line length does not equal map width.");
                for(String s : lineSplit){
                    map.hexes[x][y] = new Hex(x, y, Integer.parseInt(s));
                    x++;
                }
                x = 0;
                y++;
            }
            lineScanner.close();
            fileName = mapFile.getName().substring(0, mapFile.getName().length() - 5);
            return true;
        }catch(Exception e){
            System.out.println("Error loading map: " + e.getMessage());
            return false;
        }
    }

    
    // ENUM for program state
    enum programState {
        MENU,
        SETTINGS_HEXSIZE,
        SETTINGS_MAPWIDTH,
        SETTINGS_MAPHEIGHT,
        MAP,
        LOAD_MAP,
        LOADING,
        LOAD,
        EDIT,
        SAVE_MAP,
        SAVE,
    }
}

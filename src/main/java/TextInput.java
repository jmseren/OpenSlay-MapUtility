import processing.core.*;

public class TextInput extends GUI {
    public String text = "";
    public int size;
    public boolean focused = false;
    public Color focusedColor = new Color(255, 255, 255);
    public Color unfocusedColor = new Color(100, 100, 100);
    public TextInput(int x, int y, int maxSize, int charWidth, int height) {
        super("textinput", null, x, y, charWidth*50, height);
        size = maxSize;
    }

    public boolean click(int x, int y){
        if( x >= this.x - this.width/2 && x <= this.x + this.width/2 && y >= this.y - this.height/2 && y <= this.y + this.height/2){
            focused = true;
            return true;
        }
        MapUtility.eventHandler.pushEvent(new Event(Events.FOCUS, null));
        focused = false;
        return false;
    }
    public Event onClick(){
        return new Event(Events.FOCUS, this);
    }
    public boolean addChar(char c){
        if(text.length() < size){
            text += c;
            return true;
        }
        return false;
    }
    public void removeChar(){
        if(text.length() > 0){
            text = text.substring(0, text.length()-1);
        }
    }
    public void draw(MapUtility os) {
        os.rectMode(PConstants.CENTER);
        os.fill(focused ? focusedColor.toProcessingColor() : unfocusedColor.toProcessingColor());
        os.rect(x, y, width, height);
        os.fill(0,0,0);
        os.text(text, x, y);
        os.rectMode(PConstants.CORNER);
    }
}

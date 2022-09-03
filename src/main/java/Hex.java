public class Hex {
    public int x;
    public int y;
    public Color color;


    boolean filled;
    int code;

    public boolean castle = false;

    // Capital hex variables
    public boolean capital = false;
    public int gold = 0;

    public Hex(int x, int y, int mapCode){
        this.x = x;
        this.y = y;
        this.filled = mapCode > 0;
        this.code = mapCode;
        this.color = new Color(255, 255, 255);
    }

    public Pos getPos(){
        return new Pos(this.x, this.y);
    }
    public void setCode(int code){
        this.code = code;
        this.filled = code > 0;
    }
    public boolean isEmpty(){
        // Returns whether or not the hex has a unit or tree on it
        return this.code == 1 && !this.capital;
    }

    // The pixel position of the hex on the window
    public Pos rawPos(int xOff, int yOff){
        float h = (float)(Math.sqrt(3) * MapUtility.hexSize);
        int x = (int)(this.x * (MapUtility.hexSize * 2 * 0.75));
        int y = (int)((float)this.y * h);
        if(this.x % 2 == 1){
            y += h / 2.0;
        }
        return new Pos(xOff + x, yOff + y);
    }

}
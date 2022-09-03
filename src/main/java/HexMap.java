import java.util.*;

// HexMap is a class used to store the hex tiles for the game.
// The grid is stored in the odd-q offset format.

// A lot of the information on hex grids was gathered from
// https://redblobgames.com/grids/hexagons/

public class HexMap {

    // Lookup table for hex neighbors
    final Pos[][] hexDirections = {
        // Even Columns
        { 
            new Pos(1, 0),
            new Pos(1, -1),
            new Pos(0, -1),
            new Pos(-1, -1),
            new Pos(-1, 0),
            new Pos(0, 1)
        },
        // Odd columns
        {
            new Pos(1, 1),
            new Pos(1, 0),
            new Pos(0, -1),
            new Pos(-1, 0),
            new Pos(-1, 1),
            new Pos(0, 1)
        }
    };

    public int width;
    public int height;
    public Hex[][] hexes;

    public HexMap(int width, int height) {
        this.width = width;
        this.height = height;
        this.hexes = new Hex[width][height];
        for(int x = 0; x < width; x++){
            for(int y = 0; y < height; y++){
                this.hexes[x][y] = new Hex(x, y, 0);
            }
        }
    }


    public Hex getHex(Pos pos){
        return this.hexes[pos.x][pos.y];
    }
    public Hex getHex(int x, int y){
        return this.hexes[x][y];
    }

    // Use a lookup table to get the neighbors of the requested hex
    public Hex[] getNeighbors(Hex hex) {
        ArrayList<Hex> neighbors = new ArrayList<Hex>();
        for(Pos direction : hexDirections[hex.x % 2]){
            Pos nPos = hex.getPos().add(direction);
            if(inBounds(nPos)) neighbors.add(getHex(nPos));
        }
        return neighbors.toArray(new Hex[neighbors.size()]);
    }
    public boolean inBounds(Pos pos){
        return pos.x >= 0 && pos.x < this.width && pos.y >= 0 && pos.y < this.height;
    }

    public boolean onCoast(Hex h){
        return getNeighbors(h).length < 6;
    }

    public ArrayList<Hex> allHexes(){
        ArrayList<Hex> hexes = new ArrayList<Hex>();
        for(int x = 0; x < this.width; x++){
            for(int y = 0; y < this.height; y++){
                hexes.add(this.hexes[x][y]);
            }
        }
        return hexes;
    }

    
}


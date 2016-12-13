//Sharhad Bashar
//Ecse 211
//Creates a representaition of the map provided to us in the lab report
public class Map {
	
	// creates a map of all 48 possible orientations as shown in the lab outline
	// i represents x-axis
	// j represents y-axis
	// k represents direction (0 N, 1 W, 2 S, 3 E)
	// all start as true
	// when we determine an orientation is not possible, set it to false
	
	//this method stores boolean values
	public static boolean[][][] initializeMap() {
		
		boolean[][][] map = new boolean[4][4][4];
		
		for (int i=0; i<4; i++) {
			for (int j=0; j<4; j++) {
				for (int k=0; k<4; k++) {
					if ( (i==0 && j==0) || (i==1 && j==2) || (i==1 && j==3) || (i==3 && j==1) ) {
						map[i][j][k] = false;
					}
					else {
						map[i][j][k] = true;
					}
				}
			}
		}
		return map;
	}
	
	// map that indicates which squares have blocks
	// true represents block 
	// false represents empty
	// i represents rows or x axis
	// j represents coloums or y axis
	public static boolean[][] wallMap() {
		
		boolean[][] wallMap = new boolean[4][4];
		
		for (int i=0; i<4; i++) {
			for (int j=0; j<4; j++) {
				if ( (i==0 && j==0) || (i==1 && j==2) || (i==1 && j==3) || (i==3 && j==1) ) {
					wallMap[i][j] = true;
				}
				else {
					wallMap[i][j] = false;
				}
			}
		}
		return wallMap;
	}
	
}
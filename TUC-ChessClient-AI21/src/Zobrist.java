import java.util.Random;

public class Zobrist{

    private static final int MAX_TABLE_SIZE = 8602812; // ram
    private static final int OPEN_ADDRESSING = 1000;
    private static final int RAND_MAX = 32767;
    private static final int COLORS = 2;
    private static final int PAWNS = 3;
    private static final int POSITIONS = 35;

    private long[][] zobrist_table=null;
    private Transposition[] hashTable=null ;

    public Zobrist (){
        
        hashTable = new Transposition[(MAX_TABLE_SIZE+OPEN_ADDRESSING)];
        zobrist_table = new long[POSITIONS][COLORS*PAWNS];
    }

    public void hash_table_init(){
         
        for (int i=0; i < (MAX_TABLE_SIZE+OPEN_ADDRESSING); i++){
            hashTable[i] = new Transposition(0, 0 , 0, 0, 0, 0);
        }

    }

    public void zobrist_init(){

        Random rand = new Random();

        for (int i =0; i <POSITIONS; i++)
        {
            for(int j =0; j <PAWNS*COLORS; j++){
                zobrist_table[i][j] = ((((long) rand.nextInt() <<  0)) | (((long) rand.nextInt() << 32)));
            }
        
        }
      
    }

    public int simpleHash(long zobrist){

        int hash = (int)zobrist;

        if(hash <0){
            hash = hash * (-1);
        }

        hash = hash % MAX_TABLE_SIZE;
        return hash;
    }

    public long zobrist_hash(String[][] board){
       
        long h = 0;

        String firstLetter = null;
		String secondLetter = null;

        for (int i= 0; i < 7; i++){
            
            for (int j=0; j < 5; j++){

                firstLetter = Character.toString(board[i][j].charAt(0));

				if (firstLetter.equals("W")) {	/* white colour */

					secondLetter = Character.toString(board[i][j].charAt(1));

                    if (secondLetter.equals("P")) {

						h ^= zobrist_table[i*5 + j][0];
                        if(h <0){
                            h = h * (-1);
                        }
					} else if (secondLetter.equals("R")) {
						
                        h ^= zobrist_table[i*5 + j][1];
                        if(h <0){
                            h = h * (-1);
                        }
					} else if (secondLetter.equals("K")){

						h ^= zobrist_table[i*5 + j][2];
                        if(h <0){
                            h = h * (-1);
                        }
					}

                }else if (firstLetter.equals("B")) {	/* black colour */

					secondLetter = Character.toString(board[i][j].charAt(1));
					
					if (secondLetter.equals("P")) {

						h ^= zobrist_table[i*5 + j][3];
                        if(h <0){
                            h = h * (-1);
                        }
					} else if (secondLetter.equals("R")) {
						
                        h ^= zobrist_table[i*5 + j][4];
                        if(h <0){
                            h = h * (-1);
                        }
					} else if (secondLetter.equals("K")) {
						
                        h ^= zobrist_table[i*5 + j][5
                        ];
                        if(h <0){
                            h = h * (-1);
                        }
					}
				}

            }
        }

        return h;
    }

    public int hash_func(String[][] board){
        
        long key = zobrist_hash(board);
        
        if(key <0){
            key = key * (-1);
        }

        int hash = simpleHash(key);

        if(hash <0){
            hash = hash * (-1);
        }
        
        int i=0;
        while ( ((hashTable[hash].validity & 0x1) !=0) && (hashTable[hash].zobrist_key != key) && (i < OPEN_ADDRESSING) )
        {
            i++;
            hash++;
        } 
         
        hashTable[hash].zobrist_key = key;
        return hash;
    }

    public void saveTransposition(String[][] board, int upperBound, int lowerBound, int depth){

        Random rand = new Random();

        int hash = hash_func(board);

        if(hash <0){
            hash = hash * (-1);
        }

        if( (((hashTable[hash].validity & 0x7) !=0) && ((hashTable[hash].upperDepth + hashTable[hash].lowerDepth) >= 2*depth)) && (rand.nextInt() < RAND_MAX/2) ){
            return;
        }           

        hashTable[hash].lowerBound = lowerBound;
        hashTable[hash].upperBound = upperBound;
        hashTable[hash].validity = 0x7;
        hashTable[hash].upperDepth = depth;
        hashTable[hash].lowerDepth = depth;
        
        return;
    } 

    public void saveUpper(String[][] board, int upperBound,  int depth){
        
        long key = zobrist_hash(board);
        
        if(key <0){
            key = key * (-1);
        }

        int hash = simpleHash(key);

        if(hash <0){
            hash = hash * (-1);
        }

        Random rand = new Random();
        
        int i=0;
        while ( ((hashTable[hash].validity & 0x1) != 0) && (hashTable[hash].zobrist_key != key) && (i <OPEN_ADDRESSING) )
        {
            i++;
            hash++;
        }
        
        if( ((hashTable[hash].validity & 0x1) != 0) && (hashTable[hash].zobrist_key == key))
        {

            if( (((hashTable[hash].validity & 0x4) != 0) && (hashTable[hash].upperDepth >= depth)) && (rand.nextInt() > RAND_MAX/2) )
                return;

            hashTable[hash].upperBound = upperBound;
            hashTable[hash].upperDepth = depth;
            hashTable[hash].validity = (hashTable[hash].validity | 0x4) ;
            
            return;
        }
        
        hashTable[hash].upperBound = upperBound;
        hashTable[hash].zobrist_key = key;
        hashTable[hash].validity = 0x5;
        hashTable[hash].upperDepth = depth;
        return;
    }
    
    public void saveLower(String[][] board, int lowerBound, int depth){
        
        long key = zobrist_hash(board);
        
        if(key <0){
            key = key * (-1);
        }

        int hash = simpleHash(key);

        if(hash <0){
            hash = hash * (-1);
        }
       
        Random rand = new Random();
        
        int i = 0;
        while ( ((hashTable[hash].validity & 0x1) != 0) && (hashTable[hash].zobrist_key != key) && (i <OPEN_ADDRESSING) )
        {
            i++;
            hash++;
        }
    
        if( ((hashTable[hash].validity & 0x1) != 0) && (hashTable[hash].zobrist_key == key))
        {
            if( (((hashTable[hash].validity & 0x2) != 0) && (hashTable[hash].lowerDepth >= depth)) && (rand.nextInt() > RAND_MAX/2) )
                return;

            hashTable[hash].lowerBound = lowerBound;
            hashTable[hash].lowerDepth = depth;
            hashTable[hash].validity = (hashTable[hash].validity | 0x2);
            
            return;
        }

        hashTable[hash].lowerBound = lowerBound;
        hashTable[hash].zobrist_key = key;
        hashTable[hash].validity = 0x3;
        hashTable[hash].lowerDepth = depth;
        return;
    }

    public Transposition retrieveTransposition(String[][] board){

        long zobrist = zobrist_hash(board);
        
        if(zobrist <0){
            zobrist = zobrist * (-1);
        }

        int hash = simpleHash(zobrist);

        if(hash <0){
            hash = hash * (-1);
        }

        int i =0;

        while( ((hashTable[hash].validity & 0x1) != 0) && (i < OPEN_ADDRESSING) )
        {	
            if((hashTable[hash].zobrist_key == zobrist))
                return hashTable[hash];

            i++;
            hash++;
        }

        return null;
    }

    public void freeTable(){
	    this.hashTable = null;
        this.zobrist_table = null;
    }

}
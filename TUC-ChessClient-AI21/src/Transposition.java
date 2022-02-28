public class Transposition{

        public long zobrist_key;
        public int upperBound;
        public int lowerBound;
        public int upperDepth;
        public int lowerDepth;
        public int validity; 
        
        public Transposition(long zk, int ub, int lb, int ud, int ld, int v){
            this.zobrist_key = zk;
            this.upperBound = ub;
            this.lowerBound = lb;
            this.upperDepth = ud;
            this.lowerDepth = ld;
            this.validity = v;
        }
} 

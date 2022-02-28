
------------------------------------TUC-CHESS--------------------------------------

For this game , three clients were made :

        1] Client.java -> is for ab-pruning
        2] Client2.java -> is for minimax
        3] Client3.java -> is for MCTS

BUILD :

javac *.java

jar uf tuc-chess-client.jar AlphaBeta_template.class World.class Client.class Transposition.class
Zobrist.class

jar uf tuc-chess-client2.jar Minimax_template.class World.class Client2.class

jar uf tuc-chess-client3.jar MCTS.class World.class Client3.class


RUN :

    We open 3 terminals and in first we run the server :

                    java -jar tuc-chess-server.jar.jar

    In the other two terminals we run two of the three clients : 

                    java -jar tuc-chess-client.jar 
                    java -jar tuc-chess-client2.jar
                    java -jar tuc-chess-client3.jar          



/*********************************************
 *  Agent.java 
 *  Sample Agent for Text-Based Adventure Game
 *  COMP3411 Artificial Intelligence
 *  UNSW Session 1, 2012
*/

//Homework 2 by Nathan Wilson, Student z3287546
//
//Question:
//
// Briefly describe how your program works, including algorithms and data structures
// employed, and explain any design decisions you made along the way.
//
//Answer:
//
// I decided to write in java, with an OO style approach. My basic approach was to model 'agent' and 'map' objects, using
// a static class called MoveCalculator to hold the various search algorithms. The rules for returning a move boil down to this
// First of all, update the map with the results of the previous move, then
// - if there is a move in the move queue return it
// - else, if there is a node in the current path being followed (a path being a sequence of adjacent squares), calculate moves to get there and return the first one
// - if there is no path currently being followed - ask the AI where to go next, and use the AStar path search algorithm to calculate a path to that square
//
// The AI method 'getNext' determined a coordinate to target next. The order of priority for choosing the next target square were as follows
// 1. If we have the gold, go home, else
// 2. If there is still a node in the queue of nodes needing to be visited, go there, else
// 3. If we have the axe and there are trees within reach, chop them down. This should be lower priority than 4, but as it is much cheaper to
//      compute, I placed it first.
// 4. If we can see the gold, attempt to find a path using a recursive floodfill (floodfill exits early if it finds a path with more dynamite
//      than we currently have). If one exists, go there, else
// 5. If we can see the axe, attempt to find a path as above, else
// 6. Find the longest floodfill path such that we have the same amount of dynamite - this is to help explore more areas
// 7. Finally, if there is an unvisited land square, try to find a floodfill path there
// If we got all the way to this point, it would mean we cannot see/get to any more dynamite or new areas, so would have no idea what to do.
// In reality, all the sample maps can be solved with the first 5 actions.
//
// The major design decisions were small things such as incorporating boats/axes into my astar algorithm so that they could be handled naturally in the course of exploring.
// The biggest design hurdle was making the program fast and memory efficient while still solving the challenge. I saved a lot of time and memory by making a recursive
// floodfill rather than an iterative one (iterative quickly got too large for the heap), and by ordering the priorities in such a way that computationally cheaper actions came first
// if they did not negatively affect the ability to solve the board. I also inserted an early exit into the floodfill which saved a lot of computation - it exits if it ever gets to a point where
// it has more dynamite than to begin with. This works because it is a strictly better position to be in.

import java.io.*;
import java.net.*;

public class Agent {

    private static AI ai;

    public char get_action(char view[][]) {

      Character move = ai.getMove();

      if (move != null)
          return move;

      int ch=0;

      System.out.print("Enter Action(s): ");

      try {
         while ( ch != -1 ) {
            // read character from keyboard
            ch  = System.in.read();

            switch( ch ) { // if character is a valid action, return it
            case 'F': case 'L': case 'R': case 'C': case 'B':
            case 'f': case 'l': case 'r': case 'c': case 'b':
               return((char) ch );
            }
         }
      }
      catch (IOException e) {
         System.out.println ("IO error:" + e );
      }

      return 0;
   }

   void print_view( char view[][] )
   {
      int i,j;

      System.out.println("\n+-----+");
      for( i=0; i < 5; i++ ) {
         System.out.print("|");
         for( j=0; j < 5; j++ ) {
            if(( i == 2 )&&( j == 2 )) {
               System.out.print('^');
            }
            else {
               System.out.print( view[i][j] );
            }
         }
         System.out.println("|");
      }
      System.out.println("+-----+");
   }

   public static void main( String[] args )
   {
      InputStream in  = null;
      OutputStream out= null;
      Socket socket   = null;
      Agent  agent    = new Agent();
      GameMap map = null;
      char   view[][] = new char[5][5];
      char   action   = 'p';
      int port;
      int ch;
      int i,j;
      AgentInfo agentInfo = new AgentInfoImpl(new Coords(82, 82), 'u');

      if( args.length < 2 ) {
         System.out.println("Usage: java Agent -p <port>\n");
         System.exit(-1);
      }

      port = Integer.parseInt( args[1] );

      try { // open socket to Game Engine
         socket = new Socket( "localhost", port );
         in  = socket.getInputStream();
         out = socket.getOutputStream();
      }
      catch( IOException e ) {
         System.out.println("Could not bind to port: "+port);
         System.exit(-1);
      }


      try { // scan 5-by-5 window around current location
         while( true ) {
            for( i=0; i < 5; i++ ) {
               for( j=0; j < 5; j++ ) {
                  if( !(( i == 2 )&&( j == 2 ))) {
                     ch = in.read();
                     if( ch == -1 ) {
                        System.exit(-1);
                     }
                     view[i][j] = (char) ch;
                  }
               }
            }
            if (map == null)
            {
                map = new MapImpl(view, agentInfo);
                ai = new AIImpl(agentInfo, map);
            }
            map.update(action, view);
            action = agent.get_action(view);
            out.write( action );
         }
      }
      catch( IOException e ) {
         System.out.println("Lost connection to port: "+ port );
         System.exit(-1);
      }
      finally {
         try {
            socket.close();
         }
         catch( IOException e ) {}
      }
   }
}

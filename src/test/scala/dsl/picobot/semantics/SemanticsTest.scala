package dsl.picobot.semantics

import org.scalatest._

import dsl.picobot.ir._
import dsl.picobot.parser._
import dsl.picobot.semantics._
import edu.hmc.langtools._

import picolib.maze.Maze
import picolib.semantics.Anything
import picolib.semantics.Blocked
import picolib.semantics.East
import picolib.semantics.GUIDisplay
import picolib.semantics.North
import picolib.semantics.StayHere
import picolib.semantics.Open
import picolib.semantics.Picobot
import picolib.semantics.Rule
import picolib.semantics.South
import picolib.semantics.State
import picolib.semantics.Surroundings
import picolib.semantics.TextDisplay
import picolib.semantics.West

/*
 * Tests the semantics of our external picobot language.
 * It only tests whether the rules are evaluated correctly.
 * This is because they are the most complicated part of the
 * program, and it's also deterministic. If we tested the semantics
 * of an entire Picobot program, we would not be able to compare
 * the results because the starting location of a Picobot
 * is randomized, so two Picobots with the same rules might
 * not be exactly equivalent.
 */

class PicobotSemanticsTests extends FunSpec
    with LangInterpretMatchers[AST, List[Rule]] {
  override val parser = PicoParser.tester _
  override val interpreter = evalRules _
  
  describe("A picobot program") {

    it("should be able to interpret a single rule") { 
    	program("Consider 1 = 2.") should compute (    	    
    	    List(
    	        picolib.semantics.Rule(State("1"),
    	        	Surroundings(Anything, Anything, Anything, Anything),
    	        	StayHere,
    	        	State("2"))))
    }

    it("should be able to interpret multiple rules") {
    	program("Consider 1 + n = 2, 1 + n - w * s = 2, 2 = 2 - w.") should compute (
    		List(
    		    picolib.semantics.Rule(State("1"),
    	        	Surroundings(Blocked, Anything, Anything, Anything),
    	        	StayHere,
    	        	State("2")),
    	        picolib.semantics.Rule(State("1"),
    	        	Surroundings(Blocked, Anything, Open, Anything),
    	        	StayHere,
    	        	State("2")),
    	        picolib.semantics.Rule(State("2"),
    	        	Surroundings(Anything, Anything, Anything, Anything),
    	        	West,
    	        	State("2"))))
    }	
    
    it ("can have no rules") {
      program("Consider.") should compute (
          List.empty
      )
    }   
  }
}

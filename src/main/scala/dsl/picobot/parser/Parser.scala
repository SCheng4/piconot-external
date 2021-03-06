package dsl.picobot.parser

import scala.util.parsing.combinator._
import dsl.picobot.ir._

object PicoParser extends JavaTokenParsers with PackratParsers {

    // parsing interface
    def apply(s: String): ParseResult[AST] = parseAll(program, s)
    def tester(s: String): ParseResult[AST] = parseAll(consider, s)
       
    lazy val filename: PackratParser[String] = """[a-zA-Z0-9]+(\.[a-zA-Z0-9]+)""".r ^^ { _.toString }
    
    lazy val program: PackratParser[Program] =
      ("Proof."~"Recall "~filename~"."~consider~"QED" ^^ 
          {case "Proof."~"Recall "~mazename~"."~consider~"QED" => Program(Declaration(mazename), consider)}
          )
              
    lazy val consider: PackratParser[Consider] =
      (opt("Consider"~repsep(rule,",")~".") ^^ {
        case Some("Consider"~rules~".") => Consider(rules)
        case None => Consider(List.empty)
        case Some(_) => throw new MatchError("Incorrectly formatted Consider statement")
        }
          )
    
    lazy val rule: PackratParser[Rule] =
      (lhs~"="~rhs ^^ {case l~"="~r ⇒ Rule(l, r)})
          
    lazy val lhs: PackratParser[Lhs] =
      (   state~(surrounding*) ^^ {case s~r => Lhs(s, r)}
          )
          
    lazy val surrounding: PackratParser[Surrounding] =
      (   "+"~dir ^^ {case "+"~d ⇒ Plus(d)}
        | "-"~dir ^^ {case "-"~d ⇒ Minus(d)}
        | "*"~dir ^^ {case "*"~d ⇒ Mult(d)}
        )
      
    lazy val rhs: PackratParser[Rhs] =
      (   state~"+"~dir ^^ {case s~"+"~d ⇒ Rhs(s, d)}
        | state~"-"~dir ^^ {case s~"-"~d ⇒ Rhs(s, d)}
        | state~"*"~dir ^^ {case s~"*"~d ⇒ Rhs(s, d)}
        | state ^^ {case s => Rhs(s, Stay())}
        )
      
    lazy val dir: PackratParser[Dir] =
      (   "n" ^^ {case _ => N()}
        | "e" ^^ {case _ => E()}
        | "w" ^^ {case _ => W()}
        | "s" ^^ {case _ => S()}
        | "η" ^^ {case _ => N()}
        | "ε" ^^ {case _ => E()}
        | "ω" ^^ {case _ => W()}
        | "ς" ^^ {case _ => S()}
        | """[a-zA-Z0-9]""".r ^^ {case s => throw new MatchError("Directions should be one of n,s,e,w. Found:" + s)}
      )
          
    lazy val state: PackratParser[State] = ( 
        """[0-9]+""".r ^^ {s ⇒ State(s.toInt)} 
        | """[a-zA-Z0-9]+""".r ^^ {s => throw new MatchError("Invalid state detected. Use only positive integer values. Found:" + s)}
        )
}
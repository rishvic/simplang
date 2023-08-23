grammar SimpLang;

source
   : rules EOF
   ;

rules
   : grammarRule*
   ;

grammarRule
   : productionRule  # prodRule
   | terminalRule    # termRule
   ;


terminalRule
   : TERM '->' STRING ';'
   ;


productionRule
   : NON_TERM '->' expr ';'
   ;

expr
   : left=expr right=expr      # concatExpr
   | left=expr '|' right=expr  # orExpr
   | NON_TERM                  # nonTerm
   | TERM                      # term
   | EMPTY                     # empty
   | '(' expr ')'              # parens
   ;


ARROW   : '->' ;
SEMI    : ';' ;
PIPE    : '|' ;
LPARENS : '(' ;
RPARENS : ')' ;
EMPTY   : '%empty' ;


TERM
   : [a-z][0-9A-Za-z]*
   ;

NON_TERM
   : [A-Z][0-9A-Za-z]*
   ;


STRING
   : '"' (ESC | SAFECODEPOINT)* '"'
   ;

LineComment
   : '//' ~[\r\n]* -> channel(HIDDEN)
   ;

WS
   : [ \t]+ -> channel(HIDDEN)
   ;

NEWLINE
   : '\r'? '\n' -> channel(HIDDEN)
   ;

fragment ESC
   : '\\' (["\\/bfnrt] | UNICODE)
   ;

fragment UNICODE
   : 'u' HEX HEX HEX HEX
   ;

fragment HEX
   : [0-9a-fA-F]
   ;

fragment SAFECODEPOINT
   : ~ ["\\\u0000-\u001F]
   ;

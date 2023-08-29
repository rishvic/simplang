%{
#include "grammar2.tab.h"
%}

DIC     ([[:digit:]]{-}[0])[[:digit:]]*
HIC     0[xX][[:xdigit:]]+
OIC     0[0-7]*
IC      {DIC}|{HIC}|{OIC}

DSEQ        [[:digit:]]+
DSEQ_OPT    [[:digit:]]*
FRAC        ({DSEQ_OPT}"."{DSEQ})|{DSEQ}"."
EXP         [eE][+-]?{DSEQ}
EXP_OPT     {EXP}?
FC          ({FRAC}{EXP_OPT})|({DSEQ}{EXP})

ID      [_[:alpha:]][_[:alnum:]]*

WS      [[:blank:]]+
EOL     "\r"?"\n"

%%
"-"     return TOK_MINUS;
"scan"  return TOK_SCAN;
"do"    return TOK_DO;
"float" return TOK_FLOAT;
"while" return TOK_WHILE;
"not"   return TOK_NOT;
"and"   return TOK_AND;
"else"  return TOK_ELSE;
"end"   return TOK_END;
";"     return TOK_SEMI;
">"     return TOK_GREATER;
"if"    return TOK_IF;
"{"     return TOK_LBRACE;
"or"    return TOK_OR;
"}"     return TOK_RBRACE;
"*"     return TOK_STAR;
"<"     return TOK_LESS;
"then"  return TOK_THEN;
"+"     return TOK_PLUS;
"int"   return TOK_INT;
"prog"  return TOK_PROG;
"="     return TOK_EQUAL;
"str"   return TOK_STR;
"print" return TOK_PRINT;
")"     return TOK_RPAREN;
"/"     return TOK_SLASH;
"("     return TOK_LPAREN;
":="    return TOK_ASSIGN;

{IC}    return TOK_IC;
{ID}    return TOK_ID;
{FC}    return TOK_FC;

{WS}    /* Do nothing. */
{EOL}   /* Do nothing. */
.       return -1;

<<EOF>> return YYEOF;
%%

%{
#include <stdio.h>
#include <stdlib.h>

#include "grammar1.tab.h"
%}

EOL     "\r"?"\n"

%%
"×"|"*"    return TOK_CROSS;
"+"        return TOK_PLUS;
"n"        return TOK_N;

{EOL}      /* Do nothing. */
.          return -1;
<<EOF>>    return YYEOF;
%%

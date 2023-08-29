#include "grammar1.tab.h"

#include <stddef.h>

char symbol_reps[7][256] = {TOK_CROSS_STR, TOK_PLUS_STR, TOK_N_STR, YYEOF_STR,
                            A_STR,         B_STR,        S_STR};

int rule0[2] = {TOK_CROSS, -1};
int rule1[2] = {TOK_PLUS, -1};
int rule2[1] = {-1};
int rule3[5] = {B, A, B, TOK_N, -1};
int rule4[3] = {B, TOK_N, -1};

int *yytab[3][4] = {
    {rule0, rule1, NULL, NULL},
    {rule2, rule2, rule3, rule2},
    {NULL, NULL, rule4, NULL},
};

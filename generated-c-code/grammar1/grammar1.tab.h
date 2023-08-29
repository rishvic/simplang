#ifndef GRAMMAR1_TABLE_H_
#define GRAMMAR1_TABLE_H_

#define TERMINAL_COUNT 4
#define NONTERMINAL_COUNT 3

#define TOK_CROSS 0
#define TOK_CROSS_STR "\"×\""
#define TOK_PLUS 1
#define TOK_PLUS_STR "\"+\""
#define TOK_N 2
#define TOK_N_STR "n"
#define YYEOF 3
#define YYEOF_STR "$"

#define A 4
#define A_STR "A"
#define B 5
#define B_STR "B"
#define S 6
#define S_STR "S"

extern char symbol_reps[7][256];

extern int rule0[2];
extern int rule1[2];
extern int rule2[1];
extern int rule3[5];
extern int rule4[3];

extern int *yytab[3][4];

#endif /* GRAMMAR1_TABLE_H_ */

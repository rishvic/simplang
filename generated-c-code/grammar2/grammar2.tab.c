#include "grammar2.tab.h"

#include <stddef.h>

char symbol_reps[58][256] = {
    TOK_MINUS_STR,   TOK_SCAN_STR,   TOK_DO_STR,
    TOK_FLOAT_STR,   TOK_WHILE_STR,  TOK_NOT_STR,
    TOK_AND_STR,     TOK_ELSE_STR,   TOK_IC_STR,
    TOK_END_STR,     TOK_SEMI_STR,   TOK_ID_STR,
    TOK_GREATER_STR, TOK_IF_STR,     TOK_LBRACE_STR,
    TOK_OR_STR,      TOK_RBRACE_STR, TOK_STAR_STR,
    TOK_LESS_STR,    TOK_THEN_STR,   TOK_PLUS_STR,
    TOK_INT_STR,     TOK_PROG_STR,   TOK_EQUAL_STR,
    TOK_STR_STR,     TOK_PRINT_STR,  TOK_RPAREN_STR,
    TOK_SLASH_STR,   TOK_LPAREN_STR, TOK_FC_STR,
    TOK_ASSIGN_STR,  YYEOF_STR,      E_r_STR,
    AE_r_STR,        BE_STR,         D_STR,
    BE_r_STR,        TY_STR,         E_STR,
    F_STR,           DL_STR,         RE_p_E_STR,
    VL_p_ID_STR,     P_STR,          IS_p_IF_n_BE_n_THEN_n_SL_STR,
    T_r_STR,         S_STR,          T_STR,
    SL_STR,          WS_STR,         AE_STR,
    IS_STR,          IOS_STR,        ES_STR,
    RE_STR,          PE_STR,         VL_STR,
    NE_STR};

int rule0[1] = {-1};
int rule1[4] = {E_r, T, TOK_MINUS, -1};
int rule2[4] = {E_r, T, TOK_PLUS, -1};
int rule3[1] = {-1};
int rule4[4] = {AE_r, NE, TOK_AND, -1};
int rule5[3] = {BE_r, AE, -1};
int rule6[4] = {TOK_SEMI, VL, TY, -1};
int rule7[1] = {-1};
int rule8[4] = {BE_r, AE, TOK_OR, -1};
int rule9[2] = {TOK_FLOAT, -1};
int rule10[2] = {TOK_INT, -1};
int rule11[3] = {E_r, T, -1};
int rule12[2] = {TOK_IC, -1};
int rule13[2] = {TOK_ID, -1};
int rule14[4] = {TOK_RPAREN, E, TOK_LPAREN, -1};
int rule15[2] = {TOK_FC, -1};
int rule16[1] = {-1};
int rule17[3] = {DL, D, -1};
int rule18[3] = {E, TOK_EQUAL, -1};
int rule19[3] = {E, TOK_LESS, -1};
int rule20[3] = {E, TOK_GREATER, -1};
int rule21[1] = {-1};
int rule22[2] = {VL, -1};
int rule23[5] = {TOK_END, SL, DL, TOK_PROG, -1};
int rule24[4] = {TOK_END, SL, TOK_ELSE, -1};
int rule25[2] = {TOK_END, -1};
int rule26[1] = {-1};
int rule27[4] = {T_r, F, TOK_STAR, -1};
int rule28[4] = {T_r, F, TOK_SLASH, -1};
int rule29[2] = {IS, -1};
int rule30[2] = {WS, -1};
int rule31[2] = {IOS, -1};
int rule32[2] = {ES, -1};
int rule33[3] = {T_r, F, -1};
int rule34[1] = {-1};
int rule35[3] = {SL, S, -1};
int rule36[6] = {TOK_END, SL, TOK_DO, BE, TOK_WHILE, -1};
int rule37[3] = {AE_r, NE, -1};
int rule38[6] = {IS_p_IF_n_BE_n_THEN_n_SL, SL, TOK_THEN, BE, TOK_IF, -1};
int rule39[3] = {PE, TOK_PRINT, -1};
int rule40[3] = {TOK_ID, TOK_SCAN, -1};
int rule41[5] = {TOK_SEMI, E, TOK_ASSIGN, TOK_ID, -1};
int rule42[3] = {RE_p_E, E, -1};
int rule43[2] = {TOK_STR, -1};
int rule44[2] = {E, -1};
int rule45[3] = {VL_p_ID, TOK_ID, -1};
int rule46[3] = {NE, TOK_NOT, -1};
int rule47[4] = {TOK_RBRACE, BE, TOK_LBRACE, -1};
int rule48[2] = {RE, -1};

int *yytab[26][32] = {
    {rule1, rule0, rule0, NULL,  rule0, NULL,  rule0, rule0,
     NULL,  rule0, rule0, rule0, rule0, rule0, NULL,  rule0,
     rule0, NULL,  rule0, rule0, rule2, NULL,  NULL,  rule0,
     NULL,  rule0, rule0, NULL,  NULL,  NULL,  NULL,  NULL},
    {NULL, NULL, rule3, NULL, NULL,  NULL,  rule4, NULL, NULL,  NULL, NULL,
     NULL, NULL, NULL,  NULL, rule3, rule3, NULL,  NULL, rule3, NULL, NULL,
     NULL, NULL, NULL,  NULL, NULL,  NULL,  NULL,  NULL, NULL,  NULL},
    {NULL,  NULL, NULL, NULL,  NULL, rule5, NULL,  NULL,  rule5, NULL, NULL,
     rule5, NULL, NULL, rule5, NULL, NULL,  NULL,  NULL,  NULL,  NULL, NULL,
     NULL,  NULL, NULL, NULL,  NULL, NULL,  rule5, rule5, NULL,  NULL},
    {NULL, NULL, NULL, rule6, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
     NULL, NULL, NULL, NULL,  NULL, NULL, NULL, NULL, NULL, NULL, rule6,
     NULL, NULL, NULL, NULL,  NULL, NULL, NULL, NULL, NULL, NULL},
    {NULL, NULL, rule7, NULL, NULL,  NULL,  NULL, NULL, NULL,  NULL, NULL,
     NULL, NULL, NULL,  NULL, rule8, rule7, NULL, NULL, rule7, NULL, NULL,
     NULL, NULL, NULL,  NULL, NULL,  NULL,  NULL, NULL, NULL,  NULL},
    {NULL, NULL, NULL, rule9, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
     NULL, NULL, NULL, NULL,  NULL, NULL, NULL, NULL, NULL, NULL, rule10,
     NULL, NULL, NULL, NULL,  NULL, NULL, NULL, NULL, NULL, NULL},
    {NULL,   NULL, NULL, NULL, NULL, NULL, NULL,   NULL,   rule11, NULL, NULL,
     rule11, NULL, NULL, NULL, NULL, NULL, NULL,   NULL,   NULL,   NULL, NULL,
     NULL,   NULL, NULL, NULL, NULL, NULL, rule11, rule11, NULL,   NULL},
    {NULL,   NULL, NULL, NULL, NULL, NULL, NULL,   NULL,   rule12, NULL, NULL,
     rule13, NULL, NULL, NULL, NULL, NULL, NULL,   NULL,   NULL,   NULL, NULL,
     NULL,   NULL, NULL, NULL, NULL, NULL, rule14, rule15, NULL,   NULL},
    {NULL, rule16, NULL, rule17, rule16, NULL,   NULL, NULL,
     NULL, rule16, NULL, rule16, NULL,   rule16, NULL, NULL,
     NULL, NULL,   NULL, NULL,   NULL,   rule17, NULL, NULL,
     NULL, rule16, NULL, NULL,   NULL,   NULL,   NULL, NULL},
    {NULL, NULL,   NULL, NULL, NULL, NULL, NULL, NULL,   NULL, NULL, NULL,
     NULL, rule20, NULL, NULL, NULL, NULL, NULL, rule19, NULL, NULL, NULL,
     NULL, rule18, NULL, NULL, NULL, NULL, NULL, NULL,   NULL, NULL},
    {NULL,   NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, rule21,
     rule22, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
     NULL,   NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL},
    {NULL,   NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
     NULL,   NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
     rule23, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL},
    {NULL, NULL, NULL, NULL, NULL, NULL, NULL, rule24, NULL, rule25, NULL,
     NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,   NULL, NULL,   NULL,
     NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,   NULL, NULL},
    {rule26, rule26, rule26, NULL,   rule26, NULL,   rule26, rule26,
     NULL,   rule26, rule26, rule26, rule26, rule26, NULL,   rule26,
     rule26, rule27, rule26, rule26, rule26, NULL,   NULL,   rule26,
     NULL,   rule26, rule26, rule28, NULL,   NULL,   NULL,   NULL},
    {NULL,   rule31, NULL,   NULL,   rule30, NULL, NULL, NULL, NULL, NULL, NULL,
     rule32, NULL,   rule29, NULL,   NULL,   NULL, NULL, NULL, NULL, NULL, NULL,
     NULL,   NULL,   NULL,   rule31, NULL,   NULL, NULL, NULL, NULL, NULL},
    {NULL,   NULL, NULL, NULL, NULL, NULL, NULL,   NULL,   rule33, NULL, NULL,
     rule33, NULL, NULL, NULL, NULL, NULL, NULL,   NULL,   NULL,   NULL, NULL,
     NULL,   NULL, NULL, NULL, NULL, NULL, rule33, rule33, NULL,   NULL},
    {NULL, rule35, NULL, NULL,   rule35, NULL,   NULL, rule34,
     NULL, rule34, NULL, rule35, NULL,   rule35, NULL, NULL,
     NULL, NULL,   NULL, NULL,   NULL,   NULL,   NULL, NULL,
     NULL, rule35, NULL, NULL,   NULL,   NULL,   NULL, NULL},
    {NULL, NULL, NULL, NULL, rule36, NULL, NULL, NULL, NULL, NULL, NULL,
     NULL, NULL, NULL, NULL, NULL,   NULL, NULL, NULL, NULL, NULL, NULL,
     NULL, NULL, NULL, NULL, NULL,   NULL, NULL, NULL, NULL, NULL},
    {NULL,   NULL, NULL, NULL,   NULL,   rule37, NULL,   NULL,
     rule37, NULL, NULL, rule37, NULL,   NULL,   rule37, NULL,
     NULL,   NULL, NULL, NULL,   NULL,   NULL,   NULL,   NULL,
     NULL,   NULL, NULL, NULL,   rule37, rule37, NULL,   NULL},
    {NULL, NULL, NULL,   NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
     NULL, NULL, rule38, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
     NULL, NULL, NULL,   NULL, NULL, NULL, NULL, NULL, NULL, NULL},
    {NULL, rule40, NULL, NULL,   NULL, NULL, NULL, NULL, NULL, NULL, NULL,
     NULL, NULL,   NULL, NULL,   NULL, NULL, NULL, NULL, NULL, NULL, NULL,
     NULL, NULL,   NULL, rule39, NULL, NULL, NULL, NULL, NULL, NULL},
    {NULL,   NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
     rule41, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
     NULL,   NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL},
    {NULL,   NULL, NULL, NULL, NULL, NULL, NULL,   NULL,   rule42, NULL, NULL,
     rule42, NULL, NULL, NULL, NULL, NULL, NULL,   NULL,   NULL,   NULL, NULL,
     NULL,   NULL, NULL, NULL, NULL, NULL, rule42, rule42, NULL,   NULL},
    {NULL,   NULL, NULL,   NULL, NULL, NULL, NULL,   NULL,   rule44, NULL, NULL,
     rule44, NULL, NULL,   NULL, NULL, NULL, NULL,   NULL,   NULL,   NULL, NULL,
     NULL,   NULL, rule43, NULL, NULL, NULL, rule44, rule44, NULL,   NULL},
    {NULL,   NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
     rule45, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
     NULL,   NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL},
    {NULL,   NULL, NULL, NULL,   NULL,   rule46, NULL,   NULL,
     rule48, NULL, NULL, rule48, NULL,   NULL,   rule47, NULL,
     NULL,   NULL, NULL, NULL,   NULL,   NULL,   NULL,   NULL,
     NULL,   NULL, NULL, NULL,   rule48, rule48, NULL,   NULL},
};

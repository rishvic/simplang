#include <stdio.h>
#include <stdlib.h>

#if defined(USE_GRAMMAR1)
#include "grammar1.tab.h"
#include "grammar1.yy.h"
#elif defined(USE_GRAMMAR2)
#include "grammar2.tab.h"
#include "grammar2.yy.h"
#else
#error "No grammar specified"
#endif

#define CSI_RESET "\e[0m"
#define CSI_UNDERLINE "\e[4m"
#define CSI_BOLD "\e[1m"
#define CSI_ITALIC "\e[3m"

int buf[4096];
size_t buf_scanned = 0, buf_consumed = 0;

int scan() { return buf[buf_scanned++] = yylex(); }

int peek() {
  while (buf_consumed >= buf_scanned) {
    if (scan() < 0) {
      fprintf(stderr,
              "Invalid input: Lexing error: Unexpected character \"%s\"\n",
              yytext);
      exit(1);
    }
  }
  return buf[buf_consumed];
}

int consume() {
  while (buf_consumed >= buf_scanned) {
    buf[buf_scanned++] = yylex();
  }
  return buf[buf_consumed++];
}

int main(void) {
  int stack[4096];
  size_t stack_sz = 0;
  int stack_symbol, consumed_symbol, peek_symbol;
  int term_stack[4096];
  size_t term_sz = 0;
  int i;
  int udp, rule_sz;
  int *rule;
#if defined(USE_GRAMMAR1)
  int start_rule = S;
  const char start_rule_str[] = S_STR;
#elif defined(USE_GRAMMAR2)
  int start_rule = P;
  const char start_rule_str[] = P_STR;
#else
#error "No grammar specified"
#endif

  stack[stack_sz++] = YYEOF;
  stack[stack_sz++] = start_rule;
  printf("   %s%s%s%s %s\n", CSI_BOLD, CSI_ITALIC, start_rule_str, CSI_RESET,
         YYEOF_STR);

  while (stack_sz > 0) {
    stack_symbol = stack[--stack_sz];
    if (stack_symbol < TERMINAL_COUNT) {
      if (stack_symbol != (consumed_symbol = consume())) {
        fprintf(stderr,
                "Invalid input: Mismatched terminal: expecting %s, found %s\n",
                symbol_reps[stack_symbol], symbol_reps[consumed_symbol]);
        return 1;
      }
      term_stack[term_sz++] = stack_symbol;
    } else {
      stack_symbol -= TERMINAL_COUNT;

      peek_symbol = peek();
      rule = yytab[stack_symbol][peek_symbol];
      if (rule == NULL) {
        fprintf(stderr,
                "Invalid input: Parsing error: No suitable rule for "
                "nonterminal %s with lookahead terminal %s\n",
                symbol_reps[stack_symbol + TERMINAL_COUNT],
                symbol_reps[peek_symbol]);
        return 1;
      }
      for (i = 0; rule[i] >= 0; i++)
        stack[stack_sz++] = rule[i];
      rule_sz = i;

      printf("->");
      if (term_sz <= 3) {
        for (i = 0; i < term_sz; i++) {
          printf(" %s%s%s", "", symbol_reps[term_stack[i]], CSI_RESET);
        }
      } else {
        printf(" %s...%s", CSI_ITALIC, CSI_RESET);
      }

      udp = 0;
      if (rule_sz > 0) {
        printf(" %s", CSI_UNDERLINE);
        for (i = 0; i < rule_sz; i++) {
          stack_symbol = stack[stack_sz - i - 1];
          if (!udp && stack_symbol >= TERMINAL_COUNT) {
            udp = 1;
            printf("%s%s%s%s%s%s", i ? " " : "", CSI_BOLD, CSI_ITALIC,
                   symbol_reps[stack_symbol], CSI_RESET, CSI_UNDERLINE);
          } else {
            printf("%s%s%s%s%s", i ? " " : "",
                   stack_symbol >= TERMINAL_COUNT ? CSI_ITALIC : "",
                   symbol_reps[stack_symbol], CSI_RESET, CSI_UNDERLINE);
          }
        }
        printf("%s", CSI_RESET);
      }

      for (i = rule_sz; i < stack_sz; i++) {
        stack_symbol = stack[stack_sz - i - 1];
        if (!udp && stack_symbol >= TERMINAL_COUNT) {
          udp = 1;
          printf(" %s%s%s%s", CSI_BOLD, CSI_ITALIC, symbol_reps[stack_symbol],
                 CSI_RESET);
        } else {
          printf(" %s%s%s", stack_symbol >= TERMINAL_COUNT ? CSI_ITALIC : "",
                 symbol_reps[stack_symbol], CSI_RESET);
        }
      }
      printf("\n");
    }
  }

  if (stack_sz == 0) {
    printf("Accepted\n");
    return 0;
  } else {
    printf("Rejected\n");
    return 1;
  }
}

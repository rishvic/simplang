cmake_minimum_required(VERSION 3.25)
project(SimpleGrammars VERSION 0.1.0 LANGUAGES C)

##############################################

set(MIN_FLEX_VERSION 2.6)
find_package(FLEX ${MIN_FLEX_VERSION} REQUIRED)

##############################################
# Build the Bison & Flex targets.

FLEX_TARGET(Grammar1 grammar1/grammar1.l ${CMAKE_CURRENT_BINARY_DIR}/grammar1.yy.c
            DEFINES_FILE ${CMAKE_CURRENT_BINARY_DIR}/grammar1.yy.h)

FLEX_TARGET(Grammar2 grammar2/grammar2.l ${CMAKE_CURRENT_BINARY_DIR}/grammar2.yy.c
            DEFINES_FILE ${CMAKE_CURRENT_BINARY_DIR}/grammar2.yy.h)

##############################################
# Create target and set properties.

add_executable(grammar1
  grammar.c
  grammar1/grammar1.tab.c
  ${FLEX_Grammar1_OUTPUTS}
)
target_compile_definitions(grammar1 PRIVATE USE_GRAMMAR1)

add_executable(grammar2
  grammar.c
  grammar2/grammar2.tab.c
  ${FLEX_Grammar2_OUTPUTS}
)
target_compile_definitions(grammar2 PRIVATE USE_GRAMMAR2)

#Set target properties
foreach(grammar IN ITEMS grammar1 grammar2)
  target_compile_definitions(${grammar} PRIVATE
      _FORTIFY_SOURCE=2
      _GLIBCXX_ASSERTIONS
  )

  target_include_directories(${grammar}
      PUBLIC
          $<INSTALL_INTERFACE:include>
      PRIVATE
          ${CMAKE_CURRENT_SOURCE_DIR}/${grammar}
          ${CMAKE_CURRENT_BINARY_DIR}
  )

  target_compile_features(${grammar} PRIVATE cxx_std_17)
  target_compile_options(${grammar} PRIVATE
      -fasynchronous-unwind-tables
      -fexceptions
      -fpie
      -fstack-clash-protection
      -fstack-protector-strong
      -g -grecord-gcc-switches
      -mshstk -fcf-protection
      -O2 -pipe
      -Wall
      -Werror=format-security
      -Werror=implicit-function-declaration
  )

  target_link_options(${grammar} PRIVATE
      "LINKER:-pie"
      "LINKER:-z,defs"
      "LINKER:-z,now"
      "LINKER:-z,relro"
  )

  target_link_libraries(${grammar}
      PUBLIC
          ${FLEX_LIBRARIES}
  )
endforeach()

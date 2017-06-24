cmake_minimum_required(VERSION 3.4.1)

add_library( bridge
             SHARED
             src/main/native/bridge.cpp )

add_library( kissfft
             SHARED
             src/main/native/kissfft/kiss_fft.c )

target_include_directories( bridge
                            PRIVATE
                            src/main/native/kissfft )

target_link_libraries( bridge
                       kissfft )
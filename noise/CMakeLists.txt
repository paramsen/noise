cmake_minimum_required(VERSION 3.4.1)

add_library(noise
        SHARED
        src/main/native/noise.c
        src/main/native/kissfft/kiss_fft.c
        src/main/native/kissfft/tools/kiss_fftr.c)

target_include_directories(noise
        PRIVATE
        src/main/native/kissfft
        src/main/native/kissfft/tools)

find_library(log-lib
        log)

target_link_libraries(noise
        ${log-lib})
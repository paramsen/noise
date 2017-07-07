cmake_minimum_required(VERSION 2.8)

project(build_test_dataset)

add_executable(build_test_dataset kiss_fft_tester.cpp)

include_directories("${PROJECT_SOURCE_DIR}/../library/src/main/native/kissfft/" "${PROJECT_SOURCE_DIR}/../library/src/main/native/kissfft/tools")

add_library(kissfft SHARED "${PROJECT_SOURCE_DIR}/../library/src/main/native/kissfft/kiss_fft.c" "${PROJECT_SOURCE_DIR}/../library/src/main/native/kissfft/tools/kiss_fftr.c")

target_link_libraries(build_test_dataset kissfft)

#!/bin/sh

rm -r build 2> /dev/null
mkdir build
pushd build

cmake ../ && cmake --build . && echo "\n"

popd

chmod +x build/build_test_dataset

# Requires 3 args to build test data
./build/build_test_dataset $@

#include <stdio.h>
#include <string>
#include "kiss_fft.h"
#include "kiss_fftr.h"

using namespace std;

void real(string inPath, string outPath, int size);
void imag(string inPath, string outPath, int size);
void readFloats(string path, float *into);
void writeFloats(string path, float *from, int size);

/**
 * 
 */
int main(int argc, char *argv[]) {
  if(argc != 4) {
    printf("Usage:\n\ttester [mode r OR i] [input path] [output path]\n");
    return 1;
  }

  string type = argv[1];
  string inPath = argv[2];
  string outPath = argv[3];

  if(type == "r") {
    real(inPath, outPath, 4096);
  } else {
    imag(inPath, outPath, 4096);
  }
  return 0;
}

void real(string inPath, string outPath, int size) {
  printf("real input: (%s)\n", inPath.c_str());

  float *input = (float *) malloc(sizeof(float) * size);
  float *output = (float *) malloc(sizeof(float) * size);

  readFloats(inPath, input);

  kiss_fftr_cfg config = kiss_fftr_alloc(size, 0, 0, 0);
  kiss_fft_cpx *result = (kiss_fft_cpx *) malloc(sizeof(kiss_fft_cpx) * size);
  kiss_fftr(config, input, result);

  for (int i = 0; i < size / 2; ++i) {
    output[i * 2] = result[i].r;
    output[i * 2 + 1] = result[i].i;
  }

  writeFloats(outPath, output, size);

  printf("result saved to: (%s)\n", outPath.c_str());
}

void imag(string inPath, string outPath, int size) {
  printf("imag input: (%s)\n", inPath.c_str());

  float *input = (float *) malloc(sizeof(float) * size);
  float *output = (float *) malloc(sizeof(float) * size * 2);

  readFloats(inPath, input);

  kiss_fft_cpx *fftInput = (kiss_fft_cpx *) malloc(sizeof(kiss_fft_cpx) * size);

  for (int i = 0; i < size; i++) {
    fftInput[i].r = input[i];
    fftInput[i].i = input[i];
  }

  kiss_fft_cfg config = kiss_fft_alloc(size, 0, 0, 0);
  kiss_fft_cpx *result = (kiss_fft_cpx *) malloc(sizeof(kiss_fft_cpx) * size);
  kiss_fft(config, fftInput, result);

  for (int i = 0; i < size; i++) {
    output[i * 2] = result[i].r;
    output[i * 2 + 1] = result[i].i;
  } 
  
  writeFloats(outPath, output, size * 2);

  printf("result saved to: (%s)\n", outPath.c_str());
}

void readFloats(string path, float *into) {
  FILE *file = fopen(path.c_str(), "r");
  if(file == NULL) printf("Error location file (%s)\n", path.c_str());

  fseek(file, 4, SEEK_SET);
  fread(into, sizeof(float), 4096, file);

  //for(int i = 0; i < 4096; i++) {
  //  printf("%f", into[i]);
  //}
}


void writeFloats(string path, float *from, int size) {
  FILE *file = fopen(path.c_str(), "w");
  if(file == NULL) printf("Error location file (%s)\n", path.c_str());

  fwrite(&size, sizeof(int32_t), 1, file);
  fwrite(from, sizeof(float), size, file);
}

#pragma once
#include "Table.h"

const int FILL_VALUE = 34;

void allocTableFill34(int size);

bool allocTable2Dim(int*** table, int sizeX, int sizeY);

bool deallocTable2Dim(int***, int sizeX, int sizeY);

void modTab(Table* table, int newSize);

void modTab(Table table, int newSize);
#pragma once
#include <string>

class Table
{
public:
	Table();
	Table(string name, int tableLen);
	Table(Table& other);

private:
	string name;
	int tableLen;
};


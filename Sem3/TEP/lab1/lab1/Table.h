#pragma once

#include <string>

using namespace std;

class Table
{
private:
	string name;
	int* table;
	int tableLen;

	static const int DEFAULT_TABLE_LEN;
	static const char* DEFAULT_NAME;

public:
	Table();
	Table(string name, int tableLen);
	Table(Table& other);

	void setName(string name);
	bool setNewSize(int tableLen);

	Table* clone();

	~Table();
};


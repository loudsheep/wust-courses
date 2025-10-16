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
	static const string DEFAULT_NAME;

public:
	Table();
	Table(string name, int tableLen);
	Table(Table& other);

	void setName(string name);
	bool setNewSize(int tableLen);
	void insertHere(Table& other, int position);

	Table* clone();

	~Table();

	// Testing methods
	void printTable();
	void set(int index, int value);
};


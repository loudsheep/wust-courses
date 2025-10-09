#include "Table.h"
#include <format>
#include <iostream>

const int Table::DEFAULT_TABLE_LEN = 1;
const string Table::DEFAULT_NAME = "name";

Table::Table()
{
	this->name = DEFAULT_NAME;
	this->tableLen = DEFAULT_TABLE_LEN;
	this->table = new int[this->tableLen];
}

Table::Table(string name, int tableLen)
{
	this->name = name;
	this->tableLen = tableLen;
	this->table = new int[this->tableLen];
}

Table::Table(Table& other)
{
	this->name = other.name + "_copy";
	this->tableLen = tableLen;
	this->table = new int[this->tableLen];

	for (int i = 0; i < this->tableLen; i++)
	{
		this->table[i] = other.table[i];
	}

	std::cout << "kopiuj: " << this->name;
}

void Table::setName(string name)
{
	this->name = name;
}

bool Table::setNewSize(int tableLen)
{
	if (tableLen <= 0) return false;

	delete[] this->table;

	this->tableLen = tableLen;
	this->table = new int[tableLen];

	return true;
}

Table* Table::clone()
{
	return nullptr;
}

Table::~Table()
{
	std::cout << "usuwam: " << this->name;
}

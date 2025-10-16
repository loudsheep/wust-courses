#include "Table.h"
#include <iostream>

const int Table::DEFAULT_TABLE_LEN = 1;
const string Table::DEFAULT_NAME = "name";

Table::Table()
{
	this->name = DEFAULT_NAME;
	this->tableLen = DEFAULT_TABLE_LEN;
	this->table = new int[this->tableLen];

	std::cout << "bezp: " << this->name << std::endl;
}

Table::Table(string name, int tableLen)
{
	if (tableLen <= 0)
	{
		this->tableLen = DEFAULT_TABLE_LEN;
	}
	else
	{
		this->tableLen = tableLen;
	}

	this->name = name;
	this->table = new int[this->tableLen];

	std::cout << "parametr: " << this->name << std::endl;
}

Table::Table(Table& other)
{
	this->name = other.name + "_copy";
	this->tableLen = other.tableLen;
	this->table = new int[this->tableLen];

	for (int i = 0; i < this->tableLen; i++)
	{
		this->table[i] = other.table[i];
	}

	std::cout << "kopiuj: " << this->name << std::endl;
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

void Table::insertHere(Table& other, int position)
{
	if (position < 0 || position > this->tableLen) return;

	int newLen = this->tableLen + other.tableLen;
	int* newTable = new int[newLen];

	// First table part
	for (int i = 0; i < position; i++)
	{
		newTable[i] = this->table[i];
	}

	// Insert Other table
	for (int i = 0; i < other.tableLen; i++)
	{
		newTable[i + position] = other.table[i];
	}

	// Rest of the table
	for (int i = 0; i < this->tableLen - position; i++)
	{
		newTable[i + position + other.tableLen] = this->table[i + position];
	}

	// Dealloc old table and assign new one
	delete[] this->table;
	this->table = newTable;
	this->tableLen = newLen;
}

Table* Table::clone()
{
	return new Table(*this);
}

Table::~Table()
{
	std::cout << "usuwam: " << this->name << std::endl;
	delete[] this->table;
}

void Table::printTable()
{
	std::cout << this->name << ": ";
	for (int i = 0; i < this->tableLen; i++)
	{
		std::cout << this->table[i] << " ";
	}

	std::cout << std::endl;
}

void Table::set(int index, int value)
{
	if (index < 0 || index >= this->tableLen) return;
	this->table[index] = value;
}

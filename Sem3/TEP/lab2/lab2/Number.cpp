#include "Number.h"
#include "cmath"

void Number::fillWithZero()
{
	for (int i = 0; i < this->length; i++)
	{
		this->table[i] = 0;
	}
}

Number::Number()
{
	this->length = NUMBER_DEFAULT_LENGTH;
	this->table = new int[this->length];
	this->fillWithZero();
}

Number::Number(int length)
{
	if (length <= 0) this->length = NUMBER_DEFAULT_LENGTH;
	else this->length = length;

	this->table = new int[this->length];
	this->fillWithZero();
}

Number::Number(Number& other)
{
	this->length = other.length;
	this->table = new int[this->length];

	for (int i = 0; i < this->length; i++)
	{
		this->table[i] = other.table[i];
	}
}

void Number::operator=(const int value)
{
	delete[] this->table;

	if (value == 0) {
		this->length = 1;
		this->table = new int[1];
		this->table[0] = 0;
		return;
	}

	int n = value;
	// TODO: handle negative numbers
	if (n < 0) n = -n;

	int digits = 0;
	while (n > 0) {
		n /= 10;
		digits++;
	}

	this->length = digits;
	this->table = new int[digits];

	n = value;
	if (n < 0) n = -n;
	for (int i = digits - 1; i >= 0; i--)
	{
		this->table[i] = n % 10;
		n /= 10;
	}
}

void Number::operator=(const Number& value)
{
	delete[] this->table;

	this->length = value.length;
	this->table = new int[this->length];

	for (int i = 0; i < this->length; i++)
	{
		this->table[i] = value.table[i];
	}
}

std::string Number::toStr()
{
	std::string result = "";
	for (int i = 0; i < this->length; i++)
	{
		result += std::to_string(this->table[i]);
	}
	return result;
}

Number::~Number()
{
	delete[] this->table;
}

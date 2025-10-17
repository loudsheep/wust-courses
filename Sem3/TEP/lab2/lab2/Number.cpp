#include "Number.h"
#include <cmath>
#include <algorithm>

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
	this->isNegative = false;
	this->fillWithZero();
}

Number::Number(int length)
{
	if (length <= 0) this->length = NUMBER_DEFAULT_LENGTH;
	else this->length = length;

	this->table = new int[this->length];
	this->isNegative = false;
	this->fillWithZero();
}

Number::Number(Number& other)
{
	this->length = other.length;
	this->table = new int[this->length];
	this->isNegative = other.isNegative;

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

	if (value < 0) {
		this->isNegative = true;
	}

	int n = value;
	int digits = 0;
	while (n != 0) {
		n /= 10;
		digits++;
	}

	this->length = digits;
	this->table = new int[digits];

	n = value;
	if (n < 0) n = -n;
	for (int i = 0; i < digits; i++)
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
	this->isNegative = value.isNegative;

	for (int i = 0; i < this->length; i++)
	{
		this->table[i] = value.table[i];
	}
}

std::string Number::toStr()
{
	std::string result = "";
	if (this->isNegative) result += "-";
	for (int i = this->length - 1; i >= 0; i--)
	{
		result += std::to_string(this->table[i]);
	}
	return result;
}

Number Number::addAbs(const Number& a, const Number& b)
{
	int maxSize = std::max(a.length, b.length);
	// resereve space for up to one more digit
	int* result = new int[maxSize + 1];

	int carry = 0;
	for (int i = 0; i < maxSize; i++)
	{
		int sum = carry;
		if (i < a.length) sum += a.table[i];
		if (i < b.length) sum += b.table[i];

		result[i] = sum % 10;
		carry = sum / 10;
	}
	if (carry > 0) result[maxSize++] = carry;

	Number res(maxSize);
	for (int i = 0; i < maxSize; i++)
	{
		res.table[i] = result[i];
	}

	delete[] result;
	return res;
}

Number::~Number()
{
	delete[] this->table;
}

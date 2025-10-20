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

Number Number::operator+(Number& other)
{
	Number res;
	if (this->isNegative == other.isNegative) {
		res = addAbs(*this, other);
		res.isNegative = this->isNegative;
		return res;
	}

	int compare = compareAbs(*this, other);
	if (compare == 0) {
		res = 0;
		return res;
	}

	// this > other
	if (compare > 0) {
		res = subAbs(*this, other);
		res.isNegative = this->isNegative;
	}
	else {
		res = subAbs(other, *this);
		res.isNegative = other.isNegative;
	}

	return res;
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
	// resereve space for one more digit
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

// Assume |a| >= |b|
Number Number::subAbs(const Number& a, const Number& b)
{
	int* result = new int[a.length];
	for (int i = 0; i < a.length; i++) result[i] = 0;

	int borrow = 0;
	for (int i = 0; i < a.length; i++)
	{
		int diff = a.table[i] - (i < b.length ? b.table[i] : 0) - borrow;

		if (diff < 0) {
			diff += 10;
			borrow = 1;
		}
		else {
			borrow = 0;
		}
		result[i] = diff;
	}

	// Reduce array length
	int newLength = a.length;
	while (newLength > 1 && result[newLength - 1] == 0) newLength--;

	Number res(newLength);
	for (int i = 0; i < newLength; i++)
	{
		res.table[i] = result[i];
	}

	delete[] result;
	return res;
}

// compare just numbers, ignoe isNegative flag
int Number::compareAbs(const Number& a, const Number& b)
{
	if (a.length != b.length) return a.length > b.length ? 1 : -1;
	for (int i = a.length - 1; i >= 0; i--)
	{
		if (a.table[i] != b.table[i]) return a.table[i] > b.table[i] ? 1 : -1;
	}
	return 0;
}

Number::~Number()
{
	delete[] this->table;
}

#pragma once
#include <string>

const int NUMBER_DEFAULT_LENGTH = 1;

class Number
{
private:
	int length;
	int* table;
	bool isNegative;

	void fillWithZero();

public:
	Number();
	Number(int length);
	Number(const Number& other);

	Number& operator=(const int value);
	Number& operator=(const Number& value);

	Number operator+(const Number& other);
	Number operator-(const Number& other);
	Number operator*(const Number& other);
	Number operator/(const Number& other);

	std::string toStr();

	static Number addAbs(const Number& a, const Number& b);
	static Number subAbs(const Number& a, const Number& b);
	static int compareAbs(const Number& a, const Number& b);

	~Number();
};


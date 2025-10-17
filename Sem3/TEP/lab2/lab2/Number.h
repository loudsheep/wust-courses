#pragma once
#include <string>

const int NUMBER_DEFAULT_LENGTH = 1;

class Number
{
private:
	int length;
	int* table;

	void fillWithZero();

public:
	Number();
	Number(int length);
	Number(Number& other);

	void operator=(const int value);
	void operator=(const Number& value);

	Number operator+(Number& other);
	Number operator-(Number& other);
	Number operator*(Number& other);
	Number operator/(Number& other);

	std::string toStr();

	~Number();
};


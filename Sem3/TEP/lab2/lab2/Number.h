#pragma once
#include <string>

class Number
{
private:
	int length;
	int* table;

public:
	const int NUMBER_DEFAULT_LENGTH = 5;

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


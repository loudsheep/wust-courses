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
	Number(Number& other);

	void operator=(const int value);
	void operator=(const Number& value);

	Number operator+(Number& other);
	Number operator-(Number& other);
	Number operator*(Number& other);
	Number operator/(Number& other);

	std::string toStr();

	static Number addAbs(const Number& a, const Number& b);
	static Number subAbs(const Number& a, const Number& b);
	static Number compareAbs(const Number& a, const Number& b);


	~Number();
};


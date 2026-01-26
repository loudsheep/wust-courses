#pragma once
#include <string>

class ParsingError
{
public:
	ParsingError(const std::string& message);
	ParsingError(const ParsingError& other);

	ParsingError& operator=(const ParsingError& other);

	std::string& getMessage();
private:
	std::string message;
};


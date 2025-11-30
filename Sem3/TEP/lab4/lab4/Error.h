#pragma once
#include <string>

class Error
{
public:
	Error(std::string message);

	std::string& getMessage();
private:
	std::string message;
};


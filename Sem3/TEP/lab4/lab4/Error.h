#pragma once
#include <string>

class Error
{
public:
	Error(const std::string& message);
	Error(const Error& other);

	std::string& getMessage();
private:
	std::string message;
};


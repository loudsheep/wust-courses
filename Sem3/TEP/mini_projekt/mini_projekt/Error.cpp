#include "Error.h"

Error::Error(const std::string& message)
{
	this->message = message;
}

Error::Error(const Error& other)
{
	this->message = other.message;
}

Error& Error::operator=(const Error& other)
{
	this->message = other.message;
	return *this;
}

std::string& Error::getMessage()
{
	return this->message;
}

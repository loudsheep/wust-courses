#include "ParsingError.h"

ParsingError::ParsingError(const std::string& message)
{
	this->message = message;
}

ParsingError::ParsingError(const ParsingError& other)
{
	this->message = other.message;
}

ParsingError& ParsingError::operator=(const ParsingError& other)
{
	this->message = other.message;
	return *this;
}

std::string& ParsingError::getMessage()
{
	return this->message;
}

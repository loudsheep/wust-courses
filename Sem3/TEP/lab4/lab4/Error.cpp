#include "Error.h"

Error::Error(std::string message)
{
	this->message = message;
}

std::string& Error::getMessage()
{
	return this->message;
}

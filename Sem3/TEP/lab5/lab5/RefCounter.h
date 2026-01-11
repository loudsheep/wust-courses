#pragma once
class RefCounter
{
public:
	RefCounter() 
	{
		this->count = 0;
	}

	int add() 
	{
		return ++this->count;
	}

	int dec() 
	{
		return --this->count;
	}

	int get() 
	{
		return this->count;
	}

private:
	int count;
};


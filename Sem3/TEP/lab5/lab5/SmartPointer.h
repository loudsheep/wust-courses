#pragma once
#include "RefCounter.h"

template<typename T>
class SmartPointer
{
public:
	SmartPointer(T* ptr);
	SmartPointer(const SmartPointer& other);
	~SmartPointer();

	SmartPointer operator=(const SmartPointer& other);
	T& operator*();
	T* operator->();

private:
	T* pointer;
	RefCounter* refcounter;
};
#pragma once
#include "RefCounter.h"
#include "Tree.h" // zeby intellisense dzialal 

template<typename T>
class SmartPointer
{
public:
	SmartPointer(T* ptr);
	SmartPointer(const SmartPointer& other);
	~SmartPointer();

	SmartPointer& operator=(const SmartPointer& other);
	T& operator*();
	T* operator->();

	T* get();

private:
	T* pointer;
	RefCounter* refcounter;

	void release();
};

template<typename T>
inline SmartPointer<T>::SmartPointer(T* ptr)
{
	this->pointer = ptr;
	this->refcounter = new RefCounter();
	this->refcounter->add();
}

template<typename T>
inline SmartPointer<T>::SmartPointer(const SmartPointer& other)
{
	this->pointer = other.pointer;
	this->refcounter = other.refcounter;
	this->refcounter->add();
}

template<typename T>
inline SmartPointer<T>::~SmartPointer()
{
	this->release();
}

template<typename T>
inline SmartPointer<T>& SmartPointer<T>::operator=(const SmartPointer<T>& other)
{
	if (this == &other) return *this;

	this->release();

	this->pointer = other.pointer;
	this->refcounter = other.refcounter;
	this->refcounter->add();

	return *this;
}

template<typename T>
inline T& SmartPointer<T>::operator*()
{
	return *this->pointer;
}

template<typename T>
inline T* SmartPointer<T>::operator->()
{
	return this->pointer;
}

template<typename T>
inline T* SmartPointer<T>::get()
{
	return this->pointer;
}

template<typename T>
inline void SmartPointer<T>::release()
{
	if (this->refcounter->dec() == 0)
	{
		delete this->pointer;
		delete this->refcounter;
	}
}

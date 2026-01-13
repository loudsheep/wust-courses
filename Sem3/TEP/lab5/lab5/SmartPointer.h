#pragma once
#include "RefCounter.h"
#include "Tree.h"

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

private:
	T* pointer;
	RefCounter* refcounter;
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
	if (this->refcounter->dec() == 0) {
		delete this->pointer;
		delete this->refcounter;
	}
}

template<typename T>
inline SmartPointer<T>& SmartPointer<T>::operator=(const SmartPointer<T>& other)
{
	if (this == &other) return *this;

	if (this->refcounter->dec() == 0) {
		delete this->pointer;
		delete this->refcounter;
	}

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

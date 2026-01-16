#pragma once
#include "Tree.h"

template<typename T>
class BorrowingPointer;

template<typename T>
class RefCounter;

template<typename T>
class SmartPointer
{
	friend class BorrowingPointer<T>;

public:
	SmartPointer(T* ptr);
	SmartPointer(const SmartPointer& other);
	~SmartPointer();

	SmartPointer& operator=(const SmartPointer& other);
	T& operator*();
	T* operator->();

	/*BorrowingPointer<T> borrow()
	{
		return BorrowingPointer<T>(*this);
	}*/

	T* get();

private:
	T* pointer;
	RefCounter<T>* refcounter;

	void release();
};

template<typename T>
inline SmartPointer<T>::SmartPointer(T* ptr)
{
	this->pointer = ptr;
	this->refcounter = new RefCounter<T>();
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
		this->refcounter->nullifyBorrowers();

		delete this->pointer;
		delete this->refcounter;
	}
}


//////////////////////////////

template<typename T>
class BorrowingPointer
{
	friend class SmartPointer<T>;
	friend class RefCounter<T>;

public:
	BorrowingPointer(SmartPointer<T>& smartPointer)
	{
		this->pointer = smartPointer.pointer;
		this->counter = smartPointer.refcounter;

		if (this->counter)
		{
			this->counter->addBorrow(this);
		}
	}

	BorrowingPointer(BorrowingPointer<T>& other)
	{
		this->pointer = other.pointer;
		this->counter = other.counter;

		if (this->counter)
		{
			this->counter->addBorrow(this);
		}
	}

	~BorrowingPointer()
	{
		if (this->counter)
		{
			this->counter->removeBorrow(this);
		}
	}

	BorrowingPointer<T>& operator=(const BorrowingPointer<T>& other)
	{
		if (this == &other) return *this;

		this->pointer = other.pointer;
		this->counter = other.counter;

		if (this->counter)
		{
			this->counter->removeBorrow(this);
		}

		return *this;
	}

	T& operator*()
	{
		return *this->pointer;
	}

	T* operator->()
	{
		return this->pointer;
	}

	bool isValid()
	{
		return this->pointer != nullptr;
	}

private:
	T* pointer;
	RefCounter<T>* counter;

	void nullify()
	{
		this->pointer = nullptr;
		this->counter = nullptr;
	}
};

///////////////////////

template<typename T>
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

	void addBorrow(BorrowingPointer<T>* bp)
	{
		this->borrows.push_back(bp);
	}

	void removeBorrow(BorrowingPointer<T>* bp)
	{
		borrows.erase(std::remove(borrows.begin(), borrows.end(), bp), borrows.end());
	}

	void nullifyBorrowers()
	{
		for (BorrowingPointer<T>* borrower : this->borrows) {
			borrower->nullify();
		}
		this->borrows.clear();
	}
private:
	int count;

	std::vector<BorrowingPointer<T>*> borrows;
};
